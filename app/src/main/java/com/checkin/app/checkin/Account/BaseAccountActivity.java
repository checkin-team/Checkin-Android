package com.checkin.app.checkin.Account;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Account.AccountModel.ACCOUNT_TYPE;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Home.HomeActivity;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.ShopActivity;
import com.checkin.app.checkin.Utility.GlideApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public abstract class BaseAccountActivity extends BaseActivity {
    private final static String TAG = BaseAccountActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavAccount;

    private AccountViewModel mViewModel;
    private AccountAdapter mAccountAdapter;

    private AccountHeaderViewHolder mHeaderViewHolder;

    protected void setupUi() {
        mDrawerLayout = findViewById(R.id.drawer_root);
        mNavAccount = findViewById(R.id.nav_account);

        mAccountAdapter = new AccountAdapter(getApplicationContext(), R.layout.simple_spinner_item, new ArrayList<>());
        mAccountAdapter.setDropDownViewResource(R.layout.item_simple_spinner_dropdown);

        setupHeader();

        mNavAccount.inflateMenu(getNavMenu());
    }

    private void setupHeader() {
        mHeaderViewHolder = getHeaderViewHolder();

        ACCOUNT_TYPE[] accountTypes = getAccountTypes();

        mViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
        mViewModel.getAccounts().observe(this, listResource -> {
            if (listResource.status == Resource.Status.SUCCESS && listResource.data != null) {
                List<AccountModel> accounts = listResource.data;
                mAccountAdapter.setData(accounts);

                for (ACCOUNT_TYPE accountType: accountTypes) {
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
                Toast.makeText(getApplicationContext(), "User doesn't have access to any accounts with the given account type.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            mHeaderViewHolder.setCurrentAccount(account);
        });

        mNavAccount.addHeaderView(mHeaderViewHolder.getHeaderView());
    }

    protected abstract int getNavMenu();

    protected abstract <T extends AccountHeaderViewHolder> T getHeaderViewHolder();

    protected abstract ACCOUNT_TYPE[] getAccountTypes();

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupUi();
    }

    protected DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    protected NavigationView getNavAccount() {
        return mNavAccount;
    }

    public static class AccountHeaderViewHolder implements AdapterView.OnItemSelectedListener {
        @BindView(R.id.tv_context) TextView tvDisplayName;
        @BindView(R.id.im_context) ImageView imDisplayPic;
        @BindView(R.id.tv_account_desc) TextView tvAccountDesc;
        @BindView(R.id.account_selector) Spinner vAccountSelector;

        private BaseAccountActivity mBaseActivity;
        private final View mHeaderView;

        public AccountHeaderViewHolder(BaseAccountActivity baseAccountActivity, @LayoutRes int headerLayout) {
            mBaseActivity = baseAccountActivity;
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
            vAccountSelector.setAdapter(mBaseActivity.mAccountAdapter);
            vAccountSelector.setOnItemSelectedListener(this);
        }

        void updateAccountSelector(AccountModel account) {
            int pos = mBaseActivity.mAccountAdapter.getPosition(account);
            vAccountSelector.setSelection(pos, true);
        }

        @OnClick(R.id.btn_refresh)
        void onAccountRefreshClick() {
            Toast.makeText(mBaseActivity.getApplicationContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
            mBaseActivity.mViewModel.updateResults();
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            AccountModel account = mBaseActivity.mAccountAdapter.getItem(position);
            mBaseActivity.mViewModel.setCurrentAccount(account);
            switchAccount(mBaseActivity.getApplicationContext(), account);
        }

        protected void switchAccount(Context context, AccountModel account) {
            switch (account.getAccountType()) {
                case USER:
                    if (mBaseActivity.getClass() != HomeActivity.class)
                        context.startActivity(new Intent(context, HomeActivity.class));
                    break;
                case SHOP_OWNER:
                case SHOP_ADMIN:
                    if (mBaseActivity.getClass() != ShopActivity.class) {
                        Intent intent = new Intent(context, ShopActivity.class);
                        intent.putExtra(ShopActivity.KEY_SHOP_PK, account.getTargetPk());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    break;
                case RESTAURANT_MANAGER:
                case RESTAURANT_WAITER:
                case RESTAURANT_COOK:
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

    public static class AccountAdapter extends ArrayAdapter<AccountModel> {

        AccountAdapter(@NonNull Context context, int resource, @NonNull List<AccountModel> objects) {
            super(context, resource, objects);
        }

        public void setData(List<AccountModel> accounts) {
            clear();
            addAll(accounts);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            AccountModel account = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_spinner_dropdown, parent, false);
            }

            if (account == null) {
                Log.e(TAG, "Account item in " + position + "is NULL");
                return convertView;
            }

            CircleImageView imLogo = convertView.findViewById(R.id.im_display_pic);
            TextView tvName = convertView.findViewById(R.id.tv_display_name);

            tvName.setText(account.getName());
            GlideApp.with(convertView).load(account.getPic()).into(imLogo);

            return convertView;
        }
    }
}
