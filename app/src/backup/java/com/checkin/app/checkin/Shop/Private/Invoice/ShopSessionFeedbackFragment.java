package com.checkin.app.checkin.Shop.Private.Invoice;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShopSessionFeedbackFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.rv_shop_session_feedback)
    RecyclerView rvFeedbacks;

    private ShopSessionViewModel mViewModel;
    private ShopSessionFeedbackAdapter mAdapter;

    public static ShopSessionFeedbackFragment newInstance() {
        return new ShopSessionFeedbackFragment();
    }

    public ShopSessionFeedbackFragment() {}

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

        mAdapter = new ShopSessionFeedbackAdapter(index -> {

        });
        rvFeedbacks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvFeedbacks.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(requireActivity()).get(ShopSessionViewModel.class);
        mViewModel.fetchSessionFeedbacks();
        mViewModel.getSessionFeedbacks().observe(this, input ->{
            if (input == null)
                return;
            if (input.status == Resource.Status.SUCCESS && input.data !=  null){
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
