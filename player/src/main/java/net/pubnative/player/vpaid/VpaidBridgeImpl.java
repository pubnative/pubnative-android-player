package net.pubnative.player.vpaid;

import android.util.Log;
import android.webkit.JavascriptInterface;

import net.pubnative.player.model.vpaid.CreativeParams;

public class VpaidBridgeImpl implements VpaidBridge {

    private static final String LOG_TAG = VpaidBridgeImpl.class.getSimpleName();

    private final BridgeEventHandler mBridge;
    private final CreativeParams mCreativeParams;

    public VpaidBridgeImpl(BridgeEventHandler eventHandler, CreativeParams creativeParams) {
        mBridge = eventHandler;
        mCreativeParams = creativeParams;
    }

    //region VpaidBridge methods
    @Override
    public void prepare() {
        Log.v(LOG_TAG, "call initVpaidWrapper()");
        callJsMethod("initVpaidWrapper()");
    }

    @Override
    public void startAd() {
        Log.v(LOG_TAG, "call startAd()");
        callWrapper("startAd()");
    }

    @Override
    public void stopAd() {
        Log.v(LOG_TAG, "call stopAd()");
        callWrapper("stopAd()");
    }

    @Override
    public void pauseAd() {
        Log.v(LOG_TAG, "call pauseAd()");
        callWrapper("pauseAd()");
    }

    @Override
    public void resumeAd() {
        Log.v(LOG_TAG, "call resumeAd()");
        callWrapper("resumeAd()");
    }

    @Override
    public void getAdSkippableState() {
        Log.v(LOG_TAG, "call getAdSkippableState()");
        callWrapper("getAdSkippableState()");
    }
    //endregion

    //region Helpers
    private void runOnUiThread(Runnable runnable) {
        mBridge.runOnUiThread(runnable);
    }

    private void callJsMethod(final String url) {
        mBridge.callJsMethod(url);
    }

    private void callWrapper(String method) {
        callJsMethod("VPAIDWrapperInstance." + method);
    }
    //endregion

    //region JsCallbacks
    @JavascriptInterface
    public void wrapperReady() {
        initAd();
    }

    private void initAd() {
        Log.v(LOG_TAG, "JS: call initAd()");
        String requestTemplate = "initAd(" +
                "%1$d," + // width
                "%2$d," + // height
                "%3$s," + // viewMode
                "%4$s," + // desiredBitrate
                "%5$s," + // creativeData
                "%6$s)"; // environmentVars
        String requestFinal = String.format(requestTemplate,
                mCreativeParams.getWidth(),
                mCreativeParams.getHeight(),
                mCreativeParams.getViewMode(),
                mCreativeParams.getDesiredBitrate(),
                mCreativeParams.getCreativeData(),
                mCreativeParams.getEnvironmentVars()
        );
        callWrapper(requestFinal);
    }

    @JavascriptInterface
    public String handshakeVersionResult(String result) {
        Log.v(LOG_TAG, "JS: handshakeVersion()");
        return result;
    }

    @JavascriptInterface
    public void vpaidAdLoaded() {
        Log.v(LOG_TAG, "JS: vpaidAdLoaded");
        mBridge.onPrepared();
    }

    @JavascriptInterface
    public void vpaidAdStarted() {
        Log.v(LOG_TAG, "JS: vpaidAdStarted");
    }

    @JavascriptInterface
    public void initAdResult() {
        Log.v(LOG_TAG, "JS: Init ad done");
    }

    @JavascriptInterface
    public void vpaidAdError(String message) {
        Log.v(LOG_TAG, "JS: vpaidAdError" + message);
        mBridge.trackError(message);
    }

    @JavascriptInterface
    public void vpaidAdLog(String message) {
        Log.v(LOG_TAG, "JS: vpaidAdLog " + message);
    }

    @JavascriptInterface
    public void vpaidAdUserAcceptInvitation() {
        Log.v(LOG_TAG, "JS: vpaidAdUserAcceptInvitation");
    }

    @JavascriptInterface
    public void vpaidAdUserMinimize() {
        Log.v(LOG_TAG, "JS: vpaidAdUserMinimize");
    }

    @JavascriptInterface
    public void vpaidAdUserClose() {
        Log.v(LOG_TAG, "JS: vpaidAdUserClose");
    }

    @JavascriptInterface
    public void vpaidAdSkippableStateChange() {
        Log.v(LOG_TAG, "JS: vpaidAdSkippableStateChange");
    }

    @JavascriptInterface
    public void vpaidAdExpandedChange() {
        Log.v(LOG_TAG, "JS: vpaidAdExpandedChange");
    }

    @JavascriptInterface
    public void getAdExpandedResult(String result) {
        Log.v(LOG_TAG, "JS: getAdExpandedResult");
    }

    @JavascriptInterface
    public void vpaidAdSizeChange() {
        Log.v(LOG_TAG, "JS: vpaidAdSizeChange");
    }

    @JavascriptInterface
    public void vpaidAdDurationChange() {
        Log.v(LOG_TAG, "JS: vpaidAdDurationChange");
        callWrapper("getAdDurationResult");
        mBridge.onDurationChanged();
    }

    @JavascriptInterface
    public void vpaidAdRemainingTimeChange() {
        Log.v(LOG_TAG, "JS: vpaidAdRemainingTimeChange");
        callWrapper("getAdRemainingTime()");
    }

    @JavascriptInterface
    public void vpaidAdLinearChange() {
        Log.v(LOG_TAG, "JS: vpaidAdLinearChange");
        mBridge.onAdLinearChange();
    }

    @JavascriptInterface
    public void vpaidAdPaused() {
        Log.v(LOG_TAG, "JS: vpaidAdPaused");
        mBridge.postEvent(EventConstants.PAUSE);
    }

    @JavascriptInterface
    public void vpaidAdVideoStart() {
        Log.v(LOG_TAG, "JS: vpaidAdVideoStart");
        mBridge.postEvent(EventConstants.START);
    }

    @JavascriptInterface
    public void vpaidAdPlaying() {
        Log.v(LOG_TAG, "JS: vpaidAdPlaying");
        mBridge.postEvent(EventConstants.RESUME);
    }

    @JavascriptInterface
    public void vpaidAdClickThruIdPlayerHandles(String url, String id, boolean playerHandles) {
        if (playerHandles) {
            mBridge.openUrl(url);
        }
    }

    @JavascriptInterface
    public void vpaidAdVideoFirstQuartile() {
        mBridge.postEvent(EventConstants.FIRST_QUARTILE);
    }

    @JavascriptInterface
    public void vpaidAdVideoMidpoint() {
        Log.v(LOG_TAG, "JS: vpaidAdVideoMidpoint");
        mBridge.postEvent(EventConstants.MIDPOINT);
    }

    @JavascriptInterface
    public void vpaidAdVideoThirdQuartile() {
        Log.v(LOG_TAG, "JS: vpaidAdVideoThirdQuartile");
        mBridge.postEvent(EventConstants.THIRD_QUARTILE);
    }

    @JavascriptInterface
    public void vpaidAdVideoComplete() {
        Log.v(LOG_TAG, "JS: vpaidAdVideoComplete");
    }

    @JavascriptInterface
    public void getAdSkippableStateResult(boolean value) {
        Log.v(LOG_TAG, "JS: SkippableState: " + value);
        mBridge.setSkippableState(value);
    }

    @JavascriptInterface
    public void getAdRemainingTimeResult(int value) {
        Log.v(LOG_TAG, "JS: getAdRemainingTimeResult: " + value);
        if (value == 0) {
            mBridge.postEvent(EventConstants.COMPLETE);
        } else {
            mBridge.postEvent(EventConstants.PROGRESS, value);
        }
    }

    @JavascriptInterface
    public void getAdDurationResult(int value) {
        Log.v(LOG_TAG, "JS: getAdDurationResult: " + value);
    }

    @JavascriptInterface
    public void getAdLinearResult(boolean value) {
        Log.v(LOG_TAG, "getAdLinearResult: " + value);
    }

    @JavascriptInterface
    public void vpaidAdSkipped() {
        Log.v(LOG_TAG, "JS: vpaidAdSkipped");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBridge.onAdSkipped();
            }
        });
    }

    @JavascriptInterface
    public void vpaidAdStopped() {
        Log.v(LOG_TAG, "JS: vpaidAdStopped");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBridge.onAdStopped();
            }
        });
    }

    @JavascriptInterface
    public void vpaidAdImpression() {
        Log.v(LOG_TAG, "JS: vpaidAdImpression");
        mBridge.onAdImpression();
    }

    @JavascriptInterface
    public void vpaidAdInteraction() {
        Log.v(LOG_TAG, "JS: vpaidAdInteraction");
    }

    @JavascriptInterface
    public void vpaidAdVolumeChanged() {
        Log.v(LOG_TAG, "JS: vpaidAdVolumeChanged");
        mBridge.onAdVolumeChange();
    }

    @JavascriptInterface
    public void getAdVolumeResult() {
        Log.v(LOG_TAG, "JS: getAdVolumeResult");
    }

}
