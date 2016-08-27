package com.elixsr.core.common.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Niall on 27/08/2016.
 */
public class BaseAuthenticatedActivity extends BaseActivity {

    private static final String TAG = "BaseAuthActivity";
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: go over this!

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

            userId = user.getUid();
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    userId = user.getUid();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    //TODO - move to login activity!
                    if(userId != null && !userId.isEmpty()){
                        //this means we had a logged in idenity, and now it is null
                        //move the user to the login activity
//                        Intent loginIntent = new Intent(BaseAuthenticatedActivity.this, LoginActivity.class);
//                        startActivity(loginIntent);
                    }
                }
            }
        };

        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    public String getUserId() {
        return userId;
    }
}