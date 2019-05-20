package com.checkin.app.checkin.session.activesession;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.model.PromoDetailModel;

import butterknife.BindView;
import butterknife.OnClick;

public class ActiveSessionPromoFragment extends BaseFragment implements ActiveSessionPromoAdapter.onPromoCodeItemListener {

    @BindView(R.id.rv_available_promos)
    RecyclerView rvPromos;
    @BindView(R.id.ed_promo_code)
    EditText edPromoCode;

    private ActiveSessionInvoiceViewModel mViewModel;
    private ActiveSessionPromoAdapter mAdapter;

    public static ActiveSessionPromoFragment newInstance() {
        return new ActiveSessionPromoFragment();
    }

    public ActiveSessionPromoFragment() {
    }

    @Override
    protected int getRootLayout() {
        return R.layout.activity_active_session_promo;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(requireActivity()).get(ActiveSessionInvoiceViewModel.class);
        rvPromos.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        mAdapter = new ActiveSessionPromoAdapter(null, this);
        rvPromos.setAdapter(mAdapter);

        setupObservers();
    }

    private void setupObservers() {
        mViewModel.getPromoCodes().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Resource.Status.SUCCESS && listResource.data != null)
                mAdapter.setData(listResource.data);
            else if (listResource.status != Resource.Status.LOADING)
                Utils.toast(requireContext(), listResource.message);
        });

        mViewModel.getObservableData().observe(this, objectNodeResource -> {
            if (objectNodeResource == null)
                return;

            String msg = objectNodeResource.message;
            if (objectNodeResource.status == Resource.Status.SUCCESS) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
                if (objectNodeResource.data != null && objectNodeResource.data.has("code")) {
                    msg = "Successfully applied " + objectNodeResource.data.get("code");
                }
            }
            Utils.toast(requireContext(), msg);
        });
    }

    @OnClick(R.id.tv_apply_promo)
    public void onPromoCodeApplyClick() {
        String promoCode = edPromoCode.getText().toString();
        if (!TextUtils.isEmpty(promoCode)) {
            mViewModel.availPromoCode(promoCode);
        }
    }

    @Override
    public void onPromoApply(PromoDetailModel promoModel) {
        mViewModel.availPromoCode(promoModel.getCode());
    }
}
