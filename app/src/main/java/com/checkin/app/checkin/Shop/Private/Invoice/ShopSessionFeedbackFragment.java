package com.checkin.app.checkin.Shop.Private.Invoice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.activesession.InvoiceOrdersAdapter;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShopSessionFeedbackFragment extends Fragment {
    private Unbinder unbinder;

    private ShopSessionViewModel mViewModel;
    private ShopSessionFeedbackAdapter mAdapter;
    @BindView(R.id.rv_invoice_feedback)
    RecyclerView rvFeedback;

    public ShopSessionFeedbackFragment() {
    }

    public static ShopSessionFeedbackFragment newInstance() {
        return new ShopSessionFeedbackFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_session_feedback, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new ShopSessionFeedbackAdapter(null);
        rvFeedback.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvFeedback.setAdapter(mAdapter);


        mViewModel = ViewModelProviders.of(requireActivity()).get(ShopSessionViewModel.class);
        mViewModel.fetchRestaurantSessionReviews();
        mViewModel.getSessionReviews().observe(this, input -> {
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                mAdapter.setData(input.data);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
