package com.checkin.app.checkin.Misc;

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
    List<OrderedItemModel> mItems;
    private static OnItemInteractionListener mItemInteractionListener;
    private boolean height_small=true;
    private int height;
    WaiterItemAdapter(List<OrderedItemModel> items
    ) {

        if(items!=null)mItems = items;else mItems=new ArrayList<>();
    }
    boolean StatusShow=true;


    public void setItemInteractionListener (OnItemInteractionListener listener) {
        mItemInteractionListener= listener;
    }

    public void setHeight_small(int position){
        if(mItems.get(position).getItem().getName().contains("burg"))
        {
            height_small=true;
        }
        else
        {
            height_small=false;
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

    public void setStatusSymbol(boolean show)
    {
        this.StatusShow=show;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
    @Override
    public int getItemViewType(final int position) {
        return R.layout.waiter_item_recy;
    }





    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_view)
        CardView cardView;
        @BindView(R.id.time)
        TextView time_ago;
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.quantity)
        TextView quantity;
        @BindView(R.id.add_on)
        TextView addOn;
        @BindView(R.id.pizza_crust)
        TextView pizzaCrust;
        @BindView(R.id.add_on_signify)
        TextView addOnSignify;
        @BindView(R.id.pizza_crust_signify)
        TextView pizzaCrustSignify;
        @BindView(R.id.cancel)
        ImageView cancel;
        @BindView(R.id.current_status)
        ImageView status;
        @BindView(R.id.completed)
        ImageView completed;




        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        void bindData(OrderedItemModel item) {

            itemName.setText(item.getItem().getName());
            time_ago.setText("8 minutes Ago");
            quantity.setText("QTY: "+item.getQuantity());
            addOnSignify.setText("Add On");
            pizzaCrustSignify.setText("Pizza Crust");
            addOn.setText("Extra Cheese");
            pizzaCrust.setText("Plain Bread");
//            int size=item.getSelectedFields().size();
            StringBuilder stringBuilder=new StringBuilder();
          //  for(int i=0;i<size;i++)
           // {
             //   stringBuilder.append(item.getSelectedFields().get(i).getName());
            //}

            //addOn.setText(stringBuilder.toString());

            if(StatusShow)
            {
                status.setVisibility(View.VISIBLE);
            }
            else
            {
                status.setVisibility(View.GONE);
            }
            if(item.getItem().getName().contains("burg"))
            {
                addOn.setVisibility(View.GONE);
                pizzaCrust.setVisibility(View.GONE);
                addOnSignify.setVisibility(View.GONE);
                pizzaCrustSignify.setVisibility(View.GONE);

            }



            completed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemInteractionListener.onClickCompleted(item,getAdapterPosition());
                    status.setVisibility(View.GONE);

                }
            });

        }
    }
    public interface OnItemInteractionListener{
        public void onClickCompleted(OrderedItemModel item,int position);
    }
}

