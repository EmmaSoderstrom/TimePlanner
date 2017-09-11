package se.sockertoppar.timeplanner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    public static final String EXTRA_MESSAGE = "se.sockertoppar.timeplanner.MESSAGE";

    String TAG = "tag";
    myDbAdapter myDatabasHelper;
    myDbAdapterSubjects myDatabasHelperSubjects;

    DialogAddPlanner dialogAddPlanner;
    DialogConfirmDelete dialogConfirmDelete;
    MainActivity mainActivity;
    Context context;
    MillisekFormatChanger millisekFormatChanger;

    LayoutInflater inflater;
    LinearLayout linearLayoutListContiner;

    PlannerListContiner adapter;
    ListView listView = null;

    ArrayList<String> buttonStringArray = new ArrayList<String>();
    ArrayList<PlannerObjekt> arrayListButtonObjekt = new ArrayList<PlannerObjekt>();

    Intent myIntent;
    AlarmManager alarmManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDatabasHelper = new myDbAdapter(this);
        myDatabasHelperSubjects = new myDbAdapterSubjects(this);

        dialogAddPlanner = new DialogAddPlanner();
        dialogConfirmDelete = new DialogConfirmDelete();
        mainActivity = this;
        context = this;
        millisekFormatChanger = new MillisekFormatChanger();

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setUpButtonList();

        myIntent = new Intent(getBaseContext(), AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        setMinutsToDelayTimer();
        stopAlarm();
    }

    public void onClickAddPlanner(View view){
        dialogAddPlanner.showDialogAddPlanner(this, this);
    }

    public void onClickTextDate(View view){
        dialogAddPlanner.showCalendar();
    }

    public int saveNewTimePlannerInt(String plannerName, String plannerDate, int plannerTimeH, int plannerTimeM,
                                     String plannerDateTimeMillisek){
        int objektId = myDatabasHelper.insertDataInt(this, plannerName, plannerDate, (plannerTimeH + ":" + plannerTimeM),
                plannerDateTimeMillisek, plannerDateTimeMillisek);

        PlannerObjekt plannerListObjekt = myDatabasHelper.getObjektById(String.valueOf(objektId));
        setAlarm(plannerListObjekt);
        goToTimePlanner(objektId);

        return objektId;
    }

    public void viewdata(){
        String data = myDatabasHelper.getData();
        Message.message(this,data);
    }

    public void deleteObjektInDatabas(int id) {
        myDatabasHelper.delete(id);
        myDatabasHelperSubjects.deleteByPointingId(id);
        setUpButtonList();
    }

    public void clearButtonArrayList(){
        arrayListButtonObjekt.clear();
    }



    public void setMinutsToDelayTimer(){
        Calendar cal = Calendar.getInstance();
        int sekund = cal.getTime().getSeconds();
        int millisekToDelay = (60 - sekund) * 1000;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                setMinutsTimer();
            }
        }, millisekToDelay);
    }

    public void setMinutsTimer(){
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                // When you need to modify a UI element, do so on the UI thread.
                // 'getActivity()' is required as this is being ran from a Fragment.
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        Log.d(TAG, "run: setMinutsTimer");
                        setUpButtonList();
                    }
                });
            }
        }, 0, 1000 * 1 * 60); //1000 * 1 * 60 = 60sek
    }

    public void setUpButtonList(){
        arrayListButtonObjekt = myDatabasHelper.getDataToButton(this);

        if (adapter == null) {
            Log.d(TAG, "adapter null: ");
            adapter = new PlannerListContiner(this, arrayListButtonObjekt, this);

            listView = (ListView) findViewById(R.id.list_view_button);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    goToTimePlanner(arrayListButtonObjekt.get(position).getId());
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    dialogConfirmDelete.showDialogConfirmDelete(context,
                            arrayListButtonObjekt.get(position).getName(),
                            arrayListButtonObjekt.get(position).getId());

                    return true;
                }
            });

        }else {
            Log.d(TAG, "notifyDataSetChanged: ");
            adapter.notifyDataSetChanged();
        }

        //overridar för att kunna sätta bakgrund på aktivt objekt
        //för detta måste hela layouten för varje rad defineras
        listView.setAdapter(new ArrayAdapter<PlannerObjekt>(this, R.layout.main_list_layout, R.id.object_name, arrayListButtonObjekt){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = super.getView(position, convertView, parent);

                PlannerObjekt rowObject = arrayListButtonObjekt.get(position);

                TextView objectName = (TextView)row.findViewById(R.id.object_name);
                TextView objectStart = (TextView)row.findViewById(R.id.object_start);
                TextView objectEnd = (TextView)row.findViewById(R.id.object_end);
                objectName.setText(arrayListButtonObjekt.get(position).getName());

                String startTime = millisekFormatChanger.getTimeString(rowObject.getAlarmTime());
                String startDate = millisekFormatChanger.getDateString(rowObject.getAlarmTime());
                String endTime = millisekFormatChanger.getTimeString(rowObject.getDateTimeMillisek());
                String endDate = millisekFormatChanger.getDateString(rowObject.getDateTimeMillisek());

                if(startTime.equals(endTime) && startDate.equals(endDate)) {
                    objectStart.setText("");
                }else{
                    objectStart.setText(millisekFormatChanger.getTimeString(rowObject.getAlarmTime())
                            + ", "
                            + millisekFormatChanger.getDateString(rowObject.getAlarmTime()));
                }
                objectEnd.setText(millisekFormatChanger.getTimeString(rowObject.getDateTimeMillisek())
                        + ", "
                        + millisekFormatChanger.getDateString(rowObject.getDateTimeMillisek()));


                Calendar cal = Calendar.getInstance();
                long toDayMillisek = cal.getTimeInMillis();

                if (toDayMillisek > Long.valueOf(rowObject.getAlarmTime())
                            && toDayMillisek < (Long.valueOf(rowObject.getDateTimeMillisek()) )) {
                    //färg aktivt objekt
                    row.setBackgroundColor (ContextCompat.getColor(context, R.color.aktivSubject));
                    objectName.setTextColor(ContextCompat.getColor(context, R.color.aktivSubjectName));
                }else{
                    //färg ej aktivt objeckt
                    row.setBackgroundColor (0);
                    objectName.setTextColor(ContextCompat.getColor(context, R.color.textListName));
                }

                return row;
            }
        });
    }

    public void goToTimePlanner(int id){
        Intent intent = new Intent(this, TimePlannerActivity.class);
        String message = String.valueOf(id);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void onClickOfAlarm(View view){
        Button turnOfAlarmButtom = (Button)findViewById(R.id.turn_of_alarm);
        turnOfAlarmButtom.setVisibility(View.INVISIBLE);
        stopAlarm();
    }

    public void setAlarm(PlannerObjekt plannerListObjekt){
        Calendar cal = Calendar.getInstance();
        long toDayMillisek = cal.getTimeInMillis();

        if(Long.parseLong(plannerListObjekt.getAlarmTime()) > toDayMillisek) {
            myIntent.putExtra("extra", "yes" + ":" +  plannerListObjekt.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                    plannerListObjekt.getId(), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(plannerListObjekt.getDateTimeMillisek()), pendingIntent);

        }
    }

    //Stoppar de larm som är igång och alla som har en alarmtid som har varit.
    public void stopAlarm(){
        myIntent.putExtra("extra", "no"+ ":" + "-");
        sendBroadcast(myIntent);
        for (int i = 0; i < arrayListButtonObjekt.size(); i++) {

            Calendar cal = Calendar.getInstance();
            long toDayMillisek = cal.getTimeInMillis();
            long thisPlannerObjectAlarmTime = Long.parseLong(arrayListButtonObjekt.get(i).getAlarmTime());

            if(thisPlannerObjectAlarmTime < toDayMillisek ){
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                        i + 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.cancel(pendingIntent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "On Resume .....");
        setUpButtonList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "On Start .....");
        setUpButtonList();
    }
}
