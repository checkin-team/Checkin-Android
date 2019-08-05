package com.checkin.app.checkin.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.OnClick;

public class UserHomeFragment extends BaseFragment {

    public UserHomeFragment() {
    }

    public static UserHomeFragment newInstance() {
        return new UserHomeFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_user_home;
    }

    @OnClick(R.id.container_home_brownie_cash)
    public void brownieClick(){
        startActivity(new Intent(requireActivity(),UserBrownieCashActivity.class));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity) Objects.requireNonNull(getActivity())).enableDisableSwipeRefresh(true);
    }
}
