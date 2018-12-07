package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.app.FragmentManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Search.SearchActivity;
import com.checkin.app.checkin.Utility.Constants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.common.util.DataUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShopMembersActivity extends AppCompatActivity implements MembersShopFragment.ShopMemberAdd,
ChangeRoleFragment.onClickButtons{

    @BindView(R.id.btn_add_member)
    ImageView AddMemberButton;
    @BindView(R.id.member_container)
    View MemberContainer;
    @BindView(R.id.addText)
    TextView addMemberText;
    @BindView(R.id.change_role_container)
    View changeContainer;
    @BindView(R.id.dark_back_shop)
    View darkBack;



   private List<MemberModel> memberModels;
   private MembersShopFragment membersShopFragment;
   private MemberViewModel mViewModel;
    public static final String KEY_SHOP_PK = "shop_private.pk";
    public static final int PICK_USER= 10;
    String shopPk;
    int flag=0;

   private ChangeRoleFragment changeRoleFragment;
   Unbinder unbinder;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_members);
        ButterKnife.bind(this);
        addMemberText.setText("ADD MEMBER");
        membersShopFragment=new MembersShopFragment();
       ActionBar actionBar = getSupportActionBar();
       actionBar.setHomeButtonEnabled(true);
       actionBar.setDisplayHomeAsUpEnabled(true);
       actionBar.setElevation(0);
        shopPk=getIntent().getStringExtra(KEY_SHOP_PK);
       membersShopFragment=new MembersShopFragment();
        membersShopFragment.setInterActionListener(this);
        mViewModel= ViewModelProviders.of(this).get(MemberViewModel.class);
        mViewModel.setShopPk(shopPk);
        getFragmentManager().beginTransaction()
                .replace(MemberContainer.getId(),membersShopFragment)
                .commit();



    }

    @OnClick(R.id.btn_add_member)
    public void onClickAddMembers(View view) {
        Intent pickUserIntent=new Intent(this, SearchActivity.class);
        pickUserIntent.putExtra(Constants.ACCOUNT_TYPE,"People");
        startActivityForResult(pickUserIntent,PICK_USER);
    }


    @Override
    public void onBackPressed(){
       if(getFragmentManager().popBackStackImmediate("addRole",FragmentManager.POP_BACK_STACK_INCLUSIVE))
       {
            darkBack.setVisibility(View.GONE);
       }
       else
       {
           super.onBackPressed();

       }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void shopMemberAddition(MemberModel member, int position) {
       changeRoleFragment=new ChangeRoleFragment();
       changeRoleFragment.setOnInteractionListener(this);
       changeRoleFragment.setMemberModel(member,position);
       getFragmentManager().beginTransaction()
               .replace(changeContainer.getId(),changeRoleFragment)

               .addToBackStack("addRole")
               .commit();
        darkBack.setVisibility(View.VISIBLE);

    }

    @Override
    public void setRole(MemberModel memberModel, int position, int roles[]) {
//            memberModel.setRole(role);

        if (roles == null||roles[0]==-1) {
            mViewModel.deleteShopMember(memberModel.getUser().getPk());
               observeLiveData();
            getFragmentManager().popBackStackImmediate("addRole", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            darkBack.setVisibility(View.GONE);

        }
        else {
            memberModel.setOwner(false);memberModel.setAdmin(false);memberModel.setManager(false);memberModel.setWaiter(false);memberModel.setCook(false);
            for(int i=0;i<roles.length;i++)
            {
                switch (roles[i]){
                    case 0:
                        memberModel.setOwner(true);
                        break;
                    case 1:
                        memberModel.setAdmin(true);
                        break;
                    case 2:
                        memberModel.setManager(true);
                        break;
                    case 3:
                        memberModel.setWaiter(true);
                        break;
                    case 4:
                        memberModel.setCook(true);
                        break;


                }
            }



            flag=2;
            mViewModel.fetchShopMembers();

                mViewModel.getShopMembers().observe(this, shopMembers -> {
                    if (shopMembers != null && shopMembers.status == Resource.Status.SUCCESS) {
                        List<MemberModel> mShopMembers = shopMembers.data;
                        for (int i = 0; i < mShopMembers.size(); i++) {
                            if (mShopMembers.get(i).getUser().getPk().equals(memberModel.getUser().getPk())) {
                                flag = 1;
                                if (flag == 1) {
                                    ObjectNode data = Converters.objectMapper.createObjectNode();
                                    data.put("is_owner", (memberModel.isOwner()));
                                    data.put("is_admin", (memberModel.isAdmin()));
                                    data.put("is_manager", (memberModel.isManager()));

                                    data.put("is_waiter", (memberModel.isWaiter()));
                                    data.put("is_cook", (memberModel.isCook()));
                                    mViewModel.updateShopMember(memberModel.getUser().getPk(), data);
                                    observeLiveData();
                                }
                                break;
                            }

                        }
                        if (flag == 2) {
                            ObjectNode data = Converters.objectMapper.createObjectNode();
                            data.put("user", Integer.parseInt(memberModel.getUser().getPk()));
                            data.put("is_owner", (memberModel.isOwner()));
                            data.put("is_admin", (memberModel.isAdmin()));
                            data.put("is_manager", (memberModel.isManager()));

                            data.put("is_waiter", (memberModel.isWaiter()));
                            data.put("is_cook", (memberModel.isCook()));
                            mViewModel.addShopMember(data);
                            observeLiveData();

                        }
                    }
                });


            getFragmentManager().popBackStackImmediate("addRole", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            darkBack.setVisibility(View.GONE);


        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_USER&&resultCode==RESULT_OK)
        {
            MemberModel memberModel=new MemberModel();
            BriefModel briefModel=new BriefModel();
            briefModel.setPk(data.getStringExtra(Constants.ACCOUNT_UID));
            briefModel.setDisplayName(data.getStringExtra("userName"));
            briefModel.setDisplayPic(data.getStringExtra("userPic"));
            memberModel.setUser(briefModel);
            shopMemberAddition(memberModel,2);
        }
    }

    public void observeLiveData(){
                mViewModel.getShopMemberLiveData().observe(this,objectNodeResource ->
                {
                    if(objectNodeResource!=null)
                        if(objectNodeResource.status== Resource.Status.SUCCESS)
                        {
                            membersShopFragment=new MembersShopFragment();
                            membersShopFragment.setInterActionListener(this);
                            getFragmentManager().beginTransaction()
                                    .replace(MemberContainer.getId(),membersShopFragment)
                                    .commit();
                            mViewModel.getShopMembers().removeObservers(this);
                            mViewModel.getShopMemberLiveData().removeObservers(this);
                        }
                        else if (objectNodeResource.status == Resource.Status.LOADING) {
                            // LOADING
                        } else {
                            if(objectNodeResource.message==null)
                            {
                                membersShopFragment=new MembersShopFragment();
                                membersShopFragment.setInterActionListener(this);
                                getFragmentManager().beginTransaction()
                                        .replace(MemberContainer.getId(),membersShopFragment)
                                        .commit();
                                mViewModel.getShopMembers().removeObservers(this);
                                mViewModel.getShopMemberLiveData().removeObservers(this);

                            }
                            else
                                Toast.makeText(getApplicationContext(), "Error Posting Shop Member Data, Status: " +
                                        objectNodeResource.status.toString() + "\nDetails: " + objectNodeResource.message, Toast.LENGTH_LONG).show();

                        }


                });






    }

}
