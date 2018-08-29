package com.checkin.app.checkin.Shop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TabEmailFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.et_email)EditText email;
    @BindView(R.id.btn_next)Button next;

    public TabEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_tab_email, container, false);
        unbinder = ButterKnife.bind(this,view);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            next.setActivated(email.length() > 6);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
        }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
