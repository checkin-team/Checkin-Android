package com.checkin.app.checkin.Shop.ShopJoin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
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

    @BindView(R.id.ed_email) EditText edEmail;
    @BindView(R.id.btn_next) Button btnNext;

    private EmailInteraction mListener;

    public TabEmailFragment() {
        // Required empty public constructor
    }

    public static TabEmailFragment newInstance(EmailInteraction listener) {
        TabEmailFragment fragment = new TabEmailFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_tab_email, container, false);
        unbinder = ButterKnife.bind(this,view);
        edEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnNext.setActivated(s.length() > 6 && Patterns.EMAIL_ADDRESS.matcher(s).matches());
            }
        });
        return view;
    }
    @OnClick(R.id.btn_next)
    public void proceed(View view){
        if (view.isActivated()) {
            mListener.onEmailEntered(edEmail.getText().toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    interface EmailInteraction {
        void onEmailEntered(String email);
    }
}
