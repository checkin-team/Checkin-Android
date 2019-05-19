package com.checkin.app.checkin.session.activesession;

import android.os.Bundle;
import android.view.View;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.model.SessionBillModel;
import com.checkin.app.checkin.session.model.SessionPromoModel;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ActiveSessionPromoFragment extends BaseFragment implements ActiveSessionPromoAdapter.onPromoCodeItemListener {

    @BindView(R.id.rv_available_promos)
    RecyclerView rvPromos;
    @BindView(R.id.as_promo_search_view)
    MaterialSearchView promoSearch;

    private ActiveSessionViewModel mViewModel;
    private ActiveSessionPromoAdapter mAdapter;
    private List<SessionPromoModel> promoModelList;

    public ActiveSessionPromoFragment() {
    }

    public static ActiveSessionPromoFragment newInstance(List<SessionPromoModel> data) {
        ActiveSessionPromoFragment fragment = new ActiveSessionPromoFragment();
        fragment.promoModelList = data;
        return fragment;
    }


    @Override
    protected int getRootLayout() {
        return R.layout.activity_active_session_promo;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(requireActivity()).get(ActiveSessionViewModel.class);
        rvPromos.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        if(promoModelList != null)
        mAdapter = new ActiveSessionPromoAdapter(promoModelList, this);
        rvPromos.setAdapter(mAdapter);
      /*  mViewModel.fetchAvailablePromoCodes();
        mViewModel.getPromoCodes().observe(this,listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Resource.Status.SUCCESS && listResource.data != null) {
                mAdapter.setData(listResource.data);
            } else {
                Utils.toast(requireContext(), listResource.message);
            }
        });*/

        mViewModel.getPromoCodeAvailed().observe(this, objectNodeResource -> {
            if (objectNodeResource == null)
                return;
            if (objectNodeResource.status == Resource.Status.SUCCESS && objectNodeResource.data != null) {
                mViewModel.updateInvoice(objectNodeResource.data.getCode(), objectNodeResource.data.getOfferAmount());
                getFragmentManager().popBackStack();
            } else {
                Utils.toast(requireContext(), objectNodeResource.message);
            }
        });
    }

    private void setupSearch() {
        promoSearch.setVoiceSearch(true);
        promoSearch.setStartFromRight(false);
        promoSearch.setCursorDrawable(R.drawable.color_cursor_white);

        promoSearch.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mViewModel.searchPromoCodes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mViewModel.searchPromoCodes(query);
                return true;
            }

            @Override
            public boolean onQueryClear() {
                mViewModel.resetPromoItems();
                return true;
            }
        });

    }


    @Override
    public void onPromoApply(SessionPromoModel promoModel) {
        mViewModel.availPromoCode(promoModel.getCode());
    }
}
