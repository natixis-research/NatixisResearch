package com.natixis.natixisresearch.app.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.MailTo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.utils.Utils;


public class BrowserActivity extends BaseActivity {

    public static final java.lang.String PARAMETER_URL = "URL";
    public static final java.lang.String PARAMETER_TITLE = "TITLE";
    WebView webview = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null)
            mActionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        String filename = "";
        String title = "";
        if (extras != null) {
            filename = extras.getString(PARAMETER_URL);
            title = extras.getString(PARAMETER_TITLE);

        }
        if(mActionBar!=null){
            mActionBar.setTitle(title);
        }

        webview = (WebView) findViewById(R.id.webview);
        final WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        try {
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            //  webSettings.setUseWideViewPort(true);
            //   webview.setInitialScale(1);
            webview.setVerticalScrollBarEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // Use the API 11+ calls to disable the controls
                // Use a seperate class to obtain 1.6 compatibility
                new Runnable() {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    public void run() {
                        webSettings.setDisplayZoomControls(false);
                    }
                }.run();
            } else {
                ZoomButtonsController zoom_controll;

                zoom_controll = (ZoomButtonsController) webview.getClass().getMethod("getZoomButtonsController").invoke(webview, null);

                if (zoom_controll != null && zoom_controll.getZoomControls() != null) {
                    // Hide the controlls AFTER they where made visible by the
                    // default implementation.
                    zoom_controll.getZoomControls().setVisibility(View.GONE);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        WebViewClient webClient = new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:")) {
                    MailTo mt = MailTo.parse(url);
                    Intent i = Utils.newEmailIntent(BrowserActivity.this, mt.getTo());
                    startActivity(i);
                    view.reload();
                    return true;
                } else if (url.startsWith("tel:")) {
                    Intent intent = Utils.newPhoneIntent(BrowserActivity.this, url);
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        };
        webview.setWebViewClient(webClient);

        webview.loadUrl(filename);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

}
