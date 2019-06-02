package com.checkin.app.checkin.Auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.checkin.app.checkin.Misc.DebouncedOnClickListener;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel.GENDER;
import com.fasterxml.jackson.databind.JsonNode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class SignupUserInfoFragment extends Fragment {
    public static final String KEY_NAME = "name";
    @BindView(R.id.ed_firstname)
    EditText edFirstName;
    @BindView(R.id.ed_lastname)
    EditText edLastName;
    @BindView(R.id.im_male)
    FrameLayout imMale;
    @BindView(R.id.im_female)
    FrameLayout imFemale;
    @BindView(R.id.ed_Username)
    EditText edUsername;
    @BindView(R.id.btn_enter)
    Button btnEnter;

    private AuthFragmentInteraction fragmentInteraction;
    private Unbinder unbinder;
    private GENDER genderChosen = null;

    public SignupUserInfoFragment() {
    }

    public static SignupUserInfoFragment newInstance(AuthFragmentInteraction fragmentInteraction) {
        SignupUserInfoFragment fragment = new SignupUserInfoFragment();
        fragment.fragmentInteraction = fragmentInteraction;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_signup_user_info, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            String name = getArguments().getString(KEY_NAME);
            edFirstName.setText(name.substring(0, name.indexOf(" ")));
            edLastName.setText(name.substring(name.lastIndexOf(" ")));
        }

        AuthViewModel viewModel = ViewModelProviders.of(requireActivity()).get(AuthViewModel.class);
        viewModel.getErrors().observe(this, jsonNodes -> {
            if (jsonNodes == null)
                return;
            if (jsonNodes.has("username")) {
                JsonNode userNode = jsonNodes.get("username");
                String msg = userNode.get(0).asText();
                edUsername.setError(msg);
            }
        });

        btnEnter.setOnClickListener(new DebouncedOnClickListener(2000) {
            @Override
            public void onDebouncedClick(View v) {
                onProceedClicked(v);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.im_female, R.id.im_male})
    public void onGenderIconClicked(View icon) {
        icon.setSelected(true);
        genderChosen = (icon.getId() == R.id.im_male) ? GENDER.MALE : GENDER.FEMALE;
        if (genderChosen == GENDER.MALE) imFemale.setSelected(false);
        else imMale.setSelected(false);
    }

    public void onProceedClicked(View view) {
        String firstname = edFirstName.getText().toString();
        String lastname = edLastName.getText().toString();
        String username = edUsername.getText().toString();
        if (username.isEmpty()) {
            edUsername.setError("This field cannot be empty");
            return;
        }
        if (firstname.isEmpty()) {
            edFirstName.setError("This field cannot be empty");
            return;
        }
        if (genderChosen == null) {
            Toast.makeText(getContext(), "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }
        fragmentInteraction.onUserInfoProcess(firstname, lastname, username, genderChosen);
    }
}
