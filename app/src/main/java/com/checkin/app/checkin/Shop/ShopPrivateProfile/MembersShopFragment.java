package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.app.Fragment;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MembersShopFragment extends Fragment implements ShopMemberAdapter.OnMemberInteractionListener{
   private List<MemberModel> memberModels;
   private ShopMemberAdapter shopMemberAdapter;
    private MemberViewModel mViewModel;
    String shopPk;

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
        final View rootView = inflater.inflate(R.layout.fragment_shop_members, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        setupUI();

        return rootView;


    }

    public void setupUI()
    {
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mViewModel=ViewModelProviders.of((FragmentActivity) getActivity()).get(MemberViewModel.class);
        shopPk=mViewModel.getShopPk();
        mViewModel.fetchShopMembers();
        mViewModel.getShopMembers().observeForever( shopMembers->{

            if(shopMembers.data!=null)
            { memberModels=shopMembers.data;

            rvShopMembers.setLayoutManager(llm);

            shopMemberAdapter = new ShopMemberAdapter(memberModels);
            shopMemberAdapter.setMemberInteractionListener(this);
            rvShopMembers.setAdapter(shopMemberAdapter);}
        });

    }

    @Override
    public void onClickAssign(MemberModel member, int position) {
        interActionListener.shopMemberAddition(member,position);
    }
}
