package org.socratic.android.api.model;

/**
 * Created by pcnofelt on 2/22/17.
 */

public abstract class HeaderBarBaseCard {

    private String bg_color;
    private String text;
    private String text_color;

    public String getBgColor() {
        return bg_color;
    }

    public void setBgColor(String bg_color) {
        this.bg_color = bg_color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextColor() {
        return text_color;
    }

    public void setTextColor(String text_color) {
        this.text_color = text_color;
    }
}
