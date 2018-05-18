package com.example.hello.alarm;

/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class SignedInActivity extends AppCompatActivity {
    private static final String TAG = "SignedInActivity";
    DatabaseReference myRef;
    NavigationView mNavigationView;
    View mHeaderView;

    @BindView(android.R.id.content) View mRootView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
//    @BindView(R.id.user_enabled_providers) TextView mEnabledProviders;


    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        return new Intent().setClass(context, SignedInActivity.class)
                .putExtra(ExtraConstants.EXTRA_IDP_RESPONSE, idpResponse);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(AuthUiActivity.createIntent(this));
            finish();
            return;
        }

        IdpResponse response = getIntent().getParcelableExtra(ExtraConstants.EXTRA_IDP_RESPONSE);

        setContentView(R.layout.signed_in_layout);
        ButterKnife.bind(this);


        //Select information in the nav's header
        mNavigationView = findViewById(R.id.nav_view);
        mHeaderView = mNavigationView.getHeaderView(0);

        populateProfile(this);
//      populateIdpToken(response);


        //Prepare viewpager and add circle indicator for view pager
        ViewPager viewPager = findViewById(R.id.viewpager);
        CircleIndicator indicator = findViewById(R.id.indicator);
        setupViewPager(viewPager);
        indicator.setViewPager(viewPager);


        //Access database and reference to the data of the current's user
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(currentUser.getUid()).child("Point");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                updatePointInUi(dataSnapshot.getValue(Integer.class));
                populateProfile(getApplicationContext());
                //      populateIdpToken(response);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });

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
            Toast.makeText(this, notification,
                    Toast.LENGTH_LONG).show();
        }





        //Configure action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //Prepare nav drawer's items listeners
        prepareNavDrawerItemsListener();
    }


    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AlarmList(), "ONE");
        adapter.addFragment(new OneFragment(), "TWO");
        adapter.addFragment(new TwoFragment(), "THREE");
        viewPager.setAdapter(adapter);
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


    public void prepareNavDrawerItemsListener(){
        //Handle navigation click events
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (menuItem.getItemId()) {
                            case R.id.nav_sign_out:
                                deleteAccountClicked();
                            case R.id.nav_delete_account:
                                signOutClicked();
                        }

                        return true;
                    }
                });
    }

    public void updatePointInUi(Integer currentPoint){
        if (currentPoint != null) {
            TextView currentUserPoint = mHeaderView.findViewById(R.id.nav_point);
            currentUserPoint.setText("Current point: " + String.valueOf(currentPoint));
        }
    }


    public void signOutClicked() {
        AuthUI.getInstance()
                .signOut(getApplicationContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(AuthUiActivity.createIntent(SignedInActivity.this));
                            finish();
                        } else {
                            Log.w(TAG, "signOut:failure", task.getException());
                            showSnackbar(R.string.sign_out_failed);
                        }
                    }
                });
    }

    public void deleteAccountClicked() {
        new AlertDialog.Builder(getApplicationContext())
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton("Yes, nuke it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }

    private void deleteAccount() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(AuthUiActivity.createIntent(SignedInActivity.this));
                            finish();
                        } else {
                            showSnackbar(R.string.delete_account_failed);
                        }
                    }
                });
    }

    private void populateProfile(Context context) {
        final ImageView mUserProfilePicture = mHeaderView.findViewById(R.id.nav_profile_image);
        final TextView mUserDisplayName = mHeaderView.findViewById(R.id.nav_display_name);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUrl() != null) {
            Glide.with(context)
                    .load(user.getPhotoUrl())
                    .into(mUserProfilePicture);
        }

        mUserDisplayName.setText(
                TextUtils.isEmpty(user.getDisplayName()) ? "No display name" : user.getDisplayName());

        List<String> providers = new ArrayList<>();
        if (user.getProviderData().isEmpty()) {
            providers.add("Anonymous");
        } else {
            for (UserInfo info : user.getProviderData()) {
                switch (info.getProviderId()) {
                    case GoogleAuthProvider.PROVIDER_ID:
                        providers.add(getString(R.string.providers_google));
                        break;
                    case FacebookAuthProvider.PROVIDER_ID:
                        providers.add(getString(R.string.providers_facebook));
                        break;
                    case TwitterAuthProvider.PROVIDER_ID:
                        providers.add(getString(R.string.providers_twitter));
                        break;
                    case EmailAuthProvider.PROVIDER_ID:
                        providers.add(getString(R.string.providers_email));
                        break;
                    case PhoneAuthProvider.PROVIDER_ID:
                        providers.add(getString(R.string.providers_phone));
                        break;
                    case FirebaseAuthProvider.PROVIDER_ID:
                        // Ignore this provider, it's not very meaningful
                        break;
                    default:
                        throw new IllegalStateException(
                                "Unknown provider: " + info.getProviderId());
                }
            }
        }

//        mEnabledProviders.setText(getString(R.string.used_providers, providers));
    }

//    private void populateIdpToken(@Nullable IdpResponse response) {
//        String token = null;
//        String secret = null;
//        if (response != null) {
//            token = response.getIdpToken();
//            secret = response.getIdpSecret();
//        }
//
//        View idpTokenLayout = findViewById(R.id.idp_token_layout);
//        if (token == null) {
//            idpTokenLayout.setVisibility(View.GONE);
//        } else {
//            idpTokenLayout.setVisibility(View.VISIBLE);
//            ((TextView) findViewById(R.id.idp_token)).setText(token);
//        }
//
//        View idpSecretLayout = findViewById(R.id.idp_secret_layout);
//        if (secret == null) {
//            idpSecretLayout.setVisibility(View.GONE);
//        } else {
//            idpSecretLayout.setVisibility(View.VISIBLE);
//            ((TextView) findViewById(R.id.idp_secret)).setText(secret);
//        }
//    }

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
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
}