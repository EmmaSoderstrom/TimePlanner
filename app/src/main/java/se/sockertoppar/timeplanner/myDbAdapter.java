package se.sockertoppar.timeplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

    public void insertData(MainActivity mainActivity, String name, String date, String time) {
        Log.d(TAG, "insertData: " + name + ", " + date + ", " + time);
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(myDbHelper.NAME, name);
        contentValues.put(myDbHelper.DATE, date);
        contentValues.put(myDbHelper.TIME, time);

        dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        //mainActivity.addButton(name + ", " + date + ", " + time);
    }

    public String getData(MainActivity mainActivity) {
        SQLiteDatabase db = myhelper.getWritableDatabase();

        String[] columns = {myDbHelper.PLANNERID, myDbHelper.NAME, myDbHelper.DATE, myDbHelper.TIME};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer = new StringBuffer();

        while (cursor.moveToNext()) {
            int cid = cursor.getInt(cursor.getColumnIndex(myDbHelper.PLANNERID));
            String name = cursor.getString(cursor.getColumnIndex(myDbHelper.NAME));
            String date = cursor.getString(cursor.getColumnIndex(myDbHelper.DATE));
            String time = cursor.getString(cursor.getColumnIndex(myDbHelper.TIME));
            buffer.append(cid + ", " + name + ", " + date + ", " + time + " \n");
            Log.d(TAG, "getData: " + cid + ", " + name + ", " + date + ", " + time);
        }
        return buffer.toString();
    }

//    public String getDataToButtons2(MainActivity mainActivity) {
//        SQLiteDatabase db = myhelper.getWritableDatabase();
//
//        String[] columns = {myDbHelper.PLANNERID, myDbHelper.NAME, myDbHelper.DATE, myDbHelper.TIME};
//        Cursor cursor = db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
//        StringBuffer buffer = new StringBuffer();
//
//        mainActivity.clearButtonString();
//
//        while (cursor.moveToNext()) {
//            int cid = cursor.getInt(cursor.getColumnIndex(myDbHelper.PLANNERID));
//            String name = cursor.getString(cursor.getColumnIndex(myDbHelper.NAME));
//            String date = cursor.getString(cursor.getColumnIndex(myDbHelper.DATE));
//            String time = cursor.getString(cursor.getColumnIndex(myDbHelper.TIME));
//            buffer.append(cid + ", " + name + ", " + date + ", " + time + " \n");
//            Log.d(TAG, "getDataToButtons2: " + cid + ", " + name + ", " + date + ", " + time);
//            mainActivity.addButtonString(name + ", " + date + ", " + time);
//        }
//        return buffer.toString();
//    }



    public ArrayList<PlannerListObjekt> getDataToButton(MainActivity mainActivity) {
        SQLiteDatabase db = myhelper.getWritableDatabase();

        String[] columns = {myDbHelper.PLANNERID, myDbHelper.NAME, myDbHelper.DATE, myDbHelper.TIME};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer = new StringBuffer();

        mainActivity.clearButtonArrayList();

        while (cursor.moveToNext()) {
            int cid = cursor.getInt(cursor.getColumnIndex(myDbHelper.PLANNERID));
            String name = cursor.getString(cursor.getColumnIndex(myDbHelper.NAME));
            String date = cursor.getString(cursor.getColumnIndex(myDbHelper.DATE));
            String time = cursor.getString(cursor.getColumnIndex(myDbHelper.TIME));
            //buffer.append(cid + ", " + name + ", " + date + ", " + time + " \n");
            //Log.d(TAG, "getDataToButton: " + cid + ", " + name + ", " + date + ", " + time);
            plannerListObjekt = new PlannerListObjekt(cid, name, date, time);

            // TODO: 2017-08-22
            //sortera i kronologisk ordning. gemföra tid.



            plannerListObjektArrayList.add(plannerListObjekt);
        }
        return plannerListObjektArrayList;
    }

    public void delete(int id) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={String.valueOf(id)};

        db.delete(myDbHelper.TABLE_NAME ,myDbHelper.PLANNERID +" = ?",whereArgs);
    }

    public int updateName(String oldName , String newName) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.NAME,newName);
        String[] whereArgs= {oldName};
        int count = db.update(myDbHelper.TABLE_NAME, contentValues, myDbHelper.NAME + " = ?", whereArgs);
        return count;
    }

    static class myDbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "myDatabase";    // Database Name
        private static final String TABLE_NAME = "myTable";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version
        private static final String PLANNERID ="_id";     // Column I (Primary Key)
        private static final String NAME = "Name";    //Column II
        private static final String DATE = "Date";    // Column III
        private static final String TIME = "Time";    // Column IIII
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " (" + PLANNERID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " VARCHAR(255), " + DATE + " VARCHAR(255), " + TIME + " VARCHAR(255));";
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

