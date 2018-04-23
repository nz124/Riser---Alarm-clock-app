package com.example.hello.alarm;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MasterFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master, container);

        setListAdapter(new MenuListAdapter(R.layout.row_menu_action_item, getActivity(), MenuActionItem.values()));

        return view;
    }
}
