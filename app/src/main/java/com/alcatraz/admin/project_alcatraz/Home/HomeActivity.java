package com.alcatraz.admin.project_alcatraz.Home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.Profile.ShopProfile.ShopProfileActivity;
import com.alcatraz.admin.project_alcatraz.Profile.ShopProfile.ShopProfileActivity2;
import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Session.SessionUserActivity;
import com.alcatraz.admin.project_alcatraz.Shop.Shop;
import com.alcatraz.admin.project_alcatraz.Social.ChatActivity;
import com.alcatraz.admin.project_alcatraz.Social.ChatAdapter;
import com.alcatraz.admin.project_alcatraz.Social.MessageViewModel;
import com.alcatraz.admin.project_alcatraz.User.UserViewModel;
import com.alcatraz.admin.project_alcatraz.Utility.ClipRevealFrame;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;
import com.alcatraz.admin.project_alcatraz.Utility.EndDrawerToggle;
import com.alcatraz.admin.project_alcatraz.Utility.ItemClickSupport;
import com.alcatraz.admin.project_alcatraz.Utility.Util;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = HomeActivity.class.getSimpleName();
    @BindView(R.id.drawer_home)
    DrawerLayout drawerLayout;
    @BindView(R.id.rv_nav_messages)
    RecyclerView rvNavMessages;
    @BindView(R.id.rv_trending_shops)
    RecyclerView rvTrendingShops;
    @BindView(R.id.rv_user_activities)
    RecyclerView rvUserActivities;
    @BindView(R.id.root_home)
    View vRoot;
    @BindView(R.id.dark_back_reveal)
    ClipRevealFrame vClipRevealFrame;
    @BindView(R.id.fab_home_add)
    FloatingActionButton fabHomeAdd;
    @BindView(R.id.btn_scanner)
    View btnQrScanner;

    private UserViewModel mUserViewModel;
    private MessageViewModel mMessageViewModel;
    private ChatAdapter mChatAdapter;
    private TrendingShopAdapter mTrendingShopAdapter;
    private UserActivityAdapter mUserActivityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        mUserViewModel = ViewModelProviders.of(this, new UserViewModel.Factory(getApplication())).get(UserViewModel.class);
        mMessageViewModel = ViewModelProviders.of(this, new MessageViewModel.Factory(getApplication())).get(MessageViewModel.class);

        setupUiStuff();
        setupTrendingShops();
        setupUserActivities();
        setupMessages();
    }

    private void setupUiStuff() {
        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_home);
        ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(startToggle);
        startToggle.syncState();

        EndDrawerToggle endToggle = new EndDrawerToggle(
                this, drawerLayout, toolbar, R.string.messages_drawer_open, R.string.messages_drawer_close, R.drawable.ic_message_appbar);
        drawerLayout.addDrawerListener(endToggle);
        endToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_main);
        navigationView.setNavigationItemSelectedListener(this);

        vClipRevealFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    hideAddMenu();
                return true;
            }
        });

    }

    private void setupTrendingShops() {
        List<Shop> shops = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            if (i % 2 == 0)
                shops.add(new Shop(i, "Socials", "Bar"));
            else
                shops.add(new Shop(i, "Family First", "Restaurant"));
        }
        rvTrendingShops.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        mTrendingShopAdapter = new TrendingShopAdapter(shops);
        rvTrendingShops.setAdapter(mTrendingShopAdapter);
    }

    private void setupUserActivities() {
        rvUserActivities.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        mUserActivityAdapter = new UserActivityAdapter(null);
        rvUserActivities.setAdapter(mUserActivityAdapter);

        mUserViewModel.getAllUsers().observe(this, (users -> mUserActivityAdapter.setUsers(users)));
    }

    private void setupMessages() {
        mChatAdapter = new ChatAdapter(null);
        rvNavMessages.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));
        rvNavMessages.setAdapter(mChatAdapter);

        mMessageViewModel.getBriefChats().observe(this, briefChats -> mChatAdapter.setBriefChats(briefChats));
        ItemClickSupport.addTo(rvNavMessages).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                intent.putExtra(Constants.EXTRA_SELECTED_USER_ID, mChatAdapter.getBriefChat(position).userId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START) || drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawers();
        } else if (fabHomeAdd.isSelected()) {
            hideAddMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.nav_profile:
                intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_wallet:
                intent = new Intent(getApplicationContext(), TransactionActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(getApplicationContext(), SessionUserActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_privacy_settings:
                intent = new Intent(getApplicationContext(), ShopProfileActivity2.class);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // cancelled operation
            } else {
                String requestJson = String.format("{\"data\": %s}", result.getContents());
                String response = Util.postApi(Constants.API_URL_DECRYPT_QR, requestJson);
                try {
                    JSONObject object = new JSONObject(response);
                    SessionUserActivity.startSession(this, object.getInt("shop_id"), object.getInt("qr_id"));
                } catch (JSONException e) {
                    Toast.makeText(this, "Invalid QR!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.btn_scanner)
    public void onQrScannerClick(View v) {
        IntentIntegrator qrScan = new IntentIntegrator(this);
        qrScan.setPrompt("Scan the QR code, present on the table!");
        qrScan.setOrientationLocked(false);
        qrScan.initiateScan();
    }

    private float[] computeAddViewDetails() {
        int x = (fabHomeAdd.getLeft() + fabHomeAdd.getRight()) / 2;
        int y = (fabHomeAdd.getTop() + fabHomeAdd.getBottom()) / 2;
        float radiusOfFab = fabHomeAdd.getWidth() / 2f;
        float radiusFromFabToRoot = (float) Math.hypot(
                Math.max(x, vRoot.getWidth() - x),
                Math.max(y, vRoot.getHeight() - y));
        return new float[]{x, y, radiusOfFab, radiusFromFabToRoot};
    }

    @OnClick(R.id.fab_home_add)
    public void onAddClick(View v) {
        if (v.isSelected()) {
            hideAddMenu();
        } else {
            showAddMenu();
        }
    }

    private void showAddMenu() {
        float[] data = computeAddViewDetails();
        List<Animator> animList = new ArrayList<>();

        vClipRevealFrame.setVisibility(View.VISIBLE);
        Animator revealAnim = Util.createCircularRevealAnimator(vClipRevealFrame, (int) data[0], (int) data[1], data[2], data[3]);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        animList.add(revealAnim);

        Animator rotateAnim = Util.createRotationAnimator(fabHomeAdd, -225);
        animList.add(rotateAnim);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator tintAnim = Util.createTintAnimator(fabHomeAdd, 0, getResources().getColor(R.color.white));
            animList.add(tintAnim);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animList);
        animatorSet.start();

        fabHomeAdd.setSelected(true);
    }

    private void hideAddMenu() {
        float[] data = computeAddViewDetails();
        List<Animator> animList = new ArrayList<>();

        Animator revealAnim = Util.createCircularRevealAnimator(vClipRevealFrame, (int) data[0], (int) data[1], data[3], data[2]);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                vClipRevealFrame.setVisibility(View.INVISIBLE);
            }
        });
        animList.add(revealAnim);

        Animator rotateAnim = Util.createRotationAnimator(fabHomeAdd, 0);
        animList.add(rotateAnim);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator tintAnim = Util.createTintAnimator(fabHomeAdd, 0, getResources().getColor(R.color.colorPrimaryRed));
            animList.add(tintAnim);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animList);
        animatorSet.start();

        fabHomeAdd.setSelected(false);
    }

}
