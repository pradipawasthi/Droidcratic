package org.socratic.android.viewmodel;

import android.content.Context;

import org.socratic.android.contract.SearchInterstitialContract;
import org.socratic.android.dagger.qualifiers.AppContext;
import org.socratic.android.dagger.scopes.PerActivity;

import javax.inject.Inject;

/**
 * Created by williamxu on 7/13/17.
 */

@PerActivity
public class SearchInterstitialViewModel extends BaseViewModel<SearchInterstitialContract.View> implements SearchInterstitialContract.ViewModel {

    @Inject
    public SearchInterstitialViewModel(@AppContext Context context) {

    }
}
