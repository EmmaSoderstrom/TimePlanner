package se.sockertoppar.timeplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.R.id.list;

/**
 * Created by User on 2017-08-15.
 */

public class myDbAdapter {

    String TAG = "tag";
    myDbHelper myhelper;

    PlannerListObjekt plannerListObjekt;
    ArrayList<PlannerListObjekt> plannerListObjektArrayList = new ArrayList<PlannerListObjekt>();

    public myDbAdapter(Context context) {
        myhelper = new myDbHelper(context);
    }

    /**
     * Lägger till ett nytt objekt i databasen
     * @param mainActivity
     * @param name
     * @param date
     * @param time
     * @param dateTimeMillisek
     */
    public void insertData(MainActivity mainActivity, String name, String date, String time, String dateTimeMillisek) {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(myDbHelper.NAME, name);
        contentValues.put(myDbHelper.DATE, date);
        contentValues.put(myDbHelper.TIME, time);
        contentValues.put(myDbHelper.DATETIMEMILLISEK, dateTimeMillisek);

        dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
    }

    public int insertDataInt(MainActivity mainActivity, String name, String date, String time,
                             String dateTimeMillisek, String alarmTime) {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put(myDbHelper.NAME, name);
        contentValues.put(myDbHelper.DATE, date);
        contentValues.put(myDbHelper.TIME, time);
        contentValues.put(myDbHelper.DATETIMEMILLISEK, dateTimeMillisek);
        contentValues.put(myDbHelper.ALARMTIME, alarmTime);

        long id = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);

        return (int)id;
    }

    /**
     * hämtar all data från databasen
     * @return
     */
    public String getData() {
        SQLiteDatabase db = myhelper.getWritableDatabase();

        String[] columns = {myDbHelper.PLANNERID, myDbHelper.NAME, myDbHelper.DATE, myDbHelper.TIME,
                myDbHelper.DATETIMEMILLISEK, myDbHelper.ALARMTIME};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer = new StringBuffer();

        while (cursor.moveToNext()) {
            int cid = cursor.getInt(cursor.getColumnIndex(myDbHelper.PLANNERID));
            String name = cursor.getString(cursor.getColumnIndex(myDbHelper.NAME));
            String date = cursor.getString(cursor.getColumnIndex(myDbHelper.DATE));
            String time = cursor.getString(cursor.getColumnIndex(myDbHelper.TIME));
            String dateTimeMillisek = cursor.getString(cursor.getColumnIndex(myDbHelper.DATETIMEMILLISEK));
            String alarmTime = cursor.getString(cursor.getColumnIndex(myDbHelper.ALARMTIME));
            buffer.append(cid + ", " + name + ", " + date + ", " + time + ", "  + dateTimeMillisek + ", " + alarmTime + " \n");
        }
        return buffer.toString();
    }


    /**
     * skapar en ny arraylist med den information som behövs till listan på startsidan
     * @param mainActivity
     * @return
     */
    public ArrayList<PlannerListObjekt> getDataToButton(MainActivity mainActivity) {
        SQLiteDatabase db = myhelper.getWritableDatabase();

        String[] columns = {myDbHelper.PLANNERID, myDbHelper.NAME, myDbHelper.DATE, myDbHelper.TIME,
                myDbHelper.DATETIMEMILLISEK, myDbHelper.ALARMTIME};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);

        mainActivity.clearButtonArrayList();

        while (cursor.moveToNext()) {
            int cid = cursor.getInt(cursor.getColumnIndex(myDbHelper.PLANNERID));
            String name = cursor.getString(cursor.getColumnIndex(myDbHelper.NAME));
            String date = cursor.getString(cursor.getColumnIndex(myDbHelper.DATE));
            String time = cursor.getString(cursor.getColumnIndex(myDbHelper.TIME));
            String dateTimeMillisek = cursor.getString(cursor.getColumnIndex(myDbHelper.DATETIMEMILLISEK));
            String alarmTime = cursor.getString(cursor.getColumnIndex(myDbHelper.ALARMTIME));

            plannerListObjekt = new PlannerListObjekt(cid, name, date, time, dateTimeMillisek, alarmTime);
            plannerListObjektArrayList.add(plannerListObjekt);

            Collections.sort(plannerListObjektArrayList, new Comparator<PlannerListObjekt>() {
                public int compare(PlannerListObjekt o1, PlannerListObjekt o2) {
                    Double a = Double.valueOf(o1.getDateTimeMillisek());
                    Double b = Double.valueOf(o2.getDateTimeMillisek());

                    return Double.compare(a,b) ;
                }
            });
        }
        return plannerListObjektArrayList;
    }

    public void delete(int id) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={String.valueOf(id)};

        db.delete(myDbHelper.TABLE_NAME ,myDbHelper.PLANNERID +" = ?",whereArgs);
    }

    public PlannerListObjekt getObjektById(String id) {
        Log.d(TAG, "id: " + id);
        SQLiteDatabase db = myhelper.getWritableDatabase();

        String[] columns = {myDbHelper.PLANNERID, myDbHelper.NAME, myDbHelper.DATE, myDbHelper.TIME,
                myDbHelper.DATETIMEMILLISEK, myDbHelper.ALARMTIME};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME,columns,myDbHelper.PLANNERID + "=?",new String[]{id},null,null,null);

        if (cursor != null)
            cursor.moveToFirst();

        PlannerListObjekt plannerListObjekt = new PlannerListObjekt(Integer.valueOf(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        return plannerListObjekt;
    }

    public int updateName(String oldName , String newName) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.NAME,newName);
        String[] whereArgs= {oldName};
        int count = db.update(myDbHelper.TABLE_NAME, contentValues, myDbHelper.NAME + " = ?", whereArgs);
        return count;
    }

    public void updateAlarmTime(String plannerId , String newAlarmTime) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ALARMTIME,newAlarmTime);
        String[] whereArgs= {plannerId};
        db.update(myDbHelper.TABLE_NAME, contentValues, "_id=?", whereArgs);
    }

    static class myDbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "myDatabase";    // Database Name
        private static final String TABLE_NAME = "myTable";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version

        private static final String PLANNERID ="_id";     // Column 1 (Primary Key)
        private static final String NAME = "Name";    //Column 2
        private static final String DATE = "Date";    // Column 3
        private static final String TIME = "Time";    // Column 4
        private static final String DATETIMEMILLISEK = "DateTimeMillisek";    // Column 5
        private static final String ALARMTIME = "AlarmTime";    // Column 6

        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " (" + PLANNERID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " VARCHAR(255), " + DATE + " VARCHAR(255), " + TIME + " VARCHAR(255), "
                + DATETIMEMILLISEK + " VARCHAR(255), "
                + ALARMTIME + " VARCHAR(255));";
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

