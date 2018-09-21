package com.checkin.app.checkin.Auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel.GENDER;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class SignupUserInfoFragment extends Fragment {
    private SignUpFragmentInteraction fragmentInteraction;
    private Unbinder unbinder;
    private GENDER genderChosen = null;

    @BindView(R.id.ed_firstname) EditText edFirstname;
    @BindView(R.id.ed_lastname) EditText edLastname;
    @BindView(R.id.im_male) ImageView imMale;
    @BindView(R.id.im_female) ImageView imFemale;
    @BindView(R.id.ed_password) EditText edPassword;

    public SignupUserInfoFragment() {
        // Required empty public_selected constructor
    }

    public static SignupUserInfoFragment newInstance(SignUpFragmentInteraction fragmentInteraction) {
        SignupUserInfoFragment fragment = new SignupUserInfoFragment();
        fragment.fragmentInteraction = fragmentInteraction;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         final View view =  inflater.inflate(R.layout.fragment_signup_user_info, container, false);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({ R.id.im_female, R.id.im_male })
    public void onGenderIconClicked(ImageView icon) {
        icon.setSelected(true);
        genderChosen = (icon.getId() == R.id.im_male) ? GENDER.MALE : GENDER.FEMALE;
        if (genderChosen == GENDER.MALE) imFemale.setSelected(false);
        else    imMale.setSelected(false);
    }

    @OnClick(R.id.btn_enter)
    public void onProceedClicked(View view){
    String firstname = edFirstname.getText().toString();
    String lastname = edLastname.getText().toString();
    String password = edPassword.getText().toString();
   /* if((firstname.isEmpty()) || (lastname.isEmpty()) || (password.isEmpty()) || (genderChosen == null)){
        edFirstname.setError("This field cannot be empty");
        return;
    }*/
   if (firstname.isEmpty()){
       edFirstname.setError("This field cannot be empty");
       return;
   }
    if (lastname.isEmpty()){
        edLastname.setError("This field cannot be empty");
        return;
    }
    if(password.isEmpty()){
        edPassword.setError("This field cannot be empty");
        return;
    }
    if(genderChosen == null){
        Toast.makeText(getActivity().getApplicationContext(),"Please select gender",Toast.LENGTH_SHORT).show();
        return;
    }
    fragmentInteraction.onUserInfoProcess(firstname, lastname, password, genderChosen);

    }
}
