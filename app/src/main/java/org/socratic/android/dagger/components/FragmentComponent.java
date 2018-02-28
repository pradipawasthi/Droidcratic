package org.socratic.android.dagger.components;

import org.socratic.android.dagger.modules.FragmentModule;
import org.socratic.android.dagger.modules.ViewModelModule;
import org.socratic.android.dagger.scopes.PerFragment;
import org.socratic.android.fragments.ExplainerCardFragment;
import org.socratic.android.fragments.MathCardFragment;
import org.socratic.android.fragments.ChatFragment;
import org.socratic.android.fragments.GroupsFragment;
import org.socratic.android.fragments.NativeCardQAFragment;
import org.socratic.android.fragments.NativeDefintionCardFragment;
import org.socratic.android.fragments.NativeCardVideoFragment;
import org.socratic.android.fragments.TextSearchFragment;
import org.socratic.android.fragments.WebCardFragment;

import dagger.Component;

/**
 * Created by williamxu on 7/11/17.
 */

@PerFragment
@Component(dependencies = ActivityComponent.class, modules = {FragmentModule.class, ViewModelModule.class})
public interface FragmentComponent {

    void inject(NativeCardQAFragment fragment);
    void inject(TextSearchFragment fragment);
    void inject(WebCardFragment fragment);
    void inject(MathCardFragment fragment);
    void inject(ChatFragment fragment);
    void inject(GroupsFragment fragment);
    void inject(ExplainerCardFragment fragment);
    void inject(NativeDefintionCardFragment fragment);
    void inject(NativeCardVideoFragment fragment);
}
