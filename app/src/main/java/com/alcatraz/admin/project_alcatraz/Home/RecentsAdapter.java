package com.alcatraz.admin.project_alcatraz.Home;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TAIYAB on 27-06-2018.
 */

public class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.MyView>{



    private List<Integer> imglist;


    public static class MyView extends RecyclerView.ViewHolder {

        public ImageView circle,ring;

        public MyView(View view, int viewType) {
            super(view);
            if(viewType==0)
                return;
            if(viewType==1) {
                circle = view.findViewById(R.id.right_circle);
                ring = view.findViewById(R.id.right_ring);
            }
            if(viewType==2){
                circle= view.findViewById(R.id.left_circle);
                ring = view.findViewById(R.id.left_ring);
            }

        }
    }


    public RecentsAdapter(List<Integer> imglist){
        this.imglist=imglist;
    }


    @Override

    public RecentsAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0)
            return new RecentsAdapter.MyView(LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_header,parent,false),viewType);
        View view;
        if(viewType==1)
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_right_recent, parent, false);
        else
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_left_recent, parent, false);
        return new RecentsAdapter.MyView(view,viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecentsAdapter.MyView holder, final int position) {
        // Log.e("TAG",name.get(position));
        if(position==0)return;
        if(imglist.get(position)==1) {
            holder.circle.setImageResource(R.drawable.redring);
            holder.ring.setImageResource(R.drawable.redoval);
        }
        if(imglist.get(position)==2) {
            holder.circle.setImageResource(R.drawable.greenring);
            holder.ring.setImageResource(R.drawable.greenoval);
        }
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
        if(position==0)
            return 0;
        return position%2+1;
    }

    @Override
    public int getItemCount()
    {
        return imglist.size();
    }


}
