package com.checkin.app.checkin.User.Private;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Misc.SelectCropImageActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class UserPrivateProfileFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.tv_user_private_checkins)
    TextView tvCheckins;
    @BindView(R.id.tv_user_private_display_name)
    TextView tvDisplayName;
    @BindView(R.id.tv_user_private_locality)
    TextView tvLocality;
    @BindView(R.id.im_user_private_cover)
    ImageView imCover;
    @BindView(R.id.rv_user_private_recent_shops)
    RecyclerView rvRecentShops;

    private UserViewModel mViewModel;

    public static UserPrivateProfileFragment newInstance() {
        return new UserPrivateProfileFragment();
    }

    public UserPrivateProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user_profile_private, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(requireActivity()).get(UserViewModel.class);

        setupObservers();
        mViewModel.fetchUserData();
    }

    private void setupObservers() {
        mViewModel.getUserData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Status.SUCCESS && resource.data != null) {
                setupData(resource.data);
            } else if (resource.status == Status.LOADING) {

            } else {
                Utils.toast(requireContext(), "Error: " + resource.message);
            }
        });
    }

    private void setupData(UserModel data) {
        tvDisplayName.setText(data.getFullName());
        Utils.loadImageOrDefault(imCover, data.getProfilePic(), (data.getGender() == UserModel.GENDER.MALE) ? R.drawable.cover_unknown_male : R.drawable.cover_unknown_female);
        tvCheckins.setText(data.formatCheckins());
        tvLocality.setText(data.getAddress());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_user_private_add_logo, R.id.btn_user_private_edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_user_private_add_logo:
                Intent intent = new Intent(requireContext() , SelectCropImageActivity.class);
                startActivityForResult(intent, SelectCropImageActivity.RC_CROP_IMAGE);
                break;
            case R.id.btn_user_private_edit:
                startActivity(new Intent(requireContext(), ProfileEditActivity.class));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectCropImageActivity.RC_CROP_IMAGE && resultCode == RESULT_OK) {
            if (data.getExtras() != null) {
                File image = (File) data.getExtras().get(SelectCropImageActivity.KEY_IMAGE);
                mViewModel.updateProfilePic(image, requireContext());
            }
        }
    }

}
