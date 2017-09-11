package se.sockertoppar.timeplanner;

/**
 * Created by User on 2017-08-18.
 */

public class PlannerObjekt {

    int id;
    String name;
    String date;
    String time;
    String dateTimeMillisek;
    String alarmTime;

    public PlannerObjekt(int id, String name, String date, String time, String dateTimeMillisek, String alarmTime){

        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.dateTimeMillisek = dateTimeMillisek;
        this.alarmTime = alarmTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDateTimeMillisek() {
        return dateTimeMillisek;
    }

    public String getAlarmTime(){
        return alarmTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDateTimeMillisek(String dateTimeMillisek) {
        this.dateTimeMillisek = dateTimeMillisek;
    }
}


