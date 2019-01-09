package com.checkin.app.checkin.Waiter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WaiterTableFragment extends Fragment {

    @BindView(R.id.iv_waiter_table_male)
    ImageView ivWaiterTableMale;
    @BindView(R.id.tv_waiter_table_number_of_user)
    TextView tvWaiterTableNumberOfUser;
    @BindView(R.id.cv_waiter_table_number_of_user)
    CardView cvWaiterTableNumberOfUser;
    @BindView(R.id.ib_waiter_table_menu)
    ImageView ibWaiterTableMenu;
    @BindView(R.id.rv_waiter_table_list)
    RecyclerView rvWaiterTableList;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waiter_table, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
