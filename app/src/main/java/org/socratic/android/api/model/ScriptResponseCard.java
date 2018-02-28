package org.socratic.android.api.model;

/**
 * @date 2017-02-06
 */
public class ScriptResponseCard {
    private String invocation;
    private String name;
    private String url;

    public String getInvocation() {
        return invocation;
    }

    public void setInvocation(String invocation) {
        this.invocation = invocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}