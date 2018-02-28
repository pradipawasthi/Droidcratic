package org.socratic.android.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * @date 2017-02-03
 */
public class OcrImageResponse extends SocraticBaseResponse {

    @SerializedName("image_id")
    private String imageId;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}