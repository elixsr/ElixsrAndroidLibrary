package com.elixsr.core.common.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.elixsr.core.R;

/**
 * Created by Niall on 27/08/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;

    protected Toolbar getActionBarToolbar() {

        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                // Depending on which version of Android you are on the Toolbar or the ActionBar may be
                // active so the a11y description is set here.
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

}