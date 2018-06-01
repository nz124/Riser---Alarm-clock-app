package com.example.hello.alarm;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import me.relex.circleindicator.CircleIndicator;


public class MainActivity extends AppCompatActivity {
    static final int NUM_ITEMS = 2;
    static DatabaseReference database;
    static DatabaseReference myRef;
    ValueEventListener eventListener;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    Integer current_point;
    NavigationView navigationView;
    FirebaseUser currentUser;

    //Select information in the nav's header
    View header_view;
    TextView nav_point;
    TextView nav_name;
    ImageView nav_photo;
    MenuItem nav_sign_out, nav_sign_in;
    Context context;
    Uri defaultUri;
    Uri mPhotoUri;
    private FirebaseAuth mAuth;
    AuthCredential credential;

    //Prepare viewpager and add circle indicator for view pager
    ViewPager viewPager;

    CircleIndicator indicator;

    boolean sleepAnalysisFragment = false;
    boolean friendFragment = false;

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        return new Intent().setClass(context, MainActivity.class)
                .putExtra(ExtraConstants.EXTRA_IDP_RESPONSE, idpResponse);
    }

    public static void  restartActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        this.context = this;

        viewPager = findViewById(R.id.viewpager);
        indicator = findViewById(R.id.indicator);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        header_view = navigationView.getHeaderView(0);
        nav_point = header_view.findViewById(R.id.nav_point);
        nav_photo = header_view.findViewById(R.id.nav_profile_image);
        nav_name = header_view.findViewById(R.id.nav_display_name);
        nav_sign_in = navigationView.getMenu().findItem(R.id.nav_sign_in);
        nav_sign_out = navigationView.getMenu().findItem(R.id.nav_sign_out);

        //Access database and reference to the data of the current's user
        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        //Sign user in anonymously
        if (currentUser == null) {
            loginAsGuest();
        }
        else {
            //Listen for changes from database and update UI
            updateUI(currentUser);
            //Convert guest account to Facebook/Google account if possible
            linkAccount();
        }

        //Determine to increment or decrement point based on the extras being passed in
        String action_type = getIntent().getStringExtra("type");
        String notification = "";
        if (action_type != null) {
            if (action_type.equals("turn_off")) {
                incrementPointAndSaveToDb(getApplicationContext(), currentUser, true, 100);
                notification = "You gained 100 points";
            } else {
                incrementPointAndSaveToDb(getApplicationContext(), currentUser, false, 100);
                notification = "You lost 100 points";
            }
            Toast.makeText(this, notification,
                    Toast.LENGTH_SHORT).show();
        }



        //Configure action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    public void setupViewPager(ViewPager viewPager, boolean sleepAnalysis, boolean challenge_friend) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("sleep_analysis", sleepAnalysis);
        bundle.putBoolean("challenge_friend", challenge_friend);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AlarmListFragment(), "ONE");
        if (challenge_friend){
            adapter.addFragment(new FriendListFragment(), "TWO");
        }
        if (sleepAnalysis){
            adapter.addFragment(new SleepAnalysisFragment(), "THREE");
        }
        //Show items on the store based on what items the user already has
        StoreFragment storeFragment = new StoreFragment();
        storeFragment.setArguments(bundle);

        adapter.addFragment(storeFragment, "FOUR");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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



    public static void incrementPointAndSaveToDb(final Context context, final FirebaseUser user, final boolean increment, final int point) {
        myRef = database.child(user.getUid()).child("point");

        //Get user's current point
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                int updatedPoint = data.getValue(int.class);
                if (increment){
                    updatedPoint += point;
                }
                else if (updatedPoint != 0){
                    updatedPoint -= point;
                }
                //Update point from Database
                Map<String, Object> childUpdate = new HashMap<>();
                childUpdate.put("/"+ user.getUid() + "/" + "point", updatedPoint);
                database.updateChildren(childUpdate);
                Toast.makeText(context, "You have "+ updatedPoint + " left!", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //...
            }
        });
    }

    public void createFirstTimeUserData(FirebaseUser user) {
        String mName, mPhotoUriString, user_id, fcm_token;
        user_id = user.getUid();
        myRef = database.child(user_id);


        mName = (user.getDisplayName() == null ) ? "Guest" + new Random().nextInt() : user.getDisplayName();
        mPhotoUri = (user.getPhotoUrl() == null) ? defaultUri : user.getPhotoUrl();
        mPhotoUriString = mPhotoUri.toString();

        User newUser = new User(mName, mPhotoUriString, 0, user_id);
        myRef.setValue(newUser);

    }


    public void loginAsGuest(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            currentUser = task.getResult().getUser();
                            //Write user's profile to database
                            createFirstTimeUserData(currentUser);
                            //Listen for changes from database and update UI
                            updateUI(currentUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void linkAccount(){
        Bundle login_bundle = getIntent().getExtras();
        String provider_type = login_bundle.getString("provider_type");
        String auth_token = login_bundle.getString("auth_token");
        Log.e("hey", "linkAccount: "+ provider_type + auth_token );
        if (provider_type != null && auth_token != null){
            if (provider_type.equals("facebook.com")) {
                credential = FacebookAuthProvider.getCredential(auth_token);
            }
            else if (provider_type.equals("google.com")){
                credential = GoogleAuthProvider.getCredential(auth_token, null);
            }
            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("success", "linkWithCredential:success");
                                FirebaseUser user = task.getResult().getUser();
                                updateUI(user);
                                Toast.makeText(context, "Link account successfully!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w("fail", "linkWithCredential:failure", task.getException());
                                Toast.makeText(context, "Welcome back!",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }

    public void updateUI(FirebaseUser user){
        if (user.isAnonymous()) {
            //Display sign in and sign out buttons
            nav_sign_in.setVisible(true);
            nav_sign_out.setVisible(false);
        } else {
            nav_sign_in.setVisible(false);
            nav_sign_out.setVisible(true);
        }

        //Handle navigation click events on the side bar
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        switch(menuItem.getItemId()) {
                            case R.id.nav_sign_in:
                                startActivity(SignInActivity.createIntent(context));
                                break;
                            case R.id.nav_sign_out:
                                mAuth.signOut();
                                Toast.makeText(context, "Sign out succesfully", Toast.LENGTH_LONG).show();
                                loginAsGuest();
                                updateUI(currentUser);
                        }
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        return true;
                    }
                });


        //Show fragments and functions properly depending on user's purchased items
        database.child(currentUser.getUid()).child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Sleep Tracker").getValue() != null){
                    sleepAnalysisFragment = true;
                };
                if (dataSnapshot.child("Challenge Friends").getValue() != null) {
                    friendFragment = true;
                }
                Log.e("TRUE OR FALSE", "onDataChange: " + dataSnapshot.child("Sleep Tracker"));
                setupViewPager(viewPager, sleepAnalysisFragment, friendFragment);
                indicator.setViewPager(viewPager);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Update user information from Database
        eventListener = database.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                User user = data.getValue(User.class);
                nav_name.setText(user.name);
                nav_point.setText("Point: " + user.point);
                Log.e("yo", "onDataChange: "+ user.point );
                Glide.with(getApplicationContext())
                        .load(user.photoUriString)
                        .into(nav_photo);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //...
            }
        });
    }

    public static void clearNotification(Context context, int alarm_id){
        //Clear the notification on notification bar
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(alarm_id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.child(currentUser.getUid()).removeEventListener(eventListener);
    }

}
