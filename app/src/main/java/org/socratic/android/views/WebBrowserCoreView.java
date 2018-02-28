package org.socratic.android.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.socratic.android.BuildConfig.API_HOST;

/**
 * Created by pcnofelt on 2/17/17.
 */

public class WebBrowserCoreView extends WebView {

    public interface ContentLoadReporter {

        void onWebPageLoadStart(String url);

        void onWebPageLoadComplete(String url);

        void onWebPageLoadProgressUpdate(String url, int progress);

        void onWebPageLoadError(String url, String contextMessage);
    }

    private final static int SDK_VERSION_LOLLIPOP = 21;

    private static final String TAG = WebBrowserCoreView.class.getSimpleName();
    private final static String mathJaxPrefix = "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/";
    private final static String mathJaxFolder = "libs/mathjax/";
    private final static String fontFolder = "fonts/";
    private final static String encoding = "UTF-8";

    ContentLoadReporter mLoadReporter;

    public WebBrowserCoreView(Context context) {
        super(context);
        init();
    }

    public WebBrowserCoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebBrowserCoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //
        if (Build.VERSION.SDK_INT >= SDK_VERSION_LOLLIPOP) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        } else {
            setLayerType(LAYER_TYPE_NONE, null);
        }

        // on page finish force redraw
        WebViewClient webclient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mLoadReporter != null) {
                    mLoadReporter.onWebPageLoadStart(getUrl());
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                forceWebViewRedraw();

                if (mLoadReporter != null) {
                    mLoadReporter.onWebPageLoadComplete(getUrl());
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.d(TAG, "Loading Url -> " + url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (mLoadReporter != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mLoadReporter.onWebPageLoadError(getUrl(), error.getDescription().toString());
                    } else {
                        mLoadReporter.onWebPageLoadError(getUrl(), "Unable to load page.");
                    }
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (mLoadReporter != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mLoadReporter.onWebPageLoadError(getUrl(), errorResponse.getReasonPhrase());
                    } else {
                        mLoadReporter.onWebPageLoadError(getUrl(), "HTTP issue associated with loading page.");
                    }
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(final WebView view, String url) {
                //flag to configure whether resources for results should be fetched or loaded from cache
                boolean shouldLoadCachedResources = true;

                if (shouldLoadCachedResources) {
                    WebResourceResponse resp = null;
                    try {
                        if (url.startsWith(mathJaxPrefix)) {
                            resp = getMathJaxResource(url);
                        } else if (url.startsWith(API_HOST) && url.endsWith("otf")) {
                            resp = getFontResource(url);
                        }
                    } catch (IOException e) {
                        Log.w(TAG, "--- error loading file from assets: " + e + " | for url: " + url);
                    }

                    if (resp != null) {
                        return resp;
                    }
                }

                return super.shouldInterceptRequest(view, url);
            }
        };


        setWebViewClient(webclient);
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (mLoadReporter != null) {
                    mLoadReporter.onWebPageLoadProgressUpdate(getUrl(), newProgress);
                }
            }
        });

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);

        getSettings().setJavaScriptEnabled(true);
        getSettings().setSupportZoom(false);
        getSettings().setBuiltInZoomControls(false);
        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        getSettings().setPluginState(android.webkit.WebSettings.PluginState.ON_DEMAND);
        // if you want transparent
        setBackgroundColor(0x00000000);
    }

    /**
     * Due to bug in 4.2.2 sometimes the webView will not draw its contents
     * after the data has loaded. Triggers redraw. Does not work in webView's
     * onPageFinished callback
     */
    private void forceWebViewRedraw() {
        post(new Runnable() {
            @Override
            public void run() {
                if (getContext() instanceof Activity) {
                    Activity activity = (Activity) getContext();

                    invalidate();
                }
            }
        });
    }

    public void setContentLoadReporter(ContentLoadReporter loadReporter) {
        mLoadReporter = loadReporter;
    }

    /**
     * On content load, clear view first then force request layout, ensures proper height/width
     */
    public void loadWithUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        // If you run into layout issues you may want to call clearView().
        // Not calling this cause it may cause a UI flicker
        loadUrl(url);
        requestLayout();
    }

    /**
     * On content load, clear view first then force request layout, ensures proper height/width
     */
    public void loadWithHtmlContent(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        // If you run into layout issues you may want to call clearView().
        // Not calling this cause it may cause a UI flicker
        loadDataWithBaseURL("", content, "text/html", "utf-8", null);
        requestLayout();
    }

    @Override
    protected void onDetachedFromWindow() {
        // TODO: remove any javascript interface hooks.
        // removeJavascriptInterface(JAVASCRIPT_INTERFACE_NAME);
        mLoadReporter = null;

        loadUrl("about:blank"); // loads non-dynamic content

        clearCache(true);
        destroyDrawingCache();
        setWebChromeClient(null);
        setWebViewClient(null);

        removeAllViews();
        destroy();

        super.onDetachedFromWindow();
    }

    /**
     * Given a MathJax resource URL (js/font), sees if we have it stored
     * in the /assets folder, and returns it if so
     */
    private WebResourceResponse getMathJaxResource(String url) throws IOException {
        String filepath = url.replace(mathJaxPrefix, mathJaxFolder);
        return getWebResourceResponseForFilepath(filepath);
    }

    private WebResourceResponse getFontResource(String url) throws IOException {
        String filename = url.substring(url.lastIndexOf('/') + 1, url.length());
        String filepath = fontFolder + filename;
        return getWebResourceResponseForFilepath(filepath);
    }

    private WebResourceResponse getWebResourceResponseForFilepath(String filepath) throws IOException {
        WebResourceResponse resp = null;
        InputStream data = getContext().getAssets().open(filepath);
        if (data != null) {
            String mimeType = getMimeTypeFromFilepath(filepath);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Map<String, String> responseHeaders = new HashMap<String, String>();
                responseHeaders.put("Access-Control-Allow-Origin", "*");
                resp = new WebResourceResponse(mimeType, encoding, 200, "OK", responseHeaders, data);
            } else {
                resp = new WebResourceResponse(mimeType, encoding, data);
            }
        } else {
            Log.w(TAG, "data was null for filepath: " + filepath);
        }

        return resp;
    }

    private String getMimeTypeFromFilepath(String filepath) {
        String mimeType = "";
        String extension = MimeTypeMap.getFileExtensionFromUrl(filepath);
        switch (extension) {
            case "otf":
                mimeType = "application/x-font-opentype";
                break;
            case "woff":
                mimeType = "application/font-woff";
                break;
            case "js":
                mimeType = "text/javascript";
                break;
        }
        return mimeType;
    }

}
