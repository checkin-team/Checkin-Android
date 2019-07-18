package com.checkin.app.checkin.Home;

import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;

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
}
