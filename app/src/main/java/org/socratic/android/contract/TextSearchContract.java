package org.socratic.android.contract;

import org.socratic.android.viewmodel.ViewModelAdapter;

import java.util.List;

/**
 * Created by williamxu on 7/14/17.
 */

public interface TextSearchContract {

    interface View extends ViewAdapter {

        void displayPredictions(List<String> currentPredictions);
    }

    interface ViewModel extends ViewModelAdapter<View> {

        void getPredictions(CharSequence charSequence);

        void getCurrentPredictions();
    }
}
