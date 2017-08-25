package se.sockertoppar.timeplanner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
        setContentView(R.layout.activity_time_planner);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Log.d(TAG, "onCreate: message " + message);

        myDatabasHelper = new myDbAdapter(this);
        //viewdata();
        plannerListObjekt = myDatabasHelper.getObjektById(message);

        setUpPage();



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
