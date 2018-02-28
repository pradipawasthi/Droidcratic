package org.socratic.android.events;

/**
 * Created by williamxu on 6/22/17.
 */

public class TextSearchEvent {

    public static final int CONTEXT_STARTING = 0;
    public static final int CONTEXT_FINISHED = 1;

    private int context;

    public TextSearchEvent(int context) {
        this.context = context;
    }

    public int getContext() {
        return context;
    }
}
