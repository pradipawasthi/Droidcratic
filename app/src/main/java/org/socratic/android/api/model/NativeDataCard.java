package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.model.math.MathSteps;
import org.socratic.android.api.model.video.VideoCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NativeDataCard {

    @SerializedName("qa")
    private NativeQACard qa;

    @SerializedName("math-steps")
    private MathSteps mathSteps;

    @SerializedName("explainer")
    private Explainer explainer;
  
    @SerializedName("definitions")
    private Definitions definitions;

    @SerializedName("video")
    private VideoCard videoCard;

    public NativeQACard getQa() {
        return qa;
    }

    public void setQa(NativeQACard qa) {
        this.qa = qa;
    }

    public void setMathSteps(MathSteps mathSteps) {
        this.mathSteps = mathSteps;
    }

    public MathSteps getMathSteps() {
        return this.mathSteps;
    }

    public void setExplainer(Explainer explainer) {
        this.explainer = explainer;
    }

    public Explainer getExplainer() {
        return this.explainer;
    }
  
    public void setDefinitions(Definitions definitions) {
        this.definitions = definitions;
    }

    public Definitions getDefinitions() {
        return this.definitions;
    }

    public void setVideoCard(VideoCard videoCard) {
        this.videoCard = videoCard;
    }

    public VideoCard getVideoCard() {
        return this.videoCard;
    }
}

