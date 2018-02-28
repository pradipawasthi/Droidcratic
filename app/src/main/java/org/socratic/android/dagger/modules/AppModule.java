package org.socratic.android.dagger.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import org.socratic.android.dagger.qualifiers.AppContext;
import com.google.gson.Gson;

import org.socratic.android.dagger.qualifiers.Storage;
import org.socratic.android.dagger.scopes.PerApplication;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.globals.HttpManager;
import org.socratic.android.globals.PhotoManager;
import org.socratic.android.storage.DiskStorage;
import org.socratic.android.storage.InstallPref;

import dagger.Module;
import dagger.Provides;

/**
 * Created by williamxu on 7/11/17.
 */

@Module
public class AppModule {

    private final Application application;
    private final String filenameGeneral = "5b259fba92dd4132820df880a9ff2b2f";

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @PerApplication
    @AppContext
    Context provideAppContext() {
        return application;
    }

    @Provides
    @PerApplication
    Resources provideResources() {
        return application.getResources();
    }

    @Provides
    @PerApplication
    DiskStorage provideDiskStorage(@Storage SharedPreferences diskPreferences) {
        return new DiskStorage(diskPreferences);
    }

    @Provides
    @PerApplication
    SharedPreferences provideSharedPreferences() { return PreferenceManager.getDefaultSharedPreferences(application); }

    @Provides
    @PerApplication
    AssetManager provideAssetManager() { return application.getAssets(); }

    @Provides
    @PerApplication
    InstallPref provideInstallPref(DiskStorage diskStorage, Gson gson) { return new InstallPref(diskStorage, gson); }

    @Provides
    @PerApplication
    AnalyticsManager provideAnalyticsManager(InstallPref installPref, DiskStorage diskStorage, SharedPreferences sharedPreferences) { return new AnalyticsManager(installPref, diskStorage, sharedPreferences); }

    @Provides
    @PerApplication
    PhotoManager providePhotoManager(AnalyticsManager analyticsManager, HttpManager httpManager) {return new PhotoManager(analyticsManager, httpManager); }

    @Provides
    @PerApplication
    @Storage
    SharedPreferences provideStoragePreferences() {return application.getSharedPreferences(filenameGeneral, Context.MODE_PRIVATE);}
}
