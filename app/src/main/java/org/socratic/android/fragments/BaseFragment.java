package org.socratic.android.fragments;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.socratic.android.BR;
import org.socratic.android.contract.ViewAdapter;
import org.socratic.android.activities.BaseActivity;
import org.socratic.android.dagger.components.DaggerFragmentComponent;
import org.socratic.android.dagger.components.FragmentComponent;
import org.socratic.android.dagger.modules.FragmentModule;
import org.socratic.android.viewmodel.NoOpViewModel;
import org.socratic.android.viewmodel.ViewModelAdapter;

import javax.inject.Inject;

/**
 * Base class for Fragments and corresponding ViewModels
 * that provides the binding and view model to child class.
 *
 * Handles lifecycle events for view model.
 *
 * All children classes must call setAndBindContentView() in onCreateView()
 * and inject with the FragmentComponent
 */
public abstract class BaseFragment<B extends ViewDataBinding, V extends ViewModelAdapter> extends Fragment {

    protected B binding;
    @Inject protected V viewModel;

    private FragmentComponent fragmentComponent;

    @Override
    @CallSuper
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(viewModel != null) { viewModel.saveInstanceState(outState); }
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        if(viewModel != null) {
            viewModel.detachView();
        }
        binding = null;
        viewModel = null;
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();

        fragmentComponent = null;
    }


    protected final FragmentComponent fragmentComponent() {
        if(fragmentComponent == null) {
            fragmentComponent = DaggerFragmentComponent.builder()
                    .fragmentModule(new FragmentModule(this))
                    .activityComponent(((BaseActivity) getActivity()).activityComponent())
                    .build();
        }

        return fragmentComponent;
    }

    /* Sets the content view, creates the binding and attaches the view to the view model */
    protected final View setAndBindContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState, @LayoutRes int layoutResID) {
        if(viewModel == null) { throw new IllegalStateException("viewModel not found"); }
        binding = DataBindingUtil.inflate(inflater, layoutResID, container, false);
        binding.setVariable(BR.vm, viewModel);

        try {
            //noinspection unchecked
            viewModel.attachView((ViewAdapter) this, savedInstanceState);
        } catch(ClassCastException e) {
            if (!(viewModel instanceof NoOpViewModel)) {
                throw new RuntimeException(getClass().getSimpleName() + " does not implement ViewAdapter subclass  (" + viewModel.getClass().getSimpleName() + ")");
            }
        }

        return binding.getRoot();
    }
}
