package org.socratic.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.socratic.android.R;
import org.socratic.android.SocraticApp;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.analytics.MathCardAnalytics;
import org.socratic.android.api.model.math.Step;
import org.socratic.android.util.DimenUtil;
import org.socratic.android.util.MultiLog;
import org.socratic.android.views.CircleBadgeTextView;
import org.socratic.android.views.ExpandableLayoutView;
import org.socratic.android.views.FastOutSlowInInterpolator;
import org.socratic.android.views.JLatexView;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by byfieldj on 8/31/17.
 */

public class MathStepAdapter extends RecyclerView.Adapter<MathStepAdapter.BaseViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_STEP = 1;
    private static final int ITEM_VIEW_TYPE_RESTATEMENT = 2;
    private static final int ITEM_VIEW_TYPE_HINT = 3;
    private static final int ITEM_VIEW_TYPE_EXPAND = 4;
    private static final int ITEM_VIEW_TYPE_SOLUTION = 5;
    private static final int ITEM_VIEW_TYPE_LIGHTBULB = 6;
    private String queryText;
    private HashMap<Integer, Boolean> expandedList;


    ArrayList<Step> mathSteps;
    private Step firstStep;

    private static final String MATH_CARD_COLOR = "#1479F1";
    private static final String MATH_CARD_EXPANDED_COLOR = "#1455a1";
    private static final String LIGHTBULB_TEXT_COLOR = "#77FFFFFF";
    private static final String HINT_TEXT_COLOR = "#77FFFFFF";
    private static final String HINT_TEXT_COLOR_DEFAULT = "#33000000";
    private static final String BADGE_TEXT_COLOR = "#13519a";
    private static final double LATEX_MATH_THRESHOLD = .75;

    private static final String CARD_TYPE_MATH = "math";
    private static final String CARD_TYPE_LIGHTBULB = "lightbulb";

    private ArrayList<Integer> expandedViewHoldersList = new ArrayList<>();


    @Inject
    AnalyticsManager analyticsManager;

    public MathStepAdapter(ArrayList<Step> mathSteps, String questionLaTeX, Context context) {

        SocraticApp.getViewHolderComponent(context).inject(this);

        this.mathSteps = mathSteps;
        this.queryText = questionLaTeX;

        Step headerStep = new Step();
        headerStep.setMath(null);
        mathSteps.add(0, headerStep);

        firstStep = new Step();
        firstStep.setMath(questionLaTeX);
        mathSteps.add(1, firstStep);

        expandedList = new HashMap<>();

    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {


            case ITEM_VIEW_TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_math_step_header, parent, false);

                return new HeaderViewHolder(view);


            case ITEM_VIEW_TYPE_STEP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_math_step, parent, false);

                return new DefaultStepViewHolder(view);


            case ITEM_VIEW_TYPE_SOLUTION:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_math_step_solution, parent, false);

                return new SolutionStepViewHolder(view);

            case ITEM_VIEW_TYPE_RESTATEMENT:

                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_math_step, parent, false);
                return new DefaultStepViewHolder(view);

            case ITEM_VIEW_TYPE_HINT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_math_step_hint, parent, false);
                return new HintStepViewHolder(view);

            case ITEM_VIEW_TYPE_EXPAND:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_math_step_expand, parent, false);
                return new ExpandStepViewHolder(view);

            case ITEM_VIEW_TYPE_LIGHTBULB:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_math_step_lightbulb, parent, false);

                return new LightbulbStepViewHolder(view);

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_math_step, parent, false);
                return new DefaultStepViewHolder(view);

        }


    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {

        final String stepNumber = mathSteps.get(position).getStepNumber();

        switch (getItemViewType(position)) {


            case ITEM_VIEW_TYPE_HEADER:

                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.headerText.setTypeface(Typeface.createFromAsset(headerViewHolder.headerText.getContext().getAssets(), "fonts/cerapro-bold.otf"));


                break;


            case ITEM_VIEW_TYPE_RESTATEMENT:

                DefaultStepViewHolder restatementViewHolder = (DefaultStepViewHolder) holder;
                restatementViewHolder.latexView.setTextColor(Color.WHITE);
                restatementViewHolder.latexView.setTextSize((int) DimenUtil.convertSpToPx(16, ((DefaultStepViewHolder) holder).itemView.getContext()));
                restatementViewHolder.latexView.setLatex(firstStep.getMath(), false);
                restatementViewHolder.badgeTextView.setText("1");
                adjustLatexSizeAccordingToLength(restatementViewHolder, mathSteps.get(position).getStepNumber());

                break;

            case ITEM_VIEW_TYPE_EXPAND:

                holder.setIsRecyclable(false);
                final ExpandStepViewHolder expandStepViewHolder = (ExpandStepViewHolder) holder;
                final Step expandStep = mathSteps.get(position);

                if (expandedViewHoldersList.contains(position)) {
                    addSubstepsToCell(expandStepViewHolder.substepView, mathSteps.get(position));
                    expandStepViewHolder.badgeTextView.setTextColor(Color.WHITE);
                    expandStepViewHolder.badgeTextView.setText(stepNumber);
                    expandStepViewHolder.itemView.setBackgroundColor(Color.parseColor(MATH_CARD_EXPANDED_COLOR));
                    expandStepViewHolder.expandArrow.setBackgroundResource(R.drawable.mathstepper_collapse_ico_active);
                    expandStepViewHolder.expandableLayoutView.expand(false);
                    expandStepViewHolder.shouldExpand = false;
                }else{
                    expandStepViewHolder.itemView.setBackgroundColor(Color.parseColor(MATH_CARD_COLOR));
                    expandStepViewHolder.expandArrow.setBackgroundResource(R.drawable.mathstepper_expand_ico);
                    expandStepViewHolder.badgeTextView.setTextColor(Color.parseColor(BADGE_TEXT_COLOR));
                    expandStepViewHolder.shouldExpand = true;
                }

                expandStepViewHolder.latexView.setTextColor(Color.WHITE);
                expandStepViewHolder.latexView.setTextSize((int) DimenUtil.convertSpToPx(16, ((ExpandStepViewHolder) holder).itemView.getContext()));
                expandStepViewHolder.latexView.setLatex(expandStep.getMath(), false);
                expandStepViewHolder.badgeTextView.setText(stepNumber);
                expandStepViewHolder.contentView.setOnClickListener(new ExpandStepClickListener(expandStepViewHolder, position));
                adjustLatexSizeAccordingToLength(expandStepViewHolder, expandStep.getStepNumber());

                break;


            case ITEM_VIEW_TYPE_HINT:

                holder.setIsRecyclable(false);
                final HintStepViewHolder hintStepViewHolder = (HintStepViewHolder) holder;

                if (expandedViewHoldersList.contains(position)) {
                    addHintInfoToCell(hintStepViewHolder.hintContainer, mathSteps.get(position));
                    hintStepViewHolder.hintText.setTypeface(Typeface.createFromAsset(hintStepViewHolder.itemView.getContext().getAssets(), "fonts/cerapro-bold.otf"));
                    hintStepViewHolder.latexView.setTextColor(Color.WHITE);
                    hintStepViewHolder.latexView.setTextSize((int) DimenUtil.convertSpToPx(16, ((HintStepViewHolder) holder).itemView.getContext()));
                    hintStepViewHolder.latexView.setLatex(mathSteps.get(position).getMath(), false);
                    hintStepViewHolder.badgeTextView.setText(stepNumber);
                    hintStepViewHolder.expandableLayoutView.expand(false);
                    hintStepViewHolder.itemView.setBackgroundColor(Color.parseColor(MATH_CARD_EXPANDED_COLOR));
                    hintStepViewHolder.badgeTextView.setTextColor(Color.WHITE);
                    hintStepViewHolder.hintText.setVisibility(View.INVISIBLE);
                    hintStepViewHolder.collapseArrow.setVisibility(View.VISIBLE);
                    hintStepViewHolder.shoudExpand = false;


                } else {
                    final Step hintStep = mathSteps.get(position);
                    hintStepViewHolder.hintText.setTypeface(Typeface.createFromAsset(hintStepViewHolder.itemView.getContext().getAssets(), "fonts/cerapro-bold.otf"));
                    hintStepViewHolder.latexView.setTextColor(Color.WHITE);
                    hintStepViewHolder.latexView.setTextSize((int) DimenUtil.convertSpToPx(16, ((HintStepViewHolder) holder).itemView.getContext()));
                    hintStepViewHolder.latexView.setLatex(hintStep.getMath(), false);
                    hintStepViewHolder.badgeTextView.setText(stepNumber);
                    hintStepViewHolder.shoudExpand = true;
                }

                hintStepViewHolder.contentView.setOnClickListener(new HintStepClickListener(hintStepViewHolder, position));
                adjustLatexSizeAccordingToLength(hintStepViewHolder, stepNumber);


                break;

            case ITEM_VIEW_TYPE_STEP:

                DefaultStepViewHolder stepViewHolder = (DefaultStepViewHolder) holder;
                Step step = mathSteps.get(position);
                stepViewHolder.latexView.setTextColor(Color.WHITE);
                stepViewHolder.latexView.setTextSize((int) DimenUtil.convertSpToPx(16, ((DefaultStepViewHolder) holder).itemView.getContext()));
                stepViewHolder.latexView.setLatex(step.getMath(), false);
                stepViewHolder.badgeTextView.setText(stepNumber);
                adjustLatexSizeAccordingToLength(stepViewHolder, mathSteps.get(position).getStepNumber());


                break;

            case ITEM_VIEW_TYPE_LIGHTBULB:

                LightbulbStepViewHolder lightbulbStepViewHolder = (LightbulbStepViewHolder) holder;
                Step lightbulbStep = mathSteps.get(position);

                String lightbulbText = lightbulbStep.getLightbulbText();
                if (lightbulbText != null && !lightbulbText.isEmpty()) {
                    lightbulbStepViewHolder.lightbulbText.setTextColor(Color.parseColor(LIGHTBULB_TEXT_COLOR));
                    lightbulbStepViewHolder.lightbulbText.setTextSize((int) DimenUtil.convertSpToPx(14, lightbulbStepViewHolder.itemView.getContext()));
                    lightbulbStepViewHolder.lightbulbText.setLatex(lightbulbText, true);

                }
                break;

            case ITEM_VIEW_TYPE_SOLUTION:

                final SolutionStepViewHolder solutionViewHolder = (SolutionStepViewHolder) holder;
                solutionViewHolder.latexView.setTextColor(Color.WHITE);
                solutionViewHolder.latexView.setTextSize((int) DimenUtil.convertSpToPx(16, ((SolutionStepViewHolder) holder).itemView.getContext()));
                solutionViewHolder.latexView.setLatex(mathSteps.get(mathSteps.size() - 1).getMath(), false);

                if (mathSteps.get(position).hasSubSteps()) {
                    ((SolutionStepViewHolder) holder).expandArrow.setVisibility(View.VISIBLE);
                    ((SolutionStepViewHolder) holder).hintText.setVisibility(View.INVISIBLE);
                } else if (mathSteps.get(position).getExplanation() != null ||
                        mathSteps.get(position).getChange() != null || mathSteps.get(position).
                        getImageUrl() != null) {
                    ((SolutionStepViewHolder) holder).expandArrow.setVisibility(View.INVISIBLE);
                    ((SolutionStepViewHolder) holder).hintText.setVisibility(View.VISIBLE);
                    solutionViewHolder.hintText.setTypeface(Typeface.createFromAsset(solutionViewHolder.itemView.getContext().getAssets(), "fonts/cerapro-bold.otf"));
                    solutionViewHolder.hintText.setTextColor(Color.parseColor(HINT_TEXT_COLOR_DEFAULT));
                }

                solutionViewHolder.contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!isCellExpanded(position)) {
                            if (mathSteps.get(position).hasSubSteps()) {
                                addSubstepsToCell(((SolutionStepViewHolder) holder).substepsView, mathSteps.get(position));
                                ((SolutionStepViewHolder) holder).expandableLayoutView.expand();
                                ((SolutionStepViewHolder) holder).itemView.setBackgroundColor(Color.parseColor(MATH_CARD_EXPANDED_COLOR));
                                ((SolutionStepViewHolder) holder).expandArrow.setBackgroundResource(R.drawable.mathstepper_collapse_ico_active);
                                expandedList.put(position, true);
                                trackMathStepExpanded(mathSteps.get(position).getMath(), mathSteps.get(position).getType());
                            } else {
                                if (mathSteps.get(position).getExplanation() != null ||
                                        mathSteps.get(position).getChange() != null || mathSteps.get(position).
                                        getImageUrl() != null) {
                                    addHintInfoToCell(((SolutionStepViewHolder) holder).substepsView, mathSteps.get(position));

                                    ((SolutionStepViewHolder) holder).expandableLayoutView.expand();
                                    ((SolutionStepViewHolder) holder).itemView.setBackgroundColor(Color.parseColor(MATH_CARD_EXPANDED_COLOR));
                                    ((SolutionStepViewHolder) holder).expandArrow.setBackgroundResource(R.drawable.mathstepper_collapse_ico_active);
                                    expandedList.put(position, true);
                                    trackMathStepExpanded(mathSteps.get(position).getMath(), mathSteps.get(position).getType());

                                }
                            }


                        } else {
                            ((SolutionStepViewHolder) holder).expandableLayoutView.setOnExpansionUpdateListener(new ExpandableLayoutView.OnExpansionUpdateListener() {
                                @Override
                                public void onExpansionUpdate(float expansionFraction, int state) {
                                    if (expansionFraction == 0.0f) {
                                        removeSubstepsFromCell(((SolutionStepViewHolder) holder).substepsView);
                                        removeHintInfoFromCell(((SolutionStepViewHolder) holder).substepsView);
                                        trackMathStepCollapsed(mathSteps.get(position).getMath(), mathSteps.get(position).getType());

                                    }
                                }
                            });
                            ((SolutionStepViewHolder) holder).expandableLayoutView.collapse();
                            ((SolutionStepViewHolder) holder).itemView.setBackgroundColor(Color.parseColor(MATH_CARD_COLOR));
                            ((SolutionStepViewHolder) holder).expandArrow.setBackgroundResource(R.drawable.mathstepper_expand_ico);
                            expandedList.remove(position);

                        }
                    }
                });


                break;
        }


    }


    // Handle the collapse/expand of an expand step with substeps
    class ExpandStepClickListener implements View.OnClickListener {

        private ExpandStepViewHolder holder;
        private int stepNumber;

        public ExpandStepClickListener(ExpandStepViewHolder holder, int stepNumber) {
            this.holder = holder;
            this.stepNumber = stepNumber;
        }

        @Override
        public void onClick(View view) {
            if (holder.shouldExpand) {

                addSubstepsToCell(holder.substepView, mathSteps.get(stepNumber));
                holder.expandableLayoutView.expand();
                holder.itemView.setBackgroundColor(Color.parseColor(MATH_CARD_EXPANDED_COLOR));
                holder.expandArrow.setBackgroundResource(R.drawable.mathstepper_collapse_ico_active);
                holder.badgeTextView.setTextColor(Color.WHITE);
                expandedViewHoldersList.add(stepNumber);
                holder.shouldExpand = false;


            } else {
                holder.expandableLayoutView.setOnExpansionUpdateListener(new ExpandableLayoutView.OnExpansionUpdateListener() {
                    @Override
                    public void onExpansionUpdate(float expansionFraction, int state) {
                        if (expansionFraction == 0.0f) {
                            removeSubstepsFromCell(holder.substepView);
                            trackMathStepCollapsed(mathSteps.get(stepNumber).getMath(), mathSteps.get(stepNumber).getType());
                            holder.shouldExpand = true;

                        }
                    }
                });
                holder.expandableLayoutView.collapse();
                holder.itemView.setBackgroundColor(Color.parseColor(MATH_CARD_COLOR));
                holder.expandArrow.setBackgroundResource(R.drawable.mathstepper_expand_ico);
                holder.badgeTextView.setTextColor(Color.parseColor(BADGE_TEXT_COLOR));
                expandedViewHoldersList.remove(expandedViewHoldersList.indexOf(stepNumber));
                holder.shouldExpand = true;


            }
        }
    }

    // Handle the collapse/expand of a hint step
    class HintStepClickListener implements View.OnClickListener {

        private HintStepViewHolder holder;
        private int stepNumber;

        public HintStepClickListener(HintStepViewHolder holder, int stepNumber) {

            this.holder = holder;
            this.stepNumber = stepNumber;
        }


        @Override
        public void onClick(View view) {
            if (holder.shoudExpand) {
                addHintInfoToCell(holder.hintContainer, mathSteps.get(stepNumber));
                holder.hintText.setTextColor(Color.WHITE);
                holder.expandableLayoutView.expand();
                holder.itemView.setBackgroundColor(Color.parseColor(MATH_CARD_EXPANDED_COLOR));
                holder.badgeTextView.setTextColor(Color.WHITE);
                holder.hintText.setVisibility(View.INVISIBLE);
                holder.collapseArrow.setVisibility(View.VISIBLE);
                expandedViewHoldersList.add(stepNumber);
                holder.shoudExpand = false;


            } else {
                holder.expandableLayoutView.setOnExpansionUpdateListener(new ExpandableLayoutView.OnExpansionUpdateListener() {
                    @Override
                    public void onExpansionUpdate(float expansionFraction, int state) {
                        if (expansionFraction == 0.0f) {
                            removeHintInfoFromCell(holder.hintContainer);
                            trackMathStepCollapsed(mathSteps.get(stepNumber).getMath(), mathSteps.get(stepNumber).getType());
                            holder.shoudExpand = true;
                        }
                    }
                });
                holder.expandableLayoutView.collapse();
                holder.hintText.setTextColor(Color.parseColor(HINT_TEXT_COLOR_DEFAULT));
                holder.itemView.setBackgroundColor(Color.parseColor(MATH_CARD_COLOR));
                holder.badgeTextView.setTextColor(Color.parseColor(BADGE_TEXT_COLOR));
                holder.hintText.setVisibility(View.VISIBLE);
                holder.collapseArrow.setVisibility(View.INVISIBLE);
                expandedViewHoldersList.remove(expandedViewHoldersList.indexOf(stepNumber));
                holder.shoudExpand = true;


            }
        }
    }

    // If the laTeX will end up being too wide(more than 75% of the cell's width)
    // It will potentially bleed over onto the HINT text or expand arrow, let's check for that edge case and resize it
    private void adjustLatexSizeAccordingToLength(final BaseViewHolder holder, final String stepNumber) {

        holder.itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {

                holder.itemView.getViewTreeObserver().removeOnPreDrawListener(this);
                int cellWidth = holder.itemView.getWidth();
                int latexWidth = holder.latextView.getWidth();


                latexWidth = latexWidth + holder.badgeTextView.getWidth() + 20;


                int maxWidth = (int) (cellWidth * LATEX_MATH_THRESHOLD) - 60;
                holder.latextView.getLayoutParams().width = maxWidth;

                if (latexWidth > maxWidth) {
                    // If the oversize is just by a little, let's say 20%, just decrease the text size to 14
                    if (latexWidth - maxWidth <= (maxWidth * .20)) {

                        holder.latextView.setTextSize((int) DimenUtil.convertSpToPx(14, holder.itemView.getContext()));


                    } else if (latexWidth - maxWidth <= (maxWidth * .40)) {

                        holder.latextView.setTextSize((int) DimenUtil.convertSpToPx(11, holder.itemView.getContext()));


                    } else if (latexWidth - maxWidth <= (maxWidth * .60)) {

                        holder.latextView.setTextSize((int) DimenUtil.convertSpToPx(10, holder.itemView.getContext()));


                    } else if (latexWidth - maxWidth >= (maxWidth * .60)) {
                        MultiLog.d("MathStepAdapter", "Oversize is greater than 60%");

                        holder.latextView.setTextSize((int) DimenUtil.convertSpToPx(8, holder.itemView.getContext()));


                    }


                }
                return false;
            }


        });


    }

    private void trackMathStepExpanded(String queryText, String stepType) {

        MathCardAnalytics.MathStepExpanded expandedEvent = new MathCardAnalytics.MathStepExpanded();
        expandedEvent.query = queryText;
        expandedEvent.stepType = stepType;
        //analyticsManager.track(expandedEvent);
    }

    private void trackMathStepCollapsed(String queryText, String stepType) {

        MathCardAnalytics.MathStepCollapsed collapsedEvent = new MathCardAnalytics.MathStepCollapsed();
        collapsedEvent.query = queryText;
        collapsedEvent.stepType = stepType;
       // analyticsManager.track(collapsedEvent);
    }

    private void addHintInfoToCell(ViewGroup parent, Step currentStep) {

        trackMathStepExpanded(currentStep.getMath(), currentStep.getType());

        String stepChange = currentStep.getChange();
        JLatexView stepChangeText = new JLatexView(parent.getContext());
        stepChangeText.setTextSize((int) DimenUtil.convertSpToPx(14, parent.getContext()));
        stepChangeText.setTextColor(Color.WHITE);
        stepChangeText.setLatex(stepChange, true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 32, 30, 20);
        parent.addView(stepChangeText, params);


        if (currentStep.getExplanation() != null && !currentStep.getExplanation().isEmpty()) {
            String stepExplanation = currentStep.getExplanation();
            JLatexView stepExplanationText = new JLatexView(parent.getContext());
            stepExplanationText.setTextSize((int) DimenUtil.convertSpToPx(14, parent.getContext()));
            stepExplanationText.setTextColor(Color.parseColor(HINT_TEXT_COLOR));
            stepExplanationText.setLatex(stepExplanation, true);

            LinearLayout.LayoutParams explanationParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            explanationParams.setMargins(30, 30, 30, 30);
            parent.addView(stepExplanationText, explanationParams);
        } else {
            if (currentStep.getImageUrl() != null && !currentStep.getImageUrl().isEmpty()) {

                String imageUrl = currentStep.getImageUrl();
                ImageView hintImage = new ImageView(parent.getContext());

                LinearLayout.LayoutParams imageParms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                imageParms.setMargins(10, 40, 10, 40);
                imageParms.gravity = Gravity.CENTER_HORIZONTAL;
                imageParms.height = DimenUtil.convertDpToPx(parent.getContext(), 282.4f);
                imageParms.width = DimenUtil.convertDpToPx(parent.getContext(), 300f);

                Glide.with(parent.getContext()).load(imageUrl).into(hintImage);

                parent.addView(hintImage, imageParms);
            }
        }


    }

    private void removeHintInfoFromCell(ViewGroup parent) {

        parent.removeAllViews();
    }

    private void addSubstepsToCell(ViewGroup parent, Step currentStep) {

        trackMathStepExpanded(currentStep.getMath(), currentStep.getType());

        String stepChange = currentStep.getChange();
        JLatexView stepChangeText = new JLatexView(parent.getContext());
        stepChangeText.setTextSize((int) DimenUtil.convertSpToPx(14, parent.getContext()));
        stepChangeText.setTextColor(Color.WHITE);
        stepChangeText.setLatex(stepChange, true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 32, 30, 20);

        parent.addView(stepChangeText, params);


        if (currentStep.hasSubSteps()) {
            ArrayList<Step> subSteps = currentStep.getSubsteps();

            for (Step step : subSteps) {

                JLatexView substepChangeText = new JLatexView(parent.getContext());
                JLatexView substepMathText = new JLatexView(parent.getContext());


                substepChangeText.setLatex(step.getChange(), true);
                substepMathText.setLatex(step.getMath(), true);

                substepChangeText.setTextSize((int) DimenUtil.convertSpToPx(14, parent.getContext()));
                substepChangeText.setTextColor(Color.parseColor(LIGHTBULB_TEXT_COLOR));

                substepMathText.setTextSize((int) DimenUtil.convertSpToPx(14, parent.getContext()));
                substepMathText.setTextColor(Color.parseColor(LIGHTBULB_TEXT_COLOR));


                LinearLayout.LayoutParams substepChangeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                substepChangeParams.setMargins(30, 20, 30, 4);
                substepChangeText.setLayoutParams(substepChangeParams);

                LinearLayout.LayoutParams substepMathParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                substepMathParams.setMargins(195, 20, 30, 40);
                substepMathText.setLayoutParams(substepMathParams);


                parent.addView(substepChangeText, substepChangeParams);
                parent.addView(substepMathText, substepMathParams);


            }
        }


    }

    private void removeSubstepsFromCell(ViewGroup parent) {

        parent.removeAllViews();
    }

    private boolean isCellExpanded(int position) {

        if (expandedList.containsKey(position)) {
            if (expandedList.get(position).booleanValue()) {

                return true;
            } else
                return false;
        }

        return false;
    }

    @Override
    public int getItemCount() {

        return mathSteps.size();
    }

    @Override
    public int getItemViewType(int position) {

        Step currentStep = mathSteps.get(position);
        int viewType = position;


        // Header row
        if (position == 0) {
            viewType = ITEM_VIEW_TYPE_HEADER;
            return viewType;

            // Problem restatement row
        } else if (position == 1) {

            viewType = ITEM_VIEW_TYPE_RESTATEMENT;

            return viewType;

        }
        // Solution row
        if (position == mathSteps.size() - 1) {
            viewType = ITEM_VIEW_TYPE_SOLUTION;
        } else {

            if (currentStep.getType() != null && currentStep.getType().equals(CARD_TYPE_MATH)) {

                viewType = ITEM_VIEW_TYPE_STEP;

                // Hint row
                if (currentStep.getExplanation() != null && !currentStep.getExplanation().isEmpty()) {

                    viewType = ITEM_VIEW_TYPE_HINT;
                } else if (currentStep.getExplanation() == null || currentStep.getExplanation().isEmpty()) {

                    if (currentStep.getImageUrl() != null && !currentStep.getImageUrl().isEmpty()) {
                        viewType = ITEM_VIEW_TYPE_HINT;
                    }
                }


            } else if (currentStep.getType() != null && currentStep.getType().equals(CARD_TYPE_LIGHTBULB)) {

                viewType = ITEM_VIEW_TYPE_LIGHTBULB;
            }


            // Substeps row
            if (currentStep.hasSubSteps()) {
                viewType = ITEM_VIEW_TYPE_EXPAND;
            }


        }

        return viewType;
    }

    static class DefaultStepViewHolder extends BaseViewHolder {

        JLatexView latexView;
        CircleBadgeTextView badgeTextView;

        public DefaultStepViewHolder(View v) {
            super(v);

            latexView = (JLatexView) v.findViewById(R.id.lv_latex);
            badgeTextView = (CircleBadgeTextView) v.findViewById(R.id.cv_badge);

        }
    }

    static class HeaderViewHolder extends BaseViewHolder {

        TextView headerText;

        public HeaderViewHolder(View v) {
            super(v);

            headerText = (TextView) v.findViewById(R.id.tv_card_title);
        }


    }

    static class SolutionStepViewHolder extends BaseViewHolder {

        ImageView solutionCheckmark;
        ImageView expandArrow;
        JLatexView latexView;
        LinearLayout contentView;
        ExpandableLayoutView expandableLayoutView;
        LinearLayout substepsView;
        TextView hintText;

        public SolutionStepViewHolder(View v) {
            super(v);

            latexView = (JLatexView) v.findViewById(R.id.lv_latex);
            solutionCheckmark = (ImageView) v.findViewById(R.id.iv_badge);
            expandArrow = (ImageView) v.findViewById(R.id.iv_expand);
            contentView = (LinearLayout) v.findViewById(R.id.item_container);
            expandableLayoutView = (ExpandableLayoutView) v.findViewById(R.id.ev_container);
            substepsView = (LinearLayout) v.findViewById(R.id.ll_substeps);
            hintText = (TextView) v.findViewById(R.id.tv_hint);
        }
    }

    static class HintStepViewHolder extends BaseViewHolder {

        JLatexView latexView;
        CircleBadgeTextView badgeTextView;
        TextView hintText;
        LinearLayout hintContainer;
        ExpandableLayoutView expandableLayoutView;
        LinearLayout contentView;
        ImageView collapseArrow;
        boolean shoudExpand;

        public HintStepViewHolder(View v) {
            super(v);


            latexView = (JLatexView) v.findViewById(R.id.lv_latex);
            badgeTextView = (CircleBadgeTextView) v.findViewById(R.id.cv_badge);
            hintText = (TextView) v.findViewById(R.id.tv_hint);
            collapseArrow = (ImageView) v.findViewById(R.id.iv_collapse);
            contentView = (LinearLayout) v.findViewById(R.id.ll_content_view);
            hintContainer = (LinearLayout) v.findViewById(R.id.ll_hint_container);
            expandableLayoutView = (ExpandableLayoutView) v.findViewById(R.id.ev_hint_container);
            expandableLayoutView.setDuration(500);
            expandableLayoutView.setInterpolator(new FastOutSlowInInterpolator());
        }


    }

    static class ExpandStepViewHolder extends BaseViewHolder {

        LinearLayout contentView;
        LinearLayout substepView;
        ExpandableLayoutView expandableLayoutView;
        JLatexView latexView;
        CircleBadgeTextView badgeTextView;
        ImageView expandArrow;
        boolean shouldExpand;

        public ExpandStepViewHolder(View v) {
            super(v);


            latexView = (JLatexView) v.findViewById(R.id.lv_latex);
            badgeTextView = (CircleBadgeTextView) v.findViewById(R.id.cv_badge);
            expandArrow = (ImageView) v.findViewById(R.id.iv_expand);
            contentView = (LinearLayout) v.findViewById(R.id.item_container);
            substepView = (LinearLayout) v.findViewById(R.id.ll_substeps);
            expandableLayoutView = (ExpandableLayoutView) v.findViewById(R.id.ev_container);
            expandableLayoutView.setDuration(500);
            expandableLayoutView.setInterpolator(new FastOutSlowInInterpolator());


        }

    }

    static class LightbulbStepViewHolder extends BaseViewHolder {

        JLatexView lightbulbText;

        public LightbulbStepViewHolder(View v) {
            super(v);

            lightbulbText = (JLatexView) v.findViewById(R.id.tv_lightbulb_text);
        }
    }


    abstract static class BaseViewHolder extends RecyclerView.ViewHolder {

        JLatexView latextView;
        CircleBadgeTextView badgeTextView;

        public BaseViewHolder(View v) {
            super(v);
            latextView = (JLatexView) v.findViewById(R.id.lv_latex);
            badgeTextView = (CircleBadgeTextView) v.findViewById(R.id.cv_badge);
        }

    }
}
