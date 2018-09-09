package com.checkin.app.checkin.Utility;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.checkin.app.checkin.R;

import java.util.List;

import static com.checkin.app.checkin.Utility.Util.getImagesList;

public class EditProfileImage extends AppCompatActivity {

    protected List<MediaImage> ImageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_image);

    }

    static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false);
            return new MyViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.bindData();
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.item_gallery;
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;



            public MyViewHolder(View itemView) {
                super(itemView);
                imageView=itemView.findViewById(R.id.im_image);
            }

            void bindData() {
                ImageList=

            }
        }
    }
}
