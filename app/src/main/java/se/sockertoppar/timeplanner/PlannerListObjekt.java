package se.sockertoppar.timeplanner;

/**
 * Created by User on 2017-08-18.
 */

public class PlannerListObjekt {

    int id;
    String name;
    String date;
    String time;

    public PlannerListObjekt(int id, String name, String date, String time){

        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
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
}


