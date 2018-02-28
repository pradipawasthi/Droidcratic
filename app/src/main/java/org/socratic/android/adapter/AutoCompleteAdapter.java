package org.socratic.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.List;

/**
 * Created by williamxu on 6/28/17.
 */

public class AutoCompleteAdapter extends ArrayAdapter<String> {

    private Filter filter = new NoFilter();
    public List<String> predictions;

    public AutoCompleteAdapter(@NonNull Context context, int layout, int textViewResourceId, List<String> predictions) {
        super(context, layout, textViewResourceId, predictions);

        this.predictions = predictions;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    private class NoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults result = new FilterResults();
            result.values = predictions;
            result.count = predictions.size();
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
        }
    }
}
