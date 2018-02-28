package org.socratic.android.events;

/**
 * Created by byfieldj on 8/30/17.
 */

public class OcrNativeSearchEvent {

    public static final int CONTEXT_STARTING = 0;
    public static final int CONTEXT_FINISHED = 1;

    private int context;

    public OcrNativeSearchEvent(int context) {

        this.context = context;
    }

    public int getContext() {
        return this.context;
    }
}
