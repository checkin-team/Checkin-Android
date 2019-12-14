package com.checkin.app.checkin.Home;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GetDayDate {

    public static ArrayList<String>getFiveDays(){
        ArrayList<String>tosend=new ArrayList<>();

        for (int i = 0; i <5 ; i++) {
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.DATE,i);
            Date c = calendar.getTime();

            SimpleDateFormat df = new SimpleDateFormat("dd");
            String formattedDate = df.format(c);
            tosend.add(formattedDate);

        }

        return tosend;

        }
    public static ArrayList<String>getFiveDate(){
        int i=0;
        ArrayList<String>str=new ArrayList<>();
        str.add("Sun");
        str.add("Mon");
        str.add("Tue");
        str.add("Wed");
        str.add("Thu");
        str.add("Fri");
        str.add("Sat");
        ArrayList<String>toSend=new ArrayList<>();


        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                i=0;
                break;
            case Calendar.MONDAY:
                i=1;
                break;
            case Calendar.TUESDAY:
                i=2;
                break;
            case Calendar.WEDNESDAY:
                i=3;
                break;
            case Calendar.THURSDAY:
                i=4;
                break;

            case Calendar.FRIDAY:
                i=5;
                break;
            case Calendar.SATURDAY:
                i=6;
                break;

        }
        for (int j = i; j <i+5 ; j++) {
            if(j>=6)
            toSend.add(str.get(j%7));
            else{
                toSend.add(str.get(j));
            }
        }
        return toSend;
    }

}
