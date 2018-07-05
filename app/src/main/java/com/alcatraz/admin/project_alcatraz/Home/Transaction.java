package com.alcatraz.admin.project_alcatraz.Home;

/**
 * Created by TAIYAB on 02-07-2018.
 */

public class Transaction {
    boolean cashback = false;
    boolean showdate = false;
    String date = "15 Dec 1998", Order = "#XXXXXXXXXX", hotel = "XXX", amount = "1000";
    private String[] arr={"Aththi Palace","Brunners","Galactica","Villa 21","Terracot Palace"};

    Transaction(boolean cashback, String date, String amount, boolean showdate) {
        this.cashback = cashback;
        this.date = date;
        this.amount = amount;
        this.showdate = showdate;
        //Random Stuff
        this.hotel=arr[(int)(Math.random()*5)];
        Order="";
        for(int i=1;i<=10;i++)
            Order+=(int)(Math.random()*10);

    }
    Transaction(){
        date=Order=hotel=amount="";
    }

    Transaction(String date, String amount) {
        this(false, date, amount, false);
    }
}
