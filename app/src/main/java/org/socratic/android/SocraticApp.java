package org.socratic.android;


import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.support.multidex.MultiDex;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import org.greenrobot.eventbus.EventBus;
import org.socratic.android.activities.BaseActivity;
import org.socratic.android.dagger.components.AppComponent;
import org.socratic.android.dagger.components.DaggerAppComponent;
import org.socratic.android.dagger.components.DaggerServiceComponent;
import org.socratic.android.dagger.components.DaggerViewComponent;
import org.socratic.android.dagger.components.DaggerViewHolderComponent;
import org.socratic.android.dagger.components.ServiceComponent;
import org.socratic.android.dagger.components.ViewComponent;
import org.socratic.android.dagger.components.ViewHolderComponent;
import org.socratic.android.dagger.modules.AppModule;
import org.socratic.android.dagger.modules.ServiceModule;
import org.socratic.android.dagger.modules.ViewModule;
import org.socratic.android.util.Util;

import io.fabric.sdk.android.Fabric;
import maximsblog.blogspot.com.jlatexmath.core.AjLatexMath;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * @date 2017-02-02
 */
public class SocraticApp extends Application {

    private static SocraticApp appInstance = null;
    private static AppComponent appComponent = null;
    private static ViewComponent viewComponent = null;
    private static ViewHolderComponent viewHolderComponent = null;
    private static ServiceComponent serviceComponent = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        if (BuildConfig.DEBUG) {
            MultiDex.install(this);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appInstance = this;
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

//        Fabric.with(this, new Crashlytics());

        appComponent.diskStorage().setupDefaults();
        appComponent.installPref().loadFromDisk();

//        Crashlytics.setUserIdentifier(appComponent.installPref().getUserId());

        appComponent.httpManager().initHeaderDefaults();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cerapro-regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        EventBus.builder().throwSubscriberException(BuildConfig.DEBUG).installDefaultEventBus();

        AjLatexMath.init(this);
    }

    public static SocraticApp getAppInstance() { return appInstance; }

    public static AppComponent getAppComponent() { return appComponent; }

    public static ViewComponent getViewComponent(View view, Context context) {
        if(viewComponent == null) {
            viewComponent = DaggerViewComponent.builder()
                    .viewModule(new ViewModule(view))
                    .activityComponent(((BaseActivity) context).activityComponent())
                    .build();
        }

        return viewComponent;
    }

    public static ViewHolderComponent getViewHolderComponent(Context context) {
        if(viewHolderComponent == null) {
            viewHolderComponent = DaggerViewHolderComponent.builder()
                    .activityComponent(Util.castActivityFromContext(context, BaseActivity.class).activityComponent())
                    .build();
        }

        return viewHolderComponent;
    }

    public static ServiceComponent getServiceComponent(Service service) {
        if(serviceComponent == null) {
            serviceComponent = DaggerServiceComponent.builder()
                    .serviceModule(new ServiceModule(service))
                    .appComponent(SocraticApp.getAppComponent())
                    .build();
        }

        return serviceComponent;
    }

    public static Resources getRes() { return appInstance.getResources(); }
}
