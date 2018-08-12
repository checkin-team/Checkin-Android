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
        View view=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_with, parent, false);

        return new MyView(view,viewType);
    }


    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
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

    public  class MyView extends RecyclerView.ViewHolder {
        de.hdodenhof.circleimageview.CircleImageView img;
        TextView textView;
        public  MyView(View view,int viewtype){
            super(view);
            img=view.findViewById(R.id.cicular);
            textView=view.findViewById(R.id.textView16);

        }
    }

}
