package org.socratic.android.dagger.modules;

import org.socratic.android.contract.CameraContract;
import org.socratic.android.contract.DefinitionCardContract;
import org.socratic.android.contract.ExplainerCardContract;
import org.socratic.android.contract.InvitationsContract;
import org.socratic.android.contract.MathCardContract;
import org.socratic.android.contract.DefaultPermissionContract;

import org.socratic.android.contract.NativeCardQAContract;
import org.socratic.android.contract.NativeCardVideoContract;
import org.socratic.android.contract.SearchInterstitialContract;
import org.socratic.android.contract.SearchProgressContract;
import org.socratic.android.contract.SplashContract;
import org.socratic.android.contract.TextSearchContract;
import org.socratic.android.contract.WebCardContract;
import org.socratic.android.viewmodel.CameraViewModel;
import org.socratic.android.contract.CropperContract;
import org.socratic.android.viewmodel.CropperViewModel;
import org.socratic.android.contract.ResultsContract;
import org.socratic.android.viewmodel.ExplainerViewModel;
import org.socratic.android.viewmodel.DefinitionCardViewModel;
import org.socratic.android.viewmodel.MathCardViewModel;
import org.socratic.android.viewmodel.DefaultPermissionViewModel;
import org.socratic.android.viewmodel.InvitationsViewModel;

import org.socratic.android.viewmodel.NativeCardQAViewModel;
import org.socratic.android.viewmodel.NativeCardVideoViewModel;
import org.socratic.android.viewmodel.ResultsViewModel;
import org.socratic.android.viewmodel.SearchInterstitialViewModel;
import org.socratic.android.viewmodel.SearchProgressViewModel;
import org.socratic.android.viewmodel.SplashViewModel;
import org.socratic.android.viewmodel.TextSearchViewModel;
import org.socratic.android.viewmodel.WebCardViewModel;

import dagger.Binds;
import dagger.Module;

/**
 * Created by williamxu on 7/12/17.
 * <p>
 * Dagger Module for access to the view model classes through
 * binding the view model interfaces that they implement;
 * <p>
 * This saves boiler plate code otherwise generated from @Provide
 * <p>
 * For more info: https://android.jlelse.eu/inject-interfaces-without-providing-in-dagger-2-618cce9b1e29
 */

@Module
public abstract class ViewModelModule {

    //Activities
    @Binds
    abstract CameraContract.ViewModel bindCameraViewModel(CameraViewModel cameraViewModel);

    @Binds
    abstract CropperContract.ViewModel bindCropperViewModel(CropperViewModel cropperViewModel);

    @Binds
    abstract SearchProgressContract.ViewModel bindSearchProgressViewModel(SearchProgressViewModel searchProgressViewModel);

    @Binds
    abstract SearchInterstitialContract.ViewModel bindSearchInterstitialViewModel(SearchInterstitialViewModel searchInterstitialViewModel);

    @Binds
    abstract ResultsContract.ViewModel bindResultsViewModel(ResultsViewModel resultsViewModel);

    @Binds
    abstract SplashContract.ViewModel bindSplashViewModel(SplashViewModel splashViewModel);

    @Binds
    abstract DefaultPermissionContract.ViewModel bindDefaultPermissionsViewModel(DefaultPermissionViewModel defaultPermissionViewModel);

    @Binds
    abstract InvitationsContract.ViewModel bindInvitationsViewModel(InvitationsViewModel invitationsViewModel);

    //Fragments
    @Binds
    abstract TextSearchContract.ViewModel bindTextSearchViewModel(TextSearchViewModel textSearchViewModel);

    @Binds
    abstract NativeCardQAContract.ViewModel bindNativeCardQAViewModel(NativeCardQAViewModel nativeCardQAViewModel);

    @Binds
    abstract WebCardContract.ViewModel bindWebCardViewModel(WebCardViewModel webCardViewModel);

    @Binds
    abstract MathCardContract.ViewModel bindMathCardViewModel(MathCardViewModel mathCardViewModel);

    @Binds
    abstract ExplainerCardContract.ViewModel bindsExplainerViewModel(ExplainerViewModel explainerViewModel);

    @Binds
    abstract DefinitionCardContract.ViewModel bindDefinitionCardViewModel(DefinitionCardViewModel definitionCardViewModel);

    @Binds
    abstract NativeCardVideoContract.ViewModel bindNativeCardVideoViewModel(NativeCardVideoViewModel videoCardViewModel);
}
