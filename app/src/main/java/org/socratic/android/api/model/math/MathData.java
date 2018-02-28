package org.socratic.android.api.model.math;

import com.google.gson.annotations.SerializedName;

/**
 * Created by byfieldj on 8/30/17.
 */

public class MathData {

    @SerializedName("math-steps")
    private MathSteps mathSteps;


    public void setMathSteps(MathSteps mathSteps){
        this.mathSteps = mathSteps;
    }

    public MathSteps getMathSteps(){
        return this.mathSteps;
    }

}
