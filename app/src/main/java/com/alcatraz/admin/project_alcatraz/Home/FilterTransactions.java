package com.alcatraz.admin.project_alcatraz.Home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Social.ChatActivity;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;
import com.alcatraz.admin.project_alcatraz.Utility.ItemClickSupport;
import com.alcatraz.admin.project_alcatraz.Utility.WalletFilterAdapter;

import java.util.ArrayList;
import java.util.Scanner;

public class FilterTransactions extends AppCompatActivity {
int yf,mf,df,yt,mt,dt;
    ArrayList<Boolean> bool;
    ArrayList<String> arr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_transactions);
        RecyclerView recyclerView=findViewById(R.id.transaction_filter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        arr=new ArrayList<>() ;
        bool=new ArrayList<>();
        Scanner s=new Scanner("Brunners`fdgfdhm` jhhijhhbj`hjhvhvh`hjhjhhj`vhjhjjh`jhgvvvhh`hvhvjhh`utykfgtug");
        s.useDelimiter("`");
        while(s.hasNext()){
            arr.add(s.next());
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
