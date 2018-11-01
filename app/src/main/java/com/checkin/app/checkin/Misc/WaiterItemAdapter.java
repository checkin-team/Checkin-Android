package com.checkin.app.checkin.Misc;

import android.app.usage.UsageEvents;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.checkin.app.checkin.Menu.MenuItemModel;
import com.checkin.app.checkin.Menu.OrderedItemModel;
import com.checkin.app.checkin.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaiterItemAdapter extends RecyclerView.Adapter<WaiterItemAdapter.ViewHolder>  {
    List<EventModel> mItems;
    private static OnItemInteractionListener mItemInteractionListener;
    private boolean height_small=true;
    private int height;
    String data;
    WaiterItemAdapter(List<EventModel> items
    ) {

        if(items!=null)mItems = items;else mItems=new ArrayList<>();
    }
    boolean StatusShow=true;



    public void setItemInteractionListener (OnItemInteractionListener listener) {
        mItemInteractionListener= listener;
    }

    public boolean isOrderedItem(int position){
        return mItems.get(position).getType()== EventModel.TYPE.ORDERED_ITEM;
    }

    public void setHeight_small(int position){
        if(isOrderedItem(position)) {
            OrderedItemModel itemModel= mItems.get(position).getOrderedItem();

            if (itemModel.getRemarks()!=null) {
                height_small = true;
            } else {
                height_small = false;
            }
        }
        else {
            height_small=true;
        }
    }



    @NonNull
    @Override
    public WaiterItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        return new WaiterItemAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull WaiterItemAdapter.ViewHolder holder, int position) {
        setHeight_small(position);
        if(!height_small) {
            Log.e("WaiterItemAdapter", "Kya mai idhar humesha aata hun"+position);
            height=holder.itemView.getLayoutParams().height;
            Log.e("WaiterItemAdapter", "Kya mai idhar humesha aata hun"+height+" "+position);
            holder.itemView.getLayoutParams().height*=1.25;
            Log.e("WaiterItemAdapter", "Ab jo mai aa gya"+holder.itemView.getLayoutParams().height+" "+position);
            holder.bindData(mItems.get(position));



        }
        else
        {
            height=holder.itemView.getLayoutParams().height;
            Log.e("WaiterItemAdapter", "Kya mai idhar humesha aata hun"+height+" "+position);
            holder.bindData(mItems.get(position));



        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    @Override
    public int getItemViewType(int position) {
        if(isOrderedItem(position))
        return R.layout.waiter_item_recy;
        else if(mItems.get(position).getType()== EventModel.TYPE.CUSTOM_MESSAGE)
                    {   data=mItems.get(position).getCustomMessage();
                        return R.layout.waiter_action_custom_recy;}
        else if(mItems.get(position).getType()== EventModel.TYPE.REQUEST_FOR_WATER)
             {  data="Refill Water";
                 return R.layout.waiter_action_water_recy;}
        else if(mItems.get(position).getType()== EventModel.TYPE.REQUEST_FOR_BILL)
            {
                data="Get The Bill";
                return R.layout.waiter_action_bill_recy;}
          else if(mItems.get(position).getType()== EventModel.TYPE.REQUEST_FOR_TABLE_CLEAN)
         {   data="Clean The Table";
             return R.layout.waiter_action_clean_recy;}
        else if(mItems.get(position).getType()== EventModel.TYPE.REQUEST_FOR_CALL_WAITER)
        {   data="Attend The Table";
            return R.layout.waiter_action_custom_recy;}

        return R.layout.waiter_action_custom_recy;
    }





    class ViewHolder extends RecyclerView.ViewHolder {
        @Nullable @BindView(R.id.card_view)
        CardView cardView;
        @Nullable @BindView(R.id.time)
        TextView time_ago;
        @Nullable @BindView(R.id.item_name)
        TextView itemName;
        @Nullable @BindView(R.id.quantity)
        TextView quantity;
        @Nullable @BindView(R.id.add_on)
        TextView addOn;
        @Nullable @BindView(R.id.pizza_crust)
        TextView pizzaCrust;
        @Nullable @BindView(R.id.add_on_signify)
        TextView addOnSignify;
        @Nullable  @BindView(R.id.pizza_crust_signify)
        TextView pizzaCrustSignify;
        @Nullable @BindView(R.id.cancel)
        ImageView cancel;
        @Nullable  @BindView(R.id.current_status)
        ImageView status;
        @BindView(R.id.completed)
        ImageView completed;
        @Nullable  @BindView(R.id.image_action)
                ImageView actionImage;
        @Nullable @BindView(R.id.action_text)
                TextView actionText;




        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        void bindData(EventModel item) {
            if(item.getType()== EventModel.TYPE.ORDERED_ITEM) {
                OrderedItemModel itemModel = item.getOrderedItem();
                itemName.setText(itemModel.getItem().getName());
                time_ago.setText("8 minutes Ago");
                quantity.setText("QTY: " + itemModel.getQuantity());
                if(itemModel.getRemarks()!=null)
                { addOnSignify.setText("Add On");
                pizzaCrustSignify.setVisibility(View.GONE);
                addOn.setText(itemModel.getRemarks());
                pizzaCrust.setVisibility(View.GONE);
                }
//            int size=item.getSelectedFields().size();
                StringBuilder stringBuilder = new StringBuilder();
                //  for(int i=0;i<size;i++)
                // {
                //   stringBuilder.append(item.getSelectedFields().get(i).getName());
                //}

                //addOn.setText(stringBuilder.toString());

                if (StatusShow) {
                    status.setVisibility(View.VISIBLE);
                } else {
                    status.setVisibility(View.GONE);
                }
                if (itemModel.getRemarks()==null) {
                    addOn.setVisibility(View.GONE);
                    pizzaCrust.setVisibility(View.GONE);
                    addOnSignify.setVisibility(View.GONE);
                    pizzaCrustSignify.setVisibility(View.GONE);

                }
            }
            else
            {

                actionText.setText(data);
            }


                completed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemInteractionListener.onClickCompleted(item, getAdapterPosition());
                        if(status!=null)
                        status.setVisibility(View.GONE);

                    }
                });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemInteractionListener.onClickCancelled(item,getAdapterPosition());
                }
            });


        }
    }
    public interface OnItemInteractionListener{
        public void onClickCompleted(EventModel item,int position);
        public void onClickCancelled(EventModel item,int position);
    }
}

