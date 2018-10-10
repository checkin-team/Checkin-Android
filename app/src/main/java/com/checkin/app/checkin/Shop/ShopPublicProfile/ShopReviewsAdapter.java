package com.checkin.app.checkin.Shop.ShopPublicProfile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.MySpannable;
import com.checkin.app.checkin.Shop.ShopReviewPOJO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jogi Miglani on 07-10-2018.
 */

public class ShopReviewsAdapter extends RecyclerView.Adapter<ShopReviewsAdapter.ShopReviewViewHolder> {

    private ArrayList<ShopReviewPOJO> list_members=new ArrayList<>();
    private final LayoutInflater inflater;
    View view;
    ShopReviewViewHolder holder;
    private Context context;

    public ShopReviewsAdapter(Context context)
    {
        this.context=context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public ShopReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view =inflater.inflate(R.layout.item_review_recycler_view,parent,false);
        ShopReviewViewHolder viewHolder=new ShopReviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShopReviewViewHolder holder, int position) {
        ShopReviewPOJO list_items=list_members.get(position);
        holder.name.setText(list_items.getName());
        holder.reviewAndFollowers.setText(list_items.getReviewsAndFollowers());
        holder.totalVisits.setText(list_items.getTotalVisits());
        holder.tv.setText(list_items.getFullReview());
        makeTextViewResizable(holder.tv, 3, "read more", true);
        holder.time.setText(list_items.getTime());
        holder.rating.setText(Integer.toString(list_items.getRating()));

    }

    public void setListContent(ArrayList<ShopReviewPOJO> list_members){
        this.list_members=list_members;
        notifyItemRangeChanged(0,list_members.size());

    }

    @Override
    public int getItemCount() {
        return list_members.size();
    }

    public static class ShopReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name,reviewAndFollowers,time,totalVisits,tv,rating;
        public ShopReviewViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name=(TextView)itemView.findViewById(R.id.tv_name);
            reviewAndFollowers=(TextView)itemView.findViewById(R.id.tv_reviews_followers);
            totalVisits=(TextView)itemView.findViewById(R.id.tv_visits);
            tv=(TextView)itemView.findViewById(R.id.tv_full_review);
            time=(TextView)itemView.findViewById(R.id.tv_review_time);
            rating=(TextView)itemView.findViewById(R.id.rating_number);
        }
        @Override
        public void onClick(View v) {

        }
    }
    public void removeAt(int position) {
        list_members.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, list_members.size());
    }



    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(
                        addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false){
                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "read less", false);
                    } else {
                        makeTextViewResizable(tv, 3, "read more", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

}

