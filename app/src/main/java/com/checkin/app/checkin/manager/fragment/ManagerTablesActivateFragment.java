package com.checkin.app.checkin.manager.fragment;

import android.content.Context;
import android.widget.Button;

import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.OnClick;

public class ManagerTablesActivateFragment extends BaseFragment {

    @BindView(R.id.btn_live_order_activate)
    Button btnLiveOrderActivate;

    private LiveOrdersInteraction mListener;

    public ManagerTablesActivateFragment() {
    }

    public static ManagerTablesActivateFragment newInstance() {
        return new ManagerTablesActivateFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_manager_tables_activate;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LiveOrdersInteraction) {
            mListener = (LiveOrdersInteraction) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ManagerWorkActivity.LiveOrdersInteraction");
        }
    }

    @OnClick(R.id.btn_live_order_activate)
    public void onViewClicked() {
        mListener.setLiveOrdersActivation(true);
    }

    public interface LiveOrdersInteraction {
        void setLiveOrdersActivation(boolean isActivated);
    }
}
