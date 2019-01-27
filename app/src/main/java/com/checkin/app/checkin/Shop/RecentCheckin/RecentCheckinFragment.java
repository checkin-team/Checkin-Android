package com.checkin.app.checkin.Shop.RecentCheckin;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.RecentCheckin.Model.RecentCheckinModel;
import com.checkin.app.checkin.Shop.RecentCheckin.Model.UserCheckinModel;
import com.checkin.app.checkin.User.NonPersonalProfile.UserProfileActivity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.checkin.app.checkin.Data.Resource.Status;

public class RecentCheckinFragment extends Fragment implements RecentCheckinAdapter.RecentCheckinInteraction {
    private Unbinder unbinder;

    @BindView(R.id.tv_rc_live) TextView tvCountLive;
    @BindView(R.id.tv_rc_count_male) TextView tvCountMale;
    @BindView(R.id.tv_rc_count_female) TextView tvCountFemale;
    @BindView(R.id.rv_recent_checkins) RecyclerView rvUserCheckins;

    private long mShopId;
    private RecentCheckinViewModel mViewModel;
    private RecentCheckinAdapter mAdapter;

    public static RecentCheckinFragment newInstance(long shopPk) {
        RecentCheckinFragment fragment = new RecentCheckinFragment();
        fragment.mShopId = shopPk;
        return fragment;
    }

    public RecentCheckinFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shop_recent_checkin, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAdapter = new RecentCheckinAdapter(null, this);

        rvUserCheckins.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvUserCheckins.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(this).get(RecentCheckinViewModel.class);
        mViewModel.getRecentCheckinData().observe(this, recentCheckinModelResource -> {
            if (recentCheckinModelResource == null)
                return;
            if (recentCheckinModelResource.status == Status.SUCCESS && recentCheckinModelResource.data != null) {
                RecentCheckinModel data = recentCheckinModelResource.data;
                tvCountLive.setText(String.format(Locale.ENGLISH, "Live: %s", data.formatLiveCount()));
                tvCountFemale.setText(data.formatLiveFemale());
                tvCountMale.setText(data.formatLiveMale());
                mAdapter.updateUserCheckins(data.getCheckins());
            }
        });
        mViewModel.fetchRecentCheckins(mShopId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClickUserCheckin(UserCheckinModel userCheckinModel) {
        Intent intent = new Intent(requireContext(), UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.KEY_PROFILE_USER_ID, Long.valueOf(userCheckinModel.getUserInfo().getPk()));
        startActivity(intent);
    }
}
