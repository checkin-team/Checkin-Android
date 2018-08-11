package com.alcatraz.admin.project_alcatraz.Home;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.transition.Explode;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Utility.ItemClickSupport;

import java.util.ArrayList;

import butterknife.OnClick;

public class ActiveSession extends AppCompatActivity {
    public  static boolean canCancel=true,fragmentOpen=false;
    private  final String TAG = "ActiveSession";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_active_session);
        //findViewById(R.id.textclean).bringToFront();
        RecyclerView recyclerView = findViewById(R.id.food_with);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        //recyclerView.setLayoutManager(new GridLayoutManager(ActiveSession.this, 2));
        ArrayList<Integer> intlist = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        intlist.add(0);
        names.add("");
        for (int i = 1; i < 20; i++) {
            intlist.add(R.drawable.flier);
            names.add("Laura");
        }
        TextView mTextField=findViewById(R.id.timer);
        new CountDownTimer(70*1000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {

                long min=millisUntilFinished/1000/60;
                if(min==0)
                mTextField.setText("Time Left: " + millisUntilFinished / 1000+" seconds");
                else
                    mTextField.setText("Time Left: " + min+" minute"+(min==1?"":"s"));

            }

            public void onFinish() {
                //mTextField.setTextColor(0x00cc00);
                mTextField.setText("Your food will be delivered shortly");
            }
        }.start();
        TextView tv=findViewById(R.id.text1);
        //Use the following code when user gives order
        new CountDownTimer(65*1000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                if(!fragmentOpen)
                    return;
                long min=millisUntilFinished/1000/60;
                long sec=millisUntilFinished/1000-min*60;
                ActiveSessionFragment asf=((ActiveSessionFragment)getSupportFragmentManager().findFragmentByTag("FragmentOrder"));
                if(asf==null)
                    return;
                if(min==0)
                    asf.setText(sec+" seconds");
                else
                    asf.setText(min+":"+sec+" minutes");

            }

            public void onFinish() {

                canCancel=false;
                ActiveSessionFragment asf=((ActiveSessionFragment)getSupportFragmentManager().findFragmentByTag("FragmentOrder"));
                if(asf==null)
                    return;
                asf.disappear();
                asf.setText("Non Cancellable");
            }
        }.start();
        recyclerView.setAdapter(new AdapterFoodWith(intlist, names));
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if(position!=0)
                    return;
                AlertDialog.Builder alert = new AlertDialog.Builder(ActiveSession.this);
                alert.setMessage(Html.fromHtml(getString(R.string.username)));

                // Set an EditText view to get user input
                final EditText input = new EditText(ActiveSession.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        Log.d("", "Pin Value : " + value);
                        return;
                    }
                });

                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                return;
                            }
                        });
                alert.show();

            }
        });
        findViewById(R.id.imageView13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rect r=new Rect();
                //recyclerView.findChildViewUnder(e.getX(),e.getY()).getGlobalVisibleRect(r);
                recyclerView.getGlobalVisibleRect(r);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ActiveSessionFragment asf = new ActiveSessionFragment();
                Explode explode=new Explode();
                explode.setEpicenterCallback(new Transition.EpicenterCallback() {
                    @Override
                    public Rect onGetEpicenter(@NonNull Transition transition) {
                        Log.e(TAG, "onGetEpicenter: "+r );
                        return r;
                    }
                });
                //TransitionManager.beginDelayedTransition(findViewById(R.id.fullactivesession), explode);
                //findViewById(R.id.fullactivesession).setBackgroundColor(0x80000000);
//                asf.setEnterTransition(new Fade());
//                asf.setExitTransition(new Fade());

                ft.addToBackStack(null);
                ft.replace(R.id.fullactivesession, asf,"FragmentOrder");
                ft.commit();
                fragmentOpen=true;
            }
        });
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {


            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                Rect r=new Rect();
                //recyclerView.findChildViewUnder(e.getX(),e.getY()).getGlobalVisibleRect(r);
                recyclerView.getGlobalVisibleRect(r);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ActiveSessionFragment asf = new ActiveSessionFragment();
                Explode explode=new Explode();
                explode.setEpicenterCallback(new Transition.EpicenterCallback() {
                    @Override
                    public Rect onGetEpicenter(@NonNull Transition transition) {
                        Log.e(TAG, "onGetEpicenter: "+r );
                        return r;
                    }
                });
                //TransitionManager.beginDelayedTransition(findViewById(R.id.fullactivesession), explode);
                //findViewById(R.id.fullactivesession).setBackgroundColor(0x80000000);
//                asf.setEnterTransition(new Fade());
//                asf.setExitTransition(new Fade());

                ft.addToBackStack(null);
                ft.replace(R.id.fullactivesession, asf,"FragmentOrder");
                ft.commit();
                fragmentOpen=true;
                return false;
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                //gestureDetector.onTouchEvent(e);

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    public void closefragment(View v)
    {
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("FragmentOrder")).commit();
        getSupportFragmentManager().popBackStack();
        fragmentOpen=false;
    }
}
