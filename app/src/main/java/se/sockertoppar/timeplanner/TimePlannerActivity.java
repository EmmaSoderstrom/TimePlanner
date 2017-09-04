package se.sockertoppar.timeplanner;


import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import se.sockertoppar.timeplanner.helper.SimpleItemTouchHelperCallback;

public class TimePlannerActivity extends AppCompatActivity {

    String TAG = "tag";
    String thisObjektsId;

    myDbAdapter myDatabasHelper;
    myDbAdapterSubjects myDatabasHelperSubjects;
    PlannerListObjekt plannerListObjekt;

    private ItemTouchHelper mItemTouchHelper;
    ArrayList<String> subjects = new ArrayList<String>();
    ArrayList<Subjects> subjectsArrayList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_planner);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        thisObjektsId = message;
        //Log.d(TAG, "onCreate: message " + message);

        myDatabasHelper = new myDbAdapter(this);
        myDatabasHelperSubjects = new myDbAdapterSubjects(this);
        plannerListObjekt = myDatabasHelper.getObjektById(message);

        setUpPage();
        updateRecycleview();


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

        MillisekFormatChanger millisekFormatChanger = new MillisekFormatChanger(plannerListObjekt.getDateTimeMillisek());
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
        Log.d(TAG, "addSubjektToDatabas: " + name + " , " + time + " , " + String.valueOf(subjectsArrayList.size() + 1));
        // TODO: 2017-09-04
        //position till en mer än befintliga sysslor
        myDatabasHelperSubjects.insertData(thisObjektsId, name, time, String.valueOf(subjectsArrayList.size() + 1));
        //viewdata();
    }

    public void addSubjektToDatabas(String name, String time, int pos){
        Log.d(TAG, "addSubjektToDatabas: " + name + " , " + time + " , " + String.valueOf(pos));
        // TODO: 2017-09-04
        //position till en mer än befintliga sysslor
        myDatabasHelperSubjects.insertData(thisObjektsId, name, time, String.valueOf(pos));
        //viewdata();
    }

    public void seUpRecycleview(){
        Log.d(TAG, "seUpRecycleview: " + subjectsArrayList.size());
        RecyclerListAdapter adapter = new RecyclerListAdapter(subjectsArrayList, myDatabasHelperSubjects, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void updateRecycleview(){
        //skapar arrayList
        subjectsArrayList = myDatabasHelperSubjects.getDataToSubjectsList(this, thisObjektsId);
        seUpRecycleview();
    }


//    public void deleteObjektInDatabas(int id) {
//        Log.d(TAG, "delete: " + id);
//        //myDatabasHelper.delete(id);
//        myDatabasHelperSubjects.deleteById(id);
//        //setUpButtonList();
//    }

}
