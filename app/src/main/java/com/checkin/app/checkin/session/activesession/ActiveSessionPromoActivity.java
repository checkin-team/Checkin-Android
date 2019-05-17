package com.checkin.app.checkin.session.activesession;

import android.os.Bundle;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.model.SessionPromoModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ActiveSessionPromoActivity extends BaseActivity implements ActiveSessionPromoAdapter.onPromoCodeItemListener {

    @BindView(R.id.rv_available_promos)
    RecyclerView rvPromos;

    private ActiveSessionViewModel mViewModel;
    private ActiveSessionPromoAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session_promo);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_grey);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        mViewModel = ViewModelProviders.of(this).get(ActiveSessionViewModel.class);
        rvPromos.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mAdapter = new ActiveSessionPromoAdapter(null, this);
        rvPromos.setAdapter(mAdapter);
        mViewModel.fetchAvailablePromoCodes();
        mViewModel.getPromoCodes().observe(this,listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Resource.Status.SUCCESS && listResource.data != null) {
                mAdapter.setData(listResource.data);
            } else {
                Utils.toast(this, listResource.message);
            }
        });

        mViewModel.getObservableData().observe(this, objectNodeResource -> {
            if (objectNodeResource == null)
                return;
            if (objectNodeResource.status == Resource.Status.SUCCESS && objectNodeResource.data != null) {
                finish();
            } else {
                Utils.toast(this, objectNodeResource.message);
            }
        });
    }

    @Override
    public void onPromoApply(SessionPromoModel promoModel) {
        mViewModel.availPromoCode(promoModel.getCode());
    }
}
