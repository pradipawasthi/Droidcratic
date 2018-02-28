package org.socratic.android.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.socratic.android.R;
import org.socratic.android.contract.DefinitionCardContract;
import org.socratic.android.api.model.Definition;
import org.socratic.android.databinding.FragmentDefinitionCardBinding;
import org.socratic.android.globals.BaseSearchManager;
import org.socratic.android.globals.OcrSearchManager;
import org.socratic.android.globals.TextSearchManager;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by byfieldj on 10/3/17.
 */

public class NativeDefintionCardFragment extends BaseFragment<FragmentDefinitionCardBinding, DefinitionCardContract.ViewModel> implements DefinitionCardContract.View {


    private static final String EXTRA_CARD_INDEX = "cardIndex";
    private static final String EXTRA_IS_OCR = "isOCR";

    BaseSearchManager searchManager;

    @Inject
    TextSearchManager textSearchManager;

    @Inject
    OcrSearchManager ocrSearchManager;

    String[] definitionSources;

    public static NativeDefintionCardFragment newInstance(int index, boolean isOCR) {

        Bundle args = new Bundle();
        NativeDefintionCardFragment fragment = new NativeDefintionCardFragment();
        args.putBoolean(EXTRA_IS_OCR, isOCR);
        args.putInt(EXTRA_CARD_INDEX, index);
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
        View rootView = setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_definition_card);

        binding.headerTitle.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/cerapro-bold.otf"));
        int cardIndex = getArguments().getInt(EXTRA_CARD_INDEX);


        LinearLayout answersContainer = binding.llDefinitionsContainer;


        boolean isOCR = getArguments().getBoolean(EXTRA_IS_OCR);

        if (isOCR) {
            searchManager = ocrSearchManager;
        } else {
            searchManager = textSearchManager;
        }

        ArrayList<Definition> definitionsList = searchManager.getResponseCard(cardIndex).getData().getDefinitions().getDefinitionsList();

        definitionSources = searchManager.getResponseCard(cardIndex).getData().getDefinitions().getSources();

        binding.tvSourceTitle.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/cerapro-bold.otf"));


        setupDefinitions(definitionsList, answersContainer);

        return rootView;
    }

    private void setupDefinitions(ArrayList<Definition> definitionsList, ViewGroup container) {

        if (definitionsList != null && definitionsList.size() > 0) {

            // Display the words and their definitions
            for (int i = 0; i < definitionsList.size(); i++) {

                Definition definition = definitionsList.get(i);

                TextView wordText = new TextView(getContext());
                LinearLayout.LayoutParams wordParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                wordParams.setMargins(30, 40, 20, 20);
                wordText.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/cerapro-bold.otf"));
                wordText.setTextSize(17);
                wordText.setText(definition.getWord());
                wordText.setLayoutParams(wordParams);


                TextView definitionText = new TextView(getContext());
                LinearLayout.LayoutParams definitionParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                definitionParams.setMargins(30, 0, 30, 40);
                definitionText.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/cerapro-regular.otf"));
                definitionText.setTextSize(17);
                definitionText.setText(definition.getDefinition());
                definitionText.setLayoutParams(definitionParams);

                container.addView(wordText);
                container.addView(definitionText);


            }

            LinearLayout sourceContainer = binding.llSourceContainer;
            // Display all available sources
            for (int i = 0; i < definitionSources.length; i++) {

                TextView sourceText = new TextView(getContext());
                LinearLayout.LayoutParams sourceParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                sourceParams.setMargins(30, 40, 20, 20);
                sourceText.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/cerapro-regular.otf"));
                sourceText.setTextSize(17);
                sourceText.setText(definitionSources[i]);
                sourceText.setLayoutParams(sourceParams);
                sourceContainer.addView(sourceText);


            }


        }

    }
}



