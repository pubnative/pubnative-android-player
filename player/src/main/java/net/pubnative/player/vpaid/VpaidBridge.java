package net.pubnative.player.vpaid;

public interface VpaidBridge {

    void prepare();

    void startAd();

    void stopAd();

    void pauseAd();

    void resumeAd();

    void getAdSkippableState();

}
