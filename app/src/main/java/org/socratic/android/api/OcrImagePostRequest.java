package org.socratic.android.api;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @date 2017-02-03
 */
public class OcrImagePostRequest extends SocraticBaseRequest {

    private byte[] imageData;

    public OcrImagePostRequest(byte[] imageData) {
        this.imageData = imageData;
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.POST;
    }

    @Override
    public String getPath() {
        return "/ocr/image";
    }

    @Override
    public RequestBody buildRequestBody() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("filename", "android-upload.jpeg")
                .addFormDataPart(
                        "image",
                        "android-upload.jpeg",
                        RequestBody.create(MediaType.parse("image/jpeg"), imageData))
                .build();
        return requestBody;
    }
}