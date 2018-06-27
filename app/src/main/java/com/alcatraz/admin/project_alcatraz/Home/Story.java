package com.alcatraz.admin.project_alcatraz.Home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alcatraz.admin.project_alcatraz.R;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.HORIZONTAL;

/**
 * Created by TAIYAB on 27-06-2018.
 */

public class Story extends AppCompatActivity{
    private static final String TAG = "Story";
    int pos=0,number_of_stories=10;
    boolean b=true;
    long t=System.currentTimeMillis();
    ValueAnimator animation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_story);
        ArrayList<Integer> img_list=new ArrayList<>();
        for(int i=0;i<5;i++) {
            img_list.add(R.drawable.water);
            img_list.add(R.drawable.fin);
        }
        final RecyclerView recyclerView=findViewById(R.id.storyRecycler);
        StoryAdapter storyAdapter=new StoryAdapter(img_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(storyAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                Log.e(TAG, "onInterceptTouchEvent: "+e.getAction() );


                switch (e.getAction()){
                    case MotionEvent.ACTION_UP:
                        b=false;
                        recyclerView.smoothScrollToPosition((pos+1)%number_of_stories);
                        b=!b;
                        pos++;
                        animation.setCurrentFraction(1);
                        animation.resume();
                        return true;
                    case MotionEvent.ACTION_DOWN:
                        animation.pause();
                        t=System.currentTimeMillis();
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        if(e.getDownTime()<50||e.getDownTime()>500)
                            return false;
                        b=false;
                        recyclerView.smoothScrollToPosition((pos+1)%number_of_stories);
                        b=!b;
                        pos++;
                        animation.setCurrentFraction(1);
                        return b;

                }
                if( e.getAction() != MotionEvent.ACTION_MOVE)
                    return false;
                return true;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        final View wl=findViewById(R.id.whiteline);
        final ViewGroup.LayoutParams p=wl.getLayoutParams();
        animation = ValueAnimator.ofInt(0, getWindowManager().getDefaultDisplay().getWidth());
        animation.setDuration(5000);
        animation.start();
        animation.setRepeatCount(number_of_stories);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                //Log.e(TAG, "onAnimationUpdate: Happening" );
                // You can use the animated value in a property that uses the
                // same type as the animation. In this case, you can use the
                // float value in the translationX property.
                int animatedValue = (int)updatedAnimation.getAnimatedValue();
                p.width=animatedValue;
                wl.setLayoutParams(p);
            }

        });
        animation.addListener(new ValueAnimator.AnimatorListener(){
            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                Log.e(TAG, "onAnimationRepeat: " );
                b=false;
                recyclerView.smoothScrollToPosition((pos+1)%number_of_stories);
                b=!b;
                pos++;

            }

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.e(TAG, "onAnimationEnd: " );
                //animation.start();
            }
        });



    }

}
