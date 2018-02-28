package org.socratic.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.socratic.android.R;
import org.socratic.android.contract.WebCardContract;
import org.socratic.android.api.model.CardResponse;
import org.socratic.android.databinding.FragmentWebviewResultBinding;
import org.socratic.android.globals.BaseSearchManager;
import org.socratic.android.globals.OcrSearchManager;
import org.socratic.android.globals.TextSearchManager;
import org.socratic.android.util.MultiLog;
import org.socratic.android.views.WebBrowserResultsView;

import javax.inject.Inject;

public class WebCardFragment extends BaseFragment<FragmentWebviewResultBinding, WebCardContract.ViewModel>
    implements WebCardContract.View {

    @Inject OcrSearchManager ocrSearchManager;
    @Inject TextSearchManager textSearchManager;

    private static final String ARG_CARD_INDEX = "ARG_CARD_INDEX";
    private static final String ARG_IS_OCR = "@eio";

    WebBrowserResultsView webView;
    BaseSearchManager searchManager;


    public static WebCardFragment newInstance(int cardIndex, boolean isOCR) {
        Bundle args = new Bundle();
        args.putInt(ARG_CARD_INDEX, cardIndex);
        args.putBoolean(ARG_IS_OCR, isOCR);

        WebCardFragment frag = new WebCardFragment();
        frag.setArguments(args);
        return frag;
    }

    public WebCardFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        MultiLog.d("WebCardFragment", "WebCardFragment Created!");
        View rootView = setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_webview_result);

        webView = binding.resultsWebview;

        int cardIndex = getArguments().getInt(ARG_CARD_INDEX);
        boolean isOCR = getArguments().getBoolean(ARG_IS_OCR);

        if (isOCR) {
            searchManager = ocrSearchManager;
        } else {
            searchManager = textSearchManager;
        }

        CardResponse card = searchManager.getResponseCard(cardIndex);
        if (card != null) {
            ensureUi(card);
        }

        return rootView;
    }

    private void ensureUi(CardResponse card) {

        String url = card.getUrl();
        String displayUrl = card.getDisplayUrl();

        String bodyColor = null;
        if (card.getBody() != null && !TextUtils.isEmpty(card.getBody().getBg_color())) {
            bodyColor = card.getBody().getBg_color();
        }

        webView.setupRequest(url, displayUrl, card.getHeader(), bodyColor, card.getScripts());
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getActivity().isFinishing()) {
            webView.stopDownloads();
        }
    }
}