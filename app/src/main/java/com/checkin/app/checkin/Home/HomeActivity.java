package com.checkin.app.checkin.Home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Data.Message.ActiveSessionNotificationService;
import com.checkin.app.checkin.Data.Message.Constants;
import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Data.ProblemModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Home.fragment.UserHomeFragment;
import com.checkin.app.checkin.Misc.BaseFragmentAdapterBottomNav;
import com.checkin.app.checkin.Misc.BlankFragment;
import com.checkin.app.checkin.Misc.QRScannerActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopJoin.BusinessFeaturesActivity;
import com.checkin.app.checkin.User.Private.UserPrivateProfileFragment;
import com.checkin.app.checkin.User.Private.UserViewModel;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.OnBoardingUtils;
import com.checkin.app.checkin.Utility.OnBoardingUtils.OnBoardingModel;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.activesession.ActiveSessionActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Data.Message.ActiveSessionNotificationService.ACTIVE_RESTAURANT_DETAIL;
import static com.checkin.app.checkin.Data.Message.ActiveSessionNotificationService.ACTIVE_SESSION_PK;

public class HomeActivity extends BaseAccountActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String SP_ONBOARDING_QR_SCANNER = "onboarding.qrscanner";
    private static final int REQUEST_QR_SCANNER = 212;

    @BindView(R.id.toolbar_home)
    Toolbar toolbarHome;
    @BindView(R.id.drawer_home)
    DrawerLayout drawerLayout;
    @BindView(R.id.iv_home_navigation)
    ImageView imNavigation;
    @BindView(R.id.tabs_home)
    TabLayout tabLayout;
    @BindView(R.id.vp_home)
    DynamicSwipableViewPager vpHome;
//    @BindView(R.id.container_home_session_status)
//    ViewGroup vSessionStatus;
    @BindView(R.id.container_home_session_active_status)
    ImageView vSessionActiveStatus;
    @BindView(R.id.container_home_session_waiting_status)
    ImageView vSessionWaitingStatus;
    @BindView(R.id.sr_home)
    SwipeRefreshLayout swipeRefreshLayout;
//    @BindView(R.id.tv_home_session_active_status)
//    TextView tvSessionStatus;
//    @BindView(R.id.tv_home_session_wait_qr_busy)
//    TextView tvSessionWaitQRBusy;
    @Nullable
    ImageView imTabUserIcon;

    private HomeViewModel mViewModel;
    private UserViewModel mUserViewModel;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageModel message = MessageUtils.parseMessage(intent);
            if (message == null) return;

            switch (message.getType()) {
                case USER_SESSION_ADDED_BY_OWNER:
                    mViewModel.updateResults();
                    onSessionStatusClick();
                    break;
                case SHOP_MEMBER_ADDED:
                    getAccountViewModel().updateResults();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(startToggle);
        startToggle.syncState();

        HomeFragmentAdapter adapter = new HomeFragmentAdapter(getSupportFragmentManager());
        tabLayout.setupWithViewPager(vpHome);
        vpHome.setAdapter(adapter);
        vpHome.setEnabled(false);
        adapter.setupWithTab(tabLayout, vpHome);

        vpHome.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    toolbarHome.setVisibility(View.GONE);
                    imNavigation.setColorFilter(0);
                    imTabUserIcon.setBackground(getResources().getDrawable(R.drawable.shape_oval_orange_gradient));
                    imTabUserIcon.setPadding(6,6,6,6);
                }else if (position == 1){
                    launchScanner();
                    vpHome.setCurrentItem(0);
                    resetUserIcon();
                }else{
                    toolbarHome.setVisibility(View.VISIBLE);
                    imNavigation.setColorFilter(R.color.brownish_grey);
                    resetUserIcon();
                }
            }
        });

        initRefreshScreen(R.id.sr_home);
        getNavAccount().setNavigationItemSelectedListener(this);
        setup();
        explainQr();
    }

    @Override
    protected void updateScreen() {
        mViewModel.updateResults();
        mUserViewModel.updateResults();
        getAccountViewModel().updateResults();
    }

    public void enableDisableSwipeRefresh(boolean enable) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(enable);
        }
    }

    private void setup() {
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        userViewModel.getUserData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                UserModel data = resource.getData();
                if (imTabUserIcon != null) {
                    Utils.loadImageOrDefault(imTabUserIcon, data.getProfilePic(), (data.getGender() == UserModel.GENDER.MALE) ? R.drawable.cover_unknown_male : R.drawable.cover_unknown_female);
                }
                stopRefreshing();
            } else if (resource.getStatus() == Resource.Status.LOADING) startRefreshing();
            else stopRefreshing();
        });

        mViewModel.getQrResult().observe(this, resource -> {
            if (resource == null) return;
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                mViewModel.updateResults();
                onSessionStatusClick();
                Utils.toast(this, resource.getData().getDetail());
            } else if (resource.getStatus() != Resource.Status.LOADING) {
                Utils.toast(this, resource.getMessage());
            }
        });
        mViewModel.getSessionStatus().observe(this, resource -> {
            if (resource == null)
                return;

            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                sessionActiveStatus();
//                tvSessionStatus.setText(resource.getData().getLiveStatus());

                Intent serviceIntent = new Intent(this, ActiveSessionNotificationService.class);
                serviceIntent.setAction(Constants.SERVICE_ACTION_FOREGROUND_START);
                serviceIntent.putExtra(ACTIVE_RESTAURANT_DETAIL, resource.getData().getRestaurant());
                serviceIntent.putExtra(ACTIVE_SESSION_PK, resource.getData().getPk());
                startService(serviceIntent);
            } else if (resource.getStatus() == Resource.Status.ERROR_NOT_FOUND) {
                sessionInactive();
                ActiveSessionNotificationService.clearNotification(getApplicationContext());
            } else if (resource.getProblem() != null && resource.getProblem().getErrorCode() == ProblemModel.ERROR_CODE.SESSION_USER_PENDING_MEMBER) {
                sessionWaitingStatus();
//                tvSessionWaitQRBusy.setText(resource.getProblem().getDetail());
            }
        });

        mViewModel.getCancelDineInData().observe(this, objectNodeResource -> {
            if (objectNodeResource == null)
                return;
            if (objectNodeResource.getStatus() == Resource.Status.SUCCESS) {
                sessionInactive();
            }
        });

        mViewModel.fetchSessionStatus();
        mViewModel.fetchNearbyRestaurants();
    }

    private void sessionInactive() {
//        vSessionStatus.setVisibility(View.GONE);
        vSessionActiveStatus.setVisibility(View.GONE);
        vSessionWaitingStatus.setVisibility(View.GONE);
    }

    private void sessionActiveStatus() {
//        vSessionStatus.setVisibility(View.GONE);
        vSessionActiveStatus.setVisibility(View.VISIBLE);
        vSessionWaitingStatus.setVisibility(View.GONE);
//        vSessionStatus.setEnabled(true);
    }

    private void sessionWaitingStatus() {
//        vSessionStatus.setVisibility(View.GONE);
        vSessionActiveStatus.setVisibility(View.GONE);
        vSessionWaitingStatus.setVisibility(View.VISIBLE);
//        vSessionStatus.setEnabled(false);
    }

    private void resetUserIcon(){
        imTabUserIcon.setBackground(null);
        imTabUserIcon.setPadding(0,0,0,0);
    }

    private void explainQr() {
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        if (tab != null) {
            View qrView = tab.getCustomView();
            OnBoardingUtils.conditionalOnBoarding(this, SP_ONBOARDING_QR_SCANNER, true, new OnBoardingModel("Scan Checkin QR!", qrView, false));
        }
    }

    @OnClick(R.id.container_home_user_location)
    public void onLocationClick(){
        startActivity(new Intent(this, CurrentLocationActivity.class ));
    }

    @OnClick(R.id.iv_home_navigation)
    public void onViewClicked() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.container_home_session_active_status)
    public void onSessionStatusClick() {
//        vSessionStatus.setEnabled(false);
        startActivity(new Intent(this, ActiveSessionActivity.class));
    }

    /*@OnClick(R.id.im_home_session_wait_cancel)
    public void onCancelClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Are you sure you want to cancel the request?")
                .setPositiveButton("Ok", (dialog, which) -> mViewModel.cancelUserWaitingDineIn())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }*/

    @Override
    protected int getDrawerRootId() {
        return R.id.drawer_home;
    }

    @Override
    protected int getNavMenu() {
        return R.menu.drawer_home;
    }

    @Override
    protected <T extends AccountHeaderViewHolder> T getHeaderViewHolder() {
        return (T) new AccountHeaderViewHolder(this, R.layout.layout_header_account);
    }

    @Override
    protected AccountModel.ACCOUNT_TYPE[] getAccountTypes() {
        return new AccountModel.ACCOUNT_TYPE[]{AccountModel.ACCOUNT_TYPE.USER};
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_QR_SCANNER && resultCode == RESULT_OK) {
            String qrData = data.getStringExtra(QRScannerActivity.KEY_QR_RESULT);
            mViewModel.processQr(qrData);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_new_shop:
                startActivity(new Intent(this, BusinessFeaturesActivity.class));
                return true;
        }
        return false;
    }

    private void launchScanner() {
        Intent intent = new Intent(this, QRScannerActivity.class);
        startActivityForResult(intent, REQUEST_QR_SCANNER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.updateResults();
//        vSessionStatus.setEnabled(true);
        enableDisableSwipeRefresh(true);
        MessageModel.MESSAGE_TYPE[] types = new MessageModel.MESSAGE_TYPE[]{
                MessageModel.MESSAGE_TYPE.USER_SESSION_ADDED_BY_OWNER, MessageModel.MESSAGE_TYPE.SHOP_MEMBER_ADDED
        };

        MessageUtils.registerLocalReceiver(this, mReceiver, types);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(this, mReceiver);
    }

    @OnClick(R.id.btn_home_scanner)
    public void onScannerClick() {
        launchScanner();
    }

    private class HomeFragmentAdapter extends BaseFragmentAdapterBottomNav {

        public HomeFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getTabDrawable(int position) {
            if (position == 0)
                return R.drawable.ic_home_toggle;
            if (position == 1)
                return 0;
            return 0;
        }

        @Override
        public int getCustomView(int position) {
            if (position == 2)
                return R.layout.view_tab_bottom_nav_circle;
            return super.getCustomView(position);
        }

        @Override
        protected void bindTabIcon(ImageView imIcon, int position) {
            if (position == 2) {
                imTabUserIcon = imIcon;
                imIcon.setImageResource(R.drawable.cover_unknown_male);
            } else super.bindTabIcon(imIcon, position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return UserHomeFragment.Companion.newInstance();
                case 1:
                    return BlankFragment.newInstance();
                case 2:
                    return UserPrivateProfileFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
