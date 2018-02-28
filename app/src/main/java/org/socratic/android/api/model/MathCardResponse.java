package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.model.math.MathData;

/**
 * Created by byfieldj on 9/1/17.
 */

public class MathCardResponse extends CardResponse {

    @SerializedName("data")
    private MathData data;

    public void setData(MathData data) {
        this.data = data;
    }

    public MathData geData() {
        return this.data;
    }

}
