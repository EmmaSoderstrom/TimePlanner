package se.sockertoppar.timeplanner;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {


    public static final String EXTRA_MESSAGE = "se.sockertoppar.timeplanner.MESSAGE";

    String TAG = "tag";
    myDbAdapter myDatabasHelper;
    myDbAdapterSubjects myDatabasHelperSubjects;

    DialogAddPlanner dialogAddPlanner;
    DialogConfirmDelete dialogConfirmDelete;
    MainActivity mainActivity;
    Context context;

    LayoutInflater inflater;
    LinearLayout linearLayoutListContiner;

    ButtonListContiner adapter;
    ListView listView = null;

    ArrayList<String> buttonStringArray = new ArrayList<String>();
    ArrayList<PlannerListObjekt> arrayListButtonObjekt = new ArrayList<PlannerListObjekt>();

    Intent myIntent;
    AlarmManager alarmManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDatabasHelper = new myDbAdapter(this);
        myDatabasHelperSubjects = new myDbAdapterSubjects(this);

//        // TODO: 2017-09-04
//        //ta bort
//        String data = myDatabasHelperSubjects.getData();
//        Message.message(this,data);
//        //

        dialogAddPlanner = new DialogAddPlanner();
        dialogConfirmDelete = new DialogConfirmDelete();
        mainActivity = this;
        context = this;

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setUpButtonList();

        myIntent = new Intent(getBaseContext(), AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    }

    public void onClickAddPlanner(View view){
        Log.d(TAG, "onClickAddPlanner: ");
        dialogAddPlanner.showDialogAddPlanner(this, this);
    }

    public void onClickTextDate(View view){
        Log.d(TAG, "onClickTextDate: ");
        dialogAddPlanner.showCalendar();
    }

//    public void saveNewTimePlanner(String plannerName, String plannerDate, int plannerTimeH, int plannerTimeM, String plannerDateTimeMillisek){
//        Log.d(TAG, "saveNewTimePlanner: " + plannerName + ", " + plannerDate + ", " + plannerTimeH + ":" + plannerTimeM);
//        myDatabasHelper.insertData(this, plannerName, plannerDate, (plannerTimeH + ":" + plannerTimeM), plannerDateTimeMillisek);
//        setUpButtonList();
//    }

    public int saveNewTimePlannerInt(String plannerName, String plannerDate, int plannerTimeH, int plannerTimeM,
                                     String plannerDateTimeMillisek){
        Log.d(TAG, "saveNewTimePlanner: " + plannerName + ", " + plannerDate + ", " + plannerTimeH + ":" + plannerTimeM);
        int objektId = myDatabasHelper.insertDataInt(this, plannerName, plannerDate, (plannerTimeH + ":" + plannerTimeM),
                plannerDateTimeMillisek, plannerDateTimeMillisek);

        PlannerListObjekt plannerListObjekt = myDatabasHelper.getObjektById(String.valueOf(objektId));
        setAlarm(plannerListObjekt);

        setUpButtonList();

        return objektId;
    }

    public void viewdata(){
        String data = myDatabasHelper.getData();
        Message.message(this,data);
    }

    public void deleteObjektInDatabas(int id) {
        Log.d(TAG, "delete: " + id);
        myDatabasHelper.delete(id);
        myDatabasHelperSubjects.deleteByPointingId(id);
        setUpButtonList();
    }

    public void clearButtonArrayList(){
        arrayListButtonObjekt.clear();
    }

    public void setUpButtonList(){

        arrayListButtonObjekt = myDatabasHelper.getDataToButton(this);
        Log.d(TAG, "setUpButtonList: " + arrayListButtonObjekt);

        if (adapter == null) {
            adapter = new ButtonListContiner(this, arrayListButtonObjekt);

            listView = (ListView) findViewById(R.id.list_view_button);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemClick: " + arrayListButtonObjekt.get(position).getName()
                            + ", id, " + arrayListButtonObjekt.get(position).getId());
                    goToTimePlanner(arrayListButtonObjekt.get(position).getId());
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemLongClick: " + arrayListButtonObjekt.get(position).getName()
                            + ", id, " + arrayListButtonObjekt.get(position).getId());
                    dialogConfirmDelete.showDialogConfirmDelete(context,
                            arrayListButtonObjekt.get(position).getName(),
                            arrayListButtonObjekt.get(position).getId());

                    return true;
                }
            });

        }else {
            Log.d("tag", "notifyDataSetChanged");
            adapter.notifyDataSetChanged();
        }
    }

    public void goToTimePlanner(int id){
        Intent intent = new Intent(this, TimePlannerActivity.class);
        String message = String.valueOf(id);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }


    public void setAlarm(PlannerListObjekt plannerListObjekt){
        Log.d(TAG, "setAlarm: ");
        Calendar cal = Calendar.getInstance();
        long toDayMillisek = cal.getTimeInMillis();

        if(Long.parseLong(plannerListObjekt.getAlarmTime()) > toDayMillisek) {
            myIntent.putExtra("extra", "yes" + ":" +  plannerListObjekt.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                    plannerListObjekt.getId(), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //MillisekFormatChanger mfc = new MillisekFormatChanger(plannerListObjekt.getDateTimeMillisek());
            alarmManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(plannerListObjekt.getDateTimeMillisek()), pendingIntent);
        }
    }

    //Stoppar de larm som är igång och alla som har en alarmtid som har varit.
    public void stopAlarm(View view){
        Log.d(TAG, "stopAlarm: ");

        myIntent.putExtra("extra", "no"+ ":" + "-");
        sendBroadcast(myIntent);
        for (int i = 0; i < arrayListButtonObjekt.size(); i++) {

            Calendar cal = Calendar.getInstance();
            long toDayMillisek = cal.getTimeInMillis();
            //Log.d(TAG, "stopAlarm: " + toDayMillisek);

            long thisPlannerObjectAlarmTime = Long.parseLong(arrayListButtonObjekt.get(i).getAlarmTime());

            if(thisPlannerObjectAlarmTime < toDayMillisek ){
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                        i + 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.cancel(pendingIntent);
            }

        }

    }

}
