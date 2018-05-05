package com.alcatraz.admin.project_alcatraz.Home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;

import java.util.List;


/**
 * Created by TAIYAB on 05-02-2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyView> {

    private List<String> name;
    private List<String> lastmsg;
    private List<Integer> img;
    

    public static class MyView extends RecyclerView.ViewHolder {

        public TextView textView;
        public TextView last;
        public de.hdodenhof.circleimageview.CircleImageView im;

        public MyView(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.msg_name);
            last=view.findViewById(R.id.msg_last);
            im=view.findViewById(R.id.msg_photo);


        }
    }


    public MessageAdapter(List<String> horizontalList,List<String> last,List<Integer> img) {
        this.name = horizontalList;
        this.img=img;
        this.lastmsg=last;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {
       // Log.e("TAG",name.get(position));
        holder.textView.setText(name.get(position));
        holder.last.setText(lastmsg.get(position));
        holder.im.setImageResource(img.get(position));
    }

    @Override
    public int getItemCount()
    {
        return name.size();
    }

}