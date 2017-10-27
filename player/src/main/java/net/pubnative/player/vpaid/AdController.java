package net.pubnative.player.vpaid;

import android.widget.FrameLayout;

public interface AdController {

    interface OnPreparedListener {
        void onPrepared();
    }

    void prepare(OnPreparedListener listener);

    void setVideoFilePath(String filePath);

    void setEndCardFilePath(String filePath);

    void buildVideoAdView(FrameLayout view);

    void openUrl(String url);

    void setVolume(boolean mute);

    void skipVideo();

    void closeSelf();

    void playAd();

    void pause();

    void resume();

    void dismiss();

    void destroy();

}
