package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.app.FragmentManager;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
        shopPk=getIntent().getStringExtra(KEY_SHOP_PK);
       membersShopFragment=new MembersShopFragment();
        membersShopFragment.setInterActionListener(this);
        mViewModel= ViewModelProviders.of(this).get(MemberViewModel.class);
        mViewModel.setShopPk(shopPk);
        getFragmentManager().beginTransaction()
                .replace(MemberContainer.getId(),membersShopFragment)
                .commit();



    }
    @Override
    public void onBackPressed(){
       if(getFragmentManager().popBackStackImmediate("addRole",FragmentManager.POP_BACK_STACK_INCLUSIVE))
       {
            Log.e("ShopMembers","Pop kra");
            darkBack.setVisibility(View.GONE);
       }
       else
       {
           super.onBackPressed();

       }
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
        if (roles == null)
            mViewModel.deleteShopMember(memberModel.getUser().getPk());
        else {
            for(int i=0;i<roles.length;i++)
            {
                switch (roles[i]){
                    case 1:
                        memberModel.setOwner(true);
                        break;
                    case 2:
                        memberModel.setAdmin(true);
                        break;
                    case 3:
                        memberModel.setManager(true);
                        break;
                    case 4:
                        memberModel.setWaiter(true);
                        break;
                    case 5:
                        memberModel.setCook(true);
                        break;

                }
            }


            getFragmentManager().popBackStackImmediate("addRole", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            darkBack.setVisibility(View.GONE);
            mViewModel.fetchShopMembers();
            mViewModel.getShopMembers().observe(this,shopMembers->{
                if(shopMembers!=null&&shopMembers.status== Resource.Status.SUCCESS)
                {
                    List<MemberModel> mShopMembers=shopMembers.data;
                    for(int i=0;i<mShopMembers.size();i++)
                    {
                        if(mShopMembers.get(i).getUser().getPk().equals(memberModel.getUser().getPk()))
                        {
                            flag=1;
                            break;
                        }
                    }
                }
            });
            if(flag==1)
            {
                mViewModel.updateShopMember(memberModel);
            }
            else
                mViewModel.addShopMember(memberModel);

            membersShopFragment=new MembersShopFragment();
            membersShopFragment.setInterActionListener(this);
            getFragmentManager().beginTransaction()
                    .replace(MemberContainer.getId(),membersShopFragment)
                    .commit();

        }
    }
    public void onClickAddMembers(View view) {
    }
}
