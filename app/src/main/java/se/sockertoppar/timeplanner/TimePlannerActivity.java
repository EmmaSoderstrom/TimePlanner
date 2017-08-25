package se.sockertoppar.timeplanner;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;


import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_planner);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        thisObjektsId = message;
        Log.d(TAG, "onCreate: message " + message);

        myDatabasHelper = new myDbAdapter(this);
        myDatabasHelperSubjects = new myDbAdapterSubjects(this);
        //viewdata();
        plannerListObjekt = myDatabasHelper.getObjektById(message);

        setUpPage();

        /**
         * Gör arrayList
         */

        ArrayList<Subjects> subjectsArrayList = myDatabasHelperSubjects.getDataToSubjectsList(this, thisObjektsId);

        viewdataSubjects();


//        subjects.add("Packa det sista");
//        subjects.add("Kolla passet");
//        subjects.add("Åka taxi");
//        subjects.add("Äta lunch");

        RecyclerListAdapter adapter = new RecyclerListAdapter(subjectsArrayList);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);



    }

    public void setUpPage(){
        setTitle(plannerListObjekt.getName());
        // TODO: 2017-08-24
        //subtitle med datum och ev. tid
        //samt sista del i list med slut tid
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
//        myDatabasHelperSubjects.insertData(thisObjektsId, "Rulla tummarna", "10min", "7");
//        myDatabasHelperSubjects.insertData(thisObjektsId, "Titta på fåglarna", "20min", "6");
//        myDatabasHelperSubjects.insertData(thisObjektsId, "Flyga lite", "1h 45min", "5");
        viewdataSubjects();
    }
}
