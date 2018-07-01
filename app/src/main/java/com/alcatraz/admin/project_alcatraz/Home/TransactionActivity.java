package com.alcatraz.admin.project_alcatraz.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alcatraz.admin.project_alcatraz.R;

import java.util.ArrayList;

/**
 * Created by TAIYAB on 02-07-2018.
 */

public class TransactionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wallet_transaction);
        RecyclerView recyclerView=findViewById(R.id.Wallet_Transactions);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<Transaction> ar=new ArrayList<>();
        ar.add(new Transaction(false,"20 Jun 2018","500",true));
        for(int i=600;i<1500;i+=100)
            ar.add(new Transaction("20 Jun 2018",""+i));
        ar.add(new Transaction(true,"20 Jun 2018","800",true));
        ar.add(new Transaction(false,"20 Jun 2018","5000",true));
        for(int i=900;i<1900;i+=100)
            ar.add(new Transaction("20 Jun 2018",""+i));
        TransactionAdapter t=new TransactionAdapter(ar);
        recyclerView.setAdapter(t);
    }
}
