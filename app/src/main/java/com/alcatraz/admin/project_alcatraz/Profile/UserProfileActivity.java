package com.alcatraz.admin.project_alcatraz.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.alcatraz.admin.project_alcatraz.R;

/**
 * Created by admin on 1/20/2018.
 */

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG="ProfileACtivity";
    private static final int ACTIVITY_NUM=4;
    private Context context=UserProfileActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Log.d(TAG,"oncreate: started.");
    }
     private void setupToolbar()
     {

         Toolbar toolbar=(Toolbar)findViewById(R.id.profile_toolbar);
         setSupportActionBar(toolbar);
         toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem item) {
                 Log.d(TAG,"onMenuItemClick:Navigating to Profile Prefs");
                 return false;
             }
         });
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return true;

    }
}
