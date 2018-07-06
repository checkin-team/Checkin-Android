package com.alcatraz.admin.project_alcatraz.Home;

/**
 * Created by TAIYAB on 02-07-2018.
 */

public class Transaction {
    boolean cashback = false;
    boolean showdate = false;
    String date = "15 Dec 1998", Order = "#XXXXXXXXXX", hotel = "XXX", amount = "1000";
    private String[] arr={"Acquerello","Vetrivos","Ambience","Brunners","Galactica","Villa 21","Terracot Palace","Sushi Nakazawa","Blue Hill",};

    Transaction(boolean cashback, String date, String amount, boolean showdate) {
        this.cashback = cashback;
        this.date = date;
        this.amount = amount;
        this.showdate = showdate;
        //Random Stuff
        this.hotel=arr[(int)(Math.random()*arr.length)];
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
