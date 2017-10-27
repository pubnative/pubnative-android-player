package net.pubnative.player.vpaid;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import net.pubnative.player.R;

public class VpaidViewController {

    private final AdController mAdController;

    private WebView mWebView;
    private View mEndCardLayout;
    private ImageView mEndCardView;

    public VpaidViewController(AdController adController) {
        mAdController = adController;
    }

    public void buildVideoAdView(FrameLayout view, WebView webView) {
        Context context = view.getContext();
        mWebView = webView;

        view.removeAllViews();
        if (mWebView.getParent() != null) {
            ((ViewGroup) mWebView.getParent()).removeAllViews();
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        mEndCardLayout = LayoutInflater.from(context).inflate(R.layout.end_card, view, false);
        mEndCardLayout.setVisibility(View.GONE);

        mEndCardView = (ImageView) mEndCardLayout.findViewById(R.id.endCardView);
        ImageView closeView = (ImageView) mEndCardLayout.findViewById(R.id.closeView);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdController.closeSelf();
            }
        });

        ImageView replayView = (ImageView) mEndCardLayout.findViewById(R.id.replayView);
        replayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showControls();
                mAdController.playAd();
            }
        });

        view.addView(mEndCardLayout, params);
        view.addView(webView, params);
        webView.setBackgroundColor(Color.TRANSPARENT);
        view.setBackgroundColor(Color.BLACK);
    }

    private void showControls() {
        mEndCardLayout.setVisibility(View.GONE);
        mWebView.setVisibility(View.VISIBLE);
    }

    public void showEndCard(String imageUri) {
        mEndCardLayout.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
    }


}
