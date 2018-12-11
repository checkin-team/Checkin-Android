package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Search.SearchActivity;
import com.checkin.app.checkin.Utility.Constants;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MembersActivity extends AppCompatActivity implements ShopMembersListFragment.MemberListInteraction, MemberAssignRoleFragment.AssignRoleInteraction {
    private static final String TAG = MembersActivity.class.getSimpleName();

    public static final String KEY_SHOP_PK = "shop_members.pk";
    private static final int REQUEST_PICK_USER = 10;

    private ShopMembersListFragment mFragmentShopMembers;
    private MemberViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_members);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(10);

        mViewModel = ViewModelProviders.of(this).get(MemberViewModel.class);

        setupShopMembers();
    }

    private void setupShopMembers() {
        String shopPk = getIntent().getStringExtra(KEY_SHOP_PK);
        mViewModel.fetchShopMembers(shopPk);
        mFragmentShopMembers = ShopMembersListFragment.newInstance(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_members, mFragmentShopMembers)
                .commit();
    }

    @OnClick(R.id.btn_add_member)
    public void onClickAddMembers(View view) {
        Intent pickUserIntent = new Intent(this, SearchActivity.class);
        pickUserIntent.putExtra(Constants.ACCOUNT_TYPE,"People");
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
            MemberModel member = new MemberModel();
            changeMemberRole(member, MemberAssignRoleFragment.POSITION_NEW_MEMBER);
        }
    }
}
