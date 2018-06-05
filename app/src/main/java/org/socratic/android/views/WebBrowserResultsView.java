package org.socratic.android.views;

import android.content.Context;
import android.graphics.Color;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.socratic.android.R;
import org.socratic.android.SocraticApp;
import org.socratic.android.analytics.ResultsAnalytics;
import org.socratic.android.api.request.BasicGetRequest;
import org.socratic.android.api.model.HeaderCard;
import org.socratic.android.api.model.HeaderBarBaseCard;
import org.socratic.android.api.model.ScriptResponseCard;
import org.socratic.android.api.model.Pair;
import org.socratic.android.api.response.NoSerializeResponse;
import org.socratic.android.databinding.ViewResultsWebBrowserBinding;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.globals.HttpManager;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pcnofelt on 2/8/17.
 */

public class WebBrowserResultsView extends LinearLayout implements WebBrowserCoreView.ContentLoadReporter {

    @Inject HttpManager httpManager;
    @Inject AnalyticsManager analyticsManager;

    private static final String TAG = WebBrowserResultsView.class.getSimpleName();

    ViewGroup vgNavControls;
    ImageView ivNavBack;
    ImageView ivNavForward;
    TextView tvNavUrl;
    ViewGroup vgOverlay;
    WebBrowserCoreView vWebBrowser;
    MaterialProgressBar progressLoading;

    ViewResultsWebBrowserBinding binding;
    private HeaderCard headerCard;
    private String previousUrl;

    private String originalURL = "";
    private String displayURL;
    private boolean pageLoadComplete = false;
    private final List<Pair<String, String>> scriptInvocationPair = new ArrayList<>();
    private HashSet<String> scriptDownloadRequestTags = new HashSet<>();
    private int scriptDownloadRequestTagCounter;


    public WebBrowserResultsView(Context context) {
        super(context);
        SocraticApp.getViewComponent(this, context).inject(this);
        initView(context);
    }

    public WebBrowserResultsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SocraticApp.getViewComponent(this, context).inject(this);
        initView(context);
    }

    public WebBrowserResultsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        SocraticApp.getViewComponent(this, context).inject(this);
        initView(context);
    }

    public void setupRequest(String url, String displayUrl, HeaderCard headerCard, String colorHex, List<ScriptResponseCard> scriptCards) {
        originalURL = url;
        displayURL = displayUrl;
        vWebBrowser.loadWithUrl(url);
        String bodyColorHex = colorHex;
        String webBrowserColorHex = Integer.toHexString(this.getResources().getColor(R.color.white));
        this.headerCard = headerCard;
        List<ScriptResponseCard> scripts = scriptCards;

        if (!TextUtils.isEmpty(bodyColorHex)) {
            vgOverlay.setBackgroundColor(Color.parseColor("#" + bodyColorHex));
            vWebBrowser.setBackgroundColor(Color.parseColor("#" + webBrowserColorHex));
            this.setBackgroundColor(Color.parseColor("#" + bodyColorHex));
        }

        setupHeaderInfo();

        if (scripts != null) {
            for (int i = 0; i < scripts.size(); i++) {
                downloadAndInvokeScript(scripts.get(i));
            }
        }

    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = ViewResultsWebBrowserBinding.inflate(inflater, this, true);
        init();
    }

    private void init() {

        vgNavControls = binding.layoutWebControls;
        ivNavBack = binding.ivUrlBack;
        ivNavForward = binding.ivUrlFwd;
        tvNavUrl = binding.tvUrlText;
        vgOverlay = binding.layoutOverlay;
        vWebBrowser = binding.webCoreResults;
        progressLoading = binding.progressUrlLoad;

        vWebBrowser.setContentLoadReporter(this);

        ivNavBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vWebBrowser.canGoBack()) {
                    vWebBrowser.goBack();
                }
            }
        });

        ivNavForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vWebBrowser.canGoForward()) {
                    vWebBrowser.goForward();
                }
            }
        });

    }

    private void downloadAndInvokeScript(final ScriptResponseCard script) {

        String requestTag = toString() + scriptDownloadRequestTagCounter;
        scriptDownloadRequestTagCounter++;
        scriptDownloadRequestTags.add(requestTag);

        BasicGetRequest request = new BasicGetRequest(script.getUrl());
        request.setTag(requestTag);
        httpManager.request(request,
                new HttpManager.HttpCallback<NoSerializeResponse>(NoSerializeResponse.class) {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCancel(Request request) {
                        // Do nothing.
                    }

                    @Override
                    public void onFailure(Request request, NoSerializeResponse responseParsed,
                                          int statusCode, Exception e) {
                    }

                    @Override
                    public void onSuccess(Response response, NoSerializeResponse responseParsed) {
                        // Do nothing, use onSuccess(Response response, String responseBodyAsString, BaseResponse responseParsed)
                    }

                    @Override
                    public void onSuccess(Response response, String responseBodyAsString, NoSerializeResponse responseParsed) {
                        scriptInvocationPair.add(new Pair<>(responseBodyAsString, script.getInvocation()));
                        attemptScriptInjection();
                    }

                    @Override
                    public void onFinished() {
                    }
                });
    }

    private void injectJS(String script) {
        try {
            vWebBrowser.loadWithUrl("javascript:" + script);
        } catch (Exception e) {
            Log.e(TAG, "exception - js inject", e);
        }
    }

    private void setupHeaderInfo() {
        if (headerCard == null) {
            return;
        }

        if (headerCard.getBgColor() != null) {
            this.setBackgroundColor(Color.parseColor("#" + headerCard.getBgColor()));
            // TODO set text_color
        }

        HeaderBarBaseCard headerBar = null;
        if (headerCard.getNavigationBar() != null) {
            headerBar = headerCard.getNavigationBar();
        } else if (headerCard.getUrlBar() != null) {
            headerBar = headerCard.getUrlBar();
        }

        if (headerBar != null) {
            vgNavControls.setBackgroundColor(Color.parseColor("#" + headerBar.getBgColor()));
            tvNavUrl.setBackgroundColor(Color.parseColor("#" + headerBar.getBgColor()));
            tvNavUrl.setTextColor(Color.parseColor("#" + headerBar.getTextColor()));
            tvNavUrl.setText(headerBar.getText());
        }

        if (HeaderCard.TYPE_TITLE_WITH_URL.equals(headerCard.getType())) {

            binding.tvTitleText.setText(headerCard.getText());
            binding.tvTitleText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.banana_white_outline, 0, 0, 0);
            binding.layoutTitle.setVisibility(VISIBLE);
            vgNavControls.setVisibility(VISIBLE);
        } else if (HeaderCard.TYPE_NAVIGATION_ONLY.equals(headerCard.getType())) {
            vgNavControls.setVisibility(GONE);
        }
        // if type is not set OR default is title
        else {
            vgNavControls.setVisibility(GONE);
        }
    }

    private void updateNavButtonStates(String url) {

        // Return if headerCard info is null (will then never show) or
        // string url is null (can happen during initial load).
        if (headerCard == null || TextUtils.isEmpty(url)) {
            return;
        }

        // Only show nav controls when 1) is type TYPE_NAVIGATION_ONLY and
        // 2) the user is loading a second page.
        if (vgNavControls.getVisibility() != VISIBLE
                && HeaderCard.TYPE_NAVIGATION_ONLY.equals(headerCard.getType())
                && !TextUtils.isEmpty(previousUrl)
                && (vWebBrowser.canGoBack() || !url.equals(previousUrl))) {
            vgNavControls.setVisibility(VISIBLE);
        }

        if (HeaderCard.TYPE_TITLE_WITH_URL.equals(headerCard.getType())) {
            ivNavBack.setVisibility(GONE);
            ivNavForward.setVisibility(GONE);
        }

        // Always set the state of the nav buttons.
        ivNavForward.setEnabled(vWebBrowser.canGoForward());
        ivNavBack.setEnabled(vWebBrowser.canGoBack());
    }

    public void onWebPageLoadStart(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        Log.d(TAG, ">> Load Start URL: " + url);

        tvNavUrl.setText(displayURL);

        updateNavButtonStates(url);

        //progress bar
        progressLoading.setProgress(0);

        Transition transProgress = new Fade();
        transProgress.setDuration(850);
        TransitionManager.beginDelayedTransition(vgOverlay, transProgress);
        progressLoading.setVisibility(VISIBLE);

    }

    public void onWebPageLoadComplete(String url) {
        previousUrl = url;
        pageLoadComplete = true;
        Log.d(TAG, ">> Load Complete URL: " + url);
        // TODO hide button controls and progress bar when complete
        updateNavButtonStates(url);

        // remove overlay
        Transition transOverlay = new Fade();
        transOverlay.setDuration(925);
        transOverlay.setStartDelay(375);
        TransitionManager.beginDelayedTransition(vgOverlay, transOverlay);
        vgOverlay.setVisibility(GONE);

        // progress bar hide
        Transition transProgress = new Fade();
        transProgress.setDuration(850);

        TransitionManager.beginDelayedTransition(vgOverlay, transProgress);
        progressLoading.setVisibility(GONE);

        attemptScriptInjection();
    }

    private void attemptScriptInjection() {

        if (pageLoadComplete && !TextUtils.isEmpty(vWebBrowser.getUrl()) && originalURL.equals(vWebBrowser.getUrl())) {
            for (Pair<String, String> current : scriptInvocationPair) {
                if (!TextUtils.isEmpty(current.getL())) {
                    injectJS(current.getL());
                }
                if (!TextUtils.isEmpty(current.getR())) {
                    injectJS(current.getR());
                }
            }
            scriptInvocationPair.clear();
        }
    }


    public void onWebPageLoadProgressUpdate(String url, int progress) {
        // TODO update progress bar here
        progressLoading.setProgress(progress);
    }

    @Override
    public void onWebPageLoadError(String url, String contextMessage) {
        ResultsAnalytics.ResultWebViewLoadError event
                = new ResultsAnalytics.ResultWebViewLoadError();

        event.url = url;
        event.errorDescription = contextMessage;

        analyticsManager.track(event);
    }

    public void stopDownloads() {
        for (String requestTag : scriptDownloadRequestTags) {
            httpManager.cancelRequest(requestTag);
        }
        scriptDownloadRequestTags.clear();
    }
}