package com.example.hello.alarm;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendListFragment extends Fragment {

    AccessToken currentUserToken;
    String currentUserId;
    String currentUserTokenString;
    DatabaseReference myRef;
    ValueEventListener eventListener;

    RelativeLayout parentView;

    public FriendListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserToken = AccessToken.getCurrentAccessToken();
        if (currentUserToken != null){
            currentUserId = currentUserToken.getUserId();
        currentUserTokenString = currentUserToken.getToken();
        }

        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.friend_list_fragment_layout, container, false);
        parentView = root.findViewById(R.id.parentView);

        RadioButton showFriendButton = root.findViewById(R.id.show_friend_button);
        showFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentView.removeAllViews();
                showFriendList();
            }
        });

        RadioButton showChallengesButton = root.findViewById(R.id.show_challenge_history_button);
        showChallengesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentView.removeAllViews();
            }
        });



        return root;
    }

    public void showFriendList(){
        //Prepare and set up recycler view for store items
        final RecyclerView recyclerListView = new RecyclerView(getContext());
        recyclerListView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.MATCH_PARENT));
        recyclerListView.setHasFixedSize(true);

        // use a layout manager
        StaggeredGridLayoutManager mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerListView.setLayoutManager(mStaggeredGridLayoutManager);


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> mFriendList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User friend = snapshot.getValue(User.class);
                    mFriendList.add(friend);
                }
                // specify an adapter (see also next example)
                FriendAdapter mAdapter = new FriendAdapter(mFriendList);
                recyclerListView.setAdapter(mAdapter);
                parentView.addView(recyclerListView);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

    }
    public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
        private ArrayList<User> friendList;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView userPhoto;
            public TextView userName;
            public TextView userPoint;
            public Button challengeButton;

            public ViewHolder(LinearLayout parentView) {
                super(parentView);
                this.userPhoto = parentView.findViewById(R.id.friend_image);
                this.userName = parentView.findViewById(R.id.friend_name);
                this.userPoint= parentView.findViewById(R.id.friend_point);
                this.challengeButton = parentView.findViewById(R.id.challenge_button);
            }
        }

        // Provide a suitable constructor
        public FriendAdapter(ArrayList<User> friendList) {
            this.friendList = friendList;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                             int viewType) {
            // create a new view
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_list_item, parent, false);



            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Glide.with(FriendListFragment.this)
                    .load(friendList.get(position).photoUriString)
                    .into(holder.userPhoto);

            holder.userName.setText(friendList.get(position).name);
            holder.userPoint.setText("Point: "+String.valueOf(friendList.get(position).point));

            holder.challengeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPointPicker(getContext());
                    Toast.makeText(getContext(), friendList.get(position).name, Toast.LENGTH_SHORT).show();
                }
            });

//            //Check if item is owned
//            if (storeList.get(position).isOwned){
//                holder.itemPurchaseButton.setText("Activated");
//                holder.itemPurchaseButton.setEnabled(false);
//            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return friendList.size();
        }
    }

    public void showPointPicker(Context context) {

        final Dialog d = new Dialog(context);
        d.setTitle("How many point do you want to use for this challenge?");
        d.setContentView(R.layout.point_picker_dialog);
        Button challengeButton = d.findViewById(R.id.confirm_button);
        Button cancelButton = d.findViewById(R.id.cancel_button);
        final TextView pointDisplay = d.findViewById(R.id.pointTextView);
        final NumberPicker np = d.findViewById(R.id.number_picker);

        np.setMaxValue(100); // max value 100
        np.setMinValue(0);   // min value 0
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                picker.setValue( (newVal < oldVal) ? oldVal-5 : oldVal+5);
            }
        });
        challengeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                pointDisplay.setText(String.valueOf(np.getValue())); //set the value to textview
                d.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });
        d.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}

