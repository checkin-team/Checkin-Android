package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.R;

public class EditBasicFragment extends Fragment {

    public static EditBasicFragment newInstance() {
        EditBasicFragment fragment = new EditBasicFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_edit_basic, container, false);
    }
}
