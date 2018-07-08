package com.alcatraz.admin.project_alcatraz.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alcatraz.admin.project_alcatraz.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by TAIYAB on 02-07-2018.
 */

public class TransactionActivity extends AppCompatActivity {
    ArrayList<Transaction> ar;
    RecyclerView recyclerView;
    String mindate="",maxdate="";
    static ClickableSpan clickableSpan;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wallet_transaction);
        recyclerView = findViewById(R.id.Wallet_Transactions);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        ar = new ArrayList<>();
        ar.add(new Transaction());
        ar.add(new Transaction(false, "20 Jun 2018", "500", true));
        for (int i = 600; i < 1500; i += 100) {
            int cost=(int)(Math.random()*9+1)*100;
            ar.add(new Transaction(Math.random()<0.5,"20 Jun 2018", "" + cost,false));
        }
        ar.add(new Transaction(true, "19 Jun 2018", "800", true));
        ar.add(new Transaction(false, "03 Jun 2018", "5000", true));
        for (int i = 900; i < 1900; i += 100)
            ar.add(new Transaction(Math.random()<0.5,"03 Jun 2018", "" + i,false));
        TransactionAdapter t = new TransactionAdapter(ar);
        recyclerView.setAdapter(t);
         clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(View textView) {
                Toast.makeText(TransactionActivity.this,"We can show info about the particaular restaurant",Toast.LENGTH_LONG).show();
                //startActivity(new Intent(MyActivity.this, NextActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
    }
    public void filter(View view)
    {
        Intent intent=new Intent(this,FilterTransactions.class);
        ArrayList<String> allRestaurants=new ArrayList<>();
        for(Transaction tt:ar)
            if(tt.hotel.length()!=0)
                allRestaurants.add(tt.hotel);
        ArrayList<String> visibleRestaurants=new ArrayList<>();
        List<Transaction> lt=((TransactionAdapter)recyclerView.getAdapter()).getList();
        for(Transaction tt:lt)
            visibleRestaurants.add(tt.hotel);
        intent.putExtra("min",mindate);
        intent.putExtra("max",maxdate);
        intent.putExtra("all",allRestaurants);
        intent.putExtra("vis",visibleRestaurants);
        startActivityForResult(intent,1);

    }

    private static final String TAG = "TransactionActivity";
    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode!=1 ||resultCode!=1)
            return;
        ArrayList<String> out=data.getStringArrayListExtra("name");
        ArrayList<Transaction> res=new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        Calendar min=Calendar.getInstance();
        Log.e(TAG, "onActivityResult: "+data.getIntExtra("yf",199));
        min.set(Calendar.YEAR,data.getIntExtra("yf", 1990));
        min.set(Calendar.MONTH,data.getIntExtra("mf", 1));
        min.set(Calendar.DAY_OF_MONTH,data.getIntExtra("df",1));
        if(min.get(Calendar.YEAR)!=1990)
            mindate=min.get(Calendar.DAY_OF_MONTH)+"/"+min.get(Calendar.MONTH)+"/"+min.get(Calendar.YEAR);
        else
            mindate="";

        Calendar max=Calendar.getInstance();
        max.set(Calendar.YEAR,data.getIntExtra("yt",2099));
        max.set(Calendar.MONTH,data.getIntExtra("mt",12));
        max.set(Calendar.DAY_OF_MONTH,data.getIntExtra("dt",31));
        if(max.get(Calendar.YEAR)!=2099 &&max.get(Calendar.YEAR)!=2100)
            maxdate=max.get(Calendar.DAY_OF_MONTH)+"/"+max.get(Calendar.MONTH)+"/"+max.get(Calendar.YEAR);
        else
            maxdate="";

        Log.e(TAG, "onActivityResult: "+min.get(Calendar.YEAR) +"   "+max.get(Calendar.YEAR));
        for(Transaction tt:ar) {
            if(tt.date.length()==0){
                res.add(tt);
                continue;
            }
            Calendar d=Calendar.getInstance();
            try {
                d.setTime(sdf.parse(tt.date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(d.after(max) || d.before(min))
                continue;
            if (out.contains(tt.hotel) || tt.hotel.length() == 0)
                res.add(tt);
        }

        if(res.size()!=0) {
            Transaction element = res.get(0);
            element.showdate = true;
            res.set(0, element);
        }
        for(int i=1;i<res.size();i++){
            if(!(res.get(i).date.equals(res.get(i-1).date))) {
                Transaction element = res.get(i);
                element.showdate = true;
                res.set(i, element);
            }
            else {
                Transaction element = res.get(i);
                element.showdate = false;
                res.set(i, element);
            }
        }
            recyclerView.setAdapter(new TransactionAdapter(res));

        }
    }

