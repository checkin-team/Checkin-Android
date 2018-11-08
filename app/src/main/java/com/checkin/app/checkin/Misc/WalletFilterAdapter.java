package com.checkin.app.checkin.Misc;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.List;

public class WalletFilterAdapter extends RecyclerView.Adapter<WalletFilterAdapter.MyView>{
    private List<Boolean> l;
    List<String> name;
    public WalletFilterAdapter(List<Boolean> l, List<String> n){
        this.l=l;
        name=n;
    }
    public static class MyView extends RecyclerView.ViewHolder {

        public TextView textView;

        public MyView(View view) {
            super(view);

            textView = (TextView) view.findViewById(R.id.fillter);

        }
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ItemViewHolder should be constructed with a new View that can represent the ic_ordered_items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ItemViewHolder will be used to display ic_ordered_items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different ic_ordered_items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ItemViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyView(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_walletfilter,parent,false));
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ItemViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        if(l.get(position)){
            holder.textView.setBackgroundResource(R.drawable.wholegreen);
            holder.textView.setText(name.get(position));}
        else{
            holder.textView.setBackgroundResource(R.drawable.wholered);
            holder.textView.setText(name.get(position));}

    }

    /**
     * Returns the total number of ic_ordered_items in the data set held by the adapter.
     *
     * @return The total number of ic_ordered_items in this adapter.
     */
    @Override
    public int getItemCount() {
        return l.size();
    }
}
