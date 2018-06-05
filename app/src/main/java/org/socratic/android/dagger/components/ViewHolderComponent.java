package org.socratic.android.dagger.components;

import org.socratic.android.dagger.modules.ViewHolderModule;
import org.socratic.android.dagger.modules.ViewModelModule;
import org.socratic.android.dagger.scopes.PerViewHolder;
import org.socratic.android.adapter.MathStepAdapter;

import dagger.Component;

/**
 * Created by williamxu on 9/18/17.
 */

@PerViewHolder
@Component(dependencies = ActivityComponent.class, modules = {ViewHolderModule.class, ViewModelModule.class})
public interface ViewHolderComponent {
    void inject(MathStepAdapter mathStepAdapter);
}
