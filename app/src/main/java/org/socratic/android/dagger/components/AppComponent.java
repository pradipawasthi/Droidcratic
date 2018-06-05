package org.socratic.android.dagger.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;

import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.dagger.modules.AppModule;
import org.socratic.android.dagger.modules.NetworkModule;
import org.socratic.android.dagger.qualifiers.AppContext;
import com.google.gson.Gson;
import org.socratic.android.dagger.qualifiers.Storage;
import org.socratic.android.dagger.scopes.PerApplication;
import org.socratic.android.globals.BingPredictionsManager;
import org.socratic.android.globals.HttpManager;
import org.socratic.android.globals.InAppMessageManager;
import org.socratic.android.globals.InitManager;
import org.socratic.android.globals.OcrSearchManager;
import org.socratic.android.globals.PhotoManager;
import org.socratic.android.globals.TextSearchManager;
import org.socratic.android.storage.DiskStorage;
import org.socratic.android.storage.InstallPref;

import dagger.Component;

/**
 * Created by williamxu on 7/10/17.
 */

@PerApplication
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {

    @AppContext Context appContext();
    Resources resources();
    DiskStorage diskStorage();
    SharedPreferences sharedPreferences();
    AssetManager assetManager();
    InstallPref installPref();
    AnalyticsManager analyticsManager();
    PhotoManager photoManager();
    HttpManager httpManager();
    OcrSearchManager ocrSearchManager();
    TextSearchManager textSearchManager();
    BingPredictionsManager bingPredictionsManager();
    InitManager initManager();
    InAppMessageManager inAppMessageManager();
    Gson gson();
    @Storage SharedPreferences diskPreferences();
}
