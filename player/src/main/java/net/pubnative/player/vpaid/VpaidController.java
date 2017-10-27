package net.pubnative.player.vpaid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import net.pubnative.player.model.vpaid.CreativeParams;
import net.pubnative.player.util.PNUtils;

class VpaidController implements AdController, BridgeEventHandler {

    private static final String LOG_TAG = VpaidController.class.getSimpleName();
    private static final String BASE_URL = "http://pubnative.net";
    private static final String ENVIRONMENT_VARS = "{ " +
            "slot: document.getElementById('pubnative-slot'), " +
            "videoSlot: document.getElementById('pubnative-videoslot'), " +
            "videoSlotCanAutoPlay: true }";
    private static final String HTML_SOURCE_FILE = "pubnativeAd.html";
    private static final String VPAID_CREATIVE_URL_STRING = "[VPAID_CREATIVE_URL]";
    private static final String MIME_TYPE = "text/html";
    private static final String UTF_8 = "UTF-8";

    private final VpaidBridge mVpaidBridge;
    private final VpaidViewController mVpaidViewController;

    private OnPreparedListener mOnPreparedListener;
    private String mEndCardFilePath;
    private WebView mWebView;
    private boolean mIsWaitingForSkippableState;
    private boolean mIsWaitingForWebView;
    private boolean mIsStarted;
    private String mVastFileContent;
    private String mVpaidJsUrl;

    private Context mContext;

    VpaidController(Context context, String vpaidJsUrl, String vastFileContent) {
        mContext = context;
        mVastFileContent = vastFileContent;
        mVpaidJsUrl = vpaidJsUrl;
        mVpaidBridge = new VpaidBridgeImpl(this, createCreativeParams());
        mVpaidViewController = new VpaidViewController(this);
    }

    //region AdController methods
    @Override
    public void prepare(AdController.OnPreparedListener listener) {
        mOnPreparedListener = listener;
        try {
            initWebView();
            String html = PNUtils.readAssets(mContext.getAssets(), HTML_SOURCE_FILE);
            String finalHtml = html.replace(VPAID_CREATIVE_URL_STRING, mVpaidJsUrl);
            mWebView.loadDataWithBaseURL(BASE_URL, finalHtml, MIME_TYPE, UTF_8, null);
        } catch (Exception e) {
            Log.v(LOG_TAG, "Can't read assets: " + e.getMessage());
        }
    }

    @Override
    public void setVideoFilePath(String filePath) {
    }

    @Override
    public void setEndCardFilePath(String endCardFilePath) {
        mEndCardFilePath = endCardFilePath;
    }

    @Override
    public void buildVideoAdView(FrameLayout view) {
        mVpaidViewController.buildVideoAdView(view, mWebView);
    }

    @Override
    public void playAd() {
        mIsStarted = true;
        mVpaidBridge.startAd();
    }

    @Override
    public void pause() {
        if (mIsStarted) {
            mVpaidBridge.pauseAd();
        }
    }

    @Override
    public void resume() {
        mVpaidBridge.resumeAd();
    }

    @Override
    public void dismiss() {
        mVpaidBridge.pauseAd();
        mVpaidBridge.stopAd();
        if (mWebView != null) {
            mWebView.clearCache(true);
            mWebView.clearFormData();
            mWebView.clearView();
        }
    }

    @Override
    public void destroy() {
        if (mWebView != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWebView.getParent() != null) {
                        ((ViewGroup) mWebView.getParent()).removeAllViews();
                    }
                    mWebView.clearHistory();
                    mWebView.clearCache(true);
                    mWebView.loadUrl("about:blank");
                    mWebView.pauseTimers();
                    mWebView = null;
                }
            });
        }
    }

    @Override
    public void setVolume(boolean mute) {
    }

    @Override
    public void skipVideo() {
        mIsStarted = false;

    }
    //endregion

    //region BridgeEventHandler methods
    @Override
    public void runOnUiThread(Runnable runnable) {
        // TODO Make changes on UI
    }

    @Override
    public void callJsMethod(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWebView != null) {
                    mWebView.loadUrl("javascript:" + url);
                }
            }
        });
    }

    @Override
    public void onPrepared() {
        mOnPreparedListener.onPrepared();
    }

    @Override
    public void onAdSkipped() {
        if (!mIsStarted) {
            return;
        }
        mIsWaitingForSkippableState = true;
        mVpaidBridge.getAdSkippableState();
    }

    @Override
    public void onAdStopped() {
        if (!mIsStarted) {
            return;
        }
        postEvent(EventConstants.CLOSE);
        skipVideo();
    }

    @Override
    public void setSkippableState(boolean skippable) {
        if (!mIsStarted) {
            return;
        }
        if (mIsWaitingForSkippableState && skippable) {
            mIsWaitingForSkippableState = false;
            postEvent(EventConstants.SKIP);
            skipVideo();
        }
    }

    @Override
    public void openUrl(String url) {
        // TODO: open URL
    }

    @Override
    public void trackError(String message) {
        // TODO: Track the error
    }

    @Override
    public void postEvent(String eventType, int value) {
        // TODO: track the event with value
    }

    @Override
    public void postEvent(String eventType) {
        // TODO: track the event
    }
    //endregion

    //region other methods
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initWebView() {
        mWebView = new WebView(mContext);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mIsWaitingForWebView = true;
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (mIsWaitingForWebView) {
                    mVpaidBridge.prepare();
                    Log.v(LOG_TAG, "Init webView done");
                    mIsWaitingForWebView = false;
                }
            }
        });
        CookieManager.getInstance().setAcceptCookie(true);
        mWebView.addJavascriptInterface(mVpaidBridge, "android");
    }

    @Override
    public void closeSelf() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsWaitingForWebView = false;
                mVpaidBridge.stopAd();
            }
        });
    }

    private CreativeParams createCreativeParams() {
        // CreativeParams result = new CreativeParams(mWidth, mHeight, "normal", 720);
        // result.setAdParameters("{'AdParameters':'" + mAdParams + "'}");
        // result.setEnvironmentVars(ENVIRONMENT_VARS);
        // return result;
        return null; // TODO: return Creative params
    }
    //endregion

    @Override
    public void onDurationChanged() {

    }

    @Override
    public void onAdLinearChange() {

    }

    @Override
    public void onAdVolumeChange() {
    }

    @Override
    public void onAdImpression() {
    }

}
