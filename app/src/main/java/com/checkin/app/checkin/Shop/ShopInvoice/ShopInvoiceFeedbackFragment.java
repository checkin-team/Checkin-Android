package com.checkin.app.checkin.Shop.ShopInvoice;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Review.ShopReview.ShopReviewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShopInvoiceFeedbackFragment extends Fragment implements ShopInvoiceFeedbackAdapter.ShopInvoiceFeedbackHolder.ReviewInteraction {

    @BindView(R.id.rv_shop_invoice_feedback)
    RecyclerView rvShopInvoiceFeedback;
    Unbinder unbinder;

    public static final String SESSION_ID = "SESSION_ID";
    int sessionId;

    public static ShopInvoiceFeedbackFragment newInstance(Integer pk) {
        ShopInvoiceFeedbackFragment shopInvoiceFeedbackFragment = new ShopInvoiceFeedbackFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SESSION_ID,pk);
        shopInvoiceFeedbackFragment.setArguments(bundle);
        return shopInvoiceFeedbackFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            sessionId = savedInstanceState.getInt(SESSION_ID,0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_invoice_feedback, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ShopInvoiceFeedbackAdapter shopInvoiceFeedbackAdapter = new ShopInvoiceFeedbackAdapter(this);
        rvShopInvoiceFeedback.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvShopInvoiceFeedback.setAdapter(shopInvoiceFeedbackAdapter);

        String session_Id = String.valueOf(sessionId);

        ShopSessionViewModel mShopSessionSessionViewModel = ViewModelProviders.of(this).get(ShopSessionViewModel.class);
        mShopSessionSessionViewModel.getShopSessionFeedbackById(session_Id);
        mShopSessionSessionViewModel.getShopSessionFeedbackModel().observe(this,input ->{
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data !=  null){
                List<ShopSessionFeedbackModel> mList = input.data;
                if (mList.size() > 0){
                    shopInvoiceFeedbackAdapter.addSessionFeedbackData(mList);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onToggleLike(ShopReviewModel review) {

    }

    @Override
    public void onThumbnailClick(ShopReviewModel review, int index) {

    }

    @Override
    public void onRatingClick(ShopReviewModel review) {

    }

    @Override
    public void onFollowClick(BriefModel user) {

    }

    @Override
    public void onUserClick(BriefModel user) {

    }
}
