package se.sockertoppar.timeplanner;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import se.sockertoppar.timeplanner.helper.SimpleItemTouchHelperCallback;

public class TimePlannerActivity extends AppCompatActivity {

    String TAG = "tag";
    String thisObjektsId;

    myDbAdapter myDatabasHelper;
    myDbAdapterSubjects myDatabasHelperSubjects;
    PlannerObjekt plannerListObjekt;
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

    RecyclerListAdapter adapter;
    RecyclerView recycleView;

    Timer timer;
    Handler dalaySynkMminuts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_planner);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        thisObjektsId = message;

        timplannerActivity = this;
        myDatabasHelper = new myDbAdapter(this);
        myDatabasHelperSubjects = new myDbAdapterSubjects(this);
        millisekFormatChanger = new MillisekFormatChanger();

        plannerListObjekt = myDatabasHelper.getObjektById(message);

        recycleView = (RecyclerView) findViewById(R.id.recycler_view);

        setUpPage();
        updateArrayListToRecycleview();
        seUpRecycleview();

        myIntent = new Intent(getBaseContext(), AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        setMinutsToDelayTimerCheckIfSubjectActiv();
        setMinutsToDelayTimer();

        stopAlarm();

        setTimerToRecycleviewOk();
    }

    public void setUpPage(){
        setTitle(plannerListObjekt.getName());
        // TODO: 2017-08-24
        //subtitle med datum och ev. tid
        TextView objectName = (TextView)findViewById(R.id.object_name);
        TextView objectEndtime = (TextView)findViewById(R.id.object_endtime);
        TextView objectEnddate = (TextView)findViewById(R.id.object_enddate);

        objectName.setText(plannerListObjekt.getName());
        objectEndtime.setText(millisekFormatChanger.getTimeString(plannerListObjekt.getDateTimeMillisek()));
        objectEnddate.setText(millisekFormatChanger.getDateString(plannerListObjekt.getDateTimeMillisek()));
    }

    public void updatePage(String id){
        plannerListObjekt = myDatabasHelper.getObjektById(id);

        setUpPage();
        updateArrayListToRecycleview();
        setMinutsToDelayTimerCheckIfSubjectActiv();
        changeAlarmTime();
        //setMinutsToDelayTimer();
    }


    /**
     * Recycleview
     *
     * @param removedSubject
     * @param position
     */

    public void undoRemoveSubject(Subjects removedSubject, int position){
        addSubjektToDatabas(removedSubject.getName(), removedSubject.getTime(), position + 1);
        updateArrayListToRecycleview();
    }

    public void updateArrayListToRecycleview(){
        //skapar arrayList
        subjectsArrayList = myDatabasHelperSubjects.getDataToSubjectsList(this, thisObjektsId);
        seUpRecycleview();
    }

    public void seUpRecycleview(){
        Log.d(TAG, "seUpRecycleview: ");
        if(adapter == null) {
            adapter = new RecyclerListAdapter(subjectsArrayList, myDatabasHelperSubjects, this, plannerListObjekt, recycleView);
            //recyclerView.setHasFixedSize(true);
            recycleView.setAdapter(adapter);
            recycleView.setLayoutManager(new LinearLayoutManager(this));

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recycleView);
        }else{
            adapter.updateList(subjectsArrayList);
        }

        checkIfSubjectActiv();
    }


    //då recycleview i metoden checkIfSubjectActiv() är av någon anledning en kort när man start denna vy.
    //gör jag en fördröjning med 10 millisekunder och den då blir rätt längd på recycleview
    //typ get metoder tid att göras klart!?!
    public void setMinutsToDelayTimerCheckIfSubjectActiv(){
        Log.d(TAG, "setMinutsToDelayTimerCheckIfSubjectActiv: ");
        Calendar cal = Calendar.getInstance();
        int millisekToDelay =  20;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.d(TAG, "run: kör första check If aktiv");
                checkIfSubjectActiv();
            }
        }, millisekToDelay);
    }

    public void setMinutsToDelayTimer(){
        Calendar cal = Calendar.getInstance();
        int sekund = cal.getTime().getSeconds();
        int millisekToDelay = (60 - sekund) * 1000;

        dalaySynkMminuts = new Handler();
        dalaySynkMminuts.postDelayed(new Runnable() {
            public void run() {
                setMinutsTimer();
            }
        }, millisekToDelay);

    }
    
    public void setMinutsTimer(){
        timer = new Timer();
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                // When you need to modify a UI element, do so on the UI thread. 
                // 'getActivity()' is required as this is being ran from a Fragment.
                timplannerActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        checkIfSubjectActiv();
                    }
                });
            }
        }, 0, 1000 * 1 * 60); //1000 * 1 * 60 = 60sek
    }

    //körs varje 10 millisek till recyckeVieew ger rätt antal på getChildrenCount(); när det är rätt cancel.
    public void setTimerToRecycleviewOk(){

        if(!checkIfRecycleViewOk()) {
            final Timer timer2 = new Timer();

            timer2.schedule(new TimerTask() {
                @Override
                public void run() {

                    // When you need to modify a UI element, do so on the UI thread.
                    // 'getActivity()' is required as this is being ran from a Fragment.
                    timplannerActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.checkIfSubjectActiv();
                            if(checkIfRecycleViewOk()) {
                                timer2.cancel();
                            }
                        }
                    });
                }
            }, 0, 10);
        }
    }

    public void checkIfSubjectActiv(){

        Calendar cal = Calendar.getInstance();
        long toDayMillisek = cal.getTimeInMillis();

        for (int i = 0; i < subjectsArrayList.size(); i++) {

            Subjects subject = subjectsArrayList.get(i);

            if (subject.getStartTimeMillisek() != null) {

                //om någon syssla är aktiv
                if (toDayMillisek > Long.valueOf(subject.getStartTimeMillisek())
                        && toDayMillisek < (Long.valueOf(subject.getStartTimeMillisek()) + Long.valueOf(subject.getTime()))) {

                    //ändra bakgrund
                    changeActivBackgrund(i);

                    //kollar om larmet går igång och gör knapp synlig
                    if (millisekFormatChanger.getTimeString(toDayMillisek)
                            .equals(millisekFormatChanger.getTimeString(Long.valueOf(plannerListObjekt.getAlarmTime())))) {
                        Button turnOfAlarmButtom = (Button) findViewById(R.id.turn_of_alarm);
                        turnOfAlarmButtom.setVisibility(View.VISIBLE);
                    }

                    //break då endast ett objekt kan vara aktivt
                    break;

                } else {
                    changeActivBackgrund(-1);
                }
            }
        }

    }

    public boolean checkIfRecycleViewOk(){
        boolean ifRecycleviewIsOk = true;
        if(recycleView.getChildCount() == 0 && subjectsArrayList.size() > 0){
            ifRecycleviewIsOk = false;
        }else{
            ifRecycleviewIsOk = true;
        }
        return ifRecycleviewIsOk;
    }

    public void changeActivBackgrund(int indexPosition){

        int childCount = recycleView.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = recycleView.getChildAt(i);
            TextView subjectName = (TextView) child.findViewById(R.id.subject_name);

            if (indexPosition == i) {
                child.setBackgroundResource(R.color.aktivSubject);
                subjectName.setTextColor(ContextCompat.getColor(this, R.color.aktivSubjectName));
            }else {
                child.setBackgroundColor(0);
                subjectName.setTextColor(ContextCompat.getColor(this, R.color.textListName));
            }
        }
    }

    public void clearSubjectArrayList(){
        subjectsArrayList.clear();
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

    public void onCklickChangPlannerObject(View view){
        DialogChangePlanner  dialogChangePlanner = new DialogChangePlanner();
        dialogChangePlanner.showDialogChangePlanner(this, this, plannerListObjekt);
    }

    /**
     * Lägga till syssla
     *
     * @param view
     */

    public void onClickAddSubject(View view){
        DialogAddSubject dialogAddSubject = new DialogAddSubject();
        dialogAddSubject.showDialogAddSubject(this, this);
    }

    public void addSubjektToDatabas(String name, String time){
        //position i listan ska vara en 1 mer än befintliga sysslor i listan
        long id = myDatabasHelperSubjects.insertData(thisObjektsId, name, time, String.valueOf(subjectsArrayList.size() + 1));
        //updateArrayListToRecycleview();
        adapter.updateItemList();
        setMinutsToDelayTimerCheckIfSubjectActiv();
        changeAlarmTime();

    }

    public void addSubjektToDatabas(String name, String time, int pos){
        myDatabasHelperSubjects.insertData(thisObjektsId, name, time, String.valueOf(pos));
    }

    public void addStartTimeToSubject(int indexInArraylist, long startTimeMillisek){
        subjectsArrayList.get(indexInArraylist).setStartTimeMillisek(String.valueOf(startTimeMillisek));
    }


    /**
     * Alarm
     */

    public void onClickOfAlarm(View view){
        Button turnOfAlarmButtom = (Button)findViewById(R.id.turn_of_alarm);
        turnOfAlarmButtom.setVisibility(View.INVISIBLE);
        stopAlarm();
    }

    public void changeAlarmTime(){
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

    public void setAlarm(){
        Log.d(TAG, "setAlarm: ");

        Calendar cal = Calendar.getInstance();
        long toDayMillisek = cal.getTimeInMillis();

        if(Long.parseLong(plannerListObjekt.getAlarmTime()) > toDayMillisek) {
            myIntent.putExtra("extra", "yes" + ":" +  thisObjektsId);
            pendingIntent = PendingIntent.getBroadcast(TimePlannerActivity.this, Integer.valueOf(thisObjektsId),
                    myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(plannerListObjekt.getAlarmTime()), pendingIntent);

        }else{
            stopAlarm();
        }
    }
    public void stopAlarm(){
        Log.d(TAG, "stopAlarm: ");

        myIntent.putExtra("extra", "no"+ ":" + "-");
        sendBroadcast(myIntent);
        pendingIntent = PendingIntent.getBroadcast(TimePlannerActivity.this, Integer.valueOf(thisObjektsId),
                myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.i(TAG, "On Resume .....");
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.i(TAG, "On Start .....");
//
//    }
    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: --------------zz-z-zzzz-z-z-z-z-");
        super.onStop();
        if(timer != null) {
            timer.cancel();

        }
    }

}
