package org.socratic.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.socratic.android.R;
import org.socratic.android.contract.GroupsContract;
import org.socratic.android.databinding.FragmentGroupsBinding;


public class GroupsFragment extends BaseFragment<FragmentGroupsBinding, GroupsContract.ViewModel>
    implements GroupsContract.View {

    private static final String TAG = GroupsFragment.class.getSimpleName();

    public GroupsFragment() {
        // Required empty public constructor
    }

    public static GroupsFragment newInstance() {
        GroupsFragment fragment = new GroupsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_groups);

        return rootView;
    }
}
