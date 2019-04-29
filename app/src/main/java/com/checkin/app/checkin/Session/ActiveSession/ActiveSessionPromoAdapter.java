package com.checkin.app.checkin.Session.ActiveSession;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionPromoModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;

public class ActiveSessionPromoAdapter extends RecyclerView.Adapter<ActiveSessionPromoAdapter.ItemViewHolder> {
    private List<SessionPromoModel> mItemsList;
    private boolean mIsSessionActive;
    private OnItemInteractionListener mListener;

    public ActiveSessionPromoAdapter(List<SessionPromoModel> itemsList, OnItemInteractionListener listener, boolean isSessionActive) {
        mItemsList = itemsList;
        mListener = listener;
        mIsSessionActive = isSessionActive;
    }

    public void setMenuItems(List<SessionPromoModel> menuItems) {
        this.mItemsList = menuItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_active_session_promo;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bindData(mItemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemsList != null ? mItemsList.size() : 0;
    }

    public interface OnItemInteractionListener {
        boolean onMenuItemShowInfo(SessionPromoModel item);

        void onClickItemAvailability(SessionPromoModel mItem, boolean isChecked);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {


        private SessionPromoModel mItem;

        ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(SessionPromoModel menuItem) {
            this.mItem = menuItem;

        }
    }
}
