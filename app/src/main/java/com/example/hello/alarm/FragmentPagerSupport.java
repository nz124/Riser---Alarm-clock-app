package com.example.hello.alarm;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;


public class FragmentPagerSupport extends AppCompatActivity {
    static final int NUM_ITEMS = 2;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    static FirebaseDatabase database;
    static DatabaseReference myRef;
    Integer current_point;
    NavigationView navigationView;
    String user_name, user_email, user_id, user_photoUrlString;
    Uri user_photoUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        //Access database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(user_id).child("Point");

        //Get information from current user, if there is one.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            user_name = currentUser.getDisplayName();
            user_email = currentUser.getEmail();
            user_photoUrl = currentUser.getPhotoUrl();
            user_id = currentUser.getUid();
        };

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.alarm_clock);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = findViewById(R.id.drawer_layout);


        //Handle navigation click events
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

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

        //Listen for changes from database and update UI
        updateUiWithDbData(user_id);

    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AddNewAlarm(), "ONE");
        adapter.addFragment(new OneFragment(), "TWO");
        adapter.addFragment(new TwoFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        final List<Fragment> mFragmentList = new ArrayList<>();
        final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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


    public void updateUiWithDbData(String user_id) {
        //Select information in the nav's header
        View header_view = navigationView.getHeaderView(0);
        final TextView nav_user = header_view.findViewById(R.id.nav_user);


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                current_point = dataSnapshot.getValue(Integer.class);
                if (current_point != null) {
                    String point_display = String.valueOf(current_point);
                    //Update point if there are changes
                    nav_user.setText(point_display);
                    Log.e("", "onDataChange: " + current_point + "/" + point_display);
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
        myRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue(Integer.class) == null) {
                    currentData.setValue(0);
                } else {
                    if (increment) {
                        currentData.setValue(currentData.getValue(Integer.class) + point);
                    } else {
                        currentData.setValue(currentData.getValue(Integer.class) - point);
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
}
