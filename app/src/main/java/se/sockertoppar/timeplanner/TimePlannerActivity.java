package se.sockertoppar.timeplanner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class TimePlannerActivity extends AppCompatActivity {

    String TAG = "tag";

    myDbAdapter myDatabasHelper;
    PlannerListObjekt plannerListObjekt;

    String[] subjects = {"Packa det sista", "Kolla passet", "Äta lunch"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_time_planner);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Log.d(TAG, "onCreate: message " + message);

        myDatabasHelper = new myDbAdapter(this);
        //viewdata();
        plannerListObjekt = myDatabasHelper.getObjektById(message);

        setUpPage();

        Context context = getApplicationContext();

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recylerViewLayoutManager);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(context, subjects);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    public void setUpPage(){
        setTitle(plannerListObjekt.getName());
        // TODO: 2017-08-24
        //subtitle med datum och ev. tid
    }

    public void viewdata(){
        String data = myDatabasHelper.getData();
        Message.message(this,data);
    }
}
