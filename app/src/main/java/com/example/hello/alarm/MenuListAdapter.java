package com.example.hello.alarm;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuListAdapter extends ArrayAdapter<MenuActionItem> {
    int resource;
    Activity activity;


    public MenuListAdapter(int resource, Activity activity, MenuActionItem[] items){
        super(activity, resource, items);
        this.resource = resource;
        this.activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View rowView = convertView;

        if (rowView == null){
            rowView = activity.getLayoutInflater().inflate(resource, null);

            MenuItemViewHolder viewHolder = new MenuItemViewHolder();

            viewHolder.menuItemTextView = (TextView) rowView.findViewById(R.id.menu_item_text_view);
            viewHolder.menuItemDivider = (View) rowView.findViewById(R.id.divider);
            rowView.setTag(viewHolder);
        }

        MenuItemViewHolder holder = (MenuItemViewHolder) rowView.getTag();

        if (position == MenuActionItem.ITEM1.ordinal()){
            holder.menuItemTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(activity.getDrawable(R.drawable.resized_user), null, null, null);
            holder.menuItemTextView.setText("User");
            holder.menuItemDivider.setVisibility(View.VISIBLE);
        }
        else if (position == MenuActionItem.ITEM2.ordinal()){
            holder.menuItemTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(activity.getDrawable(R.drawable.resized_star),null, null, null);
            holder.menuItemTextView.setText("Point");
        }
        else if (position == MenuActionItem.ITEM3.ordinal()){
            holder.menuItemTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(activity.getDrawable(R.drawable.resized_friends),null, null, null);
            holder.menuItemTextView.setText("Friends");
        }

        return rowView;
    }

    private static class MenuItemViewHolder {
        TextView menuItemTextView;
        View menuItemDivider;

    }
}
