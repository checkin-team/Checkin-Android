package com.checkin.app.checkin.Session.ActiveSession;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.Utils;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PostCheckinFragment extends Fragment {

    @BindView(R.id.btn_privacy_private)
    ImageView btnPrivate;
    @BindView(R.id.btn_privacy_public)
    ImageView btnPublic;
    @BindView(R.id.im_help_choose_privacy)
    ImageView im_help_choose_privacy;
    @BindView(R.id.tooltip_help_choose_privacy)
    ToolTipRelativeLayout tooltipHelpPrivacy;

    private ToolTipView mToolTipView;
    private Unbinder unbinder;
    private PRIVACY_LEVEL mSelectedPrivacy;
    private PostCheckinInteraction mListener;

    enum PRIVACY_LEVEL {
        PRIVATE, PUBLIC
    }

    public PostCheckinFragment() {
        // Required empty constructor
    }

    public static PostCheckinFragment newInstance(PostCheckinInteraction interactionListener) {
        PostCheckinFragment fragment = new PostCheckinFragment();
        fragment.mListener = interactionListener;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_post_checkin, container, false);
        unbinder = ButterKnife.bind(this, view);
        view.setOnTouchListener((v, event) -> true);
        return view;
    }

    private void addToolTipView() {
        ToolTip toolTip = new ToolTip()
                .withText("A tooltip button to show text.")
                .withColor(getResources().getColor(R.color.primary_red))
                .withTextColor(getResources().getColor(R.color.white))
                .withShadow()
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);

        mToolTipView = tooltipHelpPrivacy.showToolTipForView(toolTip, im_help_choose_privacy);
        mToolTipView.setOnToolTipViewClickedListener(toolTipView -> mToolTipView = null);
    }

    @OnClick(R.id.im_help_choose_privacy)
    public void onClickHelp(View v) {
        if (mToolTipView == null) {
            addToolTipView();
        } else {
            mToolTipView.remove();
            mToolTipView = null;
        }
    }

    @OnClick(R.id.btn_proceed)
    public void OnProceedClicked() {
        if (mSelectedPrivacy == null) {
            Utils.toast(requireContext(), "Please select privacy level.");
            return;
        }
        mListener.OnProceedClicked(mSelectedPrivacy == PRIVACY_LEVEL.PUBLIC);
        requireFragmentManager().popBackStack();
    }

    @OnClick({R.id.btn_privacy_public, R.id.btn_privacy_private})
    public void onPrivacySelected(ImageView view) {
        view.setSelected(true);
        mSelectedPrivacy = (view.getId() == R.id.btn_privacy_private) ? PRIVACY_LEVEL.PRIVATE : PRIVACY_LEVEL.PUBLIC;
        if (mSelectedPrivacy == PRIVACY_LEVEL.PRIVATE) btnPublic.setSelected(false);
        else btnPrivate.setSelected(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public interface PostCheckinInteraction {
        void OnProceedClicked(boolean isPublic);
    }
}
