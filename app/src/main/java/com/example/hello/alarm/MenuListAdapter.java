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

            viewHolder.menuItemImageView = (ImageView) rowView.findViewById(R.id.menu_item_image_view);
            viewHolder.menuItemTextView = (TextView) rowView.findViewById(R.id.menu_item_text_view);

            rowView.setTag(viewHolder);
        }

        MenuItemViewHolder holder = (MenuItemViewHolder) rowView.getTag();

        if (position == MenuActionItem.ITEM1.ordinal()){
            holder.menuItemImageView.setImageDrawable(activity.getDrawable(R.drawable.star));
            holder.menuItemTextView.setText("Points");
        }
        else if (position == MenuActionItem.ITEM2.ordinal()){
            holder.menuItemImageView.setImageDrawable(activity.getDrawable(R.drawable.friends));
            holder.menuItemTextView.setText("Friends");
        }

        return rowView;
    }

    private static class MenuItemViewHolder {
        public ImageView menuItemImageView;
        public TextView menuItemTextView;
    }
}
