package com.checkin.app.checkin.User.Private;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.Misc.SelectCropImageActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.checkin.app.checkin.User.Private.ProfileEditActivity.KEY_USER_DATA;

public class UserPrivateProfileFragment extends BaseFragment {
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
    @Nullable
    private UserModel mUserModel;
    private UserCheckinAdapter mUserCheckinAdapter;

    public UserPrivateProfileFragment() {
    }

    public static UserPrivateProfileFragment newInstance() {
        return new UserPrivateProfileFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_user_profile_private;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(requireActivity()).get(UserViewModel.class);
        setupObservers();
        rvRecentShops.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mUserCheckinAdapter = new UserCheckinAdapter();
        rvRecentShops.setAdapter(mUserCheckinAdapter);

        mViewModel.fetchUserData();
        mViewModel.fetchUserRecentCheckinsData();
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

        mViewModel.getUserRecentCheckinsData().observe(this, input -> {
            if (input == null)
                return;
            if (input.status == Status.SUCCESS && input.data != null) {
                if (input.data.size() > 0) {
                    mUserCheckinAdapter.setUserCheckinsData(input.data);
                }
            }
        });

        mViewModel.getImageUploadResult().observe(this, voidResource -> {
            if (voidResource == null)
                return;
            switch (voidResource.status) {
                case SUCCESS:
                    mViewModel.updateResults();
                    break;
                case ERROR_UNKNOWN:
                    Utils.toast(requireContext(), voidResource.message);
                    break;
            }
        });
    }

    private void setupData(UserModel data) {
        mUserModel = data;
        tvDisplayName.setText(data.getFullName());
        Utils.loadImageOrDefault(imCover, data.getProfilePic(), (data.getGender() == UserModel.GENDER.MALE) ? R.drawable.cover_unknown_male : R.drawable.cover_unknown_female);
        tvCheckins.setText(data.formatCheckins());
        tvLocality.setText(data.getAddress());
    }

    @OnClick({R.id.btn_user_private_add_logo, R.id.btn_user_private_edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_user_private_add_logo:
                Intent intent = new Intent(requireContext(), SelectCropImageActivity.class);
                intent.putExtra(SelectCropImageActivity.KEY_FILE_SIZE_IN_MB, 4L);
                startActivityForResult(intent, SelectCropImageActivity.RC_CROP_IMAGE);
                break;
            case R.id.btn_user_private_edit:
                if (mUserModel == null) {
                    mViewModel.fetchUserData();
                }else {
                    Intent editProfileIntent = new Intent(requireContext(), ProfileEditActivity.class);
                    editProfileIntent.putExtra(KEY_USER_DATA, mUserModel);
                    startActivityForResult(editProfileIntent, 111);
                }
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
        }else {
            mViewModel.updateResults();
        }
    }
}
