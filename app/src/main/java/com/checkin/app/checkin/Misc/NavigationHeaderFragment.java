package com.checkin.app.checkin.Misc;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NavigationHeaderFragment extends Fragment {
  @BindView(R.id.im_context)
  ImageView imageView;
  @BindView(R.id.user_name)
  TextView userName;
  @BindView(R.id.tv_account_desc)
  TextView accountType;
  @BindView(R.id.account_selector)
    Spinner accountSelector;
    String name;
    String account;
    Bitmap image;
    Unbinder unbinder;


        public void onCreate()
        {
            name="";
            account="";
        }

    public void setDetails(String name,String account)
    {
        this.name=name;
        this.account=account;
    }
        public NavigationHeaderFragment() {
            // Required empty public_selected constructor
        }

        public static NavigationHeaderFragment newInstance() {
            NavigationHeaderFragment navigationHeaderFragment=new NavigationHeaderFragment();

            return navigationHeaderFragment;
        }


        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.layout_header_account, container, false);
            unbinder = ButterKnife.bind(this,view);
            String[] accountTypes=new String[]{"Social","Vivek Sharma"};
            List<String> accountsList = new ArrayList<>(Arrays.asList(accountTypes));
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item,accountsList);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            accountSelector.setAdapter(spinnerAdapter);
            String selected=accountSelector.getSelectedItem().toString();
            accountSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Object item=parent.getSelectedItem();
                    if(item.toString().contains("cial"))
                    {
                        accountType.setText("Social's account");
                    }
                    else
                        accountType.setText(account);
                }

                @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    accountType.setText("Kuch nhi");
                }
            });
            userName.setText(name);

            return view;

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            unbinder.unbind();
        }
    }


