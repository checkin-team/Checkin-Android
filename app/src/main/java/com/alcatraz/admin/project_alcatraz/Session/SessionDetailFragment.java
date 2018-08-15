package com.alcatraz.admin.project_alcatraz.Session;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.Chip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SessionDetailFragment extends Fragment {
    @BindView(R.id.input_friends) ChipsInput mInputFriends;
    @BindView(R.id.session_ordered_list) RecyclerView mOrderedList;
    private Unbinder unbinder;
    private OrderedItemAdapter mOrderedItemAdapter;

    private boolean mCanCheckout = false;

    public SessionDetailFragment() {
        // Required empty public_selected constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sessionStarted Has Session started? => True, Else => False
     * @return A new instance of fragment SessionDetailFragment.
     */
    public static SessionDetailFragment newInstance(boolean sessionStarted) {
        SessionDetailFragment fragment = new SessionDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setCanCheckout(boolean canCheckout) {
        mCanCheckout = canCheckout;
    }

    private void dismissSession() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                .putBoolean(Constants.SP_CHECKED_IN, false)
                .putInt(Constants.SESSION_ID, 0)
                .apply();
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    @OnClick(R.id.action_call_waiter)
    public void callWaiter(View v) {
        Toast.makeText(getContext(), "Waiter will come to you soon!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.action_checkout_session)
    public void checkoutSession(View v) {
        if (!mCanCheckout) {
            Toast.makeText(getContext(), "Order items or cancel them before checking out the session.", Toast.LENGTH_SHORT).show();
            return;
        }
        dismissSession();
    }

    @OnClick(R.id.action_cancel_session)
    public void cancelSession(View v) {
        if (mOrderedItemAdapter.getItemCount() > 0) {
            Toast.makeText(getContext(), "Pay for the items bought before cancelling out.", Toast.LENGTH_SHORT).show();
            return;
        }
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int button) {
                switch (button) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dismissSession();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmation")
                .setMessage("Are you sure you wanna cancel this session?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    public void orderAndGetDetails(OrderedItem[] items) {
        //TODO: Get details and remaining time for order to be served.
        mOrderedItemAdapter.addItems(items);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_session_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        //TODO: Implement method to get friends list!
        List<Chip> friendsList = new ArrayList<>();
        friendsList.add(new Chip("Ram", null));
        friendsList.add(new Chip("Sam", null));
        friendsList.add(new Chip("Gyan", "wise guy."));
        friendsList.add(new Chip("Man", null));
        mInputFriends.setFilterableList(friendsList);

        mOrderedList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mOrderedItemAdapter = new OrderedItemAdapter(getContext());
        mOrderedList.setAdapter(mOrderedItemAdapter);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
