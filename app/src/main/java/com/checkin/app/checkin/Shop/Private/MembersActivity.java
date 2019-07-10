package com.checkin.app.checkin.Shop.Private;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Search.SearchActivity;
import com.checkin.app.checkin.Utility.Utils;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MembersActivity extends BaseActivity implements ShopMembersListFragment.MemberListInteraction, MemberAssignRoleFragment.AssignRoleInteraction {
    public static final String KEY_SHOP_PK = "shop_members.pk";
    private static final String TAG = MembersActivity.class.getSimpleName();
    private static final int REQUEST_PICK_USER = 10;

    private ShopMembersListFragment mFragmentShopMembers;
    private MemberViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_members);

        ButterKnife.bind(this);
        initRefreshScreen(R.id.sv_shop_members);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        actionBar.setElevation(10);

        mViewModel = ViewModelProviders.of(this).get(MemberViewModel.class);


        setupShopMembers();
    }

    private void setupShopMembers() {
        long shopPk = getIntent().getLongExtra(KEY_SHOP_PK, 0);
        mViewModel.fetchShopMembers(shopPk);
        mFragmentShopMembers = ShopMembersListFragment.newInstance(this);

        mViewModel.getShopMembers().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.getStatus() == Resource.Status.SUCCESS && listResource.getData() != null) {
                stopRefreshing();
            } else if (listResource.getStatus() == Resource.Status.LOADING) {
                startRefreshing();
            } else {
                stopRefreshing();
                Utils.toast(this, listResource.getMessage());
            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_members, mFragmentShopMembers)
                .commit();
    }

    @OnClick(R.id.btn_add_member)
    public void onClickAddMembers(View view) {
        Intent pickUserIntent = new Intent(this, SearchActivity.class);
        pickUserIntent.putExtra(SearchActivity.KEY_SEARCH_TYPE, SearchActivity.TYPE_PEOPLE);
        pickUserIntent.putExtra(SearchActivity.KEY_SEARCH_MODE, SearchActivity.MODE_SELECT);
        startActivityForResult(pickUserIntent, REQUEST_PICK_USER);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void changeMemberRole(MemberModel member, int position) {
        mViewModel.setCurrentMember(member);

        MemberAssignRoleFragment dialog = MemberAssignRoleFragment.newInstance(position, this);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onNewMember(MemberModel member) {
        mFragmentShopMembers.addMember(member);
    }

    @Override
    public void onUpdateMember(MemberModel member, int position) {
        mFragmentShopMembers.updateMember(member, position);
    }

    @Override
    public void onRemoveMember(MemberModel member, int position) {
        mFragmentShopMembers.removeMember(position);
    }

    @Override
    public void onCancel() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_USER && resultCode == RESULT_OK) {
            String userName = data.getStringExtra(SearchActivity.KEY_RESULT_NAME);
            long userPk = data.getLongExtra(SearchActivity.KEY_RESULT_PK, 0L);
            String userPic = data.getStringExtra(SearchActivity.KEY_RESULT_IMAGE);
            MemberModel member = new MemberModel(userPk, userName, userPic);

            changeMemberRole(member, MemberAssignRoleFragment.POSITION_NEW_MEMBER);
        }
    }

    @Override
    protected void updateScreen() {
        super.updateScreen();
        mViewModel.updateResults();
    }
}
