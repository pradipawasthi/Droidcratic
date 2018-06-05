package org.socratic.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import org.socratic.android.R;
import org.socratic.android.contract.ViewAdapter;
import org.socratic.android.databinding.ActivityInAppMessageWebViewBinding;
import org.socratic.android.viewmodel.NoOpViewModel;

import javax.inject.Inject;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class InAppWebViewActivity extends BaseActivity<ActivityInAppMessageWebViewBinding, NoOpViewModel>
        implements ViewAdapter {
    private static final String TAG = InAppWebViewActivity.class.getSimpleName();

    WebView webView;

    @Inject SharedPreferences sharedPreferences;

    String callerName;
    Class callerClass;
    String defaultUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(savedInstanceState, R.layout.activity_in_app_message_web_view);

        webView = binding.webView;

        overridePendingTransition(R.anim.in_app_message_web_view_slide_up, 0);

        defaultUrl = getString(R.string.default_url);

        boolean isFromInAppMessage = getIntent().getBooleanExtra("from_in_app_message", false);

        if (isFromInAppMessage) {
            setupInAppMessageWebView();
        } else {
            setupRegularWebView(getIntent());
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        String caller = "org.socratic.android.activities.";
        callerName = getIntent().getStringExtra("caller");
        caller += callerName;

        try {
            callerClass = Class.forName(caller);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (callerClass != null) {
            setOnClick(binding.inAppMessageWebViewCloseButton, callerClass);
        } else {
            Log.d(TAG, "Caller class is null");
        }

    }

    private void setupRegularWebView(Intent intent) {
        String url = intent.getStringExtra("url");

        if (url != null && !url.isEmpty()) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl(defaultUrl);
        }
    }

    private void setupInAppMessageWebView() {
        String messageUrl = sharedPreferences.getString("message_url", "");

        if (messageUrl != "") {
            webView.loadUrl(messageUrl);
        } else {
            webView.loadUrl(defaultUrl);
        }
    }

    private void setOnClick(final ImageButton btn, final Class previousActivityClass){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext(), previousActivityClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });
    }
}
