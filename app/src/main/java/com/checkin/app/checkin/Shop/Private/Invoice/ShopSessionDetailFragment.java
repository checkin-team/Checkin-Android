package com.checkin.app.checkin.Shop.Private.Invoice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BillHolder;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.InvoiceOrdersAdapter;
import com.checkin.app.checkin.Utility.Utils;

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

public class ShopSessionDetailFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.rv_shop_session_orders)
    RecyclerView rvSessionOrders;
    @BindView(R.id.tv_shop_session_total_time)
    TextView tvTotalTime;
    @BindView(R.id.tv_shop_session_preparation_time)
    TextView tvPreparationTime;
    @BindView(R.id.tv_shop_session_bill_total)
    TextView tvBillTotal;

    private BillHolder mBillHolder;
    private InvoiceOrdersAdapter mOrdersAdapter;
    private ShopSessionViewModel mViewModel;


    public static ShopSessionDetailFragment newInstance() {
        return new ShopSessionDetailFragment();
    }

    public ShopSessionDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shop_session_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mBillHolder = new BillHolder(view);

        mOrdersAdapter = new InvoiceOrdersAdapter(null);
        rvSessionOrders.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvSessionOrders.setAdapter(mOrdersAdapter);

        mViewModel = ViewModelProviders.of(requireActivity()).get(ShopSessionViewModel.class);
        mViewModel.fetchSessionDetail();
        mViewModel.getSessionDetail().observe(this, input -> {
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data != null) {
                ShopSessionDetailModel data = input.data;

                tvBillTotal.setText(String.format(Locale.ENGLISH, Utils.getCurrencyFormat(requireContext()), data.getBill().getTotal()));
                tvTotalTime.setText(data.formatTotalTime());
                tvPreparationTime.setText(data.formatPreparationTime());
                mOrdersAdapter.setData(data.getOrderedItems());
                mBillHolder.bind(data.getBill());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
