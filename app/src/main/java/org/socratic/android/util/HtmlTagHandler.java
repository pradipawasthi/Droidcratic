package org.socratic.android.util;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

/**
 * Created by byfieldj on 10/6/17.
 */

public class HtmlTagHandler implements Html.TagHandler {

    private static final String placeholder = "---";

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.equalsIgnoreCase("hr")) {
            handleHrTag(opening, output);
        }
    }

    private void handleHrTag(boolean opening, Editable output) {
        if (opening) {
            output.insert(output.length(), placeholder);
        } else {
            output.setSpan(new HrSpan(), output.length() - placeholder.length(), output.length(), 0);
        }
    }
}