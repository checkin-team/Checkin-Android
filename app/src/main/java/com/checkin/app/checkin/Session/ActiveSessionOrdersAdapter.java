package com.checkin.app.checkin.Session;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.checkin.app.checkin.Menu.OrderedItemModel;
import com.checkin.app.checkin.R;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveSessionOrdersAdapter extends RecyclerView.Adapter<ActiveSessionOrdersAdapter.ViewHolder> {
    private final static String TAG = ActiveSessionOrdersAdapter.class.getSimpleName();
    private List<OrderedItemModel> orderedItems;
    private SessionOrdersInteraction mOrdersInteractionListener;
    private ViewHolder mPrevDetailedViewHolder;

    ActiveSessionOrdersAdapter(SessionOrdersInteraction ordersInteraction) {
        mOrdersInteractionListener = ordersInteraction;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_session_order;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(v -> toggleDetails(holder));
        return holder;
    }

    private void toggleDetails(ViewHolder holder) {
        if (holder.isDetailShown) {
            hideDetails(holder);
        } else {
            hideDetails(mPrevDetailedViewHolder);
            showDetails(holder);
        }
    }

    private void hideDetails(ViewHolder holder) {
        if (holder != null) {
            mPrevDetailedViewHolder = null;
            holder.hideDetails();
        }
    }

    private void showDetails(ViewHolder holder) {
        if (holder != null) {
            mPrevDetailedViewHolder = holder;
            holder.showDetails();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(orderedItems.get(position));
    }

    @Override
    public int getItemCount() {
        return orderedItems != null ? orderedItems.size() : 0;
    }

    public void setOrderedItems(List<OrderedItemModel> orderedItems) {
        this.orderedItems = orderedItems;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name) TextView tvItemName;
        @BindView(R.id.btn_cancel_order) Button btnCancel;
        @BindView(R.id.tv_cancel_time) TextView tvCancelTime;
        private OrderedItemModel mOrderedItem;

        boolean isDetailShown = false;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindData(OrderedItemModel orderedItem) {
            mOrderedItem = orderedItem;
            tvItemName.setText(orderedItem.getItem().getName());
        }

        @OnClick(R.id.btn_cancel_order)
        void onCancelOrder() {
            mOrdersInteractionListener.onCancelOrder(orderedItems.get(getAdapterPosition()));
            orderedItems.remove(mOrderedItem);
            notifyItemRemoved(getAdapterPosition());
        }

        void showDetails() {
            isDetailShown = true;
            if (mOrderedItem.canCancel()) {
                Log.e(TAG, "Can cancel " + mOrderedItem.getItem().getName());
                new CountDownTimer(mOrderedItem.getRemainingCancelTime(), 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long min = millisUntilFinished/1000/60;
                        long sec = millisUntilFinished/1000-min*60;
                        if (min == 0)
                            tvCancelTime.setText(String.format(Locale.ENGLISH, "%02d seconds", sec));
                        else
                            tvCancelTime.setText(String.format(Locale.ENGLISH, "%02d:%02d minutes", min, sec));
                    }

                    @Override
                    public void onFinish() {
                        tvItemName.setVisibility(View.VISIBLE);
                        tvItemName.setText(R.string.item_non_cancelable);
                        tvCancelTime.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.GONE);
                    }
                }.start();
            }
            Transition slideStart = new Slide(Gravity.START);
            slideStart.addListener(new Transition.TransitionListenerAdapter() {

                @Override
                public void onTransitionStart(@NonNull Transition transition) {
                    tvItemName.setText(mOrderedItem.getItem().getName());
                    tvCancelTime.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    itemView.setBackgroundColor(0x000000);
                }

                @Override
                public void onTransitionEnd(@NonNull Transition transition) {
                    tvItemName.setVisibility(View.GONE);
                    if (mOrderedItem.canCancel()) {
                        TransitionManager.beginDelayedTransition(((ViewGroup) itemView));
                        btnCancel.setVisibility(View.VISIBLE);
                        tvCancelTime.setVisibility(View.VISIBLE);
                    } else {
                        TransitionManager.beginDelayedTransition(((ViewGroup) itemView), new Slide(Gravity.END));
                        tvItemName.setText(R.string.item_non_cancelable);
                        tvItemName.setVisibility(View.VISIBLE);
                    }
                    itemView.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.translucent_half_black));
                    mPrevDetailedViewHolder = ViewHolder.this;
                }
            });
            TransitionManager.beginDelayedTransition(((ViewGroup) itemView), slideStart);
            tvItemName.setVisibility(View.INVISIBLE);
        }

        void hideDetails() {
            isDetailShown = false;
            Transition slideEnd = new Slide(Gravity.END);
            slideEnd.addListener(new Transition.TransitionListenerAdapter() {

                @Override
                public void onTransitionStart(@NonNull Transition transition) {
                    itemView.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.translucent_half_black));
                }

                @Override
                public void onTransitionEnd(@NonNull Transition transition) {
                    tvCancelTime.setVisibility(View.GONE);
                    tvItemName.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.GONE);
                    tvItemName.setText(mOrderedItem.getItem().getName());
                    itemView.setBackgroundColor(0x000000);
                    mPrevDetailedViewHolder = null;
                }
            });
            TransitionManager.beginDelayedTransition(((ViewGroup) itemView), slideEnd);
            tvItemName.setVisibility(View.INVISIBLE);
        }
    }

    interface SessionOrdersInteraction {
        void onCancelOrder(OrderedItemModel orderedItem);
    }
}
