package com.checkin.app.checkin.Home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Account.AccountModel;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Data.Message.ActiveSessionNotification;
import com.checkin.app.checkin.Data.Message.Constants;
import com.checkin.app.checkin.Data.Message.MessageModel;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseFragmentAdapterBottomNav;
import com.checkin.app.checkin.Misc.BlankFragment;
import com.checkin.app.checkin.Misc.QRScannerActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionActivity;
import com.checkin.app.checkin.Shop.ShopJoin.BusinessFeaturesActivity;
import com.checkin.app.checkin.User.Private.UserPrivateProfileFragment;
import com.checkin.app.checkin.User.Private.UserViewModel;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.DynamicSwipableViewPager;
import com.checkin.app.checkin.Utility.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Data.Message.ActiveSessionNotification.ACTIVE_RESTAURANT_LOGO;
import static com.checkin.app.checkin.Data.Message.ActiveSessionNotification.ACTIVE_RESTAURANT_NAME;
import static com.checkin.app.checkin.Data.Message.ActiveSessionNotification.ACTIVE_RESTAURANT_PK;
import static com.checkin.app.checkin.Data.Message.ActiveSessionNotification.ACTIVE_SESSION_PK;

public class HomeActivity extends BaseAccountActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_QR_SCANNER = 212;

    @BindView(R.id.drawer_home)
    DrawerLayout drawerLayout;
    @BindView(R.id.iv_home_navigation)
    ImageView imNavigation;
    @BindView(R.id.tabs_home)
    TabLayout tabLayout;
    @BindView(R.id.vp_home)
    DynamicSwipableViewPager vpHome;

    @BindView(R.id.container_home_session_status)
    ViewGroup vSessionStatus;
    @BindView(R.id.tv_home_session_active_status)
    TextView tvSessionStatus;

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
                if (position == 1) {
                    launchScanner();
                    vpHome.setCurrentItem(0);
                }
            }
        });

        getNavAccount().setNavigationItemSelectedListener(this);

        initRefreshScreen(R.id.sr_home);

        setup();
    }

    @Override
    protected void updateScreen() {
        mViewModel.updateResults();
        mUserViewModel.updateResults();
        getAccountViewModel().updateResults();
    }

    private void setup() {
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        userViewModel.getUserData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                UserModel data = resource.data;
                if (imTabUserIcon != null) {
                    Utils.loadImageOrDefault(imTabUserIcon, data.getProfilePic(), (data.getGender() == UserModel.GENDER.MALE) ? R.drawable.cover_unknown_male : R.drawable.cover_unknown_female);
                }
                stopRefreshing();
            } else if (resource.status == Resource.Status.LOADING) startRefreshing();
            else stopRefreshing();
        });

        mViewModel.getQrResult().observe(this, resource -> {
            if (resource == null) return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                mViewModel.updateResults();
                onSessionStatusClick();
                Utils.toast(this, resource.data.getDetail());
            } else if (resource.status != Resource.Status.LOADING) {
                Utils.toast(this, resource.message);
            }
        });
        mViewModel.getSessionStatus().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                vSessionStatus.setVisibility(View.VISIBLE);
                tvSessionStatus.setText(resource.data.getLiveStatus());

                Intent serviceIntent = new Intent(this, ActiveSessionNotification.class);
                serviceIntent.setAction(Constants.STARTFOREGROUND_ACTION);
                serviceIntent.putExtra(ACTIVE_RESTAURANT_NAME,resource.data.getRestaurant().getDisplayName());
                serviceIntent.putExtra(ACTIVE_RESTAURANT_LOGO,resource.data.getRestaurant().getDisplayPic());
                serviceIntent.putExtra(ACTIVE_RESTAURANT_PK,resource.data.getRestaurant().getPk());
                serviceIntent.putExtra(ACTIVE_SESSION_PK,resource.data.getPk());
                startService(serviceIntent);
            } else if (resource.status == Resource.Status.ERROR_NOT_FOUND) {
                vSessionStatus.setVisibility(View.GONE);
                Intent serviceIntent = new Intent(this, ActiveSessionNotification.class);
                serviceIntent.setAction(Constants.STOPFOREGROUND_ACTION);
                startService(serviceIntent);
            }
        });

        mViewModel.fetchSessionStatus();
    }

    @OnClick(R.id.iv_home_navigation)
    public void onViewClicked() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.container_home_session_status)
    public void onSessionStatusClick() {
        startActivity(new Intent(this, ActiveSessionActivity.class));
    }

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
        MessageUtils.registerLocalReceiver(this, mReceiver, MessageModel.MESSAGE_TYPE.USER_SESSION_ADDED_BY_OWNER);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageUtils.unregisterLocalReceiver(this, mReceiver);
    }

    private class HomeFragmentAdapter extends BaseFragmentAdapterBottomNav {

        public HomeFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getTabDrawable(int position) {
            if (position == 1)
                return R.drawable.ic_qr_code_grey;
            return 0;
        }

        @Override
        public int getCustomView(int position) {
            if (position == 0)
                return R.layout.view_tab_bottom_nav_circle;
            return super.getCustomView(position);
        }

        @Override
        protected void bindTabText(TextView tvTitle, int position) {
            tvTitle.setVisibility(View.GONE);
        }

        @Override
        protected void bindTabIcon(ImageView imIcon, int position) {
            if (position == 0) {
                imTabUserIcon = imIcon;
                imIcon.setImageResource(R.drawable.cover_unknown_male);
            } else super.bindTabIcon(imIcon, position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return UserPrivateProfileFragment.newInstance();
                case 1:
                    return BlankFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        protected void onTabClick(int position) {
            if (position == 1) launchScanner();
            else super.onTabClick(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "User";
                case 1:
                    return "QR Scanner";
            }
            return null;
        }
    }
}
