package org.socratic.android.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.socratic.android.R;
import org.socratic.android.contract.MathCardContract;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.analytics.MathCardAnalytics;
import org.socratic.android.api.model.math.Step;
import org.socratic.android.databinding.FragmentMathCardBinding;
import org.socratic.android.globals.OcrSearchManager;
import org.socratic.android.util.MultiLog;
import org.socratic.android.adapter.MathStepAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by byfieldj on 8/28/17.
 * <p>
 * Fragment that will be used to display latex solutions natively, rather than leveraging the
 * performance-limited webview that is currently being used.
 */

public class MathCardFragment extends BaseFragment<FragmentMathCardBinding, MathCardContract.ViewModel> implements MathCardContract.View {

    private static boolean timerRunning;
    private static String latexQuery;
    private static final String EXTRA_LATEX_QUERY = "latexQuery";
    private static int currentTimerSeconds;

    @Inject
    OcrSearchManager ocrSearchManager;

    MathStepAdapter mathStepAdapter;

    private static Handler mTimerHandler = new Handler();
    private static Runnable mTimerRunnable;
    private static final int TIMER_INTERVAL = 1000;

    public static MathCardFragment newInstance(String latexQuery) {

        Bundle args = new Bundle();

        MathCardFragment fragment = new MathCardFragment();
        args.putString(EXTRA_LATEX_QUERY, latexQuery);

        fragment.setArguments(args);
        return fragment;
    }

    public MathCardFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_math_card);

        RecyclerView recyclerView = binding.mathStepsRecycler;
        ArrayList<Step> mathSteps = ocrSearchManager.getResponseCard(0).getData().getMathSteps().getSteps();
        String questionLaTeX = ocrSearchManager.getResponse().getQuestionText();

        mathStepAdapter = new MathStepAdapter(mathSteps, questionLaTeX, getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mathStepAdapter);

        if (savedInstanceState != null) {
            latexQuery = getArguments().getString(EXTRA_LATEX_QUERY);
        }


        startTimer();

        return rootView;

    }

    public static void startTimer() {

        mTimerRunnable = new Runnable() {
            @Override
            public void run() {

                ++currentTimerSeconds;
                timerRunning = true;
                MultiLog.d("MathCardFragment", "Math Card visible for " + currentTimerSeconds);

                mTimerHandler.postDelayed(this, TIMER_INTERVAL);

            }
        };

        mTimerHandler.postDelayed(mTimerRunnable, TIMER_INTERVAL);



    }

    public static void stopTimer(AnalyticsManager analyticsManager) {

        if (mTimerHandler != null && timerRunning) {
            mTimerHandler.removeCallbacks(mTimerRunnable);
            mTimerRunnable = null;
        }

        // Track the Amplitude event for Swiping off the MathCard
        MathCardAnalytics.SwipeOffStepper swipeOffEvent = new MathCardAnalytics.SwipeOffStepper();
        swipeOffEvent.query = latexQuery;
        analyticsManager.track(swipeOffEvent);

        // Track the Amplitude event for the number of seconds spent on the Math Card
        MathCardAnalytics.TimeOnStepper timeOnStepperEvent = new MathCardAnalytics.TimeOnStepper();
        timeOnStepperEvent.query = latexQuery;
        timeOnStepperEvent.eventDurationInSeconds = currentTimerSeconds;
        analyticsManager.track(timeOnStepperEvent);

        MultiLog.d("MathCardFragment", "Math Card swiped away, viewed for " + currentTimerSeconds);


    }

}
