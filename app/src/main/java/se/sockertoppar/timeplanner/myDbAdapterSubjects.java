package se.sockertoppar.timeplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by User on 2017-08-15.
 */

public class myDbAdapterSubjects {

    String TAG = "tag";
    myDbHelper myhelper;

    Subjects SubjectsListObjekt;
    ArrayList<Subjects> subjectsArrayList = new ArrayList<Subjects>();

    public myDbAdapterSubjects(Context context) {
        myhelper = new myDbHelper(context);
    }

    /**
     * Lägger till ett nytt objekt i databasen
     * @param pointingId
     * @param name
     * @param time
     * @param position
     */
    public void insertData(String pointingId, String name, String time, String position) {
        Log.d(TAG, "insertData: " + pointingId + ", " + name + ", " + time + ", " + position);
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(myDbHelper.POINTINGID, pointingId);
        contentValues.put(myDbHelper.NAME, name);
        contentValues.put(myDbHelper.TIME, time);
        contentValues.put(myDbHelper.POSITION, position);

        dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
    }


    /**
     * hämtar all data från databasen
     * @return
     */
    public String getData() {
        SQLiteDatabase db = myhelper.getWritableDatabase();

        String[] columns = {myDbHelper.SUBJECTSID, myDbHelper.POINTINGID, myDbHelper.NAME, myDbHelper.TIME, myDbHelper.POSITION};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer = new StringBuffer();

        while (cursor.moveToNext()) {
            int cid = cursor.getInt(cursor.getColumnIndex(myDbHelper.SUBJECTSID));
            String pointingId = cursor.getString(cursor.getColumnIndex(myDbHelper.POINTINGID));
            String name = cursor.getString(cursor.getColumnIndex(myDbHelper.NAME));
            String time = cursor.getString(cursor.getColumnIndex(myDbHelper.TIME));
            String position = cursor.getString(cursor.getColumnIndex(myDbHelper.POSITION));
            buffer.append(cid + ", " + pointingId + ", " + name + ", " + time + ", "  + position + " \n");
        }
        return buffer.toString();
    }

    /**
     * skapar en ny arraylist med den information som behövs till listan på startsidan
     * @param timePlannerActivity
     * @return
     */
    public ArrayList<Subjects> getDataToSubjectsList(TimePlannerActivity timePlannerActivity, String thisObjektId) {
        //Log.d(TAG, "getDataToSubjectsList: " + thisObjektId);
        SQLiteDatabase db = myhelper.getWritableDatabase();

        String[] columns = {myDbHelper.SUBJECTSID, myDbHelper.POINTINGID, myDbHelper.NAME, myDbHelper.TIME, myDbHelper.POSITION};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer = new StringBuffer();

        //tar bort sysslor i arraylist innan det läggs till nya ned rätt pointingID
        timePlannerActivity.clearSubjectArrayList();

        while (cursor.moveToNext()) {
            int cid = cursor.getInt(cursor.getColumnIndex(myDbHelper.SUBJECTSID));
            String pointingId = cursor.getString(cursor.getColumnIndex(myDbHelper.POINTINGID));
            String name = cursor.getString(cursor.getColumnIndex(myDbHelper.NAME));
            String time = cursor.getString(cursor.getColumnIndex(myDbHelper.TIME));
            String position = cursor.getString(cursor.getColumnIndex(myDbHelper.POSITION));
            //buffer.append(cid + ", " + name + ", " + date + ", " + time + " \n");

            if(thisObjektId.equals(pointingId)) {
                //Log.d(TAG, "getDataToSubjectsList: " + thisObjektId + " , " + pointingId);
                SubjectsListObjekt = new Subjects(cid, pointingId, name, time, position);
                subjectsArrayList.add(SubjectsListObjekt);
            }

            //sortera i ordning baserat på position.
            Collections.sort(subjectsArrayList, new Comparator<Subjects>() {
                public int compare(Subjects o1, Subjects o2) {
                    Double a = Double.valueOf(o1.getPosition());
                    Double b = Double.valueOf(o2.getPosition());

                    return Double.compare(a,b) ;
                }
            });
        }
        return subjectsArrayList;
    }

    public void deleteByPointingId(int id) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={String.valueOf(id)};

        db.delete(myDbHelper.TABLE_NAME , myDbHelper.POINTINGID +" = ?",whereArgs);
    }

    public void deleteById(int id) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={String.valueOf(id)};

        db.delete(myDbHelper.TABLE_NAME , myDbHelper.SUBJECTSID +" = ?",whereArgs);
    }

    public PlannerListObjekt getObjektById(String id) {
        SQLiteDatabase db = myhelper.getWritableDatabase();

        String[] columns = {myDbHelper.SUBJECTSID, myDbHelper.POINTINGID, myDbHelper.NAME, myDbHelper.TIME, myDbHelper.POSITION};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME,columns, myDbHelper.SUBJECTSID + "=?",new String[]{id},null,null,null);

        if (cursor != null)
            cursor.moveToFirst();

        //String test = cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2);
        PlannerListObjekt plannerListObjekt = new PlannerListObjekt(Integer.valueOf(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        return plannerListObjekt;

    }

    public int updateName(String oldName , String newName) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.SUBJECTSID,newName);
        String[] whereArgs= {oldName};
        int count = db.update(myDbHelper.TABLE_NAME, contentValues, myDbHelper.SUBJECTSID + " = ?", whereArgs);
        return count;
    }

    public int updatePos(String id , String newPos) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.POSITION,newPos);
        String[] whereArgs= {id};
        int count = db.update(myDbHelper.TABLE_NAME, contentValues, myDbHelper.SUBJECTSID + " = ?", whereArgs);
        return count;
    }

    static class myDbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "myDatabaseSubjects";    // Database Name
        private static final String TABLE_NAME = "myTableSubjects";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version

        private static final String SUBJECTSID ="_id";     // Column 1 (Primary Key)
        private static final String POINTINGID = "PointingId";    //Column 2
        private static final String NAME = "Name";    // Column 3
        private static final String TIME = "Time";    // Column 4
        private static final String POSITION = "Position";    // Column 5
        //private static final ArrayList<ArrayList<String>> SUBJECTSARRAYLIST = subjectsArrayList ;    // Column 6

        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " (" + SUBJECTSID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + POINTINGID + " VARCHAR(255), " + NAME + " VARCHAR(255), " + TIME + " VARCHAR(255), "
                + POSITION + " VARCHAR(255));";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Message.message(context, "" + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context,"OnUpgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {
                Message.message(context,""+e);
            }
        }
    }
}

