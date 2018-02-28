package org.socratic.android.dagger.components;

import android.content.Context;

import org.socratic.android.activities.ChatDetailActivity;
import org.socratic.android.activities.CameraActivity;
import org.socratic.android.activities.CropperActivity;
import org.socratic.android.activities.DefaultPermissionActivity;
import org.socratic.android.activities.IdentityActivity;
import org.socratic.android.activities.InAppMessageWebViewActivity;
import org.socratic.android.activities.ResultsActivity;
import org.socratic.android.activities.SearchInterstitialActivity;
import org.socratic.android.activities.SearchProgressActivity;
import org.socratic.android.activities.SettingsActivity;
import org.socratic.android.activities.SplashActivity;
import org.socratic.android.activities.SplashPermissionsActivity;
import org.socratic.android.activities.StartActivity;
import org.socratic.android.dagger.modules.ActivityModule;
import org.socratic.android.dagger.modules.ViewModelModule;
import org.socratic.android.dagger.qualifiers.ActivityContext;
import org.socratic.android.dagger.scopes.PerActivity;

import dagger.Component;

/**
 * Created by williamxu on 7/11/17.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = {ActivityModule.class, ViewModelModule.class})
public interface ActivityComponent extends AppComponent {

    @ActivityContext Context activityContext();

    void inject(CameraActivity activity);
    void inject(CropperActivity activity);
    void inject(ResultsActivity activity);
    void inject(SearchInterstitialActivity activity);
    void inject(SearchProgressActivity activity);
    void inject(SplashActivity activity);
    void inject(SplashPermissionsActivity activity);
    void inject(StartActivity activity);
    void inject(IdentityActivity activity);
    void inject(InAppMessageWebViewActivity activity);
    void inject(DefaultPermissionActivity activity);
    void inject(ChatDetailActivity activity);
    void inject(SettingsActivity activity);
}
