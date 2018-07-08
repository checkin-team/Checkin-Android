package com.alcatraz.admin.project_alcatraz.Home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;

import java.util.List;

/**
 * Created by TAIYAB on 27-06-2018.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.MyView>{
  
    

        private List<Integer> imglist;


        public static class MyView extends RecyclerView.ViewHolder {

            public com.jsibbold.zoomage.ZoomageView img;

            public MyView(View view, int viewType) {
                super(view);
                img=view.findViewById(R.id.storyimage);

            }
        }


        public StoryAdapter(List<Integer> imglist){
            this.imglist=imglist;
        }


        @Override

        public StoryAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.story_image, parent, false);
            return new StoryAdapter.MyView(view,viewType);

        }

        @Override
        public void onBindViewHolder(final StoryAdapter.MyView holder, final int position) {
            // Log.e("TAG",name.get(position));
            holder.img.setImageResource(imglist.get(position));
        }

        @Override
        public int getItemCount()
        {
            return imglist.size();
        }

    
}
