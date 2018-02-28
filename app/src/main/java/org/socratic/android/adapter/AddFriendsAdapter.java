package org.socratic.android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.socratic.android.R;

import java.util.ArrayList;

/**
 * Created by jessicaweinberg on 9/27/17.
 */

public class AddFriendsAdapter extends ArrayAdapter<String> {
    public AddFriendsAdapter(Context context, ArrayList<String> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_add_friend, parent, false);
        }

        // Get the data item for this position
        String user = getItem(position);
        Log.d("add friends", user);

        // Lookup view for data population
        TextView addFriendName = (TextView) convertView.findViewById(R.id.addFriendName);
        // Populate the data into the template view using the data object
        addFriendName.setTextColor(getContext().getResources().getColor(R.color.confetti_blue));

        addFriendName.setText(user);

        // Return the completed view to render on screen
        return convertView;
    }
}
