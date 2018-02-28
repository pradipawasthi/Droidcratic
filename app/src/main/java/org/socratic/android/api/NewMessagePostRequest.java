package org.socratic.android.api;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.RequestBody;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class NewMessagePostRequest extends SocraticBaseRequest {
    private int personID;
    private int channelID;
    private String content;
    private String contentType;
    private byte[] imageData;


    public NewMessagePostRequest(int personID, int channelID, String content, String contentType, byte[] imageData) {
        this.personID = personID;
        this.channelID = channelID;
        this.content = content;
        this.contentType = contentType;
        this.imageData = imageData;
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.POST;
    }

    @Override
    public String getPath() {
        return String.format("/channel/%s/message", this.channelID);
    }

    @Override
    public RequestBody buildRequestBody() {
        Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("person_id", String.valueOf(this.personID))
                .addFormDataPart("content", this.content)
                .addFormDataPart("content_type", this.contentType);

        if (this.imageData != null) {
            builder.addFormDataPart("filename", "text-image.jpeg")
                    .addFormDataPart(
                            "image",
                            "text-image.jpeg",
                            RequestBody.create(MediaType.parse("image/jpeg"), imageData));
        }

        RequestBody requestBody = builder.build();

        return requestBody;
    }
}

