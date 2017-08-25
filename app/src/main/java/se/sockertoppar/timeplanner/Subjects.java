package se.sockertoppar.timeplanner;

/**
 * Created by User on 2017-08-25.
 */

public class Subjects {

    int id;
    String pointingId;
    String name;
    String time;
    String position;

    public Subjects (int id, String pointingId, String name, String time, String position){

        this.id = id;
        this.pointingId = pointingId;
        this.name = name;
        this.time = time;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public String getPointingId() {
        return pointingId;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getPosition() {
        return position;
    }
}
