package org.socratic.android.dagger.components;

import dagger.Component;

import org.socratic.android.dagger.modules.ServiceModule;
import org.socratic.android.dagger.scopes.PerService;
import org.socratic.android.services.ChatMessagingService;
import org.socratic.android.services.FirebaseIDService;

/**
 * Created by jessicaweinberg on 10/10/17.
 */

@PerService
@Component(dependencies = AppComponent.class, modules = {ServiceModule.class})
public interface ServiceComponent {
    void inject(FirebaseIDService firebaseIDService);
    void inject(ChatMessagingService chatMessagingService);
}
