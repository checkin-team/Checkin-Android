package com.checkin.app.checkin.Session;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.NonPersonalProfile.UserViewModel;
import com.checkin.app.checkin.Misc.SelectListItem;
import com.checkin.app.checkin.Misc.SelectListViewAdapter;
import com.checkin.app.checkin.Utility.Util;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.hootsuite.nachos.ChipConfiguration;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.chip.ChipSpan;
import com.hootsuite.nachos.chip.ChipSpanChipCreator;
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveSessionActivity extends AppCompatActivity implements ActiveSessionMemberAdapter.SessionMemberInteraction {
    private static final String TAG = ActiveSessionActivity.class.getSimpleName();
    private ActiveSessionViewModel mActiveSessionViewModel;
    private UserViewModel mUserViewModel;
    private ActiveSessionMemberAdapter mSessionMembersAdapter;
    public static final String IDENTIFIER = "active_session";
    @BindView(R.id.rv_session_members) RecyclerView rvMembers;
    @BindView(R.id.tv_bill) TextView tvBill;

    private MaterialStyledDialog mAddMemberDialog;
    private MaterialStyledDialog mCheckoutDialog;
    private Receiver mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_session);
        ButterKnife.bind(this);

        rvMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSessionMembersAdapter = new ActiveSessionMemberAdapter(null, this);
        rvMembers.setAdapter(mSessionMembersAdapter);

        mUserViewModel = ViewModelProviders.of(this, new UserViewModel.Factory(getApplication())).get(UserViewModel.class);
        mActiveSessionViewModel = ViewModelProviders.of(this, new ActiveSessionViewModel.Factory(getApplication())).get(ActiveSessionViewModel.class);
        mActiveSessionViewModel.getActiveSessionDetail().observe(this, resource -> {
            if (resource == null) return;
            ActiveSessionModel data = resource.data;
            switch (resource.status) {
                case SUCCESS: {
                    mSessionMembersAdapter.setUsers(data != null ? data.getMembers() : null);
                    tvBill.setText(String.valueOf(data != null ? data.getBill() : 0));
                }
                case LOADING: {
                    break;
                }
                default: {
                    Log.e(resource.status.name(), resource.message == null ? "Null" : resource.message);
                }
            }
        });
        mActiveSessionViewModel.getObservableData().observe(this, resource -> {
            switch (resource.status) {
                case SUCCESS: {
                    Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                    break;
                }
                default: {

                }
            }
        });
        setupAddMember();
        setupCheckoutDialog();
        mHandler = new Receiver(mActiveSessionViewModel);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mHandler, new IntentFilter(Util.getActivityIntentFilter(getApplicationContext(),IDENTIFIER)));
    }

    private void setupCheckoutDialog() {
        mCheckoutDialog = new MaterialStyledDialog.Builder(this)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle("Checkout Session")
                .setDescription("Request for bill?")
                .setPositiveText("Confirm")
                .setNegativeText("Cancel")
                .onPositive((dialog, which) -> {
                    mActiveSessionViewModel.checkoutSession();
                })
                .build();
    }

    private void setupAddMember() {
        final View view = LayoutInflater.from(this).inflate(R.layout.view_select_list_multiple, null);
        final NachoTextView userSelect = view.findViewById(R.id.select_text_view);
        userSelect.setThreshold(2);
        userSelect.setChipTokenizer(new SpanChipTokenizer<>(this, new ChipSpanChipCreator() {
            @Override
            public ChipSpan createChip(@NonNull Context context, @NonNull CharSequence text, Object data) {
                return new ChipSpan(ActiveSessionActivity.this, text, getResources().getDrawable(R.drawable.ic_close), data);
            }

            @Override
            public void configureChip(@NonNull ChipSpan chip, @NonNull ChipConfiguration chipConfiguration) {
                super.configureChip(chip, chipConfiguration);
                chip.setShowIconOnLeft(false);
                chip.setIconBackgroundColor(getResources().getColor(R.color.primary_red));
            }
        }, ChipSpan.class));
        userSelect.setOnChipClickListener((chip, event) -> {
            Objects.requireNonNull(userSelect.getChipTokenizer()).deleteChipAndPadding(chip, userSelect.getEditableText());
        });

        List<SelectListItem> items = new ArrayList<>();
        mUserViewModel.getAllUsers().observe(this, (Resource<List<UserModel>> userResource) -> {
            if (userResource != null && userResource.status == Resource.Status.SUCCESS) {
                List<UserModel> users = userResource.data;
                for (UserModel user: users) {
                    items.add(new SelectListItem(user.getProfilePic(), user.getUsername(), "", user.getId()));
                }
            }
        });
        SelectListViewAdapter adapter = new SelectListViewAdapter(this, items);
        userSelect.setAdapter(adapter);

        mAddMemberDialog = new MaterialStyledDialog.Builder(this)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle("Add Session member")
                .setPositiveText("Confirm")
                .setNegativeText("Cancel")
                .setCustomView(view, 10, 10, 10, 10)
                .onPositive((dialog, which) -> {
                    List<Long> ids = new ArrayList<>();
                    for (Chip chip: userSelect.getAllChips()) {
                        ids.add(((Long) ((SelectListItem) Objects.requireNonNull(chip.getData())).getData()));
                    }
                    mActiveSessionViewModel.addMembers(ids);
                })
                .build();
    }

    @OnClick(R.id.btn_list_orders)
    public void onListOrders() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.root_view, ActiveSessionOrdersFragment.newInstance(this));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.btn_checkout)
    public void onCheckout() {
        mCheckoutDialog.show();
    }

    @Override
    public void onAddMemberClicked() {
        mAddMemberDialog.show();
    }

    private static class Receiver extends BroadcastReceiver {
        ActiveSessionViewModel mSessionViewModel;
        Receiver(ActiveSessionViewModel viewModel) {
            mSessionViewModel = viewModel;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            mSessionViewModel.updateResults();
        }
    }
}
