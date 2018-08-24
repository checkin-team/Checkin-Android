package com.checkin.app.checkin.Shop;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class SignUpIntroPager extends PagerAdapter {
    private List<View> views = new ArrayList<>();

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view;
        ViewHolder holder;

        if (views.size() <= position) {
            view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.view_pager_signup_intro, container, false);
            views.add(view);
        } else {
            view = views.get(position);
        }

        if (view.getTag() != null) {
            holder = ((ViewHolder) view.getTag());
        } else {
            holder = new ViewHolder(view);
        }
        holder.bindData(position);
        view.setTag(holder);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    class ViewHolder {
        View itemView;
        TextView tv1, tv2;
        ImageView im1;

        ViewHolder(@NonNull View view) {
            this.itemView = view;

            tv1 = view.findViewById(R.id.tv_1);
            tv2 = view.findViewById(R.id.tv_2);
            im1 = view.findViewById(R.id.im_1);
        }

        void bindData(int position) {
            switch (position) {
                case 2:
                    tv1.setText("Insights");
                    tv2.setText("Learn about the followers and see\n" +
                            "how your business is performing.");
                    im1.setImageResource(R.drawable.noun_data_insight_1572885);
                    break;
                case 3:
                    tv1.setText("Activity");
                    tv2.setText("Track your in-store traffic, live orders \n" +
                            "and expand the reach of your business \n" +
                            "among consumers.");
                    im1.setImageResource(R.drawable.noun_cog_404491);
                    break;

                case 0:
                    tv1.setText("Business Profile");
                    tv2.setText("Add phone number, email or location so\n" +
                            "that Checkin help you grow your business.");
                    im1.setImageResource(R.drawable.noun_shop_799701);
                    break;
                case 1:
                    tv1.setText("Dynamic Menu");
                    tv2.setText("Upload your regular menu and have a\n" +
                            " real time control of managing the inventory.");
                    im1.setImageResource(R.drawable.noun_1092662_cc);
                    break;
            }
        }
    }
}
