package com.checkin.app.checkin.User;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.checkin.app.checkin.R;

/**
 * Created by Bhavik Patel on 25/08/2018.
 */

public class MutualCheckInActivityDemo extends AppCompatActivity implements MutualCheckInFragment.MutualCheckInFragmentCompat{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_mutual_checkin);
        getSupportFragmentManager().beginTransaction().add(R.id.frame,new MutualCheckInFragment()).commit();
    }

    @Override
    public void onClick(View view,MutualCheckInModel mutualCheckInModel) {
        Toast.makeText(getApplicationContext()," hotel = " + mutualCheckInModel.getActionName(),Toast.LENGTH_SHORT).show();
    }
}
