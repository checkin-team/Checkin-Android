package com.checkin.app.checkin.Session.ActiveSession;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.SessionViewOrdersModel;
import com.checkin.app.checkin.Utility.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActiveSessionViewOrdersActivity extends AppCompatActivity implements ActiveSessionViewOrdersAdapters.SessionOrdersInteraction {
    @BindView(R.id.rv_active_session_orders) RecyclerView rvOrders;
    private ActiveSessionViewOrdersAdapters mOrdersAdapter;
    private ActiveSessionViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session_view_orders);
        ButterKnife.bind(this);

        rvOrders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mOrdersAdapter = new ActiveSessionViewOrdersAdapters(null, ActiveSessionViewOrdersActivity.this, (ActiveSessionViewOrdersAdapters.SessionOrdersInteraction) this);
        rvOrders.setAdapter(mOrdersAdapter);

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionViewModel.class);

        mViewModel.getSessionOrdersData().observe(this, new Observer<Resource<List<SessionViewOrdersModel>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<SessionViewOrdersModel>> listResource) {
                if (listResource != null && listResource.status == Resource.Status.SUCCESS)
                    mOrdersAdapter.setData(listResource.data);
            }
        });

        mViewModel.getObservableData().observe(this, resource -> {
            if (resource == null)
                return;
            switch (resource.status) {
                case SUCCESS: {
                    Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                    mViewModel.getSessionOrdersData().observe(this, listResource -> {
                        if (listResource != null && listResource.status == Resource.Status.SUCCESS)
                            mOrdersAdapter.setData(listResource.data);
                    });
                    break;
                }
                case LOADING:
                    break;
                default: {
                    Util.toast(this, resource.message);
                }
            }
        });
    }

    @Override
    public void onCancelOrder(int pk) {
        mViewModel.deleteSessionOrder(String.valueOf(pk));
    }
}
