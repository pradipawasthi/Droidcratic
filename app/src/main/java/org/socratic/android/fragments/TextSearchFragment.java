package org.socratic.android.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.socratic.android.R;
import org.socratic.android.activities.CameraActivity;
import org.socratic.android.activities.SearchProgressActivity;
import org.socratic.android.contract.TextSearchContract;
import org.socratic.android.util.Util;
import org.socratic.android.adapter.AutoCompleteAdapter;
import org.socratic.android.databinding.FragmentTextSearchBinding;
import org.socratic.android.events.BingPredictionsEvent;
import org.socratic.android.globals.TextSearchManager;
import org.socratic.android.storage.DiskStorage;
import org.socratic.android.util.MultiLog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TextSearchFragment extends BaseFragment<FragmentTextSearchBinding, TextSearchContract.ViewModel>
    implements TextSearchContract.View{

    @Inject DiskStorage diskStorage;
    @Inject TextSearchManager textSearchManager;

    private static final String TAG = TextSearchFragment.class.getSimpleName();

    AutoCompleteTextView textSearchView;

    AutoCompleteAdapter predictionAdapter;
    List<String> predictions = new ArrayList<>();

    public TextSearchFragment() {
        // Required empty public constructor
    }

    public static TextSearchFragment newInstance() {
        TextSearchFragment fragment = new TextSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_text_search);

        textSearchView = binding.textSearchView;

        //configure settings for text search
        textSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        textSearchView.setRawInputType(InputType.TYPE_CLASS_TEXT);
        predictionAdapter = new AutoCompleteAdapter(getActivity(), R.layout.autocomplete_item,
                R.id.autocomplete_item_textview, predictions);
        textSearchView.setThreshold(1);
        textSearchView.setTextColor(Color.BLACK);
        textSearchView.setAdapter(predictionAdapter);
        textSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.getPredictions(s);

                if (s.length() != 0) {
                    binding.clearTextButton.setVisibility(View.VISIBLE);
                } else {
                    binding.clearTextButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //do nothing
            }
        });
        textSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = textSearchView.getText().toString();

                    startTextSearch(query);
                }

                return false;
            }
        });

        binding.clearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSearchView.setText("");
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void setup() {
        textSearchView.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textSearchView, InputMethodManager.SHOW_IMPLICIT);

        ((CameraActivity) getActivity()).compressUI();
        Util.hideStatusBar(getActivity());
    }

    private void startTextSearch(String query) {
        MultiLog.d(TAG, "Text search starting");

        textSearchManager.setResponse(null);

        diskStorage.incrementSearchCount();

        Intent intent = new Intent(getActivity(), SearchProgressActivity.class);
        SearchProgressActivity.prepareIntent(intent, query);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BingPredictionsEvent event) {
        if (event.getContext() == BingPredictionsEvent.CONTEXT_FINISHED) {

            MultiLog.d(TAG, "Bing prediction search completed");

            predictions.clear();
            viewModel.getCurrentPredictions();
        }
    }

    @Override
    public void displayPredictions(List<String> currentPredictions) {
        predictions.addAll(currentPredictions);
        predictionAdapter.notifyDataSetChanged();
    }

    public AutoCompleteTextView getTextSearchView() {
        return textSearchView;
    }
}
