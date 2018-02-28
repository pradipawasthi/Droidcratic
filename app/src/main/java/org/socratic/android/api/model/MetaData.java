package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by williamxu on 6/8/17.
 */

public class MetaData {

    @SerializedName("classification_course")
    public String classification_course;

    @SerializedName("classification_topic")
    public String classification_topic;

    @SerializedName("triggered_topic")
    public boolean triggered_topic;

    @SerializedName("is_math")
    public boolean is_math;

    @SerializedName("has_explainer")
    public boolean has_explainer;

    @SerializedName("has_video")
    public boolean has_video;

    @SerializedName("web_search_id")
    public long web_search_id;

    @SerializedName("image_id")
    public String image_id;

    @SerializedName("classification_confidence")
    public double classification_confidence;

    @SerializedName("has_qa")
    public boolean has_qa;

    @SerializedName("max_bing_ranking_score")
    public double max_bing_ranking_score;

    @SerializedName("max_qa_ranking_score")
    public double max_qa_ranking_score;
}
