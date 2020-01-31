package com.checkin.app.checkin.Shop.Private.Invoice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.misc.BillHolder;
import com.checkin.app.checkin.session.activesession.InvoiceOrdersAdapter;
import com.checkin.app.checkin.utility.Utils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShopSessionDetailFragment extends Fragment {
    @BindView(R.id.rv_shop_session_orders)
    RecyclerView rvSessionOrders;
    @BindView(R.id.tv_shop_session_total_time)
    TextView tvTotalTime;
    @BindView(R.id.tv_shop_session_preparation_time)
    TextView tvPreparationTime;
    @BindView(R.id.tv_shop_session_bill_total)
    TextView tvBillTotal;
    @BindView(R.id.tv_invoice_session_paid_via)
    TextView tvPaidVia;
    private Unbinder unbinder;
    private BillHolder mBillHolder;
    private InvoiceOrdersAdapter mOrdersAdapter;
    private ShopSessionViewModel mViewModel;


    public ShopSessionDetailFragment() {
    }

    public static ShopSessionDetailFragment newInstance() {
        return new ShopSessionDetailFragment();
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

        mOrdersAdapter = new InvoiceOrdersAdapter(null,null);
        rvSessionOrders.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvSessionOrders.setAdapter(mOrdersAdapter);

        mViewModel = ViewModelProviders.of(requireActivity()).get(ShopSessionViewModel.class);
        mViewModel.fetchSessionDetail();
        mViewModel.getSessionDetail().observe(this, input -> {
            if (input == null)
                return;
            if (input.getStatus() == Resource.Status.SUCCESS && input.getData() != null) {
                ShopSessionDetailModel data = input.getData();

                tvBillTotal.setText(String.format(Locale.ENGLISH, Utils.getCurrencyFormat(requireContext()), data.getBill().getTotal()));
                tvTotalTime.setText(data.formatTotalTime());
                tvPreparationTime.setText(data.formatPreparationTime());
                mOrdersAdapter.setData(data.getOrderedItems());
                mBillHolder.bind(data.getBill());
                if (data.getPaymentMode() != null)
                    tvPaidVia.setCompoundDrawablesWithIntrinsicBounds(0, 0, ShopModel.getPaymentModeIcon(data.getPaymentMode()), 0);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
