package com.alcatraz.admin.project_alcatraz.Home;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.transitionseverywhere.*;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static android.support.constraint.Constraints.TAG;

public class ActiveSessionFragment extends android.support.v4.app.Fragment {
    ListView lv;
    boolean swiped=false;
    ArrayList<String> listItems;
    Set<Integer> swipedInt=new HashSet<>();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activesession_order,
                container, false);
        lv=view.findViewById(R.id.list);

        listItems=new ArrayList<String>();
        String arr="Burger Chowmein Manchurian Egg_Roll Chicken";
        Scanner sc=new Scanner(arr);
        while(sc.hasNext())
            listItems.add(sc.next());
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(inflater.getContext(),R.layout.simple_text,R.id.text1,listItems);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.e(TAG, "onItemClick: Idef you");
                TextView tv=(TextView)(view.findViewById(R.id.text1));
                Button cancel=view.findViewById(R.id.cancel_button);
                if(tv.getText().equals(listItems.get(position))) {
                    swipedInt.add(position);
                    swiped=true;
                    Transition t=new Slide(Gravity.LEFT);
                    t.addListener(new Transition.TransitionListener(){

                        @Override
                        public void onTransitionStart(@NonNull Transition transition) {

                        }


                        @Override
                        public void onTransitionEnd(@NonNull Transition transition) {
                            if(ActiveSession.canCancel)
                            {
                                TransitionManager.beginDelayedTransition((ViewGroup)view);
                                cancel.setVisibility(View.VISIBLE);
                                ConstraintSet set=new ConstraintSet();
                                set.clone((ConstraintLayout)view);
                                set.setHorizontalBias(R.id.text1,.2f);
                                set.applyTo((ConstraintLayout)view);
                                tv.setVisibility(View.VISIBLE);
                                view.setBackgroundColor(0x80000000);


                            }
                            else {
                                tv.setText("Non Cancellable");
                                TransitionManager.beginDelayedTransition((ViewGroup) view, new Slide(Gravity.RIGHT));
                                tv.setVisibility(View.VISIBLE);
                                view.setBackgroundColor(0x80000000);

                            }

                        }


                        @Override
                        public void onTransitionCancel(@NonNull Transition transition) {
                            if(ActiveSession.canCancel)
                            {
                                //TransitionManager.beginDelayedTransition((ViewGroup)view);
                                cancel.setVisibility(View.VISIBLE);
                                ConstraintSet set=new ConstraintSet();
                                set.clone((ConstraintLayout)view);
                                set.setHorizontalBias(R.id.text1,.2f);
                                set.applyTo((ConstraintLayout)view);
                                tv.setVisibility(View.VISIBLE);
                                view.setBackgroundColor(0x80000000);


                            }
                            else {
                                tv.setText("Non Cancellable");
                                //TransitionManager.beginDelayedTransition((ViewGroup) view, new Slide(Gravity.RIGHT));
                                tv.setVisibility(View.VISIBLE);
                                view.setBackgroundColor(0x80000000);

                            }
                        }


                        @Override
                        public void onTransitionPause(@NonNull Transition transition) {

                        }


                        @Override
                        public void onTransitionResume(@NonNull Transition transition) {

                        }
                    });
                    TransitionManager.beginDelayedTransition((ViewGroup)view,t);
                    tv.setVisibility(View.INVISIBLE);


                }
                else {
                    Log.e(TAG, "onItemClick: I Read You");
                    swiped=false;
                    swipedInt.remove(position);
                    Transition t=new Slide(Gravity.LEFT);
                    t.addListener(new Transition.TransitionListener(){

                        @Override
                        public void onTransitionStart(@NonNull Transition transition) {

                        }


                        @Override
                        public void onTransitionEnd(@NonNull Transition transition) {
                            if(ActiveSession.canCancel)
                            {
                                TransitionManager.beginDelayedTransition((ViewGroup)view);
                                cancel.setVisibility(View.GONE);
                                ConstraintSet set=new ConstraintSet();
                                set.clone((ConstraintLayout)view);
                                set.setHorizontalBias(R.id.text1,.5f);
                                set.applyTo((ConstraintLayout)view);



                            }
                            tv.setText(listItems.get(position));
                            TransitionManager.beginDelayedTransition((ViewGroup)view,new Slide(Gravity.RIGHT));
                            tv.setVisibility(View.VISIBLE);
                            view.setBackgroundColor(0x000000);

                        }


                        @Override
                        public void onTransitionCancel(@NonNull Transition transition) {

                            if(ActiveSession.canCancel)
                            {
                                //TransitionManager.beginDelayedTransition((ViewGroup)view);
                                cancel.setVisibility(View.GONE);
                                ConstraintSet set=new ConstraintSet();
                                set.clone((ConstraintLayout)view);
                                set.setHorizontalBias(R.id.text1,.5f);
                                set.applyTo((ConstraintLayout)view);



                            }
                            tv.setText(listItems.get(position));
                            //TransitionManager.beginDelayedTransition((ViewGroup)view,new Slide(Gravity.RIGHT));
                            tv.setVisibility(View.VISIBLE);
                            view.setBackgroundColor(0x000000);



                        }


                        @Override
                        public void onTransitionPause(@NonNull Transition transition) {

                        }


                        @Override
                        public void onTransitionResume(@NonNull Transition transition) {

                        }
                    });
                    TransitionManager.beginDelayedTransition((ViewGroup)view,t);
                    tv.setVisibility(View.INVISIBLE);

                }
            }
        });
        return view;
    }
    public void disappear()
    {
        for(int pos:swipedInt)
        {
            ConstraintLayout view=(ConstraintLayout)lv.getChildAt(pos);
            TransitionManager.beginDelayedTransition((ViewGroup)view);
            (view.findViewById(R.id.cancel_button)).setVisibility(View.GONE);
            ConstraintSet set=new ConstraintSet();
            set.clone((ConstraintLayout)view);
            set.setHorizontalBias(R.id.text1,.5f);
            set.applyTo((ConstraintLayout)view);
            TextView tv=((TextView)view.findViewById(R.id.text1));

            tv.setText(listItems.get(pos));
            TransitionManager.beginDelayedTransition((ViewGroup)view,new Slide(Gravity.RIGHT));
            tv.setVisibility(View.VISIBLE);
            //view.setBackgroundColor(0x000000);
        }
    }
    public void setText(String s){
        for(int pos:swipedInt)
        {
            ConstraintLayout v=(ConstraintLayout)lv.getChildAt(pos);
            TextView tv=((TextView)v.findViewById(R.id.text1));
            tv.setText(s);
        }
    }
}
