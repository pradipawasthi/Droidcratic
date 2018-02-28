package org.socratic.android.dagger.modules;

import android.app.Service;

import dagger.Module;

/**
 * Created by jessicaweinberg on 10/10/17.
 */

@Module
public class ServiceModule {

    private final Service service;

    public ServiceModule(Service service) {
        this.service = service;
    }

}
