package org.socratic.android.api.model.video;

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.model.BodyCard;
import org.socratic.android.api.model.HeaderCard;

import java.util.ArrayList;

/**
 * Created by byfieldj on 9/22/17.
 */

public class VideoCard {


    HeaderCard header;

    BodyCard body;

    @SerializedName("primary_video")
    Video video;

    @SerializedName("related_videos")
    ArrayList<Video> relatedVideos;


    public Video getPrimaryVideo() {
        return this.video;
    }

    public ArrayList<Video> getRelatedVideos() {
        return this.relatedVideos;
    }

}
