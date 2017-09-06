package se.sockertoppar.timeplanner;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import se.sockertoppar.timeplanner.helper.SimpleItemTouchHelperCallback;

public class TimePlannerActivity extends AppCompatActivity {

    String TAG = "tag";
    String thisObjektsId;

    myDbAdapter myDatabasHelper;
    myDbAdapterSubjects myDatabasHelperSubjects;
    PlannerListObjekt plannerListObjekt;
    MillisekFormatChanger millisekFormatChanger;

    private ItemTouchHelper mItemTouchHelper;
    ArrayList<String> subjects = new ArrayList<String>();
    ArrayList<Subjects> subjectsArrayList = new ArrayList<>();

    public Intent myIntent;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private AlarmReceiver alarm;
    TimePlannerActivity timplannerActivity;
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_planner);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        thisObjektsId = message;

        myDatabasHelper = new myDbAdapter(this);
        myDatabasHelperSubjects = new myDbAdapterSubjects(this);
        plannerListObjekt = myDatabasHelper.getObjektById(message);

        millisekFormatChanger = new MillisekFormatChanger(plannerListObjekt.getDateTimeMillisek());
        setUpPage();
        updateRecycleview();

        timplannerActivity = this;
        myIntent = new Intent(getBaseContext(), AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    }

    public void clearSubjectArrayList(){
        Log.d(TAG, "clearSubjectArrayList: ");
        subjectsArrayList.clear();
    }

    public void setUpPage(){
        setTitle(plannerListObjekt.getName());
        // TODO: 2017-08-24
        //subtitle med datum och ev. tid
        TextView objectName = (TextView)findViewById(R.id.object_name);
        TextView objectEndtime = (TextView)findViewById(R.id.object_endtime);
        objectName.setText(plannerListObjekt.getName());


        objectEndtime.setText(millisekFormatChanger.getTimeString());
    }

    public void viewdata(){
        String data = myDatabasHelper.getData();
        Message.message(this,data);
    }

    public void viewdataSubjects(){
        String data = myDatabasHelperSubjects.getData();
        Message.message(this,data);
    }

    public ArrayList<String> getSubjectsString(){
        return subjects;
    }

    public void onClickAddSubject(View view){
        DialogAddSubject dialogAddSubject = new DialogAddSubject();
        dialogAddSubject.showDialogAddSubject(this, this);
    }

    public void addSubjektToDatabas(String name, String time){
        Log.d(TAG, "addSubjektToDatabas: ");
        //position till 1 mer än befintliga sysslor i listan
        myDatabasHelperSubjects.insertData(thisObjektsId, name, time, String.valueOf(subjectsArrayList.size() + 1));
        changeAlarmTime();
    }

    public void addSubjektToDatabas(String name, String time, int pos){
        Log.d(TAG, "addSubjektToDatabas: med pos");
        myDatabasHelperSubjects.insertData(thisObjektsId, name, time, String.valueOf(pos));
    }

    public void changeAlarmTime(){
        Log.d(TAG, "changeAlarmTime: ");
        //hämtar tid från alla sysslor
        ArrayList<Subjects> arrayLisSubjects = myDatabasHelperSubjects.getDataToSubjectsList(this, thisObjektsId);
        long totalTimeToGive = 0;
        for (Subjects subjectToGetTime : arrayLisSubjects) {
            totalTimeToGive = totalTimeToGive + Long.valueOf(subjectToGetTime.getTime());
        }
        long endTime = Long.valueOf(plannerListObjekt.getDateTimeMillisek());
        long newAlarmTime = endTime - totalTimeToGive;

        myDatabasHelper.updateAlarmTime(thisObjektsId, String.valueOf(newAlarmTime));
        plannerListObjekt = myDatabasHelper.getObjektById(thisObjektsId);
        setAlarm();
    }

    public void undoRemoveSubject(Subjects removedSubject, int position){
        Log.d(TAG, "undoRemoveSubject: confirmation + " );
        addSubjektToDatabas(removedSubject.getName(), removedSubject.getTime(), position + 1);
        updateRecycleview();
    }

    public void updateRecycleview(){
        //skapar arrayList
        subjectsArrayList = myDatabasHelperSubjects.getDataToSubjectsList(this, thisObjektsId);
        seUpRecycleview();
    }

    public void seUpRecycleview(){
        Log.d(TAG, "seUpRecycleview: ");
        RecyclerListAdapter adapter = new RecyclerListAdapter(subjectsArrayList, myDatabasHelperSubjects, this, plannerListObjekt);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    

    public void setAlarm(){

        Calendar cal = Calendar.getInstance();
        long toDayMillisek = cal.getTimeInMillis();

        if(Long.parseLong(plannerListObjekt.getAlarmTime()) > toDayMillisek) {
            myIntent.putExtra("extra", "yes");
            pendingIntent = PendingIntent.getBroadcast(TimePlannerActivity.this, Integer.valueOf(thisObjektsId),
                    myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //MillisekFormatChanger mfc = new MillisekFormatChanger(plannerListObjekt.getAlarmTime());
            alarmManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(plannerListObjekt.getAlarmTime()), pendingIntent);

            Log.d(TAG, "setAlarm: " + millisekFormatChanger.getTimeString(Long.valueOf(plannerListObjekt.getAlarmTime())));
        }
    }

    public void stopAlarm(View view){
        Log.d(TAG, "stopAlarm: ");

        myIntent.putExtra("extra", "no");
        sendBroadcast(myIntent);
        pendingIntent = PendingIntent.getBroadcast(TimePlannerActivity.this, Integer.valueOf(thisObjektsId), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

}
