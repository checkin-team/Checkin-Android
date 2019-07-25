package com.checkin.app.checkin.User.Private;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.checkin.app.checkin.Utility.BackgroundShadow;
import com.checkin.app.checkin.Utility.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.checkin.app.checkin.User.Private.ProfileEditActivity.KEY_USER_DATA;

public class UserPrivateProfileFragment extends BaseFragment {
    @BindView(R.id.shimmer_user_private_cover)
    ShimmerFrameLayout shimmerLayout;
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
    @BindView(R.id.container_checkedin_count)
    LinearLayout containerCheckedinCount;

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
        containerCheckedinCount.setBackground(BackgroundShadow.generateViewShadow(containerCheckedinCount, R.color.white,
                R.dimen.card_corner_radius, R.color.translucent_aqua_blue, R.dimen.spacing_elevation_fix_extreme_tiny, Gravity.BOTTOM));
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
            if (resource.getStatus() == Status.SUCCESS && resource.getData() != null) {
                setupData(resource.getData());
            } else if (resource.getStatus() == Status.LOADING) {

            } else {
                Utils.toast(requireContext(), "Error: " + resource.getMessage());
            }
        });

        mViewModel.getUserRecentCheckinsData().observe(this, input -> {
            if (input == null)
                return;
            if (input.getStatus() == Status.SUCCESS && input.getData() != null) {
                if (input.getData().size() > 0) {
                    mUserCheckinAdapter.setUserCheckinsData(input.getData());
                }
            }
        });

        mViewModel.getImageUploadResult().observe(this, voidResource -> {
            if (voidResource == null)
                return;
            switch (voidResource.getStatus()) {
                case SUCCESS:
                    mViewModel.updateResults();
                    break;
                case ERROR_UNKNOWN:
                    Utils.toast(requireContext(), voidResource.getMessage());
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

        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
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
