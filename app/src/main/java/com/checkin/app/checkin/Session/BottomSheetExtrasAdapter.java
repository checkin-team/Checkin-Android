package com.checkin.app.checkin.Session;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.HeaderFooterRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetExtrasAdapter extends RecyclerView.Adapter<BottomSheetExtrasAdapter.BottomSheetExtrasViewHolder> {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> extrasList;

    public BottomSheetExtrasAdapter(Context context, ArrayList<String> extrasList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.extrasList = extrasList;
    }

    @Override
    public BottomSheetExtrasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder = layoutInflater.inflate(R.layout.item_bottom_sheet_extras, parent, false);
        return new BottomSheetExtrasViewHolder(viewHolder);
    }


    @Override
    public void onBindViewHolder(BottomSheetExtrasViewHolder holder, final int position) {
        holder.tv_extras_name.setText(extrasList.get(position));
    }


    @Override
    public int getItemCount() {
        return extrasList.size();
    }


    public class BottomSheetExtrasViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_extras_name)
        public TextView tv_extras_name;

        public BottomSheetExtrasViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}