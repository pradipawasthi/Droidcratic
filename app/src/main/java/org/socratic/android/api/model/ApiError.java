package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * @date 2017-02-06
 */
public class ApiError {

    private int code;

    private String title;

    @SerializedName("interface_title")
    private String interfaceTitle;

    @SerializedName("primary_action")
    private String primaryAction;

    private int action;

    private String message;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInterfaceTitle() {
        return interfaceTitle;
    }

    public void setInterfaceTitle(String interfaceTitle) {
        this.interfaceTitle = interfaceTitle;
    }

    public String getPrimaryAction() {
        return primaryAction;
    }

    public void setPrimaryAction(String primaryAction) {
        this.primaryAction = primaryAction;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}