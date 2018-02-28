package org.socratic.android.dagger.components;

import org.socratic.android.dagger.modules.ViewModelModule;
import org.socratic.android.dagger.modules.ViewModule;
import org.socratic.android.dagger.scopes.PerView;
import org.socratic.android.views.CropWidgetView;
import org.socratic.android.views.InAppMessageView;
import org.socratic.android.views.InputMethodTabsView;
import org.socratic.android.views.InvitationsView;
import org.socratic.android.views.SocraticRatingView;
import org.socratic.android.views.NavTabsView;
import org.socratic.android.views.WebBrowserResultsView;

import dagger.Component;

/**
 * Created by williamxu on 7/13/17.
 */

@PerView
@Component(dependencies = ActivityComponent.class, modules = {ViewModule.class, ViewModelModule.class})
public interface ViewComponent {

    void inject(CropWidgetView cropWidgetView);
    void inject(SocraticRatingView socraticRatingView);
    void inject(WebBrowserResultsView webBrowserResultsView);
    void inject(InAppMessageView inAppMessageView);
    void inject(NavTabsView navTabsView);
    void inject(InvitationsView invitationsView);
    void inject(InputMethodTabsView inputMethodTabsView);
}
