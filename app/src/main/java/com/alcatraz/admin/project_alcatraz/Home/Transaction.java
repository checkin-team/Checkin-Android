package com.alcatraz.admin.project_alcatraz.Home;

/**
 * Created by TAIYAB on 02-07-2018.
 */

public class Transaction {
    boolean cashback=false;
    boolean showdate=false;
    String date="15 Dec 1998",Order="#XXXXXXXXXX",hotel="XXX",amount="1000";
    Transaction(boolean cashback,String date,String amount,boolean showdate)
    {
        this.cashback=cashback;
        this.date=date;
        this.amount=amount;
        this.showdate=showdate;

    }
    Transaction(String date,String amount)
    {
        this(false,date,amount,false);
    }
}
