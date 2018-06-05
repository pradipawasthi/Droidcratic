package org.socratic.android.dagger.components;

import dagger.Component;

import org.socratic.android.dagger.modules.ServiceModule;
import org.socratic.android.dagger.scopes.PerService;

/**
 * Created by jessicaweinberg on 10/10/17.
 */

@PerService
@Component(dependencies = AppComponent.class, modules = {ServiceModule.class})
public interface ServiceComponent {
}
