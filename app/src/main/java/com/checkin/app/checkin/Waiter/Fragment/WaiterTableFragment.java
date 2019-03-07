package com.checkin.app.checkin.Waiter.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.OnClick;

public class WaiterTableFragment extends BaseFragment implements KeyboardVisibilityEventListener {
    private static final String KEY_WAITER_TABLE_ID = "waiter.table";

    @BindView(R.id.container_waiter_table_actions)
    ViewGroup containerActions;
    @BindView(R.id.tv_waiter_table_members_count)
    TextView tvMembersCount;
    @BindView(R.id.btn_waiter_table_bill)
    Button btnWaiterTableBill;
    @BindView(R.id.container_waiter_member_add_details)
    CardView containerAddDetails;
    @BindView(R.id.container_waiter_member_details)
    CardView containerAddMemeberDetails;

    private WaiterTableInteraction mListener;
    private WaiterTableViewModel mViewModel;
    private WaiterWorkViewModel mWorkViewModel;

    private long shopPk;

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

        shopPk = ViewModelProviders.of(requireActivity()).get(WaiterWorkViewModel.class).getShopPk();

        mViewModel = ViewModelProviders.of(this).get(WaiterTableViewModel.class);
        mWorkViewModel = ViewModelProviders.of(requireActivity()).get(WaiterWorkViewModel.class);
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
                if (resource.data.isCheckout()) {
                    mWorkViewModel.markSessionEnd(resource.data.getSessionPk());
                }
                Utils.toast(requireContext(), resource.data.getMessage());
            } else if (resource.status != Status.LOADING && resource.message != null) {
                Utils.toast(requireContext(), resource.message);
            }
        });
        mViewModel.getSessionContactData().observe(this, input -> {
            if (input == null)
                return;
            if (input.status == Status.SUCCESS && input.data != null)
                Utils.toast(requireContext(), "User contact details added successfully.");
            else if (input.status != Status.LOADING && input.message != null)
                Utils.toast(requireContext(), input.message);
        });
    }

    private void setupTableData(SessionBriefModel data) {
        if (data.getCustomerCount() > 0) {
            containerAddMemeberDetails.setVisibility(View.VISIBLE);
            tvMembersCount.setText(data.formatCustomerCount());
            containerAddDetails.setVisibility(View.GONE);
        } else {
            containerAddMemeberDetails.setVisibility(View.GONE);
            containerAddDetails.setVisibility(View.VISIBLE);
        }

        containerAddDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        btnWaiterTableBill.setText(Utils.formatCurrencyAmount(requireContext(),String.valueOf(data.getBill())));
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

    private Dialog mDialog;

    private void showDialog() {
        mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.view_dialog_waiter_table_bill);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        EditText etPhone = mDialog.findViewById(R.id.et_contact_phone);
        EditText etEmail = mDialog.findViewById(R.id.et_contact_email);
        TextView btnDone = mDialog.findViewById(R.id.btn_contact_done);

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().equals(""))
                    etEmail.setEnabled(false);
                else
                    etEmail.setEnabled(true);
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().equals(""))
                    etPhone.setEnabled(false);
                else
                    etPhone.setEnabled(true);
            }
        });


        btnDone.setOnClickListener(v -> {
            String phone = etPhone.getText().toString();
            String email = etEmail.getText().toString();

            if ((!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(email)) || (TextUtils.isEmpty(phone) && TextUtils.isEmpty(email))) {
                Utils.toast(requireContext(), "Please enter only phone number or email.");
                return;
            } else if (etPhone.isEnabled() && !Patterns.PHONE.matcher(phone).matches()) {
                Utils.toast(requireContext(), "Please enter valid phone number.");
                return;
            } else if (etEmail.isEnabled() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Utils.toast(requireContext(), "Please enter valid email.");
                return;
            } else {
                if (phone != null && !phone.equals(""))
                    addDetails(null, phone);
                else if (email != null && !email.equals(""))
                    addDetails(email, null);
            }
            mDialog.cancel();
        });
        KeyboardVisibilityEvent.setEventListener(getActivity(), this);
    }

    private void addDetails(String email, String phone) {
        mViewModel.postSessionContact(email, phone);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateScreen();
    }

    public WaiterTableViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onVisibilityChanged(boolean isOpen) {
        if (!isOpen) {
            if (mDialog != null)
                mDialog.dismiss();
        }
    }

    public interface WaiterTableInteraction {
        void endSession(long sessionPk);
    }
}
