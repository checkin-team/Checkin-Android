package com.checkin.app.checkin.Session;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.NonPersonalProfile.UserViewModel;
import com.checkin.app.checkin.Misc.SelectListItem;
import com.checkin.app.checkin.Misc.SelectListViewAdapter;
import com.hootsuite.nachos.ChipConfiguration;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.chip.ChipSpan;
import com.hootsuite.nachos.chip.ChipSpanChipCreator;
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PostCheckinFragment extends Fragment {

    @BindView(R.id.btn_privacy_private) ImageView btnPrivate;
    @BindView(R.id.btn_privacy_public) ImageView btnPublic;
    @BindView(R.id.select_text_view) NachoTextView selectMembers;
    @BindView(R.id.im_help_choose_privacy) ImageView im_help_choose_privacy;
    @BindView(R.id.tooltip_help_choose_privacy) ToolTipRelativeLayout tooltip_help_choose_privacy;

    private ToolTipView mToolTipView;
    private Unbinder unbinder;
    private PostCheckinInteraction interactionListener;
    private UserViewModel mUserViewModel;
    private PRIVACY_LEVEL selectedPrivacy;
    public enum PRIVACY_LEVEL {
        PUBLIC, PRIVATE
    }

    public PostCheckinFragment() {
        // Required empty constructor
    }

    public static PostCheckinFragment newInstance(PostCheckinInteraction interactionListener){
        PostCheckinFragment fragment = new PostCheckinFragment();
        fragment.interactionListener = interactionListener;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_checkin, container, false);
        unbinder = ButterKnife.bind(this,view);
        view.setOnTouchListener((v, event) -> true);

        mUserViewModel = ViewModelProviders.of(getActivity(), new UserViewModel.Factory(getActivity().getApplication())).get(UserViewModel.class);
        setupAddMember();
        im_help_choose_privacy.setOnClickListener(v -> {
            if (mToolTipView == null) {
                addToolTipView();
            } else {
                mToolTipView.remove();
                mToolTipView = null;
            }});
        return view;
    }

    private void setupAddMember() {
        selectMembers.setThreshold(2);
        selectMembers.setChipTokenizer(new SpanChipTokenizer<>(getContext(), new ChipSpanChipCreator() {
            @Override
            public ChipSpan createChip(@NonNull Context context, @NonNull CharSequence text, Object data) {
                return new ChipSpan(getContext(), text, getResources().getDrawable(R.drawable.ic_close), data);
            }

            @Override
            public void configureChip(@NonNull ChipSpan chip, @NonNull ChipConfiguration chipConfiguration) {
                super.configureChip(chip, chipConfiguration);
                chip.setShowIconOnLeft(false);
                chip.setIconBackgroundColor(getResources().getColor(R.color.primary_red));
            }
        }, ChipSpan.class));
        selectMembers.setOnChipClickListener((chip, event) -> {
            Objects.requireNonNull(selectMembers.getChipTokenizer()).deleteChipAndPadding(chip, selectMembers.getEditableText());
        });

        List<SelectListItem> items = new ArrayList<>();
        mUserViewModel.getAllUsers().observe(this, userResource -> {
            if (userResource != null && userResource.status == Resource.Status.SUCCESS) {
                List<UserModel> users = userResource.data;
                for (UserModel user: users) {
                    items.add(new SelectListItem(user.getProfilePic(), user.getUsername(), "", user.getId()));
                }
            }
        });
        SelectListViewAdapter adapter = new SelectListViewAdapter(getContext(), items);
        selectMembers.setAdapter(adapter);
    }

    private void addToolTipView() {
        ToolTip toolTip = new ToolTip()
                .withText("A tooltip button to show text.")
                .withColor(getResources().getColor(R.color.primary_red))
                .withTextColor(getResources().getColor(R.color.white))
                .withShadow()
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);

        mToolTipView = tooltip_help_choose_privacy.showToolTipForView(toolTip, im_help_choose_privacy);
        mToolTipView.setOnToolTipViewClickedListener(toolTipView -> {
            mToolTipView = null;});
    }

    @OnClick(R.id.btn_proceed)
    public void OnProceedClicked() {
        List<Long> ids = new ArrayList<>();
        for (Chip chip: selectMembers.getAllChips()) {
            ids.add(((Long) ((SelectListItem) Objects.requireNonNull(chip.getData())).getData()));
        }
        if (selectedPrivacy == null) {
            Toast.makeText(getContext(), "Please select privacy level.", Toast.LENGTH_SHORT).show();
            return;
        }
        interactionListener.OnProceedClicked(ids, selectedPrivacy);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @OnClick({ R.id.btn_privacy_public,R.id.btn_privacy_private })
    public void onPrivacySelected(ImageView view) {
        view.setSelected(true);
        selectedPrivacy = (view.getId() == R.id.btn_privacy_private) ? PRIVACY_LEVEL.PRIVATE : PRIVACY_LEVEL.PUBLIC;
        if (selectedPrivacy == PRIVACY_LEVEL.PRIVATE)   btnPublic.setSelected(false);
        else btnPrivate.setSelected(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public interface PostCheckinInteraction {
        void OnProceedClicked(List<Long> userIds, PRIVACY_LEVEL selectedPrivacy);
    }
}
