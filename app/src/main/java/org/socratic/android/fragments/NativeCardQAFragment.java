package org.socratic.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.socratic.android.BuildConfig;
import org.socratic.android.R;
import org.socratic.android.contract.NativeCardQAContract;
import org.socratic.android.api.model.CardResponse;
import org.socratic.android.api.model.Highlight;
import org.socratic.android.api.model.NativeQACard;
import org.socratic.android.api.model.QuestionHighlights;
import org.socratic.android.databinding.FragmentNativeCardQaBinding;
import org.socratic.android.globals.BaseSearchManager;
import org.socratic.android.globals.OcrSearchManager;
import org.socratic.android.globals.TextSearchManager;
import org.socratic.android.util.MultiLog;
import org.socratic.android.util.Util;

import java.util.HashMap;

import javax.inject.Inject;

public class NativeCardQAFragment extends BaseFragment<FragmentNativeCardQaBinding, NativeCardQAContract.ViewModel>
        implements NativeCardQAContract.View {

    @Inject
    OcrSearchManager ocrSearchManager;
    @Inject
    TextSearchManager textSearchManager;

    public static final String ARG_CARD_INDEX = "ARG_CARD_INDEX";
    private static final String ARG_IS_OCR = "@eio";

    private static HashMap<String, Integer> sIcons;

    static {
        sIcons = new HashMap<>();
        sIcons.put("banana", R.drawable.result_card_banana);
        sIcons.put("blueberry", R.drawable.result_card_blueberry);
        sIcons.put("broccoli", R.drawable.result_card_broccoli);
        sIcons.put("carrot", R.drawable.result_card_carrot);
        sIcons.put("cherry", R.drawable.result_card_cherry);
        sIcons.put("grapes_header", R.drawable.result_card_grape);
    }

    ViewGroup cardContent;

    BaseSearchManager searchManager;

    public static NativeCardQAFragment newInstance(int cardIndex, boolean isOCR) {
        Bundle args = new Bundle();
        args.putInt(ARG_CARD_INDEX, cardIndex);
        args.putBoolean(ARG_IS_OCR, isOCR);

        NativeCardQAFragment frag = new NativeCardQAFragment();
        frag.setArguments(args);
        return frag;
    }

    public NativeCardQAFragment() {
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
        MultiLog.d("WebCardFragment", "NativeCardQAFragment Created!");

        View rootView = setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_native_card_qa);

        cardContent = binding.cardContent;

        int cardIndex = getArguments().getInt(ARG_CARD_INDEX);
        boolean isOCR = getArguments().getBoolean(ARG_IS_OCR);

        if (isOCR) {
            searchManager = ocrSearchManager;
        } else {
            searchManager = textSearchManager;
        }

        CardResponse card = searchManager.getResponseCard(cardIndex);
        if (isValidCardData(card)) {
            ensureUi(card);
        }

        return rootView;
    }

    private void ensureUi(CardResponse card) {

        NativeQACard qa = card.getData().getQa();
        NativeQACard.Content content = qa.getContent();
        NativeQACard.Header header = qa.getHeader();
        NativeQACard.Body body = qa.getBody();

        if (BuildConfig.DEBUG) {
            binding.score.setText("Score: " + String.valueOf(content.getScore()));
            binding.score.setVisibility(View.VISIBLE);
        }

        binding.cardScrollview.setBackgroundColor(Util.parseApiColor(body.getBg_color()));

        if (sIcons.containsKey(header.getIcon())) {
            int headerIconId = sIcons.get(header.getIcon());
            binding.headerIcon.setImageResource(headerIconId);
        }
        binding.headerTitle.setText(header.getText());

        binding.questionSourceText.setText(content.getName());
        Glide.with(this).load(content.getLogo_img()).into(binding.questionSourceIcon);
        setTextWithHighlights(binding.questionText, content.getQuestion(), content.getQuestion_highlights());

        binding.answerText.setText(content.getAnswer_text());

        binding.source.setText(content.getUrl());
    }

    private void setTextWithHighlights(TextView tv, String text, QuestionHighlights questionHighlights) {
        if (questionHighlights == null || questionHighlights.getHighlights() == null || questionHighlights.getHighlights().isEmpty()) {
            tv.setText(text);
            return;
        }

        Spannable span = new SpannableString(text);
        for (Highlight h : questionHighlights.getHighlights()) {
            if (h.getColor() == null || h.getRange() == null) {
                continue;
            }

            int start = h.getRange().getLocation();
            int end = start + h.getRange().getLength();

            if (start < 0 || end > text.length()) {
                continue;
            }

            int color = Util.parseApiColor(h.getColor());
            span.setSpan(new BackgroundColorSpan(color), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        tv.setText(span);
    }

    public static boolean isValidCardData(CardResponse card) {
        return card != null && card.getData() != null &&
                card.getData().getQa() != null && card.getData().getQa().getContent() != null &&
                card.getData().getQa().getHeader() != null &&
                card.getData().getQa().getBody() != null;
    }
}