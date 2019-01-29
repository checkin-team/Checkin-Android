package com.checkin.app.checkin.Manager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionEventBasicModel;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_EVENT_TYPE.EVENT_CONCERN;
import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_EVENT_TYPE.EVENT_REQUEST_SERVICE;
import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_EVENT_TYPE.EVENT_SESSION_CHECKIN;
import static com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_EVENT_TYPE.EVENT_SESSION_CHECKOUT;

public class ManagerSessionEventAdapter extends RecyclerView.Adapter<ManagerSessionEventAdapter.ViewHolder> {
    private List<WaiterEventModel> mEvent;
    private SessionEventInteraction mListener;

    ManagerSessionEventAdapter(SessionEventInteraction ordersInterface) {
        mListener = ordersInterface;
    }

    public void setData(List<WaiterEventModel> data) {
        this.mEvent = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bindData(mEvent.get(position));
    }

    @Override
    public int getItemCount() {
        return mEvent != null ? mEvent.size() : 0;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_manager_session_events;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_as_ms_event)
        ImageView imMsEvent;
        @BindView(R.id.tv_ms_event_msg)
        TextView tvMsEventMsg;
        @BindView(R.id.tv_ms_event_order_status)
        TextView tvMsEventOrderStatus;
        @BindView(R.id.btn_ms_event_done)
        TextView btnMsEventDone;
        @BindView(R.id.btn_ms_event_approve)
        Button btnMsEventApprove;

        private WaiterEventModel mEventModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(WaiterEventModel eventModel) {
            this.mEventModel = eventModel;
                tvMsEventMsg.setText(eventModel.getMessage());
                imMsEvent.setImageResource(SessionEventBasicModel.getEventIcon(eventModel.getType(), eventModel.getService(), null));

                if (eventModel.getType() == EVENT_SESSION_CHECKOUT) {
                    btnMsEventApprove.setVisibility(View.VISIBLE);
                    btnMsEventDone.setVisibility(View.GONE);
                }

                if (eventModel.getType() == EVENT_SESSION_CHECKIN)
                    btnMsEventDone.setVisibility(View.GONE);


                if (eventModel.getType() == EVENT_REQUEST_SERVICE)
                    tvMsEventOrderStatus.setVisibility(View.VISIBLE);

                if(eventModel.getType() == EVENT_CONCERN){
                    tvMsEventOrderStatus.setVisibility(View.VISIBLE);
                    tvMsEventOrderStatus.setText(tvMsEventOrderStatus.getContext().getResources().getString(R.string.quality));
                    tvMsEventOrderStatus.setBackgroundColor(tvMsEventOrderStatus.getContext().getResources().getColor(R.color.primary_red));
                }


        }
    }

    public interface SessionEventInteraction {
        void onOrderStatusChange();
    }
}