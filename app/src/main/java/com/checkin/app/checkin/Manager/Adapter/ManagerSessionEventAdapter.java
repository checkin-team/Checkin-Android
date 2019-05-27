package com.checkin.app.checkin.Manager.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.Manager.Model.ManagerSessionEventModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE;
import com.checkin.app.checkin.session.activesession.chat.SessionEventBasicModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_EVENT_TYPE.EVENT_REQUEST_CHECKOUT;
import static com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_EVENT_TYPE.EVENT_REQUEST_SERVICE;

public class ManagerSessionEventAdapter extends RecyclerView.Adapter<ManagerSessionEventAdapter.ViewHolder> {
    private List<ManagerSessionEventModel> mEvent;
    private SessionEventInteraction mListener;

    public ManagerSessionEventAdapter(SessionEventInteraction ordersInterface) {
        this.mListener = ordersInterface;
    }

    public void setData(List<ManagerSessionEventModel> data) {
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
        return R.layout.item_manager_session_event;
    }

    public interface SessionEventInteraction {
        void onEventMarkDone(ManagerSessionEventModel eventModel);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_ms_event_type)
        ImageView imEventType;
        @BindView(R.id.tv_ms_event_msg)
        TextView tvEventMsg;
        @BindView(R.id.tv_ms_event_subtype)
        TextView tvEventSubType;
        @BindView(R.id.btn_ms_event_done)
        TextView btnEventDone;

        private ManagerSessionEventModel mEventModel;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            btnEventDone.setOnClickListener(v -> mListener.onEventMarkDone(mEventModel));
        }

        private void resetLayout() {
            btnEventDone.setVisibility(View.GONE);
            tvEventSubType.setVisibility(View.GONE);
            tvEventSubType.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.pinkish_grey));
        }

        void bindData(ManagerSessionEventModel eventModel) {
            resetLayout();
            this.mEventModel = eventModel;

            tvEventSubType.setVisibility(View.GONE);

            tvEventMsg.setText(eventModel.getMessage());
            imEventType.setImageResource(SessionEventBasicModel.getEventIcon(eventModel.getType(), eventModel.getService(), eventModel.getConcern()));

            if (eventModel.getType() == EVENT_REQUEST_CHECKOUT && eventModel.getStatus() == CHAT_STATUS_TYPE.OPEN) {
                btnEventDone.setVisibility(View.GONE);
            }

            if (eventModel.getType() == EVENT_REQUEST_SERVICE && eventModel.getStatus() != CHAT_STATUS_TYPE.DONE)
                btnEventDone.setVisibility(View.VISIBLE);
            else btnEventDone.setVisibility(View.GONE);

            switch (eventModel.getType()) {
                case EVENT_REQUEST_SERVICE:
                    tvEventSubType.setVisibility(View.VISIBLE);
                    tvEventSubType.setText(R.string.title_request);
                    break;
                case EVENT_CONCERN:
                    tvEventSubType.setVisibility(View.VISIBLE);
                    tvEventSubType.setText(eventModel.formatConcernType());
                    tvEventSubType.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.primary_red));
                    break;
            }
        }
    }
}