package org.socratic.android.api;

/**
 * @date 2017-02-03
 */
public class OcrTextGetRequest extends SocraticBaseRequest {

    public static final String NATIVE_CARD_TYPE_QA = "qa";
    public static final String NATIVE_CARD_TYPE_MATH_STEPS = "math-steps";
    public static final String NATIVE_CARD_TYPE_EXPLAINER = "explainer";
    public static final String NATIVE_CARD_TYPE_DEFINITIONS = "definitions";
    public static final String NATIVE_CARD_TYPE_VIDEO = "video";
    public static final String NATIVE_CARD_TYPE_QA_SOCRATIC = "qa-socratic";

    public OcrTextGetRequest(String imageId) {
        putParam("image_id", imageId);
    }

    public OcrTextGetRequest(String imageId,
                             int cropX0,
                             int cropY0,
                             int cropX1,
                             int cropY1,
                             String... nativeCardTypes) {
        StringBuilder crop = new StringBuilder(128);
        crop.append(cropX0).append(",");
        crop.append(cropY0).append(",");
        crop.append(cropX1).append(",");
        crop.append(cropY1);

        putParam("image_id", imageId);
        putParam("crop", crop.toString());
        putParam("include_search", "1"); // always include results.

        if (nativeCardTypes != null && nativeCardTypes.length > 0) {
            StringBuilder nativeCardTypesFormatted = new StringBuilder();
            for (int i = 0; i < nativeCardTypes.length; i++) {
                nativeCardTypesFormatted.append(nativeCardTypes[i]);
                if (i < nativeCardTypes.length - 1) {
                    nativeCardTypesFormatted.append(",");
                }
            }
            putParam("request_native", nativeCardTypesFormatted.toString());
        }
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.GET;
    }

    @Override
    public String getPath() {
        return "/ocr/text";
    }
}