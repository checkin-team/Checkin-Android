package com.checkin.app.checkin.Home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Account.AccountModel.ACCOUNT_TYPE;
import com.checkin.app.checkin.Account.BaseAccountActivity;
import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Menu.SessionUserActivity;
import com.checkin.app.checkin.Misc.QRScannerActivity;
import com.checkin.app.checkin.Notifications.NotificationActivity;
import com.checkin.app.checkin.Profile.ShopProfile.ShopProfileActivity2;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.RestaurantActivity.Waiter.WaitorWork;
import com.checkin.app.checkin.Search.SearchActivity;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionActivity;
import com.checkin.app.checkin.Shop.ShopJoin.BusinessFeaturesActivity;
import com.checkin.app.checkin.Shop.ShopPublicProfile.ShopActivity;
import com.checkin.app.checkin.Shop.ShopReview.ShopReviewFragment;
import com.checkin.app.checkin.User.NonPersonalProfile.UserViewModel;
import com.checkin.app.checkin.User.PersonalProfile.UserProfileActivity;
import com.checkin.app.checkin.Utility.ClipRevealFrame;
import com.checkin.app.checkin.Utility.ItemClickSupport;
import com.checkin.app.checkin.Utility.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseAccountActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int REQUEST_QR_SCANNER = 202;

    @BindView(R.id.drawer_root)
    DrawerLayout drawerLayout;
    @BindView(R.id.rv_trending_shops)
    RecyclerView rvTrendingShops;
    @BindView(R.id.rv_user_activities)
    RecyclerView rvUserActivities;
    @BindView(R.id.root_home)
    View vRoot;
    @BindView(R.id.dark_back_mask)
    View vClipRevealMask;
    @BindView(R.id.dark_back_reveal)
    ClipRevealFrame vClipRevealFrame;
    @BindView(R.id.add_quarter_circle)
    View vAddQuarterCircle;
    @BindView(R.id.fab_home_add)
    ImageView fabHomeAdd;

    @BindView(R.id.text_shops_category)
    TextView tvShopsCategory;
    @BindView(R.id.im_shop_category_back)
    ImageView imShopsCategoryBack;

    private UserViewModel mUserViewModel;
    private HomeViewModel mHomeViewModel;
    private TrendingShopAdapter mTrendingShopAdapter;
    private UserActivityAdapter mUserActivityAdapter;
//    private final float PERCENT_LEFT_SHIFTED = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        mUserViewModel = ViewModelProviders.of(this, new UserViewModel.Factory(getApplication())).get(UserViewModel.class);
        mHomeViewModel = ViewModelProviders.of(this, new HomeViewModel.Factory(getApplication())).get(HomeViewModel.class);

        setupUiStuff();
        setupTrendingShops();
        setupUserActivities();

        mHomeViewModel.getQrResult().observe(this, resource -> {
            if (resource == null)   return;
            if (resource.status == Status.SUCCESS && resource.data != null) {
                Util.toast(this, resource.data.getDetail());
            } else if (resource.status != Status.LOADING) {
                Util.toast(this, resource.message);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupUiStuff() {
        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_root);
        ActionBarDrawerToggle startToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(startToggle);
        startToggle.syncState();

        getNavAccount().setNavigationItemSelectedListener(this);

        vClipRevealMask.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                hideAddMenu();
            return true;
        });
        vAddQuarterCircle.setOnTouchListener((v, event) -> true);

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        final float maxTranslationX = PERCENT_LEFT_SHIFTED * displayMetrics.widthPixels / 100;
//        rvTrendingShops.setTranslationX(maxTranslationX);
//        //((LinearLayoutManager) rvTrendingShops.getLayoutManager()).fin
//        rvTrendingShops.setOnTouchListener(new DragTouchListener() {
//            @Override
//            public boolean shouldDrag() {
//                Log.e(TAG, "shouldDrag: "+(rvTrendingShops.getTranslationX()>0) );
//                if(rvTrendingShops.getTranslationX()>0)
//                    return true;
//                return false;
//            }
//
//            boolean drag(float dx) {
//                boolean shouldDrag = false;
//                if (rvTrendingShops.getTranslationX() > 0 && dx < 0) {
//                    shouldDrag = true;
//                } else if (hasMinTranslation() && dx > 0) {
//                    int position = ((LinearLayoutManager) rvTrendingShops.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
//                    if (position == 0)
//                        shouldDrag = true;
//                } else if (!hasMaxTranslation() && !hasMinTranslation() && dx > 0) {
//                    shouldDrag = true;
//                } else if (hasMaxTranslation() && dx > 0) {
//                    return true;
//                }
//                if (shouldDrag) {
//                    animateShopContainer(rvTrendingShops.getTranslationX() + dx, maxTranslationX);
//                }
//                return shouldDrag;
//            }
//
//            boolean hasMinTranslation() {
//                return rvTrendingShops.getTranslationX() <= 0f;
//            }
//
//            boolean hasMaxTranslation() {
//                return rvTrendingShops.getTranslationX() >= maxTranslationX;
//            }
//
//            @Override
//            public boolean onDragX(float dx) {
//                float currTranslationX = rvTrendingShops.getTranslationX();
//                if (currTranslationX < 0f && currTranslationX > maxTranslationX) {
//                    Log.e(TAG, "outsideLimits!");
//                    animateShopContainer(currTranslationX < 0 ? 0 : maxTranslationX, maxTranslationX);
//                    return false;
//                }
//                if (drag(dx))
//                    return true;
//                //rvTrendingShops.scrollBy((int) -dx, 0);
//                return false;
//            }
//
//            @Override
//            public boolean onDragY(float dy) {
//                return false;
//            }
//
//            @Override
//            public void onDragCancel() {
//                Log.e(TAG, "DragCancelled");
//                ValueAnimator animator;
//                float currTranslationX = rvTrendingShops.getTranslationX();
//                if (currTranslationX <= 0.5 * maxTranslationX) {
//                    animator = ValueAnimator.ofFloat(currTranslationX, 0f);
//                }
//                else  if(currTranslationX<maxTranslationX){
//                    rvTrendingShops.setTranslationX(maxTranslationX);
//                    animator = ValueAnimator.ofFloat(currTranslationX, maxTranslationX);
//                }
//                else return;
//                animator.setInterpolator(new AccelerateDecelerateInterpolator());
//                animator.setDuration(Util.DEFAULT_DURATION);
//                animator.addUpdateListener(animation -> {
//                    float animatedValue = (float) animation.getAnimatedValue();
//                    animateShopContainer(animatedValue, maxTranslationX);
//                });
//                animator.start();
//            }
//        });
//
//       // rvTrendingShops.setOnTouchListener();
//
////        rvTrendingShops.addOnScrollListener(new RecyclerView.OnScrollListener() {
////            /**
////             * Callback method to be invoked when the RecyclerView has been scrolled. This will be
////             * called after the scroll has completed.
////             * <p>
////             * This callback will also be called if visible item range changes after a layout
////             * calculation. In that case, dx and dy will be 0.
////             *
////             * @param recyclerView The RecyclerView which scrolled.
////             * @param dx           The amount of horizontal scroll.
////             * @param dy           The amount of vertical scroll.
////             */
////            @Override
////            public_selected void onScrolled(RecyclerView recyclerView, int dx, int dy) {
////                if(false)
////                {
////                    super.onScrolled(recyclerView,dx,dy);
////                    return;
////                }
////                Log.e(TAG, "onScrolled: CODE"+dx );
////                //int position = ((LinearLayoutManager) rvTrendingShops.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
////                boolean shouldDrag = false;
////                if (rvTrendingShops.getTranslationX() > 0 && dx > 0) {
////                    shouldDrag = true;
////                } else if (hasMinTranslation() && dx < 0) {
////                    int position = ((LinearLayoutManager) rvTrendingShops.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
////                    if (position == 0)
////                        shouldDrag = true;
////                } else if (!hasMaxTranslation() && !hasMinTranslation() && dx < 0) {
////                    shouldDrag = true;
////                } else if (hasMaxTranslation() && dx < 0) {
////                    return;
////                }
////                if (shouldDrag) {
////                    animateShopContainer(rvTrendingShops.getTranslationX() - dx, maxTranslationX);
////                    rvTrendingShops.scrollToPosition(0);
////                }
////                else
////                    super.onScrolled(recyclerView, dx, dy);
////            }
////            boolean hasMinTranslation() {
////                return rvTrendingShops.getTranslationX() <= 0f;
////            }
////
////            boolean hasMaxTranslation() {
////                return rvTrendingShops.getTranslationX() >= maxTranslationX;
////            }
////        });
//        final GestureDetector gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener() {
//
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Fragment fragment = getSupportFragmentManager().findFragmentByTag("RECENT_CHECKIN");
//
//                if(fragment == null)
//                    return false;
//                FragmentManager fm = getSupportFragmentManager();
//                fm.popBackStack();
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.remove(fragment);
//
//                RecentCheckInFragment rcif=new RecentCheckInFragment();
//                Slide slide = new Slide();
//                slide.setDuration(500);
//                slide.setInterpolator(new DecelerateInterpolator());
//                rcif.setEnterTransition(slide);
//                ft.add(R.id.fragmentHolder,rcif,"RECENT_CHECKIN");
//                //ft.replace(R.id.card_user_activities,new RecentCheckInFragment(),"RECENT_CHECKIN");
//                ft.addToBackStack(null);
//                ft.commit();
//                Log.e(TAG, "onSingleTapConfirmed: " );
//                return  true;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//                Fragment fragment = getSupportFragmentManager().findFragmentByTag("RECENT_CHECKIN");
//                if(fragment != null)
//                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//                FragmentManager fm = getSupportFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                RecentCheckInFragment rcif=new RecentCheckInFragment();
//                Slide slide = new Slide();
//                slide.setDuration(500);
//                slide.setInterpolator(new DecelerateInterpolator());
//                rcif.setEnterTransition(slide);
//                ft.add(R.id.fragmentHolder,rcif,"RECENT_CHECKIN");
//                //ft.replace(R.id.card_user_activities,new RecentCheckInFragment(),"RECENT_CHECKIN");
//                ft.addToBackStack(null);
//                ft.commit();
//                Log.e(TAG, "Longpress detected");
//            }
//
//        });
//
//
//        rvTrendingShops.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
//                rv.requestDisallowInterceptTouchEvent(true);
//                Log.e(TAG, "LongGood till here" +event );
//                gestureDetector.onTouchEvent(event);
//                return false;
//                //return gestureDetector.onTouchEvent(e);
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });
    }

    void animateShopContainer(float currTranslateX, float maxTranslateX) {
        float fractionTranslateX = currTranslateX / maxTranslateX;
        imShopsCategoryBack.setAlpha(fractionTranslateX);
        tvShopsCategory.setAlpha(fractionTranslateX);
        rvTrendingShops.setTranslationX(currTranslateX);
    }

    private void setupTrendingShops() {
        rvTrendingShops.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        mTrendingShopAdapter = new TrendingShopAdapter(null);
        rvTrendingShops.setAdapter(mTrendingShopAdapter);

        mHomeViewModel.getTrendingRestaurants().observe(this, listResource -> {
            if (listResource != null && listResource.status == Status.SUCCESS)
                mTrendingShopAdapter.setData(listResource.data);
        });

        ItemClickSupport.addTo(rvTrendingShops).setOnItemClickListener((recyclerView, position, v) -> {
            Intent intent = new Intent(getApplicationContext(), ShopActivity.class);
            intent.putExtra(ShopActivity.KEY_SHOP_PK, mTrendingShopAdapter.getByPosition(position).getId());
            startActivity(intent);
        });
    }

    private void setupUserActivities() {
        rvUserActivities.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        mUserActivityAdapter = new UserActivityAdapter(null, this);
        rvUserActivities.setAdapter(mUserActivityAdapter);

        ItemClickSupport.addTo(rvUserActivities).setOnItemClickListener((recyclerView, position, v) -> {
            Intent intent =new Intent(getApplicationContext(), com.checkin.app.checkin.User.NonPersonalProfile.UserProfileActivity.class);
            intent.putExtra(com.checkin.app.checkin.User.NonPersonalProfile.UserProfileActivity.KEY_PROFILE_USER_ID, mUserActivityAdapter.getUserByPosition(position).getId());
            startActivity(intent);
        });

        mUserViewModel.getAllUsers().observe(this, (userResource -> {
            if (userResource != null && userResource.status == Status.SUCCESS)
                mUserActivityAdapter.setUsers(userResource.data);
        }));
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
            case R.id.nav_settings:
                SessionUserActivity.startSession(this, 1, 1);
                break;
            case R.id.nav_privacy_settings:
                intent = new Intent(getApplicationContext(), ShopProfileActivity2.class);
                startActivity(intent);
                break;
            case R.id.notif_activity:
                intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
                break;
            case R.id.waiter_work_activity:
                intent = new Intent(getApplicationContext(), WaitorWork.class);
                startActivity(intent);
                break;
            case R.id.search_activity:
                intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_QR_SCANNER && resultCode == RESULT_OK) {
            String qrData = data.getStringExtra(QRScannerActivity.KEY_QR_RESULT);
            mHomeViewModel.processQr(qrData);
        }
    }

    @OnClick(R.id.appbar_title)
    public void onQrScannerClick(View v) {
        Intent intent = new Intent(getApplicationContext(), QRScannerActivity.class);
        startActivityForResult(intent, REQUEST_QR_SCANNER);
    }

    private float[] computeAddViewDetails(View v) {
        int x = (fabHomeAdd.getLeft() + fabHomeAdd.getRight()) / 2;
        int y = (fabHomeAdd.getTop() + fabHomeAdd.getBottom()) / 2;
        float radiusOfFab = fabHomeAdd.getWidth() / 2f;
        float radiusFromFabToRoot = (float) Math.hypot(
                Math.max(x, v.getWidth() - x),
                Math.max(y, v.getHeight() - y));
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
        float[] data = computeAddViewDetails(vRoot);
        List<Animator> animList = new ArrayList<>();
        vClipRevealFrame.setVisibility(View.VISIBLE);

        vClipRevealMask.setVisibility(View.VISIBLE);
        Animator revealAnim = Util.createCircularRevealAnimator(vClipRevealFrame, (int) data[0], (int) data[1], data[2], data[3]);
        animList.add(revealAnim);

        Animator rotateAnim = Util.createRotationAnimator(fabHomeAdd, -225);
        animList.add(rotateAnim);
        Animator tintAnim = Util.createTintAnimator(fabHomeAdd, 0, getResources().getColor(R.color.white));
        animList.add(tintAnim);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animList);
        animatorSet.setDuration(450L);
        animatorSet.start();

        fabHomeAdd.setSelected(true);
    }

    private void hideAddMenu() {
        float[] data = computeAddViewDetails(vRoot);
        List<Animator> animList = new ArrayList<>();

        Animator revealAnim = Util.createCircularRevealAnimator(vClipRevealFrame, (int) data[0], (int) data[1], data[3], data[2]);
        animList.add(revealAnim);

        Animator rotateAnim = Util.createRotationAnimator(fabHomeAdd, 0);
        animList.add(rotateAnim);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator tintAnim = Util.createTintAnimator(fabHomeAdd, 0, getResources().getColor(R.color.primary_red));
            animList.add(tintAnim);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                vClipRevealFrame.setVisibility(View.GONE);
            }
        });
        animatorSet.setDuration(450L);
        animatorSet.start();

        fabHomeAdd.setSelected(false);
    }

    @OnClick(R.id.action_dine_in)
    public  void dine_in(View v){
        startActivity(new Intent(this,ActiveSessionActivity.class));
    }

    @OnClick(R.id.action_delivery)
    public void delivery(View v) {
        /*startActivity(new Intent(this, BusinessFeaturesActivity.class));*/
        ShopReviewFragment fragment = new ShopReviewFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentHolder,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        }

    @Override
    protected int getNavMenu() {
        return R.menu.main_navigation_menu;
    }

    @Override
    protected <T extends AccountHeaderViewHolder> T getHeaderViewHolder() {
        return (T) new AccountHeaderViewHolder(this, R.layout.layout_header_account);
    }

    @Override
    protected ACCOUNT_TYPE[] getAccountTypes() {
        return new ACCOUNT_TYPE[] { ACCOUNT_TYPE.USER };
    }
}
