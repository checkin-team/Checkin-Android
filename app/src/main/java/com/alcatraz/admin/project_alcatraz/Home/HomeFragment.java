package com.alcatraz.admin.project_alcatraz.Home;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.Profile.ShopProfileActivity;
import com.alcatraz.admin.project_alcatraz.Profile.UserProfileActivity;
import com.alcatraz.admin.project_alcatraz.R;


/**
 * Created by admin on 1/15/2018.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG="HomeFragment";
    Button userprof,shopprof;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view =inflater.inflate(R.layout.fragment_home,container, false);
     userprof=(Button)view.findViewById(R.id.button_userprofile);
     userprof.setOnClickListener(this);
    return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.button_userprofile:
                startActivity(new Intent(getActivity(), UserProfileActivity.class));
                break;

            case R.id.button_shopprofile:
                startActivity(new Intent(getActivity(), ShopProfileActivity.class));
                break;

            default:
                Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
        }

    }
}
