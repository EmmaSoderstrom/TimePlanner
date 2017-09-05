package se.sockertoppar.timeplanner;



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;

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
        Log.d(TAG, "addSubjektToDatabas: ");
        //position till 1 mer än befintliga sysslor i listan
        myDatabasHelperSubjects.insertData(thisObjektsId, name, time, String.valueOf(subjectsArrayList.size() + 1));
    }

    public void addSubjektToDatabas(String name, String time, int pos){
        Log.d(TAG, "addSubjektToDatabas: med pos");
        myDatabasHelperSubjects.insertData(thisObjektsId, name, time, String.valueOf(pos));
    }

    public void undoRemoveSubject(Subjects removedSubject, int position){
        Log.d(TAG, "undoRemoveSubject: confirmation + " );
        addSubjektToDatabas(removedSubject.getName(), removedSubject.getTime(), position + 1);
        updateRecycleview();
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

}
