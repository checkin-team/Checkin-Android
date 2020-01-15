package com.checkin.app.checkin.Account;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.checkin.app.checkin.Account.AccountModel.ACCOUNT_TYPE;
import com.checkin.app.checkin.Cook.CookWorkActivity;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.Private.ShopPrivateActivity;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.GlideApp;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.WaiterWorkActivity;
import com.checkin.app.checkin.manager.activities.ManagerWorkActivity;
import com.checkin.app.checkin.misc.activities.BaseActivity;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseAccountsActivity extends BaseActivity {
    // Required to include "incl_account_base" layout.
    private final static String TAG = BaseAccountsActivity.class.getSimpleName();

    //private DrawerLayout mDrawerLayout;
    //private NavigationView mNavAccount;
    private Button btnLogout;

    private AccountViewModel mViewModel;
    //private AccountAdapter mAccountAdapter;

    private AccountHeaderViewHolder mHeaderViewHolder;

    protected void setupUi() {
        // mDrawerLayout = findViewById(getDrawerRootId());
        // mNavAccount = findViewById(R.id.nav_account);
        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this::onLogoutClick);

        //mAccountAdapter = new AccountAdapter(getApplicationContext(), R.layout.simple_spinner_item, new ArrayList<>());
        //mAccountAdapter.setDropDownViewResource(R.layout.item_simple_spinner_dropdown);

        //setupHeader();
        setupAccountHeader();
        // mNavAccount.inflateMenu(getNavMenu());
        // mNavAccount.setItemIconTintList(null);
    }


    protected void setupAccountHeader() {
        mHeaderViewHolder = getHeaderViewHolder();

        ACCOUNT_TYPE[] accountTypes = getAccountTypes();
        String userName = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.SP_USER_PROFILE_NAME, null);

        mViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);

        mViewModel.getAccounts().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.getStatus() == Resource.Status.SUCCESS && listResource.getData() != null) {
                List<AccountModel> accounts = listResource.getData();


                AccountHeaderBuilder accountHeaderBuilder    = new AccountHeaderBuilder()

                        .withActivity(this)
                        .withHeaderBackground(R.drawable.ic_profile_background);
                for (AccountModel account : accounts) {
                    accountHeaderBuilder.addProfiles(new ProfileDrawerItem()
                            .withName(account.getName())
                            .withEmail(account.formatAccountType())
                            .withIcon(account.getPic()));
                }

                accountHeaderBuilder.withTextColor(ContextCompat.getColor(this, R.color.material_drawer_dark_primary_text));

                AccountHeader headerResult = accountHeaderBuilder.build();



                Drawer result = new DrawerBuilder()
                        .withActivity(this)
                        .withToolbar(findViewById(getToolbarId()))
                        .withAccountHeader(headerResult)
                        .addDrawerItems(
                                new PrimaryDrawerItem().withName("Create shop profile")
                        ).build();




                //have to add all the views to the navigation here


                if (userName == null) {
                    for (AccountModel accountModel : accounts) {
                        if (accountModel.getAccountType() == ACCOUNT_TYPE.USER) {
                            String[] split = accountModel.getName().split("\\s");
                            PreferenceManager.getDefaultSharedPreferences(this).edit()
                                    .putString(Constants.SP_USER_PROFILE_NAME, split[0])
                                    .apply();
                            break;
                        }
                    }
                }

                for (ACCOUNT_TYPE accountType : accountTypes) {
                    AccountModel account = AccountModel.getByAccountType(accounts, accountType);
                    if (account != null) {
                        mViewModel.setCurrentAccount(account);
                        break;
                    }
                }

            }
        });

        mViewModel.getCurrentAccount().observe(this, account -> {
            if (account == null) {
                // User doesn't have rights to access this account.
                Utils.toast(this, "User doesn't have access to any accounts with the given account type.");
                finish();
                return;
            }

            mHeaderViewHolder.setCurrentAccount(account);
        });


    }

    private void setupHeader() {
        mHeaderViewHolder = getHeaderViewHolder();

        ACCOUNT_TYPE[] accountTypes = getAccountTypes();
        String userName = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.SP_USER_PROFILE_NAME, null);

        mViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
        mViewModel.getAccounts().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.getStatus() == Resource.Status.SUCCESS && listResource.getData() != null) {
                List<AccountModel> accounts = listResource.getData();


                // mAccountAdapter.setData(accounts);


                if (userName == null) {
                    for (AccountModel accountModel : accounts) {
                        if (accountModel.getAccountType() == ACCOUNT_TYPE.USER) {
                            String[] split = accountModel.getName().split("\\s");
                            PreferenceManager.getDefaultSharedPreferences(this).edit()
                                    .putString(Constants.SP_USER_PROFILE_NAME, split[0])
                                    .apply();
                            break;
                        }
                    }
                }

                for (ACCOUNT_TYPE accountType : accountTypes) {
                    AccountModel account = AccountModel.getByAccountType(accounts, accountType);
                    if (account != null) {
                        mViewModel.setCurrentAccount(account);
                        break;
                    }
                }
            }
        });

        mViewModel.getCurrentAccount().observe(this, account -> {
            if (account == null) {
                // User doesn't have rights to access this account.
                Utils.toast(this, "User doesn't have access to any accounts with the given account type.");
                finish();
                return;
            }

            mHeaderViewHolder.setCurrentAccount(account);
        });

        //  mNavAccount.addHeaderView(mHeaderViewHolder.getHeaderView());
    }

    protected void onLogoutClick(View v) {
        if (!Utils.logoutFromApp(getApplicationContext()))
            Utils.toast(getApplicationContext(), "Unable to logout. Manually remove account from settings.");
    }

//    @IdRes
//    protected abstract int getDrawerRootId();
//
//    protected abstract int getNavMenu();

    protected abstract <T extends AccountHeaderViewHolder> T getHeaderViewHolder();

    protected abstract ACCOUNT_TYPE[] getAccountTypes();

    @IdRes
    protected abstract int getToolbarId();

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupUi();
        new DrawerBuilder().withActivity(this).build();


    }


    protected AccountViewModel getAccountViewModel() {
        return mViewModel;
    }

//    protected DrawerLayout getDrawerLayout() {
//        return mDrawerLayout;
//    }

//    protected NavigationView getNavAccount() {
//        return mNavAccount;
//    }

    public static class AccountHeaderViewHolder implements AdapterView.OnItemSelectedListener {
        private final View mHeaderView;
        @BindView(R.id.tv_context)
        TextView tvDisplayName;
        @BindView(R.id.im_context)
        ImageView imDisplayPic;
        @BindView(R.id.tv_account_desc)
        TextView tvAccountDesc;
        @BindView(R.id.account_selector)
        Spinner vAccountSelector;
        private BaseAccountsActivity mBaseActivity;

        public AccountHeaderViewHolder(BaseAccountsActivity baseAccountsActivity, @LayoutRes int headerLayout) {
            mBaseActivity = baseAccountsActivity;
            mHeaderView = LayoutInflater.from(mBaseActivity.getApplicationContext()).inflate(headerLayout, null, false);
            ButterKnife.bind(this, mHeaderView);
            setupAccountSelector();
        }

        public View getHeaderView() {
            return mHeaderView;
        }

        void setDisplayName(CharSequence text) {
            tvDisplayName.setText(text);
        }

        void setAccountDesc(CharSequence text) {
            tvAccountDesc.setText(text);
        }

        void setDisplayPic(String url) {
            GlideApp.with(imDisplayPic).load(url).into(imDisplayPic);
        }

        void setupAccountSelector() {
            //vAccountSelector.setAdapter(mBaseActivity.mAccountAdapter);
            vAccountSelector.setOnItemSelectedListener(this);
        }

        void updateAccountSelector(AccountModel account) {
            //int pos = mBaseActivity.mAccountAdapter.getPosition(account);
           // vAccountSelector.setSelection(pos, true);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//           AccountModel account = mBaseActivity.mAccountAdapter.getItem(position);
//            if (account == null) return;
//            AccountModel currAccount = mBaseActivity.mViewModel.getCurrentAccount().getValue();
//            if (!account.equals(currAccount)) {
//                mBaseActivity.mViewModel.setCurrentAccount(account);
//                switchAccount(mBaseActivity.getApplicationContext(), account);
//            }
        }

        void switchAccount(Context context, AccountModel account) {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putInt(Constants.SP_LAST_ACCOUNT_TYPE, account.getAccountType().id)
                    .putLong(Constants.SP_LAST_ACCOUNT_PK, Long.valueOf(account.getTargetPk()))
                    .apply();

            Intent intent;
            switch (account.getAccountType()) {
                case USER:
                    Utils.navigateBackToHome(context);
                    break;
                case SHOP_OWNER:
                case SHOP_ADMIN:
                    intent = Intent.makeRestartActivityTask(new ComponentName(context, ShopPrivateActivity.class));
                    intent.putExtra(ShopPrivateActivity.KEY_SHOP_PK, Long.valueOf(account.getTargetPk()));
                    context.startActivity(intent);
                    break;
                case RESTAURANT_MANAGER:
                    intent = Intent.makeRestartActivityTask(new ComponentName(context, ManagerWorkActivity.class));
                    intent.putExtra(ManagerWorkActivity.KEY_RESTAURANT_PK, Long.valueOf(account.getTargetPk()));
                    context.startActivity(intent);
                    break;
                case RESTAURANT_WAITER:
                    intent = Intent.makeRestartActivityTask(new ComponentName(context, WaiterWorkActivity.class));
                    intent.putExtra(WaiterWorkActivity.KEY_SHOP_PK, Long.valueOf(account.getTargetPk()));
                    context.startActivity(intent);
                    break;
                case RESTAURANT_COOK:
                    intent = Intent.makeRestartActivityTask(new ComponentName(context, CookWorkActivity.class));
                    intent.putExtra(CookWorkActivity.KEY_RESTAURANT_PK, Long.valueOf(account.getTargetPk()));
                    context.startActivity(intent);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        void setCurrentAccount(AccountModel account) {
            this.setDisplayName(account.getName());
            this.setDisplayPic(account.getPic());
            this.setAccountDesc(account.formatAccountType());
            this.updateAccountSelector(account);
        }
    }

//    public static class AccountAdapter extends ArrayAdapter<AccountModel> {
//
//        AccountAdapter(@NonNull Context context, int resource, @NonNull List<AccountModel> objects) {
//            super(context, resource, objects);
//        }
//
//        public void setData(List<AccountModel> accounts) {
//            clear();
//            addAll(accounts);
//        }
//
//        @Override
//        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            AccountModel account = getItem(position);
//
//            if (convertView == null) {
//                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_spinner_dropdown, parent, false);
//            }
//
//            if (account == null) {
//                Log.e(TAG, "Account item in " + position + "is NULL");
//                return convertView;
//            }
//
//            CircleImageView imLogo = convertView.findViewById(R.id.im_display_pic);
//            TextView tvName = convertView.findViewById(R.id.tv_display_name);
//
//            tvName.setText(account.getName());
//            GlideApp.with(convertView).load(account.getPic()).into(imLogo);
//
//            return convertView;
//        }
//    }
}
