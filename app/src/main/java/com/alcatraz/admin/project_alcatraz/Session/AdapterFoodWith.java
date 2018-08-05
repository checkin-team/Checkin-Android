package com.alcatraz.admin.project_alcatraz.Session;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.User.User;
import com.bumptech.glide.Glide;

import java.util.List;

public class AdapterFoodWith extends RecyclerView.Adapter<AdapterFoodWith.MyView> {

    List<User> users;
    Context context;

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_with, parent, false);

        return new MyView(view,viewType);
    }


    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        if (users != null){
            holder.textView.setText(users.get(position).getUsername());
            Glide.with(context.getApplicationContext()).load(users.get(position).getImageUrl()).into(holder.img);
        }
        else {
            // Data not ready
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if(users != null) return users.size();
        else return 0;

    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public  AdapterFoodWith(Context context,List<User> users){
        this.context = context;
        this.users = users;
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
