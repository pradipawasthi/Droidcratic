package org.socratic.android.api.model.math;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by byfieldj on 8/30/17.
 */

public class MathSteps {

    @SerializedName("header")
    Header header;

    @SerializedName("steps")
    ArrayList<Step> steps;


    public ArrayList<Step> getSteps(){
        return this.steps;
    }

}
