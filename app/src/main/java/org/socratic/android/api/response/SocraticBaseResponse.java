package org.socratic.android.api.response;

import org.socratic.android.api.model.ApiError;

import java.util.List;

/**
 * Created by pcnofelt on 3/14/17.
 */

public class SocraticBaseResponse extends BaseResponse {

    private List<ApiError> errors;

    public List<ApiError> getErrors() {
        return errors;
    }

    public void setErrors(List<ApiError> errors) {
        this.errors = errors;
    }

}
