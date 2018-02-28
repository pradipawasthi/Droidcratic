package org.socratic.android.viewmodel;

import org.socratic.android.contract.MathCardContract;

import javax.inject.Inject;

/**
 * Created by byfieldj on 8/28/17.
 */

public class MathCardViewModel extends BaseViewModel<MathCardContract.View> implements MathCardContract.ViewModel {

    @Inject
    MathCardViewModel(){

    }
}
