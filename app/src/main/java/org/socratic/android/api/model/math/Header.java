package org.socratic.android.api.model.math;

import com.google.gson.annotations.SerializedName;

/**
 * Created by byfieldj on 8/29/17.
 */

public class Header {

    @SerializedName("bg_color")
    String backgroundColor;

    @SerializedName("text_color")
    String text_color;

    @SerializedName("text")
    String text;

    @SerializedName("icon")
    String icon;
}
