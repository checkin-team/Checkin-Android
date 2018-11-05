package com.checkin.app.checkin.Misc;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MembersShopFragment extends Fragment implements ShopMemberAdapter.OnMemberInteractionListener {
   private List<MemberModel> memberModels;
   private ShopMemberAdapter shopMemberAdapter;
   private ShopMemberAdd interActionListener;
    Unbinder unbinder;

    @BindView(R.id.shop_member)
    RecyclerView rvShopMembers;
    public interface ShopMemberAdd
    {
        public void shopMemberAddition(MemberModel member,int position);
    }
    public MembersShopFragment()
    {

    }

    public ShopMemberAdapter getShopMemberAdapter() {
        return shopMemberAdapter;
    }

    public void setMemberModels(List<MemberModel> memberModels) {
        this.memberModels = memberModels;
    }


    public List<MemberModel> getMemberModels() {
        return memberModels;
    }

    public void setInterActionListener(ShopMemberAdd interActionListener) {
        this.interActionListener = interActionListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.member_shop_recy_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        setupUI();

        return rootView;


    }

    public void setupUI()
    {
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvShopMembers.setLayoutManager(llm);
            if(memberModels==null) {
                memberModels=new ArrayList<>();
                memberModels.add(new MemberModel("Shivansh Saini", null));
                memberModels.add(new MemberModel("Shambhav Tyagi", "Waiter"));
                memberModels.add(new MemberModel("Suryansh Awasthi", "Manager"));
                shopMemberAdapter = new ShopMemberAdapter(memberModels);
                shopMemberAdapter.setMemberInteractionListener(this);
                rvShopMembers.setAdapter(shopMemberAdapter);

            }
    }

    @Override
    public void onClickAssign(MemberModel member, int position) {
        interActionListener.shopMemberAddition(member,position);
    }
}
