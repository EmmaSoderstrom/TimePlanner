package se.sockertoppar.timeplanner;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by User on 2017-08-28.
 */

public class MillisekFormatChanger {

    Calendar cal;

    public MillisekFormatChanger(String millisek){
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

    public String getDateString(){
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String dateStr = day + "/" + month + "/" + year;
        return dateStr;
    }

    public String getTimeString(){
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
}