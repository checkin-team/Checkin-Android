package com.checkin.app.checkin.user.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.BackgroundShadow;
import com.checkin.app.checkin.Utility.SwipeTouchListener;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.misc.activities.SelectCropImageActivity;
import com.checkin.app.checkin.misc.fragments.BaseFragment;
import com.checkin.app.checkin.user.activities.ProfileEditActivity;
import com.checkin.app.checkin.user.adapters.UserCheckinAdapter;
import com.checkin.app.checkin.user.models.UserModel;
import com.checkin.app.checkin.user.viewmodels.UserViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.checkin.app.checkin.data.resource.Resource.Status.LOADING;
import static com.checkin.app.checkin.data.resource.Resource.Status.SUCCESS;
import static com.checkin.app.checkin.user.activities.ProfileEditActivity.KEY_USER_DATA;

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
    @BindView(R.id.container_user_info)
    FrameLayout containerUserDetails;
    @BindView(R.id.container_user_private_top)
    FrameLayout containerUserPrivateTop;

    private UserViewModel mViewModel;
    @Nullable
    private UserModel mUserModel;
    private UserCheckinAdapter mUserCheckinAdapter;
    private int oldHeight;
    private int newHeight = 0;
//    protected boolean isExpanded = false;

    public UserPrivateProfileFragment() {
    }

    public static UserPrivateProfileFragment newInstance() {
        return new UserPrivateProfileFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_user_profile_private;
    }

    @SuppressLint("ClickableViewAccessibility")
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

        containerUserPrivateTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                ((HomeActivity) Objects.requireNonNull(getActivity())).enableDisableSwipeRefresh(true);
                return true;
            }
        });


        containerUserDetails.setOnTouchListener(new SwipeTouchListener(getContext()){
            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
//                ((HomeActivity) Objects.requireNonNull(getActivity())).enableDisableSwipeRefresh(false);
                toggleView(false);
            }

            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                toggleView(true);
            }

        });

        rvRecentShops.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                ((HomeActivity) Objects.requireNonNull(getActivity())).enableDisableSwipeRefresh(false);
                toggleView(true);
                return false;
            }
        });

        containerUserPrivateTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                ((HomeActivity) Objects.requireNonNull(getActivity())).enableDisableSwipeRefresh(true);
                return true;
            }
        });

    }

    private void setupObservers() {
        mViewModel.getUserData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.getStatus() == SUCCESS && resource.getData() != null) {
                setupData(resource.getData());
            } else if (resource.getStatus() == LOADING) {

            } else {
                Utils.toast(requireContext(), "Error: " + resource.getMessage());
            }
        });

        mViewModel.getUserRecentCheckinsData().observe(this, input -> {
            if (input == null)
                return;
            if (input.getStatus() == SUCCESS && input.getData() != null) {
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
                } else {
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
        } else {
            mViewModel.updateResults();
        }
    }


    private ValueAnimator getToggleAnimation(View view, int startHeight, int endHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(startHeight, endHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
                params.height = val;
                view.setLayoutParams(params);
            }
        });
        animator.setDuration(100);
        return animator;
    }

    private void toggleView(boolean isExpanded) {
//        if (containerUserDetails.getHeight() == newHeight) {
        if (isExpanded) {
            Animator toggleAnimation = getToggleAnimation(containerUserDetails, containerUserDetails.getHeight(), oldHeight);
//            Animator showView = AnimUtils.showView(containerCheckedinCount);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(toggleAnimation);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    containerCheckedinCount.setVisibility(View.VISIBLE);
                    rvRecentShops.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animatorSet.start();

        } else {
            oldHeight = containerUserDetails.getHeight();
            newHeight = oldHeight + 800;

            Animator toggleAnimation = getToggleAnimation(containerUserDetails, oldHeight, newHeight);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(toggleAnimation);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    rvRecentShops.requestLayout();
                    ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) rvRecentShops.getLayoutParams();
                    layoutParams.height = 400;
                    layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    rvRecentShops.setLayoutParams(layoutParams);

                    rvRecentShops.setVisibility(View.VISIBLE);
                    mUserCheckinAdapter.notifyDataSetChanged();
                    containerCheckedinCount.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animatorSet.start();


//            containerCheckedinCount.startAnimation(AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_up));
        }
    }
}
