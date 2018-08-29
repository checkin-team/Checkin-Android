package com.checkin.app.checkin.Misc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Social.ChatActivity;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.ItemClickSupport;
import com.checkin.app.checkin.Utility.WalletFilterAdapter;

import java.util.ArrayList;
import java.util.Scanner;

public class FilterTransactions extends AppCompatActivity {
int yf=1990,mf=1,df=1,yt=2099,mt=12,dt=31;
boolean from=false,to=false;
    ArrayList<Boolean> bool;
    ArrayList<String> arr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_transactions);
        RecyclerView recyclerView=findViewById(R.id.transaction_filter);


        arr=new ArrayList<>() ;
        bool=new ArrayList<>();
        Scanner s;
        String r=getIntent().getStringExtra("min");
        if(r.length()!=0) {
            from=true;
            s = new Scanner(r);
            s.useDelimiter("/");
            df = Integer.valueOf(s.next());
            mf = Integer.valueOf(s.next());
            yf = Integer.valueOf(s.next());
            ((TextView)findViewById(R.id.textView10)).setText(r);
            ((TextView) findViewById(R.id.textView10)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        }
        r=getIntent().getStringExtra("max");
        if(r.length()!=0) {
            to=true;
            s = new Scanner(r);
            s.useDelimiter("/");
            dt = Integer.valueOf(s.next());
            mt = Integer.valueOf(s.next());
            yt = Integer.valueOf(s.next());
            ((TextView)findViewById(R.id.textView11)).setText(r);
            ((TextView) findViewById(R.id.textView11)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        }
        ArrayList<String> allRestaurants=getIntent().getStringArrayListExtra("all");
        ArrayList<String> visibleRestaurants=getIntent().getStringArrayListExtra("vis");
        for(String i:allRestaurants) {
            if(arr.contains(i))
                continue;
            arr.add(i);
            if (visibleRestaurants.contains(i))
                bool.add(true);
            else
                bool.add(false);
        }
        findViewById(R.id.calendarView).setVisibility(View.GONE);
        WalletFilterAdapter adapter=new WalletFilterAdapter(bool,arr);
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                bool.set(position,!bool.get(position));
                recyclerView.setAdapter(new WalletFilterAdapter(bool,arr));
            }
        });

        GridLayoutManager layoutManager=new GridLayoutManager(this,3);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
           /* *
             * Returns the number of span occupied by the item at <code>position</code>.
             *
             * @param position The adapter position of the item
             * @return The number of spans occupied by the item at the provided position
             */
            @Override
            public int getSpanSize(int position) {

                if(arr.get(position).length()>10)
                    return 2;
                return 1;
            }


        });
        recyclerView.setLayoutManager(layoutManager);



    }
    public void from(View v){

        CalendarView calendarView=findViewById(R.id.calendarView);
        calendarView.setVisibility(View.VISIBLE);
        findViewById(R.id.button).setVisibility(View.GONE);
        findViewById(R.id.calendarView).setVisibility(View.VISIBLE);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Log.e("", "onSelectedDayChange: notnow" );
                if(v.getId() ==R.id.textView10)
                {yf=i;mf=i1;df=i2;}
                else {yt=i;mt=i1;dt=i2;}
                findViewById(R.id.button).setVisibility(View.VISIBLE);
                findViewById(R.id.calendarView).setVisibility(View.GONE);
                ((TextView)v).setText(i2+"/"+i1+"/"+i);
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            }
        });
    }


    public void submit(View v)
    {
        Intent intent=new Intent();
        intent.putExtra("yf",yf);
        intent.putExtra("yt",yt);
        intent.putExtra("mf",mf);
        intent.putExtra("mt",mt);
        intent.putExtra("df",df);
        intent.putExtra("dt",dt);
        ArrayList<String> selected=new ArrayList<>();
        for(int i=0;i<bool.size();i++)
            if(bool.get(i))
                selected.add(arr.get(i));
        intent.putExtra("name",selected);
        setResult(1,intent);
        finish();
    }
}
