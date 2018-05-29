package com.example.hello.alarm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class StoreFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mStaggeredGridLayoutManager;
    private ArrayList<StoreItem> storeList;

    private static String superLongPhotoString = "data:image/png;base64,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";

    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize store list
        storeList = new ArrayList<>();
        storeList.add(new StoreItem(superLongPhotoString, "Title 1", "description", 100));
        storeList.add(new StoreItem(superLongPhotoString, "Title 1", "description", 100));
        storeList.add(new StoreItem(superLongPhotoString, "Title 1", "description", 100));
        storeList.add(new StoreItem(superLongPhotoString, "Title 1", "description", 100));
        storeList.add(new StoreItem(superLongPhotoString, "Title 1", "description", 100));
        storeList.add(new StoreItem(superLongPhotoString, "Title 1", "description", 100));
        storeList.add(new StoreItem(superLongPhotoString, "Title 1", "description", 100));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.store_fragment_layout, container, false);

        //Prepare and set up recycler view for store items
        mRecyclerView = rootView.findViewById(R.id.items_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a layout manager
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new StoreItemsAdapter(storeList);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }



    public class StoreItemsAdapter extends RecyclerView.Adapter<StoreItemsAdapter.ViewHolder> {
        private ArrayList<StoreItem> storeList;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public ImageView itemImage;
            public TextView itemTitle;
            public TextView itemPrice;
            public TextView itemDescription;
            public Button itemPurchaseButton;

            public ViewHolder(LinearLayout parentView) {
                super(parentView);
                this.itemImage = parentView.findViewById(R.id.itemImage);
                this.itemTitle = parentView.findViewById(R.id.itemTitle);
                this.itemPrice = parentView.findViewById(R.id.itemPrice);
                this.itemDescription = parentView.findViewById(R.id.itemDescription);
                this.itemPurchaseButton = parentView.findViewById(R.id.purchaseButton);
            }
        }

        // Provide a suitable constructor
        public StoreItemsAdapter(ArrayList<StoreItem> storeList) {
            this.storeList = storeList;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public StoreItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.store_item, parent, false);

            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Glide.with(StoreFragment.this)
                    .load(storeList.get(position).imageUriString)
                    .into(holder.itemImage);

            holder.itemTitle.setText(storeList.get(position).title);
            holder.itemPrice.setText(String.valueOf(storeList.get(position).price));
            holder.itemDescription.setText(storeList.get(position).description);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return storeList.size();
        }
    }
}
