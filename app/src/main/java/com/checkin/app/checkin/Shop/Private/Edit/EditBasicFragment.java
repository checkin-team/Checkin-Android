package com.checkin.app.checkin.Shop.Private.Edit;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.Private.ShopProfileViewModel;
import com.checkin.app.checkin.Shop.RestaurantModel;
import com.checkin.app.checkin.auth.exceptions.InvalidOTPException;
import com.checkin.app.checkin.auth.fragments.OtpVerificationDialog;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.misc.views.MultiSpinner;
import com.checkin.app.checkin.misc.views.PrefixEditText;
import com.checkin.app.checkin.misc.views.TimeEditText;
import com.checkin.app.checkin.utility.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.auth.PhoneAuthCredential;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

import static android.view.View.GONE;

public class EditBasicFragment extends Fragment implements OtpVerificationDialog.AuthCallback {
    private static final String TAG = EditBasicFragment.class.getSimpleName();
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone)
    PrefixEditText etPhone;
    @BindView(R.id.spinner_non_working_days)
    MultiSpinner vNonWorkingDays;
    @BindView(R.id.et_website)
    EditText etWebsite;
    @BindView(R.id.et_tag_line)
    EditText etTagLine;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_opening_time)
    TimeEditText etOpeningTime;
    @BindView(R.id.et_closing_time)
    TimeEditText etClosingTime;
    @BindView(R.id.btn_verify_phone)
    ImageButton btnVerifyPhone;
    @BindView(R.id.btn_verify_email)
    ImageButton btnVerifyEmail;
    private Unbinder unbinder;
    private BasicFragmentInteraction mListener;
    private ShopProfileViewModel mViewModel;


    public static EditBasicFragment newInstance(BasicFragmentInteraction listener) {
        EditBasicFragment fragment = new EditBasicFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shop_edit_basic, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() == null)
            return;

        mViewModel = ViewModelProviders.of(getActivity()).get(ShopProfileViewModel.class);

        mViewModel.getShopData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                this.setupValues(resource.getData());
            }
        });

        mViewModel.shouldCollectBasicData().observe(this, shouldCollectData -> {
            if (shouldCollectData != null && shouldCollectData) {
                String name = etName.getText().toString();
                String tagline = etTagLine.getText().toString();
                String website = etWebsite.getText().toString();
                CharSequence[] nonWorkingDays = vNonWorkingDays.getSelectedValues();
                long openingTime = etOpeningTime.getTotalTime();
                long closingTime = etClosingTime.getTotalTime();

                RestaurantModel shop = mViewModel.updateBasicData(
                        name, website, tagline, nonWorkingDays, openingTime, closingTime);
                mListener.updateShopBasics(shop);
            }
        });

        mViewModel.getErrors().observe(this, errors -> {
            if (errors == null)
                return;
            if (errors.has("website")) {
                JsonNode node = errors.get("website").get(0);
                etWebsite.setError(node.asText());
            }
        });
    }

    @OnTextChanged(value = R.id.et_email, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onEmailChange(Editable editable) {
        btnVerifyEmail.setVisibility(View.VISIBLE);
        checkDataValid();
    }

    @OnTextChanged(value = R.id.et_phone, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPhoneChange(Editable editable) {
        btnVerifyPhone.setVisibility(View.VISIBLE);
        checkDataValid();
    }

    @OnTextChanged(R.id.et_name)
    public void onNameChange(Editable editable) {
        checkDataValid();

        if (editable.toString().isEmpty())
            etName.setError("Name is required.");
    }

    private void checkDataValid() {
        mListener.onBasicDataValidStatus(isDataValid());
    }

    private boolean isDataValid() {
        boolean isEmailVerified = btnVerifyEmail.getVisibility() == GONE;
        boolean isPhoneVerified = btnVerifyPhone.getVisibility() == GONE;
        boolean isNameValid = !etName.getText().toString().isEmpty();
        return isEmailVerified && isPhoneVerified && isNameValid;
    }

    @OnClick(R.id.btn_verify_email)
    public void onVerifyEmail(View v) {
        String email = etEmail.getText().toString();
        if (email.isEmpty()) {
            Utils.toast(requireContext(), "Email is required.");
            return;
        }
        mViewModel.updateShopContact(null, email);
        btnVerifyEmail.setVisibility(GONE);
        checkDataValid();
    }

    @OnClick(R.id.btn_verify_phone)
    public void onVerifyPhone(View v) {
        String phone = etPhone.getText().toString();
        if (phone.isEmpty()) {
            Utils.toast(requireContext(), "Phone is required.");
            return;
        }
        verifyPhone(phone);
    }

    private void verifyPhone(String phone) {
        OtpVerificationDialog dialog = OtpVerificationDialog.Builder.with(getActivity())
                .setAuthCallback(this)
                .build();
        dialog.verifyPhoneNumber(phone);
        dialog.show();
    }

    @Override
    public void onSuccessVerification(DialogInterface dialog, @NotNull PhoneAuthCredential credential, @NotNull String idToken) {
        Utils.toast(requireContext(), "Phone verified!");

        mViewModel.updateShopContact(idToken, null);
        btnVerifyPhone.setVisibility(GONE);
        checkDataValid();
        dialog.dismiss();
    }

    @Override
    public void onCancelVerification(DialogInterface dialog) {
    }

    @Override
    public void onFailedVerification(DialogInterface dialog, @NonNull Exception exception) {
        Utils.toast(requireContext(), exception.getLocalizedMessage());
        if (!(exception instanceof InvalidOTPException)) dialog.dismiss();
    }

    private void setupValues(RestaurantModel restaurant) {
        etName.setText(restaurant.getName());
        etPhone.setText(restaurant.getPhone());
        etTagLine.setText(restaurant.getTagline());
        etWebsite.setText(restaurant.getWebsite());

        etEmail.setText(restaurant.getEmail());
        if (restaurant.isEmailUnconfirmed())
            etEmail.setError("Email not confirmed yet!");

        vNonWorkingDays.selectValues(restaurant.getNonWorkingDays());
        etOpeningTime.setTotalTime(restaurant.getOpeningHour());
        etClosingTime.setTotalTime(restaurant.getClosingHour());

        if (restaurant.getPhone() != null && !restaurant.getPhone().isEmpty())
            btnVerifyPhone.setVisibility(GONE);
        if (restaurant.getEmail() != null && !restaurant.getEmail().isEmpty())
            btnVerifyEmail.setVisibility(GONE);
        checkDataValid();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = ((BasicFragmentInteraction) context);
        } catch (ClassCastException ignored) {
        }
    }

    public interface BasicFragmentInteraction {
        void updateShopBasics(RestaurantModel shop);

        void onBasicDataValidStatus(boolean isValid);
    }
}
