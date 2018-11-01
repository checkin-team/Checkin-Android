package com.checkin.app.checkin.Misc;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.ClipRevealFrame;

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
   private ShopMemberAdapter shopMemberAdapter;
   private ChangeRoleFragment changeRoleFragment;
   Unbinder unbinder;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_members);
        ButterKnife.bind(this);
        addMemberText.setText("ADD MEMBER");
        membersShopFragment=new MembersShopFragment();
        membersShopFragment.setInterActionListener(this);
       shopMemberAdapter=membersShopFragment.getShopMemberAdapter();
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
       Log.e("ShopMembersActivity",member.getFullName()+String.valueOf(position));
       getFragmentManager().beginTransaction()
               .replace(changeContainer.getId(),changeRoleFragment)
               .addToBackStack("addRole")
               .commit();

        darkBack.setVisibility(View.VISIBLE);
//        ArrayAdapter<String> sm=new ArrayAdapter<String>(this,R.layout.role_spinner_item);
//        sm.setDropDownViewResource(R.layout.role_spinner_item);
    }

    @Override
    public void setRole(MemberModel memberModel, int position, String role) {
            memberModel.setRole(role);
            List<MemberModel> models=membersShopFragment.getMemberModels();
            if(role==null)
            models.remove(position);
        getFragmentManager().popBackStackImmediate("addRole",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        darkBack.setVisibility(View.GONE);
            membersShopFragment.getShopMemberAdapter().notifyDataSetChanged();
    }
}
