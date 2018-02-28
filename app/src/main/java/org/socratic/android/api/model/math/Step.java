package org.socratic.android.api.model.math;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by byfieldj on 8/29/17.
 */

public class Step {


    @SerializedName("math")
    String math;

    @SerializedName("change")
    String change;

    @SerializedName("changeType")
    String changeType;

    @SerializedName("type")
    String type;

    @SerializedName(("substeps"))
    ArrayList<Step> substeps;

    @SerializedName("image_url")
    String imageUrl;

    @SerializedName("number")
    String stepNumber;

    @SerializedName("text")
    String lightbulbText;

    @SerializedName("explanation")
    String explanation;

    public void setMath(String math) {
        this.math = math;
    }

    public String getMath() {
        return this.math;
    }

    public boolean hasSubSteps() {

        if (substeps == null) {
            return false;
        } else {
            if (!substeps.isEmpty() && substeps.size() > 0) {
                return true;
            }
        }

        return false;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Step> getSubsteps() {
        return substeps;
    }

    public void setSubsteps(ArrayList<Step> substeps) {
        this.substeps = substeps;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(String stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getLightbulbText() {
        return lightbulbText;
    }

    public void setLightbulbText(String lightbulbText) {
        this.lightbulbText = lightbulbText;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
