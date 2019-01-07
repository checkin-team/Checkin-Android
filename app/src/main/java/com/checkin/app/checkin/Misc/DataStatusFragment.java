package com.checkin.app.checkin.Misc;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataStatusFragment extends Fragment{
    private Unbinder unbinder;

    @BindView(R.id.container_progress)
    ViewGroup containerProgress;
    @BindView(R.id.container_error)
    ViewGroup containerError;
    @BindView(R.id.load_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_error)
    TextView tvErrorMsg;
    private boolean isNetworkRequired;

    public DataStatusFragment() {
        // Required empty public constructor
    }

    public static DataStatusFragment newInstance(boolean isNetworkRequired) {
        DataStatusFragment fragment = new DataStatusFragment();
        fragment.isNetworkRequired = isNetworkRequired;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_data_status, container, false);

        // To prevent clicks behind the fragment
        view.setOnTouchListener((v, event) -> true);

        unbinder = ButterKnife.bind(this, view);

        if (isNetworkRequired && !Utils.isNetworkConnected(getActivity().getApplicationContext())) {
            showErrorStatus(Resource.Status.ERROR_DISCONNECTED, getResources().getString(R.string.error_unavailable_network));
        } else {
            showLoadingStatus();
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void showLoadingStatus() {
        containerError.setVisibility(View.GONE);
        containerProgress.setVisibility(View.VISIBLE);
    }

    public void showErrorStatus(@NonNull Resource.Status status, @Nullable String message) {
        containerError.setVisibility(View.VISIBLE);
        containerProgress.setVisibility(View.GONE);
        if (message != null) {
            tvErrorMsg.setText(message);
        }
    }
}
