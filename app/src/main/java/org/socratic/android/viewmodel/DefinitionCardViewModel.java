package org.socratic.android.viewmodel;

import org.socratic.android.contract.DefinitionCardContract;

import javax.inject.Inject;

/**
 * Created by byfieldj on 10/3/17.
 */

public class DefinitionCardViewModel extends BaseViewModel<DefinitionCardContract.View> implements DefinitionCardContract.ViewModel {

    @Inject
    DefinitionCardViewModel(){

    }
}
