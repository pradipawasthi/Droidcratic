package org.socratic.android.events;

public class OcrSearchEvent {

    public static final int CONTEXT_STARTING = 0;
    public static final int CONTEXT_FINISHED = 1;

    private int context;

    public OcrSearchEvent(int context) {
        this.context = context;
    }

    public int getContext() {
        return context;
    }
}