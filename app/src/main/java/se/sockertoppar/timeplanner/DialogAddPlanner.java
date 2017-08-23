package se.sockertoppar.timeplanner;

import android.content.Context;
import android.content.DialogInterface;

import java.text.SimpleDateFormat;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

/**
 * Created by User on 2017-08-14.
 */

public class DialogAddPlanner {

    String TAG = "tag";
    View diaView;
    TextView textDate;

    int day;
    int month;
    int year;



    public DialogAddPlanner(){
        super();
    }

    public void showDialogAddPlanner(final Context context, final MainActivity mainActivity) {
        Log.d(TAG, "showDialogAddPlanner: ");

        AlertDialog.Builder builderAddPlanner = new AlertDialog.Builder(context);
        //builder1.setMessage(R.string.dialog_add_planner_message);
        builderAddPlanner.setTitle(R.string.dialog_add_planner_title);
        //builderAddPlanner.setCancelable(false);

        diaView = View.inflate(context, R.layout.dialog_add_planner, null);
        builderAddPlanner.setView(diaView);


        /**
         * Val av datum
         */
        textDate = (TextView) diaView.findViewById(R.id.textDate);

        /**
         * Hämtar dagens datum och sätter datumstexten till datumet
         */
        Calendar cal = Calendar.getInstance();
        day = cal.get(Calendar.DATE);
        // TODO: 2017-08-14
        //Varför + 1 på månad
        month = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);
        final String date = day + "/" + month + "/" + year;
        textDate.setText(date);

        /**
         * Calender, ändrar text när man klickar på ett nytt datum
         */
        CalendarView calendarView = (CalendarView) diaView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int sYear, int sMonth, int sDay) {
                day = sDay;
                month = sMonth;
                year = sYear;

                String newDate = day + "/" + (month + 1) + "/" + year;
                textDate.setText(newDate);
            }
        });


        /**
         * Val av tid
         */
        final TimePicker timePicker = (TimePicker) diaView.findViewById(R.id.timePicker);
        // TODO: 2017-08-14
        //KOlla vilken inställning tellefonen her och set rätt format på am/pm
        timePicker.setIs24HourView(true); // to set 24 hours mode
        //timePicker.setIs24HourView(false); // to set 12 hours mode



        /**
         * Knappar på dialog
         */

        builderAddPlanner.setPositiveButton(
                "Klar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Måste finnas, men skrivs över med ennan metod
                    }
                });
//        builderAddPlanner.setNegativeButton(
//                "Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        //Måste finnas, men skrivs över med ennan metod
//                    }
//                });

        final AlertDialog alertAddPlanner = builderAddPlanner.create();
        alertAddPlanner.show();

        alertAddPlanner.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag", "Dialog Klar");
                // TODO: 2017-08-14
                //repitition av LLCalendarView
                LinearLayout LLCalendarView = (LinearLayout) diaView.findViewById(R.id.LinerCalendarView);
                boolean visibility = getVisibilityCalender(LLCalendarView);
                if(visibility) {
                    showCalendar();
                }else{

                    EditText editTextPlannerName = (EditText)diaView.findViewById(R.id.editTextPlannerName);
                    int textLength = editTextPlannerName.getText().length();

                    if(textLength > 0) {
                        String plannerName = editTextPlannerName.getText().toString();
                        String plannerDate = (String) textDate.getText();
                        int plannerTimeH = (int)timePicker.getCurrentHour();
                        int plannerTimeM = (int)timePicker.getCurrentMinute();


                        String str_date= year + "-" + month + "-" + day + " "
                                + plannerTimeH + ":" + plannerTimeM + ":" + "00";               // tex. "2017-01-07 10:11:23"
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        Date date = null;
                        long plannerDateTimeMillisek = 0;
                        try {
                            date = formatter.parse(str_date);
                            //Log.d(TAG, "onClick: Today is " + date.getTime());
                            plannerDateTimeMillisek = date.getTime();
                        }catch(Exception  e){
                            Log.d(TAG, "error: " + e);
                        }

                        mainActivity.saveNewTimePlanner(plannerName, plannerDate, plannerTimeH, plannerTimeM, plannerDateTimeMillisek);
                        alertAddPlanner.dismiss();
                    }else{
                        Toast.makeText(context, (R.string.dialog_no_name_message), Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

//        alertAddPlanner.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("tag", "Dialog Cancel");
//                LinearLayout LLCalendarView = (LinearLayout) diaView.findViewById(R.id.LinerCalendarView);
//                boolean visibility = getVisibilityCalender(LLCalendarView);
//                if(visibility) {
//                    showCalendar();
//                    Log.d(TAG, "klar: true " + visibility);
//                }else{
//                    Log.d(TAG, "klar: false " + visibility);
//                    alertAddPlanner.dismiss();
//
//                }
//            }
//        });

    }
    
    public void showCalendar(){
        Log.d(TAG, "showCalendar: ");
        LinearLayout LLCalendarView = (LinearLayout) diaView.findViewById(R.id.LinerCalendarView);
        boolean visibility = getVisibilityCalender(LLCalendarView);

        if(visibility) {
            LLCalendarView.setVisibility(View.GONE);
            Log.d(TAG, "showCalendar: true " + visibility);
        }else{
            LLCalendarView.setVisibility(View.VISIBLE);
        }
    }

    public boolean getVisibilityCalender(LinearLayout LLCalendarView){
        boolean visibility;
        int calendarVisibility = LLCalendarView.getVisibility();
        if(calendarVisibility == 0) {
            visibility = true;
        }else{
            visibility = false;
        }
        return visibility;
    }


}
