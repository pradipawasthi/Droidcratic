package org.socratic.android.fragments;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.commonsware.cwac.anddown.AndDown;

import org.socratic.android.R;
import org.socratic.android.contract.ExplainerCardContract;
import org.socratic.android.databinding.FragmentExplainerCardBinding;
import org.socratic.android.globals.BaseSearchManager;
import org.socratic.android.globals.OcrSearchManager;
import org.socratic.android.globals.TextSearchManager;
import org.socratic.android.util.GlideImageGetter;
import org.socratic.android.util.HtmlTagHandler;

import javax.inject.Inject;

/**
 * Created by byfieldj on 10/6/17.
 */

public class ExplainerCardFragment extends BaseFragment<FragmentExplainerCardBinding, ExplainerCardContract.ViewModel> implements ExplainerCardContract.View {


    private static final String EXTRA_CARD_INDEX = "cardIndex";
    private static final String EXTRA_IS_OCR = "isOCR";

    BaseSearchManager searchManager;

    @Inject
    TextSearchManager textSearchManager;

    @Inject
    OcrSearchManager ocrSearchManager;

    public static ExplainerCardFragment newInstance(int cardIndex, boolean isOCR) {

        Bundle args = new Bundle();

        ExplainerCardFragment fragment = new ExplainerCardFragment();
        args.putInt(EXTRA_CARD_INDEX, cardIndex);
        args.putBoolean(EXTRA_IS_OCR, isOCR);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_explainer_card);

        boolean isOCR = getArguments().getBoolean(EXTRA_IS_OCR);

        if (isOCR) {
            searchManager = ocrSearchManager;
        } else {
            searchManager = textSearchManager;
        }

        int cardIndex = getArguments().getInt(EXTRA_CARD_INDEX);

        String explainerContent = searchManager.getResponseCard(cardIndex).getData().getExplainer().getContent();
        binding.headerTitle.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/cerapro-bold.otf"));


        if (explainerContent != null && !explainerContent.isEmpty()) {
            AndDown andDown = new AndDown();

            String parsed = explainerContent.replace("---", "<hr>");

            String result = andDown.markdownToHtml(parsed);

            GlideImageGetter glideImageGetter = new GlideImageGetter(getContext(), binding.tvContent);

            Spanned spanned;

            // Html.fromHtml() does not support horizontal rules, but our explainer card layout by default contains
            // Horizontal rules. So here we use a custom HtmlTagHandler() to manually draw a divider between sections of text
            if (Build.VERSION.SDK_INT >= 24) {
                spanned = Html.fromHtml(result, Html.FROM_HTML_MODE_LEGACY, glideImageGetter, new HtmlTagHandler());
            } else {
                spanned = Html.fromHtml(result, glideImageGetter, new HtmlTagHandler());
            }
            binding.tvContent.setText(spanned);
            binding.tvContent.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/cerapro-regular.otf"));
        }
        return rootView;
    }
}
