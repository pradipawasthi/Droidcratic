package org.socratic.android.adapter;

import android.view.View;

import org.socratic.android.contract.ViewAdapter;

/**
 * Created by williamxu on 9/19/17.
 */

public class MathStepsViewHolder extends BaseViewHolder implements ViewAdapter {

    public MathStepsViewHolder(View itemView) {
        super(itemView);

        //TODO: finish configuring view holders with data binding and dagger
//        viewHolderComponent().inject(this);
        bindContentView(itemView);
    }
}
