//
//  Copyright (c) 2014, Nexage, Inc.
//  All rights reserved.
//  Provided under BSD-3 license as follows:
//
//  Redistribution and use in source and binary forms, with or without modification,
//  are permitted provided that the following conditions are met:
//
//  Redistributions of source code must retain the above copyright notice, this
//  list of conditions and the following disclaimer.
//
//  Redistributions in binary form must reproduce the above copyright notice, this
//  list of conditions and the following disclaimer in the documentation and/or
//  other materials provided with the distribution.
//
//  Neither the name of Nexage nor the names of its
//  contributors may be used to endorse or promote products derived from
//  this software without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
//  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
//  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package net.pubnative.player;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.player.model.TRACKING_EVENTS_TYPE;
import net.pubnative.player.model.VASTModel;
import net.pubnative.player.util.BitmapDownloaderTask;
import net.pubnative.player.util.HttpTools;
import net.pubnative.player.util.VASTLog;
import net.pubnative.player.widget.CountDownView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VASTPlayer extends RelativeLayout implements MediaPlayer.OnCompletionListener,
                                                          MediaPlayer.OnErrorListener,
                                                          MediaPlayer.OnPreparedListener,
                                                          MediaPlayer.OnVideoSizeChangedListener,
                                                          View.OnClickListener,
                                                          SurfaceHolder.Callback {

    private static final String TAG = VASTPlayer.class.getName();

    /**
     * Player type will lead to different layouts and behaviour to improve campaign type
     */
    public enum CampaignType {

        // Cost per click, this will improve player click possibilities
        CPC,

        // Cost per million (of impressions), this will improve impression behaviour (keep playing)
        CPM
    }

    /**
     * Callbacks for following the player behaviour
     */
    public interface Listener {

        void onVASTPlayerLoadFinish();
        void onVASTPlayerFail(Exception exception);
        void onVASTPlayerPlaybackStart();
        void onVASTPlayerPlaybackFinish();
        void onVASTPlayerClick();
    }

    private enum PlayerState {

        None,
        Loader,
        Ready,
        Player,
        Banner
    }

    // LISTENERS
    private Listener mListener = null;

    // TIMERS
    private Timer mLayoutTimer;
    private Timer mProgressTimer;
    private Timer mTrackingEventsTimer;

    private static final long TIMER_TRACKING_INTERVAL = 250;
    private static final long TIMER_PROGRESS_INTERVAL = 50;
    private static final long TIMER_LAYOUT_INTERVAL   = 50;

    private static final int MAX_PROGRESS_TRACKING_POINTS = 20;

    // TRACKING
    private HashMap<TRACKING_EVENTS_TYPE, List<String>> mTrackingEventMap;

    // DATA
    private VASTModel mVastModel;
    private String    mSkipName;
    private int       mSkipTime;

    // PLAYER
    private MediaPlayer   mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;

    // VIEWS
    private RelativeLayout mPlayerRoot;

    // View
    private RelativeLayout mPlayerView;
    private RelativeLayout mPlayerViewContainer;
    private SurfaceView    mPlayerViewContainerSurface;
    private RelativeLayout mPlayerViewLayout;
    private TextView       mPlayerViewLayoutSkip;
    private ImageView      mPlayerViewLayoutMute;
    private ImageView      mPlayerViewLayoutLearnMore;
    private CountDownView  mPlayerViewLayoutCountDown;
    private RelativeLayout mPlayerViewLoader;
    private RelativeLayout mPlayerLoader;
    private RelativeLayout mPlayerBanner;
    private ImageView      mPlayerBannerImage;

    // OTHERS
    private Handler       mainHandler        = null;
    private boolean       mIsSurfaceReady    = false;
    private int           mVideoHeight       = 0;
    private int           mVideoWidth        = 0;
    private boolean       mIsSkipHidden      = true;
    private boolean       mIsVideoMute       = false;
    private boolean       mIsPlayerReady     = false;
    private boolean       mIsCachingRequired = false;
    private boolean       mIsBufferingShown  = false;
    private int           mQuartile          = 0;
    private CampaignType  mCampaignType      = CampaignType.CPM;
    private PlayerState   mPlayerState       = PlayerState.None;
    private List<Integer> mProgressTracker   = null;

    //=======================================================
    // State machine
    //=======================================================

    private boolean canSetState(PlayerState playerState) {

        boolean result = false;

        switch (playerState) {

            case None:
                result = true;
                break;
            case Loader:
                result = (mPlayerState == PlayerState.None || mPlayerState == PlayerState.Banner);
                break;
            case Ready:
                result = (mPlayerState == PlayerState.Loader || mPlayerState == PlayerState.Banner);
                break;
            case Player:
                result = (mPlayerState == PlayerState.Ready);
                break;
            case Banner:
                result = (mPlayerState == PlayerState.Player);
                break;
        }

        return result;
    }

    /**
     * this method controls the associated state machine of the video player behaviour
     *
     * @param playerState state to set
     */
    private void setState(PlayerState playerState) {

        Log.v(TAG, "setState: " + playerState.name());

        if (canSetState(playerState)) {

            switch (playerState) {

                case None:
                    setNoneState();
                    break;
                case Loader:
                    setLoaderState();
                    break;
                case Ready:
                    setReadyState();
                    break;
                case Player:
                    setPlayerState();
                    break;
                case Banner:
                    setBannerState();
                    break;
            }

            mPlayerState = playerState;
        }
    }

    private void setNoneState() {

        Log.v(TAG, "setNoneState");

        mPlayerBanner.setVisibility(GONE);
        mPlayerView.setVisibility(GONE);
        mPlayerLoader.setVisibility(GONE);

        /**
         * Do not change this order, since cleaning the media player before invalidating timers
         * could make the timers threads access an invalid media player
         */
        stopTimers();
        cleanMediaPlayer();
    }

    private void setLoaderState() {

        Log.v(TAG, "setLoaderState");

        mPlayerBanner.setVisibility(GONE);
        mPlayerView.setVisibility(VISIBLE);
        mPlayerLoader.setVisibility(VISIBLE);

        if (mIsSurfaceReady) {

            startCaching();

        } else {

            mIsCachingRequired = true;
            // Do nothing since surfaceCreated method will start caching automatically
        }
    }

    private void setReadyState() {

        Log.v(TAG, "setReadyState");
    }

    private void setPlayerState() {

        Log.v(TAG, "setPlayerState");

        /**
         * Don't change the order of this, since starting the media player after te timers could
         * lead to an invalid mediaplayer required inside the timers.
         */
        calculateAspectRatio();
        mMediaPlayer.start();
        startTimers();

        mPlayerBanner.setVisibility(GONE);
        mPlayerView.setVisibility(VISIBLE);
        mPlayerLoader.setVisibility(GONE);
    }

    private void setBannerState() {

        Log.v(TAG, "setBannerState");

        mPlayerBanner.setVisibility(VISIBLE);
        mPlayerView.setVisibility(GONE);
        mPlayerLoader.setVisibility(GONE);

        stopTimers();
    }

    //=======================================================
    // Public
    //=======================================================

    /**
     * Constructor, generally used automatically by a layout inflater
     *
     * @param context
     * @param attrs
     */
    public VASTPlayer(Context context, AttributeSet attrs) {

        super(context, attrs);

        createLayout();

        mainHandler = new Handler(getContext().getMainLooper());
    }

    /**
     * Sets the campaign type of the player so it will affect
     * the next load of the player. (whichs sets up the layout for the loaded model).
     *
     * @param campaignType
     */
    public void setCampaignType(CampaignType campaignType) {

        Log.v(TAG, "setCampaignType");
        mCampaignType = campaignType;
    }

    /**
     * Sets listener for callbacks related to status of player
     *
     * @param listener Listener
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Sets skip string to be shown in the skip button
     *
     * @param skipName
     */
    public void setSkipName(String skipName) {

        Log.v(TAG, "setSkipName");
        mSkipName = skipName;
    }

    /**
     * Sets the amount of time that has to be played to be able to skip the video
     *
     * @param skipTime
     */
    public void setSkipTime(int skipTime) {

        Log.v(TAG, "setSkipTime");
        mSkipTime = skipTime;
    }

    /**
     * Starts loading a video VASTModel in the player, it will notify when it's ready with
     * CachingListener.onVASTPlayerCachingFinish(), so you can start video reproduction.
     *
     * @param model model containing the parsed VAST XML
     */
    public void load(VASTModel model) {

        VASTLog.v(TAG, "load");

        setState(PlayerState.None);

        mVastModel = model;
        mTrackingEventMap = mVastModel.getTrackingUrls();
        // This createMediaPlayer is required for reuse of the VASTPlayer when
        // still having a loaded video
        createMediaPlayer();

        setState(PlayerState.Loader);
    }

    /**
     * Starts video playback
     */
    public void play() {

        VASTLog.v(TAG, "play");

        setState(PlayerState.Player);
    }

    /**
     * Stops video playback
     */
    public void stop() {

        VASTLog.v(TAG, "stop");

        setState(PlayerState.None);
    }

    public void clean() {

        VASTLog.v(TAG, "clean");

        setState(PlayerState.None);
    }

    //=======================================================
    // Private
    //=======================================================

    // User Interaction
    //-------------------------------------------------------

    public void onMuteClick(View v) {

        VASTLog.v(TAG, "onMuteClick");

        ImageView muteView = (ImageView) v;

        if (mMediaPlayer != null) {

            if (mIsVideoMute) {

                mMediaPlayer.setVolume(1.0f, 1.0f);
                muteView.setImageResource(R.drawable.pubnative_btn_mute);
                processEvent(TRACKING_EVENTS_TYPE.unmute);

            } else {

                mMediaPlayer.setVolume(0.0f, 0.0f);
                muteView.setImageResource(R.drawable.pubnative_btn_unmute);
                processEvent(TRACKING_EVENTS_TYPE.mute);
            }

            mIsVideoMute = !mIsVideoMute;
        }
    }

    public void onSkipClick(View v) {

        VASTLog.v(TAG, "onSkipClick");

        processEvent(TRACKING_EVENTS_TYPE.close);

        mMediaPlayer.stop();
        setState(PlayerState.Banner);
    }

    public void onPlayerLearnMoreClick(View v) {

        VASTLog.v(TAG, "onPlayerLearnMoreClick");

        openOffer();
        mMediaPlayer.stop();
        setState(PlayerState.Banner);
    }

    public void onBannerClick(View v) {

        VASTLog.v(TAG, "onBannerClick");

        if (mIsPlayerReady) {

            setState(PlayerState.Ready);
            play();

        } else {

            setState(PlayerState.Loader);
        }
    }

    public void onPlayerClick(View v) {

        VASTLog.v(TAG, "onPlayerClick");

        openOffer();
        mMediaPlayer.stop();
        setState(PlayerState.Banner);
    }

    private void openOffer() {

        VASTLog.v(TAG, "openOffer");

        String clickThroughUrl = mVastModel.getVideoClicks().getClickThrough();
        VASTLog.d(TAG, "openOffer - clickThrough url: " + clickThroughUrl);

        // Before we send the app to the click through url, we will process ClickTracking URL's.
        List<String> urls = mVastModel.getVideoClicks().getClickTracking();
        fireUrls(urls);

        // Navigate to the click through url
        try {

            Uri uri = Uri.parse(clickThroughUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            ResolveInfo resolvable = getContext().getPackageManager().resolveActivity(intent, PackageManager.GET_INTENT_FILTERS);

            if (resolvable == null) {

                VASTLog.e(TAG, "openOffer -clickthrough error occured, uri unresolvable");
                return;

            } else {

                getContext().startActivity(intent);
                invokeOnPlayerClick();
            }

        } catch (NullPointerException e) {

            VASTLog.e(TAG, e.getMessage(), e);
        }
    }

    // Layout
    //-------------------------------------------------------

    private void createLayout() {

        VASTLog.v(TAG, "createLayout");

        mPlayerRoot = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.pubnative_player_layout, null);

        mPlayerView = (RelativeLayout) mPlayerRoot.findViewById(R.id.player_view);

        mPlayerViewContainer = (RelativeLayout) mPlayerView.findViewById(R.id.player_view_container);
        mPlayerViewContainerSurface = (SurfaceView) mPlayerViewContainer.findViewById(R.id.player_view_container_surface);
        mSurfaceHolder = mPlayerViewContainerSurface.getHolder();
        mSurfaceHolder.addCallback(this);

        mPlayerViewLoader = (RelativeLayout) mPlayerView.findViewById(R.id.player_view_buffering);

        mPlayerViewLayout = (RelativeLayout) mPlayerView.findViewById(R.id.player_view_layout);
        mPlayerViewLayoutCountDown = (CountDownView) mPlayerViewLayout.findViewById(R.id.player_view_layout_count_down);
        mPlayerViewLayoutSkip = (TextView) mPlayerViewLayout.findViewById(R.id.player_view_layout_skip);
        mPlayerViewLayoutMute = (ImageView) mPlayerViewLayout.findViewById(R.id.player_view_layout_mute);
        mPlayerViewLayoutLearnMore = (ImageView) mPlayerViewLayout.findViewById(R.id.player_view_layout_learn_more);

        mPlayerLoader = (RelativeLayout) mPlayerRoot.findViewById(R.id.player_loader);

        mPlayerBanner = (RelativeLayout) mPlayerRoot.findViewById(R.id.player_banner);
        mPlayerBannerImage = (ImageView) mPlayerBanner.findViewById(R.id.player_banner_image);
        mPlayerBannerImage.setOnClickListener(this);

        // Set campaign type behaviour
        if (CampaignType.CPC == mCampaignType) {

            mPlayerView.setOnClickListener(this);
            mPlayerViewLayoutLearnMore.setVisibility(GONE);

        } else {

            mPlayerViewLayoutSkip.setText(mSkipName);
            mPlayerViewLayoutSkip.setOnClickListener(this);
            mPlayerViewLayoutLearnMore.setOnClickListener(this);
        }

        mPlayerViewLayoutMute.setOnClickListener(this);

        addView(mPlayerRoot);

        mPlayerBanner.setVisibility(GONE);
        mPlayerView.setVisibility(GONE);
        mPlayerLoader.setVisibility(GONE);
    }

    private void startCaching() {

        VASTLog.v(TAG, "startCaching");

        try {

            String videoURL = mVastModel.getPickedMediaFileURL();
            mMediaPlayer.setDataSource(videoURL);
            mMediaPlayer.prepareAsync();

        } catch (Exception exception) {

            invokeOnFail(exception);
            setState(PlayerState.None);
        }
    }

    // Media player
    //-------------------------------------------------------

    private void createMediaPlayer() {

        VASTLog.v(TAG, "createMediaPlayer");

        if (mMediaPlayer == null) {

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    private void cleanMediaPlayer() {

        VASTLog.v(TAG, "cleanUpMediaPlayer");

        if (mMediaPlayer != null) {

            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnVideoSizeChangedListener(null);
            mMediaPlayer.release();
            mMediaPlayer = null;

            mIsPlayerReady = false;
        }
    }

    protected void calculateAspectRatio() {

        VASTLog.v(TAG, "calculateAspectRatio");

        if (mVideoWidth == 0 || mVideoHeight == 0) {

            VASTLog.w(TAG, "calculateAspectRatio - video source width or height is 0, skipping...");
            return;
        }

        double widthRatio  = 1.0 * getWidth() / mVideoWidth;
        double heightRatio = 1.0 * getHeight() / mVideoHeight;

        double scale = Math.min(widthRatio, heightRatio);

        int surfaceWidth  = (int) (scale * mVideoWidth);
        int surfaceHeight = (int) (scale * mVideoHeight);

        VASTLog.i(TAG, " view size:     " + getWidth() + "x" + getHeight());
        VASTLog.i(TAG, " video size:    " + mVideoWidth + "x" + mVideoHeight);
        VASTLog.i(TAG, " surface size:  " + surfaceWidth + "x" + surfaceHeight);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(surfaceWidth, surfaceHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerViewContainerSurface.setLayoutParams(params);

        mSurfaceHolder.setFixedSize(surfaceWidth, surfaceHeight);
    }

    // Event processing
    //-------------------------------------------------------
    private void processEvent(TRACKING_EVENTS_TYPE eventName) {

        VASTLog.v(TAG, "processEvent: " + eventName);

        if (mTrackingEventMap != null) {

            List<String> urls = mTrackingEventMap.get(eventName);
            fireUrls(urls);
        }
    }

    private void processImpressions() {

        VASTLog.v(TAG, "processImpressions");

        List<String> impressions = mVastModel.getImpressions();
        fireUrls(impressions);
    }

    private void processErrorEvent() {

        VASTLog.v(TAG, "processErrorEvent");

        List<String> errorUrls = mVastModel.getErrorUrl();
        fireUrls(errorUrls);
    }

    private void fireUrls(List<String> urls) {

        VASTLog.v(TAG, "fireUrls");

        if (urls != null) {

            for (String url : urls) {

                VASTLog.v(TAG, "\tfiring url:" + url);
                HttpTools.httpGetURL(url);
            }

        } else {

            VASTLog.d(TAG, "\turl list is null");
        }
    }

    //=======================================================
    // Timers
    //=======================================================

    private void stopTimers() {

        VASTLog.v(TAG, "stopTimers");

        stopQuartileTimer();
        stopLayoutTimer();
        stopVideoProgressTimer();

        mainHandler.removeCallbacksAndMessages(null);
    }

    private void startTimers() {

        VASTLog.v(TAG, "startTimers");

        // Stop previous timers so they don't remain hold
        stopTimers();

        // start timers
        startQuartileTimer();
        startLayoutTimer();
        startVideoProgressTimer();
    }

    // Progress timer
    //-------------------------------------------------------
    private void startVideoProgressTimer() {

        VASTLog.d(TAG, "startVideoProgressTimer");

        mProgressTimer = new Timer();
        mProgressTracker = new ArrayList<Integer>();
        mProgressTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                if (mProgressTracker.size() > MAX_PROGRESS_TRACKING_POINTS) {

                    int firstPosition = mProgressTracker.get(0);
                    int lastPosition = mProgressTracker.get(mProgressTracker.size() - 1);

                    if (lastPosition > firstPosition) {

                        if(mIsBufferingShown) {

                            mIsBufferingShown = false;
                            mainHandler.post(new Runnable() {

                                @Override
                                public void run() {

                                    mPlayerViewLoader.setVisibility(GONE);
                                }
                            });
                        }

                    } else {

                        if(!mIsBufferingShown) {

                            mIsBufferingShown = true;
                            mainHandler.post(new Runnable() {

                                @Override
                                public void run() {

                                    mPlayerViewLoader.setVisibility(VISIBLE);
                                }
                            });
                        }
                    }

                    mProgressTracker.remove(0);
                }

                mProgressTracker.add(mMediaPlayer.getCurrentPosition());
            }

        }, 0, TIMER_PROGRESS_INTERVAL);
    }

    private void stopVideoProgressTimer() {

        VASTLog.d(TAG, "stopVideoProgressTimer");

        if (mProgressTimer != null) {

            mProgressTimer.cancel();
            mProgressTimer = null;
        }
    }

    // Quartile timer
    //-------------------------------------------------------
    private void startQuartileTimer() {

        VASTLog.v(TAG, "startQuartileTimer");

        mQuartile = 0;

        mTrackingEventsTimer = new Timer();
        mTrackingEventsTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                int percentage = 0;
                try {

                    // wait for the video to really start
                    if (mMediaPlayer.getCurrentPosition() == 0) {

                        return;
                    }

                    percentage = 100 * mMediaPlayer.getCurrentPosition() / mMediaPlayer.getDuration();

                } catch (Exception e) {

                    VASTLog.e(TAG, "QuartileTimer error: " + e.getMessage());
                    cancel();

                    return;
                }

                if (percentage >= 25 * mQuartile) {

                    if (mQuartile == 0) {

                        VASTLog.i(TAG, "Video at start: (" + percentage + "%)");
                        processImpressions();
                        processEvent(TRACKING_EVENTS_TYPE.start);
                        invokeOnPlayerPlaybackStart();

                    } else if (mQuartile == 1) {

                        VASTLog.i(TAG, "Video at first quartile: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.firstQuartile);

                    } else if (mQuartile == 2) {

                        VASTLog.i(TAG, "Video at midpoint: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.midpoint);

                    } else if (mQuartile == 3) {

                        VASTLog.i(TAG, "Video at third quartile: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.thirdQuartile);
                        stopQuartileTimer();
                    }

                    mQuartile++;
                }
            }

        }, 0, TIMER_TRACKING_INTERVAL);
    }

    private void stopQuartileTimer() {

        VASTLog.v(TAG, "stopQuartileTimer");

        if (mTrackingEventsTimer != null) {

            mTrackingEventsTimer.cancel();
            mTrackingEventsTimer = null;
        }
    }

    // Layout timer
    //-------------------------------------------------------
    private void startLayoutTimer() {

        VASTLog.v(TAG, "startLayoutTimer");

        mLayoutTimer = new Timer();
        mLayoutTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                if (mMediaPlayer == null) {

                    cancel();
                    return;
                }

                // Execute with handler to be sure we execute this on the UIThread
                mainHandler.post(new Runnable() {

                    @Override
                    public void run() {

                        try {

                            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {

                                int currentPosition = mMediaPlayer.getCurrentPosition();
                                mPlayerViewLayoutCountDown.setProgress(currentPosition, mMediaPlayer.getDuration());

                                if (mSkipTime >= 0 &&
                                    mSkipTime * 1000 < currentPosition &&
                                    mIsSkipHidden) {

                                    mIsSkipHidden = false;
                                    mPlayerViewLayoutSkip.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (Exception e) {

                            Log.e(TAG, "Layout timer error: " + e);

                            cancel();
                            return;
                        }
                    }
                });
            }
        }, 0, TIMER_LAYOUT_INTERVAL);
    }

    private void stopLayoutTimer() {

        VASTLog.d(TAG, "stopLayoutTimer");

        if (mLayoutTimer != null) {

            mLayoutTimer.cancel();
            mLayoutTimer = null;
        }
    }

    // Listener helpers
    //-------------------------------------------------------

    private void invokeOnPlayerClick() {

        VASTLog.v(TAG, "invokeOnPlayerClick");

        if (mListener != null) {

            mListener.onVASTPlayerClick();
        }
    }

    private void invokeOnPlayerLoadFinish() {

        VASTLog.v(TAG, "invokeOnPlayerLoadFinish");

        if (mListener != null) {

            mListener.onVASTPlayerLoadFinish();
        }
    }

    private void invokeOnFail(Exception exception) {

        VASTLog.v(TAG, "invokeOnFail");

        if (mListener != null) {

            mListener.onVASTPlayerFail(exception);
        }
    }

    private void invokeOnPlayerPlaybackStart() {

        VASTLog.v(TAG, "invokeOnPlayerPlaybackStart");

        if (mListener != null) {

            mListener.onVASTPlayerPlaybackStart();
        }
    }

    private void invokeOnPlayerPlaybackFinish() {

        VASTLog.v(TAG, "invokeOnPlayerPlaybackFinish");

        if (mListener != null) {

            mListener.onVASTPlayerPlaybackFinish();
        }
    }

    //=============================================
    // CALLBACKS
    //=============================================

    // MediaPlayer.OnCompletionListener
    //---------------------------------------------
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        VASTLog.v(TAG, "onCompletion -- (MediaPlayer callback)");

        if (mQuartile > 3) {

            processEvent(TRACKING_EVENTS_TYPE.complete);
            invokeOnPlayerPlaybackFinish();
        }

        setState(PlayerState.Banner);
    }

    // MediaPlayer.OnErrorListener
    //---------------------------------------------
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        VASTLog.v(TAG, "onError -- (MediaPlayer callback)");

        processErrorEvent();

        String exceptionMessage;
        switch (what) {

            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                exceptionMessage = "not valid for progressive playback: ";
                break;

            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                exceptionMessage = "server died: ";
                break;

            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
            default:
                exceptionMessage = "unknown: ";
        }

        exceptionMessage += extra;

        invokeOnFail(new Exception("VASTPlayer error: " + exceptionMessage));
        setState(PlayerState.Banner);

        return true;
    }

    // MediaPlayer.OnPreparedListener
    //---------------------------------------------
    @Override
    public void onPrepared(MediaPlayer mp) {

        VASTLog.v(TAG, "onPrepared --(MediaPlayer callback) ....about to play");

        final String bannerURLString = mVastModel.getExtensionURL(VASTModel.EXTENSION_POSTVIEW_BANNER);

        new BitmapDownloaderTask().setListener(new BitmapDownloaderTask.Listener() {

            @Override
            public void onBitmapDownloaderFinished(Bitmap bitmap) {

                if (bitmap != null) {

                    mPlayerBannerImage.setImageBitmap(bitmap);
                    mIsPlayerReady = true;
                    setState(PlayerState.Ready);
                    invokeOnPlayerLoadFinish();

                } else {

                    invokeOnFail(new Exception("VASTPlayer error: Background banner couldn't be downloaded: " + bannerURLString));
                    setState(PlayerState.None);
                }

            }
        }).execute(bannerURLString);
    }

    // MediaPlayer.OnVideoSizeChangedListener
    //---------------------------------------------
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        VASTLog.v(TAG, "onVideoSizeChanged -- " + width + " x " + height);

        mVideoWidth = width;
        mVideoHeight = height;
    }

    // View.OnClickListener
    //---------------------------------------------
    public void onClick(View view) {

        VASTLog.v(TAG, "onClick -- (View.OnClickListener callback)");

        if (mPlayerViewContainer == view) {

            onPlayerClick(view);

        } else if (mPlayerViewLayoutLearnMore == view) {

            onPlayerLearnMoreClick(view);

        } else if (mPlayerViewLayoutSkip == view) {

            onSkipClick(view);

        } else if (mPlayerViewLayoutMute == view) {

            onMuteClick(view);

        } else if (mPlayerBannerImage == view) {

            onBannerClick(view);
        }
    }

    // SurfaceHolder.Callback
    //---------------------------------------------

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        VASTLog.v(TAG, "surfaceCreated");

        mIsSurfaceReady = true;

        createMediaPlayer();
        mMediaPlayer.setDisplay(holder);

        if (mIsCachingRequired) {

            mIsCachingRequired = false;
            startCaching();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        VASTLog.v(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        VASTLog.v(TAG, "surfaceDestroyed");

        mIsSurfaceReady = false;

        stopTimers();
        cleanMediaPlayer();
    }
}
