package se.sockertoppar.timeplanner;

/**
 * Created by User on 2017-08-18.
 */

public class PlannerListObjekt {

    int id;
    String name;
    String date;
    String time;
    String dateTimeMillisek;

    public PlannerListObjekt(int id, String name, String date, String time, String dateTimeMillisek){

        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.dateTimeMillisek = dateTimeMillisek;
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


