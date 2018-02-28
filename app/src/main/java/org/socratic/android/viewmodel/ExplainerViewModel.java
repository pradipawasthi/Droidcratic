package org.socratic.android.viewmodel;

import org.socratic.android.contract.ExplainerCardContract;

import javax.inject.Inject;

/**
 * Created by byfieldj on 10/6/17.
 */

public class ExplainerViewModel extends BaseViewModel<ExplainerCardContract.View> implements ExplainerCardContract.ViewModel {

    @Inject
    ExplainerViewModel(){

    }
}

