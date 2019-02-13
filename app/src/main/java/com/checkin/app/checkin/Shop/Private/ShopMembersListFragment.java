package com.checkin.app.checkin.Shop.Private;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShopMembersListFragment extends Fragment implements MemberAdapter.OnMemberInteractionListener {
    private Unbinder unbinder;

    @BindView(R.id.rv_shop_members)
    RecyclerView rvShopMembers;

    private MemberAdapter mAdapter;
    private MemberViewModel mViewModel;
    private MemberListInteraction mListener;

    public static ShopMembersListFragment newInstance(MemberListInteraction listener) {
        ShopMembersListFragment fragment = new ShopMembersListFragment();
        fragment.mListener = listener;
        return fragment;
    }

    public ShopMembersListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shop_members_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupUI();
        mViewModel = ViewModelProviders.of(requireActivity()).get(MemberViewModel.class);

        mViewModel.getShopMembers().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Status.SUCCESS && listResource.data != null) {
                mAdapter.setMembers(listResource.data);
            }
        });
    }

    public void setupUI() {
        mAdapter = new MemberAdapter(null);
        mAdapter.setItemListener(this);
        rvShopMembers.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        rvShopMembers.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void addMember(MemberModel member) {
        mAdapter.addMember(member);
    }

    public void removeMember(int position) {
        mAdapter.removeMember(position);
    }

    public void updateMember(MemberModel member, int position) {
        mAdapter.updateMember(member, position);
    }

    @Override
    public void onAssignRole(MemberModel member, int position) {
        mListener.changeMemberRole(member, position);
    }

    public interface MemberListInteraction {
        void changeMemberRole(MemberModel member, int position);
    }
}
