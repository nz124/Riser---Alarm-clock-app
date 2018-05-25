package com.example.hello.alarm;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import me.relex.circleindicator.CircleIndicator;


public class FragmentPagerSupport extends AppCompatActivity {
    static final int NUM_ITEMS = 2;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    static FirebaseDatabase database;
    static DatabaseReference myRef;
    Integer current_point;
    NavigationView navigationView;
    FirebaseUser currentUser;
    String user_name, user_email, user_id, user_photoUrlString;
    Uri user_photoUrl;
    //Select information in the nav's header
    View header_view;
    TextView nav_point;
    TextView nav_name;
    ImageView nav_photo;
    Context context;
    Uri defaultUri;
    Uri mPhotoUri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        defaultUri = Uri.parse("data:image/png;base64,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");

        this.context = this;
        navigationView = findViewById(R.id.nav_view);
        header_view = navigationView.getHeaderView(0);
        nav_point = header_view.findViewById(R.id.nav_point);
        nav_photo = header_view.findViewById(R.id.nav_profile_image);
        nav_name = header_view.findViewById(R.id.nav_display_name);

        //Access database and reference to the data of the current's user
        database = FirebaseDatabase.getInstance();

        //Get information from current user, if there is one.
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            mAuth.signInAnonymously();
            currentUser = mAuth.getCurrentUser();
        }
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update user information
                            currentUser = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Error", "signInAnonymously:failure", task.getException());
                            Toast.makeText(FragmentPagerSupport.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
        Log.e("info", "onCreate: "+ currentUser.getDisplayName() );

        user_name = (currentUser.getDisplayName().equals("")) ? "Guest": currentUser.getDisplayName();
        user_email = (currentUser.getEmail() == null) ? "No Email Provided": currentUser.getEmail();
        user_photoUrl = (currentUser.getPhotoUrl() == null) ? defaultUri: currentUser.getPhotoUrl();
        user_id = currentUser.getUid();
        //Update user's profile
        writeUserData(user_name, user_photoUrl);

        //Listen for changes from database and update UI
        updateUiWithDbData(user_id);


        //Determine to increment or decrement point based on the extras being passed in
        String action_type = getIntent().getStringExtra("type");
        String notification = "";
        if (action_type != null) {
            if (action_type.equals("turn_off")) {
                incrementPointAndSaveToDb(true, 100);
                notification = "You gained 100 points";
            } else {
                incrementPointAndSaveToDb(false, 100);
                notification = "You lost 100 points";
            }
            ;
            Toast.makeText(this, notification,
                    Toast.LENGTH_LONG).show();
        }



        //Configure action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = findViewById(R.id.drawer_layout);


        //Handle navigation click events
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        if (menuItem.getItemId() == R.id.nav_sign_out){
                            mAuth.signOut();
                        }
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        //Prepare viewpager and add circle indicator for view pager
        ViewPager viewPager = findViewById(R.id.viewpager);

        CircleIndicator indicator = findViewById(R.id.indicator);
        setupViewPager(viewPager);
        indicator.setViewPager(viewPager);


    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AlarmList(), "ONE");
        adapter.addFragment(new OneFragment(), "TWO");
        adapter.addFragment(new TwoFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.e("hey", "DUYy");
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_profile:
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void updateUiWithDbData(final String user_id) {
        // Read from the database
        database.getReference().child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                mPhotoUri = Uri.parse(dataSnapshot.child("Photo").getValue().toString());
                current_point = dataSnapshot.child("Point").getValue(Integer.class);
//                Glide.with(context)
//                        .load(mPhotoUri)
//                        .into(nav_photo);
                nav_name.setText(dataSnapshot.child("Name").getValue().toString());
                if (current_point != null){
                    String point_display = String.valueOf(current_point);
                    //Update point if there are changes
                    nav_point.setText("Current point: " + point_display);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });
    }


    public void incrementPointAndSaveToDb(final boolean increment, final Integer point){
        myRef = database.getReference(user_id).child("Point");
        myRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (Integer.valueOf(currentData.getValue().toString()) == 0) {
                    currentData.setValue(0);
                } else {
                    if (increment) {
                        currentData.setValue(Integer.valueOf(currentData.getValue().toString()) + point);
                    } else {
                        currentData.setValue(Integer.valueOf(currentData.getValue().toString()) - point);
                    }
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.d("Fail:", "Firebase counter increment failed." + databaseError);
                } else {
                    Log.d("Success", "Increment successfully");
                }
            }
        });
    }

    public void writeUserData(String name, Uri imageUrl) {
        String mPhotoUri;
        if (imageUrl != null){
            mPhotoUri = imageUrl.toString();
        }
        else {
            mPhotoUri = defaultUri.toString();
        }
        myRef = database.getReference(user_id).child("Name");
        myRef.setValue(name);
        myRef = database.getReference(user_id).child("Photo");
        myRef.setValue(mPhotoUri);
    }
}
