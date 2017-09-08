package se.sockertoppar.timeplanner;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

        dialogAddPlanner = new DialogAddPlanner();
        dialogConfirmDelete = new DialogConfirmDelete();
        mainActivity = this;
        context = this;

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setUpButtonList();

        myIntent = new Intent(getBaseContext(), AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        checkIfSubjectActiv();
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
        myDatabasHelper.delete(id);
        myDatabasHelperSubjects.deleteByPointingId(id);
        setUpButtonList();
    }

    public void clearButtonArrayList(){
        arrayListButtonObjekt.clear();
    }

    public void setUpButtonList(){
        arrayListButtonObjekt = myDatabasHelper.getDataToButton(this);

        if (adapter == null) {
            adapter = new ButtonListContiner(this, arrayListButtonObjekt);

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
            adapter.notifyDataSetChanged();
        }
    }

    public void goToTimePlanner(int id){
        Intent intent = new Intent(this, TimePlannerActivity.class);
        String message = String.valueOf(id);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }



    public void checkIfSubjectActiv(){
        Log.d(TAG, "checkIfSubjectActiv: ");
        Calendar cal = Calendar.getInstance();
        long toDayMillisek = cal.getTimeInMillis();

        //ListView listView = (ListView) findViewById(R.id.list_view_button);

        for (int i = 0; i < arrayListButtonObjekt.size(); i++) {

            PlannerListObjekt object = arrayListButtonObjekt.get(i);

            if(object.getAlarmTime() != null) {

                //om någon object är aktiv
                if (toDayMillisek > Long.valueOf(object.getAlarmTime())
                        && toDayMillisek < (Long.valueOf(object.getDateTimeMillisek()) )) {

                    Log.d(TAG, "checkIfSubjectActiv: aktivt ");

                    //ändra bakgrund
                    changeActivBackgrund(i, listView);

                    MillisekFormatChanger millisekFormatChanger = new MillisekFormatChanger(String.valueOf(toDayMillisek));
                    //kollar om larmet går igång och gör knapp synlig
                    if(millisekFormatChanger.getTimeString(toDayMillisek)
                            .equals(millisekFormatChanger.getTimeString(Long.valueOf(object.getAlarmTime())))){
                        FloatingActionButton turnOfAlarmButtom = (FloatingActionButton)findViewById(R.id.turn_of_alarm);
                        turnOfAlarmButtom.setVisibility(View.VISIBLE);
                    }

                }else if(toDayMillisek > Long.valueOf(object.getDateTimeMillisek())){
                    //changeActivBackgrund(-1, listView);
                }
            }
        }
    }

    public void changeActivBackgrund(int pos, ListView lv){
        View test;
        test = listView.getAdapter().getView(pos, null, listView);

        Log.d(TAG, "changeActivBackgrund: " + pos);
        //listView.getChildAt(pos).setBackgroundColor(Color.parseColor("#00743D"));

//        final int firstListItemPosition = listView.getFirstVisiblePosition();
//        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
//
//        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
//             test = listView.getAdapter().getView(pos, null, listView);
//            Log.d(TAG, "changeActivBackgrund: detta ");
//        } else {
//            final int childIndex = pos - firstListItemPosition;
//             test = listView.getChildAt(childIndex);
//            Log.d(TAG, "changeActivBackgrund: detta eller detta");
//        }
        Log.d(TAG, "changeActivBackgrund: test " + test.getTag());
        //test.setBackgroundResource(R.color.aktivSubject);
        //test.setBackgroundColor(0x45f6400);
        LinearLayout ll = (LinearLayout) test.findViewById(R.id.main_list_ll);
        Log.d(TAG, "changeActivBackgrund: test " + ll.getTag());
        ll.setBackgroundResource(R.color.aktivSubject);
        //Log.d(TAG, "changeActivBackgrund: " + listView.getChildAt(indexPosition));

        //listView.setBackgroundResource(R.color.colorAccent);
        int childCount = listView.getChildCount();
//
        Log.d(TAG, "changeActivBackgrund:  childCount " + childCount);
        for (int i = 0; i < childCount; i++) {
            View child = listView.getChildAt(i);
            Log.d(TAG, "changeActivBackgrund: child " + child);
            //TextView subjectName = (TextView) child.findViewById(R.id.subject_name);

            if (i == pos) {
                child.setBackgroundResource(R.color.aktivSubject);
                //subjectName.setTextColor(ContextCompat.getColor(this, R.color.aktivSubjectName));
            }else {
                child.setBackgroundColor(0);
                //subjectName.setTextColor(ContextCompat.getColor(this, R.color.textListName));
            }
        }
    }


    public void onClickOfAlarm(View view){
        FloatingActionButton turnOfAlarmButtom = (FloatingActionButton)findViewById(R.id.turn_of_alarm);
        turnOfAlarmButtom.setVisibility(View.INVISIBLE);
        stopAlarm();
    }

    public void setAlarm(PlannerListObjekt plannerListObjekt){
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
}
