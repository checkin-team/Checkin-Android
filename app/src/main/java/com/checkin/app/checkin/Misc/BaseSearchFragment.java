package com.checkin.app.checkin.Misc;

import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import butterknife.BindView;

public class BaseSearchFragment extends Fragment {
    // Requires R.layout.incl_search_progress to be included!!

    @BindView(R.id.container_progress) ViewGroup vContainerProgress;
    @BindView(R.id.tv_status) TextView tvResultStatus;
    @BindView(R.id.load_progress) ProgressBar vLoadProgress;

    public BaseSearchFragment() {}

    protected void showLoadProgress() {
        tvResultStatus.setText(R.string.status_searching);
        vLoadProgress.setVisibility(View.VISIBLE);
        vContainerProgress.setVisibility(View.VISIBLE);
    }

    protected void hideLoadProgress() {
        vContainerProgress.setVisibility(View.GONE);
    }

    protected void noResultFound() {
        tvResultStatus.setText(R.string.status_search_not_found);
        vLoadProgress.setVisibility(View.GONE);
        vContainerProgress.setVisibility(View.VISIBLE);
    }
}
