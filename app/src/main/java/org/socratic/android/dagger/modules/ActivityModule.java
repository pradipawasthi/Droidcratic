package org.socratic.android.dagger.modules;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.socratic.android.dagger.qualifiers.ActivityContext;
import org.socratic.android.dagger.scopes.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by williamxu on 7/12/17.
 */

@Module
public class ActivityModule {

    private final AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    @ActivityContext
    Context provideActivityContext() { return activity; }
}
