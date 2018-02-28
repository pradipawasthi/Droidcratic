package org.socratic.android.viewmodel;

import org.socratic.android.dagger.scopes.PerFragment;
import org.socratic.android.globals.BingPredictionsManager;
import org.socratic.android.contract.TextSearchContract;
import org.socratic.android.util.MultiLog;

import javax.inject.Inject;

/**
 * Created by williamxu on 7/14/17.
 */

@PerFragment
public class TextSearchViewModel extends BaseViewModel<TextSearchContract.View> implements TextSearchContract.ViewModel {

    private static final String TAG = TextSearchViewModel.class.getSimpleName();

    BingPredictionsManager bingPredictionsManager;

    @Inject
    public TextSearchViewModel(BingPredictionsManager bingPredictionsManager) {
        this.bingPredictionsManager = bingPredictionsManager;
    }

    @Override
    public void getPredictions(CharSequence charSequence) {
        if (!bingPredictionsManager.isRunning()) {

            MultiLog.d(TAG, "getBingPredictions");

            bingPredictionsManager.getPredictions(charSequence.toString());
        }
    }

    @Override
    public void getCurrentPredictions() {
        getView().displayPredictions(bingPredictionsManager.getCurrentPredictions());
    }
}
