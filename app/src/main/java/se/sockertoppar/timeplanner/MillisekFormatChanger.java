package se.sockertoppar.timeplanner;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by User on 2017-08-28.
 */

public class MillisekFormatChanger {

    Calendar cal;

    public MillisekFormatChanger(){


//        int day = cal.get(Calendar.DAY_OF_MONTH);
//        int month = cal.get(Calendar.MONTH) + 1;
//        int year = cal.get(Calendar.YEAR);
//        // TODO: 2017-08-23
//        //om på engelska ändra timmarna till 12timmars klocka
//        int hour = cal.get(Calendar.HOUR_OF_DAY);
//        int minute = cal.get(Calendar.MINUTE);
//
//        objectEndtime.setText(hour + ":" + minute);
    }

    public String getDateString(String millisek){

        /**
         * Gör om strängen med fullt datum och tid till long och date
         */
        long dateLong = Long.parseLong(millisek);
        //Date date = new Date(dateLong);

        /**
         * calender, för att kunna hämta datum och tid var för sig
         */
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(dateLong);
//        this.cal = cal;

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String dateStr = day + "/" + month + "/" + year;
        return dateStr;
    }

    public String getTimeString(String millisek){

        /**
         * Gör om strängen med fullt datum och tid till long och date
         */
        long dateLong = Long.parseLong(millisek);
        //Date date = new Date(dateLong);

        /**
         * calender, för att kunna hämta datum och tid var för sig
         */
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateLong);
        this.cal = cal;

        String minuteStr;
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        if(minute <= 9){
            minuteStr = "0" + minute;
        }else{
            minuteStr = String.valueOf(minute);
        }
        String timeStr = hour + ":" + minuteStr;
        return timeStr;
    }

    public String getTimeString(long newMillisek){

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(newMillisek);

        String minuteStr;
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        if(minute <= 9){
            minuteStr = "0" + minute;
        }else{
            minuteStr = String.valueOf(minute);
        }
        String timeStr = hour + ":" + minuteStr;
        return timeStr;
    }

    public String getTimeStringMH(){
        String hourStr = "";
        String minuteStr = "";
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour > 0){
            hourStr = hour + "h";
        }
        int minute = cal.get(Calendar.MINUTE);
        if(minute > 0){
            minuteStr = minute + "min";
        }

        String timeStr = hourStr + " " + minuteStr;
        return timeStr;
    }

    public String getTimeStringMH(long newMillisek){

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(newMillisek);

        String hourStr = "";
        String minuteStr = "";

        //räknar manuelt pga cal.get(Calendar.HOUR) inte fungerar på samsung s4
        long allMinuts = newMillisek / 60000;
        long minute = allMinuts % 60;
        long hourInMinuts = allMinuts - minute;
        long hour = hourInMinuts / 60;

        //int hour = cal.get(Calendar.HOUR);
        //int minute = cal.get(Calendar.MINUTE);
        if(hour > 0){
            hourStr = hour + "h";
        }
        if(minute > 0){
            minuteStr = minute + "min";
        }

        String timeStr = hourStr + " " + minuteStr;
        return timeStr;
    }

    public int getTimeStringIntMin(long newMillisek){

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(newMillisek);

        int minute = cal.get(Calendar.MINUTE);

        //räknar manuelt pga cal.get(Calendar.HOUR) inte fungerar på samsung s4
//        long allMinuts = newMillisek / 60000;
//        long minute = allMinuts % 60;
//        long hourInMinuts = allMinuts - minute;
//        long hour = hourInMinuts / 60;


        return minute;
    }

    public int getTimeStringIntHour(long newMillisek){

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(newMillisek);

        int hour = cal.get(Calendar.HOUR_OF_DAY);

        //räknar manuelt pga cal.get(Calendar.HOUR) inte fungerar på samsung s4
//        long allMinuts = newMillisek / 60000;
//        long minute = allMinuts % 60;
//        long hourInMinuts = allMinuts - minute;
//        long hour = hourInMinuts / 60;
//
//        Log.d("tag", "getTimeStringIntHour: " + hour);

        return hour;
    }
}
