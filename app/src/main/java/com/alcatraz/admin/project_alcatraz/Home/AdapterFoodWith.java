package com.alcatraz.admin.project_alcatraz.Home;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.User.User;
import com.alcatraz.admin.project_alcatraz.Utility.GlideApp;

import java.util.List;

public class AdapterFoodWith extends RecyclerView.Adapter<AdapterFoodWith.MyView> {
    List<Integer> image;List<String> names;


    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==0)
         view=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_with, parent, false);
        else
            view=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_food_add, parent, false);

        return new MyView(view,viewType);
    }

    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     * <p>
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>. Type codes need not be contiguous.
     */
    @Override
    public int getItemViewType(int position) {
        return position==0?1:0;
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        if(position==0)
            return;
        holder.textView.setText(names.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return names.size();
    }
    public  AdapterFoodWith(List<Integer> image,List<String> names){
        this.image=image;
        this.names=names;

    }

    public  class MyView extends RecyclerView.ViewHolder{
        de.hdodenhof.circleimageview.CircleImageView img;
        TextView textView;
        public  MyView(View view,int viewtype){
            super(view);
            if(viewtype==1)
                return;
            img=view.findViewById(R.id.cicular);
            textView=view.findViewById(R.id.textView16);

        }
    }

}
