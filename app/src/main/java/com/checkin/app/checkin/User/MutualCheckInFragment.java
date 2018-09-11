package com.checkin.app.checkin.User;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhavik Patel on 25/08/2018.
 */

public class MutualCheckInFragment extends Fragment {

    @BindView(R.id.mutualCheckInRV) RecyclerView mutualCheckInRV;
    private MutualCheckInAdapter mutualCheckInAdapter;
    private MutualCheckInFragmentCompat compat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_mutual_checkin_fragment,container,false);
        ButterKnife.bind(this,view);
        mutualCheckInAdapter = new MutualCheckInAdapter(getMutualCheckInModels());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mutualCheckInRV.setLayoutManager(layoutManager);
        mutualCheckInRV.setAdapter(mutualCheckInAdapter);
        if(getActivity() instanceof MutualCheckInFragmentCompat){
            compat = (MutualCheckInFragmentCompat) getActivity();
        }
        return view;
    }

    class MutualCheckInAdapter extends RecyclerView.Adapter<MutualCheckInAdapter.ViewHolder>{

        private List<MutualCheckInModel> mutualCheckInModels;


        class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.profile) ImageView profileImage;
            @BindView(R.id.action) ImageView actionImage;
            @BindView(R.id.profileName) TextView profileName;
            @BindView(R.id.actionName) TextView actionName;
            View container;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                container = itemView;
            }
            public void bind(MutualCheckInModel mutualCheckInModel){
                //Glide.with(getContext()).load(mutualCheckInModel.getProfileUrl()).into(profileImage);
                //Glide.with(getContext()).load(mutualCheckInModel.getActionUrl()).into(actionImage);
                profileName.setText(mutualCheckInModel.getProfileName());
                actionName.setText(mutualCheckInModel.getActionName());
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        compat.onClick(v,mutualCheckInModel);
                    }
                });
            }
        }

        public MutualCheckInAdapter(List<MutualCheckInModel> mutualCheckInModels) {
            if(mutualCheckInModels != null)this.mutualCheckInModels = mutualCheckInModels;
            else this.mutualCheckInModels = new ArrayList<>();
        }

        @NonNull
        @Override
        public MutualCheckInAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_profile_mutual_checkin_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MutualCheckInAdapter.ViewHolder holder, int position) {
            holder.bind(mutualCheckInModels.get(position));
        }

        @Override
        public int getItemCount() {
            return mutualCheckInModels.size();
        }
    }

    private List<MutualCheckInModel> getMutualCheckInModels(){
        List<MutualCheckInModel> mutualCheckInModels = new ArrayList<>();
        for(int i = 0;i < 10;i++){
            mutualCheckInModels.add(new MutualCheckInModel("","","Bhavik Patel" ,"Ramada Hotel " + i));
        }
        return mutualCheckInModels;
    }

    public interface MutualCheckInFragmentCompat{
        void onClick(View view,MutualCheckInModel mutualCheckInModel);
    }
}
