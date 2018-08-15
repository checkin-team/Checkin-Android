package com.alcatraz.admin.project_alcatraz.Home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.GlideApp;

import java.net.URL;
import java.util.List;

public class AdapterFoodWith extends RecyclerView.Adapter<AdapterFoodWith.MyView> {
    List<Integer> image;List<String> names;
    private Context context;


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
        GlideApp.with(context).load("data:image/jpeg;base64,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").into(holder.img);
        //holder.img.setImageResource(image.get(position));
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
    public  AdapterFoodWith(List<Integer> image,List<String> names,Context context){
        this.image=image;
        this.names=names;
        this.context=context;

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
