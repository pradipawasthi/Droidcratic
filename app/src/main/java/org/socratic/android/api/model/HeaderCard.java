package org.socratic.android.api.model;

/**
 * Created by pcnofelt on 2/22/17.
 */

public class HeaderCard {

    public final static String TYPE_TITLE = "title";
    public final static String TYPE_TITLE_WITH_URL = "title_with_url";
    public final static String TYPE_NAVIGATION_ONLY = "navigation_only";

    private String bg_color;
    private String text_color;
    private String text;
    private String icon;
    private String type;

    private HeaderUrlBarCard url_bar;
    private HeaderNavigationBarCard navigation_bar;

    public String getBgColor() {
        return bg_color;
    }

    public void setBgColor(String bg_color) {
        this.bg_color = bg_color;
    }

    public String getTextColor() {
        return text_color;
    }

    public void setTextColor(String text_color) {
        this.text_color = text_color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HeaderUrlBarCard getUrlBar() {
        return url_bar;
    }

    public void setUrlBar(HeaderUrlBarCard urlBar) {
        this.url_bar = urlBar;
    }

    public HeaderNavigationBarCard getNavigationBar() {
        return navigation_bar;
    }

    public void setNavigationBar(HeaderNavigationBarCard navigation_bar) {
        this.navigation_bar = navigation_bar;
    }
}
