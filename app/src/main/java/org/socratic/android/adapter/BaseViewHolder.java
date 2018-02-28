package org.socratic.android.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import org.socratic.android.activities.BaseActivity;
import org.socratic.android.contract.ViewAdapter;
import org.socratic.android.dagger.components.DaggerViewHolderComponent;
import org.socratic.android.dagger.components.ViewHolderComponent;
import org.socratic.android.util.Util;
import org.socratic.android.viewmodel.NoOpViewModel;
import org.socratic.android.viewmodel.ViewModelAdapter;

import javax.inject.Inject;

/** Base class for ViewHolders that provides the binding and the view model to the subclass
 *
 * All children classes must call viewHolderComponent().inject(this);
 * and bindContentView(view); in the constructor.
 *
 */
public abstract class BaseViewHolder<B extends ViewDataBinding, V extends ViewModelAdapter> extends RecyclerView.ViewHolder {

    protected B binding;
    @Inject protected V viewModel;

    protected final View itemView;

    private ViewHolderComponent viewHolderComponent;

    public BaseViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    protected final ViewHolderComponent viewHolderComponent() {
        if(viewHolderComponent == null) {
            viewHolderComponent = DaggerViewHolderComponent.builder()
                    .activityComponent(Util.castActivityFromContext(itemView.getContext(), BaseActivity.class).activityComponent())
                    .build();
        }

        return viewHolderComponent;
    }

    protected final void bindContentView(@NonNull View view) {
        if(viewModel == null) { throw new IllegalStateException("viewModel must not be null and should be injected via viewHolderComponent().inject(this)"); }
        binding = DataBindingUtil.bind(view);
        binding.setVariable(BR.vm, viewModel);

        try {
            //noinspection unchecked
            viewModel.attachView((ViewAdapter) this, null);
        } catch(ClassCastException e) {
            if (!(viewModel instanceof NoOpViewModel)) {
                throw new RuntimeException(getClass().getSimpleName() + " must implement ViewAdapter subclass");
            }
        }
    }

    public final V viewModel() {
        return viewModel;
    }

    public final void executePendingBindings() {
        if(binding != null) { binding.executePendingBindings(); }
    }
}
