package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.MultiSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChangeRoleFragment extends DialogFragment implements MultiSpinner.MultiSpinnerListener {
    @BindView(R.id.user_image)
    ImageView displayPic;
    @BindView(R.id.user_fullname)
    TextView fullName;
    @BindView(R.id.spinner_roles)
    MultiSpinner vRoles;
    @BindView(R.id.save_button)
    TextView saveButton;
    @BindView(R.id.prev_role)
    TextView prevRole;


    @Nullable @BindView(R.id.remove_button)
    TextView removeButton;

    private MemberModel memberModel;
    private int positionF;
    Unbinder unbinder;
    private onClickButtons onInteractionListener;
    public void ChangeRoleFragment(){

    }

    @Override
    public void onItemsSelected(boolean[] selected) {

    }

    public interface onClickButtons
    {
        public void setRole(MemberModel memberModel, int position, int roles[]);

    }

    public void setOnInteractionListener(onClickButtons onInteractionListener) {
        this.onInteractionListener = onInteractionListener;
    }

    public void setMemberModel(MemberModel memberModel, int position) {
        this.memberModel = memberModel;
        this.positionF=position;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;

        if(memberModel.isAdmin()||memberModel.isOwner()||memberModel.isManager()||memberModel.isWaiter()||memberModel.isCook())
        {rootView = inflater.inflate(R.layout.fragment_shop_member_edit, container, false);
            }
        else
        {            rootView = inflater.inflate(R.layout.fragment_shop_member_add, container, false);

        }
        unbinder = ButterKnife.bind(this, rootView);
        if(memberModel!=null) {
            if(memberModel.getUser().getDisplayPic()!=null)
            Glide.with(fullName.getContext()).load(memberModel.getUser().getDisplayPic()).into(displayPic);
            fullName.setText(memberModel.getUser().getDisplayName());
            rootView.findViewById(R.id.container_roles).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vRoles.performClick();
                }
            });
            vRoles.setListener(this);
            int flag=0;
            List<CharSequence> selectedList = new ArrayList<>();
            if(memberModel.isOwner())  selectedList.add("Owner");
            if(memberModel.isAdmin()) selectedList.add("Shop Admin");
            if(memberModel.isManager()) selectedList.add("Restaurant Manager");
            if(memberModel.isWaiter()) selectedList.add("Waiter");
            if(memberModel.isCook()) selectedList.add("Cook");
            if(selectedList.size()>0) {
                if (selectedList.get(0).toString().contains(" "))
                    prevRole.setText(selectedList.get(0).subSequence(selectedList.get(0).toString().indexOf(' ') + 1, selectedList.get(0).length()));
                else
                    prevRole.setText(selectedList.get(0));
            }
            else prevRole.setText("Select");
            vRoles.selectEntries(selectedList.toArray(new CharSequence[] {}));
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int roles[] = vRoles.getSelectedItemsPosition();
                    Log.e("Fragment", String.valueOf(roles[0]));
                    onInteractionListener.setRole(memberModel, positionF, roles);

                }
            });
            if (removeButton != null)
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onInteractionListener.setRole(memberModel, positionF, null);
                    }
                });
        }
        return rootView;


    }



}
