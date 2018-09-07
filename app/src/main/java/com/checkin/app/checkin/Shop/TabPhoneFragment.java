package com.checkin.app.checkin.Shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import butterknife.Unbinder;

public class TabPhoneFragment extends Fragment {
    private Unbinder unbinder;
    @BindView(R.id.ed_phone) EditText edPhone;
    @BindView(R.id.btn_next) Button btnNext;


    public TabPhoneFragment() {
        // Required empty public constructor
    }

    public static TabPhoneFragment newInstance() {
        return new TabPhoneFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_tab_phone, container, false);
        unbinder = ButterKnife.bind(this, view);

        edPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnNext.setActivated(s.length() > 12);
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

