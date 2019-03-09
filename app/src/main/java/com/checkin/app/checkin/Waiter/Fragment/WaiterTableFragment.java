package com.checkin.app.checkin.Waiter.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionBriefModel;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.Waiter.Model.SessionContactModel;
import com.checkin.app.checkin.Waiter.WaiterTableViewModel;
import com.checkin.app.checkin.Waiter.WaiterWorkViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.OnClick;

public class WaiterTableFragment extends BaseFragment {
    private static final String KEY_WAITER_TABLE_ID = "waiter.table";

    @BindView(R.id.container_waiter_table_actions)
    ViewGroup containerActions;
    @BindView(R.id.tv_waiter_table_members_count)
    TextView tvMembersCount;
    @BindView(R.id.tv_waiter_session_bill)
    TextView tvSessionBill;
    @BindView(R.id.container_waiter_no_member)
    ViewGroup containerWaiterAddContact;

    private WaiterTableInteraction mListener;
    private WaiterTableViewModel mViewModel;

    private long shopPk;
    private Dialog mContactAddDialog;

    public static WaiterTableFragment newInstance(long tableNumber, WaiterTableInteraction listener) {
        WaiterTableFragment fragment = new WaiterTableFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_WAITER_TABLE_ID, tableNumber);
        fragment.setArguments(bundle);
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_waiter_table;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null)
            return;

        buildContactAddDialog();

        shopPk = ViewModelProviders.of(requireActivity()).get(WaiterWorkViewModel.class).getShopPk();

        mViewModel = ViewModelProviders.of(this).get(WaiterTableViewModel.class);
        mViewModel.fetchSessionDetail(getArguments().getLong(KEY_WAITER_TABLE_ID, 0));

        mViewModel.getSessionDetail().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Status.SUCCESS && resource.data != null) {
                setupTableData(resource.data);
            } else if (resource.status == Status.ERROR_NOT_FOUND) {
                mListener.endSession(mViewModel.getSessionPk());
            }
        });
        mViewModel.getCheckoutData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Status.SUCCESS && resource.data != null) {
                Utils.toast(requireContext(), resource.data.getMessage());
                if (resource.data.isCheckout()) {
                    mListener.endSession(mViewModel.getSessionPk());
                }
            } else if (resource.status != Status.LOADING && resource.message != null) {
                Utils.toast(requireContext(), resource.message);
            }
        });
        mViewModel.getObservableData().observe(this, input -> {
            if (input == null)
                return;
            if (input.status == Status.SUCCESS && input.data != null)
                Utils.toast(requireContext(), "User contact details added successfully.");
            else if (input.status != Status.LOADING && input.message != null)
                Utils.toast(requireContext(), input.message);
        });

        mViewModel.fetchSessionContacts();

        mViewModel.getSessionContactListData().observe(this, input -> {
            if (input == null)
                return;
            if (input.status == Status.SUCCESS && input.data != null) {
                if (input.data.size() > 0) {
                    setupContactData(input.data.get(input.data.size() - 1));
                }
            } else if (input.status != Status.LOADING && input.message != null)
                Utils.toast(requireContext(), input.message);
        });
    }

    private void buildContactAddDialog() {
        mContactAddDialog = new Dialog(requireContext());
        mContactAddDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContactAddDialog.setContentView(R.layout.view_dialog_waiter_table_bill);
        mContactAddDialog.setCanceledOnTouchOutside(true);
        mContactAddDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mContactAddDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        final EditText etPhone = mContactAddDialog.findViewById(R.id.et_contact_phone);
        final EditText etEmail = mContactAddDialog.findViewById(R.id.et_contact_email);
        final Button btnDone = mContactAddDialog.findViewById(R.id.btn_contact_done);

        mContactAddDialog.setOnShowListener(dialogInterface -> Utils.showSoftKeyboard(requireContext()));
        mContactAddDialog.setOnCancelListener(dialogInterface -> Utils.hideSoftKeyboard(requireContext()));
        mContactAddDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.cancel();
                return true;
            }
            return false;
        });

        btnDone.setOnClickListener(v -> {
            String phone = etPhone.getText().toString();
            String email = etEmail.getText().toString();

            if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(email)) {
                Utils.toast(requireContext(), "Please enter at least phone number or email.");
                return;
            }
            if (!TextUtils.isEmpty(phone) && !Patterns.PHONE.matcher(phone).matches()) {
                Utils.toast(requireContext(), "Please enter valid phone number.");
                return;
            }
            if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Utils.toast(requireContext(), "Please enter valid email.");
                return;
            }
            mViewModel.postSessionContact(email, phone);
            mContactAddDialog.dismiss();
        });
    }

    private void setupContactData(SessionContactModel sessionContactModel) {
        final EditText etPhone = mContactAddDialog.findViewById(R.id.et_contact_phone);
        final EditText etEmail = mContactAddDialog.findViewById(R.id.et_contact_email);

        String email = sessionContactModel.getEmail();
        String phone = sessionContactModel.getPhone();

        if (email != null)
            etEmail.setText(email);
        if (phone != null)
            etPhone.setText(phone);
    }

    private void setupTableData(SessionBriefModel data) {
        if (data.getCustomerCount() > 0) {
            containerWaiterAddContact.setVisibility(View.GONE);
            tvMembersCount.setText(data.formatCustomerCount());
        } else {
            containerWaiterAddContact.setVisibility(View.VISIBLE);
        }

        tvSessionBill.setText(Utils.formatCurrencyAmount(requireContext(), data.getBill()));
        if (data.isRequestedCheckout()) {
            containerActions.setVisibility(View.GONE);
            showCollectBill();
        } else {
            containerActions.setVisibility(View.VISIBLE);
            showEventList();
        }
    }

    private void showEventList() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.container_waiter_table_fragment, WaiterTableEventFragment.newInstance())
                .commit();
    }

    private void showCollectBill() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.container_waiter_table_fragment, WaiterTableCollectCashFragment.newInstance())
                .commit();
    }

    @Override
    public void updateScreen() {
        mViewModel.updateResults();
    }

    @OnClick(R.id.btn_waiter_table_checkout)
    public void onClickCheckout() {
        mViewModel.requestSessionCheckout();
    }

    @OnClick(R.id.btn_waiter_table_menu)
    public void onClickMenu() {
        SessionMenuActivity.withSession(requireContext(), shopPk, mViewModel.getSessionPk());
    }

    @OnClick(R.id.container_waiter_no_member)
    public void onClickAddContact() {
        showAddContactDialog();
    }

    private void showAddContactDialog() {
        mContactAddDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateScreen();
    }

    public WaiterTableViewModel getViewModel() {
        return mViewModel;
    }

    public interface WaiterTableInteraction {
        void endSession(long sessionPk);
    }
}
