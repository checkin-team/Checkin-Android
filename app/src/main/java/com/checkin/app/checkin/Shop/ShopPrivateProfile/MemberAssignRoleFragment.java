package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;
import com.checkin.app.checkin.Utility.MultiSpinner;
import com.checkin.app.checkin.Utility.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MemberAssignRoleFragment extends DialogFragment {
    private static final String TAG = MemberAssignRoleFragment.class.getSimpleName();
    private Unbinder unbinder;

    public static final int POSITION_NEW_MEMBER = -1;

    @BindView(R.id.tv_user_name) TextView tvUserName;
    @BindView(R.id.im_user_pic) ImageView imUserPic;
    @BindView(R.id.spinner_roles) MultiSpinner vSpinnerRoles;
    @BindView(R.id.btn_remove) Button btnRemove;

    private int mPosition;
    private AssignRoleInteraction mListener;
    private MemberViewModel mViewModel;

    public static MemberAssignRoleFragment newInstance(int position, AssignRoleInteraction listener) {
        MemberAssignRoleFragment fragment = new MemberAssignRoleFragment();
        fragment.mPosition = position;
        fragment.mListener = listener;
        return fragment;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shop_member_assign_role, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialogTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Window window = getDialog().getWindow();
        assert window != null;

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupViewModel();

        MemberModel member = mViewModel.getCurrentMember();
        if (member == null)
            return;

        if (isNewMember()) {
            btnRemove.setText(R.string.btn_cancel);
            btnRemove.setOnClickListener(this::onCancelClick);
        } else {
            btnRemove.setOnClickListener(this::onRemoveClick);
        }

        tvUserName.setText(member.getUser().getDisplayName());

        String picUrl = member.getUser().getDisplayPic();
        GlideApp.with(requireContext())
                .load(picUrl != null ? picUrl : R.drawable.cover_unknown_male)
                .into(imUserPic);

        vSpinnerRoles.selectValues(member.getRoles());
    }

    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(MemberViewModel.class);

        mViewModel.getObservableData().observe(this, resource -> {
           if (resource == null)
               return;
           if (resource.status == Status.SUCCESS) {
               if (isNewMember()) {
                   mListener.onNewMember(mViewModel.getCurrentMember());
               } else {
                   mListener.onUpdateMember(mViewModel.getCurrentMember(), mPosition);
               }
               this.finishDialog();
           } else if (resource.message != null) {
               Util.toast(requireContext(), resource.message);
           }
           mViewModel.resetObservableData();
        });

        mViewModel.getRemovedMemberData().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Status.SUCCESS) {
                mListener.onRemoveMember(mViewModel.getCurrentMember(), mPosition);
                this.finishDialog();
            } else if (resource.message != null){
                Util.toast(requireContext(), resource.message);
            }
            mViewModel.resetRemovedMemberData();
        });
    }

    public void finishDialog() {
        dismiss();
    }

    public void onCancelClick(View v) {
        mListener.onCancel();
        dismiss();
    }

    public void onRemoveClick(View v) {
        mViewModel.deleteShopMember();
    }

    @OnClick(R.id.btn_save)
    public void onSaveClick(View v) {
        CharSequence[] roles = vSpinnerRoles.getSelectedValues();
        if (isNewMember())
            mViewModel.addShopMember(roles);
        else
            mViewModel.updateShopMember(roles);
    }

    private boolean isNewMember() {
        return mPosition == POSITION_NEW_MEMBER;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public interface AssignRoleInteraction {
        void onNewMember(MemberModel member);
        void onUpdateMember(MemberModel member, int position);
        void onRemoveMember(MemberModel member, int position);
        void onCancel();
    }
}
