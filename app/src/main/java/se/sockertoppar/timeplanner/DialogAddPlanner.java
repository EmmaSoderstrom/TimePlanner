package se.sockertoppar.timeplanner;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.text.SimpleDateFormat;

import android.content.Intent;
import android.content.res.Resources;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
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
    EditText editTextPlannerName;
    TextView textDate;

    DatePicker datePicker;
    int day;
    int month;
    int year;


    public DialogAddPlanner(){
        super();
    }

    public void showDialogAddPlanner(final Context context, final MainActivity mainActivity) {

        AlertDialog.Builder builderAddPlanner = new AlertDialog.Builder(context);
        //builder1.setMessage(R.string.dialog_add_planner_message);
        builderAddPlanner.setTitle(R.string.dialog_add_planner_title);
        //builderAddPlanner.setCancelable(false);

        diaView = View.inflate(context, R.layout.dialog_add_planner, null);
        builderAddPlanner.setView(diaView);

        editTextPlannerName = (EditText)diaView.findViewById(R.id.editTextPlannerName);
        editTextPlannerName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        datePicker = (DatePicker) diaView.findViewById(R.id.calendarView);


        /**
         * Val av datum
         */
        textDate = (TextView) diaView.findViewById(R.id.textDate);

        /**
         * Hämtar dagens datum och sätter datumstexten till datumet
         */
        Calendar cal = Calendar.getInstance();
        day = cal.get(Calendar.DATE);
        month = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);


        final String date = day + "/" + month + "/" + year;
        textDate.setText(date);

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
                // TODO: 2017-08-14
                //repitition av LLCalendarView
                //LinearLayout LLCalendarView = (LinearLayout) diaView.findViewById(R.id.LinerCalendarView);
                boolean visibility = getVisibilityCalender(datePicker);
                if(visibility) {
                    showCalendar();
                }else{

                    EditText editTextPlannerName = (EditText)diaView.findViewById(R.id.editTextPlannerName);
                    int textLength = editTextPlannerName.getText().length();

                    if(textLength > 0) {
                        String plannerName = editTextPlannerName.getText().toString();
                        //String plannerDate = (String) textDate.getText();
                        int plannerTimeH = (int)timePicker.getCurrentHour();
                        int plannerTimeM = (int)timePicker.getCurrentMinute();


                        day = datePicker.getDayOfMonth();
                        month = datePicker.getMonth() + 1;
                        year = datePicker.getYear();

                        String strDate = year + "-" + month + "-" + day + " "
                                + plannerTimeH + ":" + plannerTimeM + ":" + "00";               // tex. "2017-01-07 10:11:23"
                        //String test = Locale.getDefault().getLanguage();
                        //Locale l = new Locale ( "sv" , "SV" );
                        //Log.d(TAG, "test: " + test);
                        // TODO: 2017-08-23
                        //språk
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        Date date = null;
                        long plannerDateTimeMillisek = 0;
                        try {
                            date = formatter.parse(strDate);
                            Log.d(TAG, "date: " + date.getTime());
                            plannerDateTimeMillisek = date.getTime();
                        }catch(Exception  e){
                            Log.d(TAG, "error: " + e);
                        }

                        int objektId = mainActivity.saveNewTimePlannerInt(plannerName, strDate, plannerTimeH, plannerTimeM,
                                String.valueOf(plannerDateTimeMillisek));

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
        TimePicker timePicker = (TimePicker)diaView.findViewById(R.id.timePicker);

        boolean visibility = getVisibilityCalender(datePicker);
        if(visibility) {
            datePicker.setVisibility(View.GONE);
            editTextPlannerName.setVisibility(View.VISIBLE);
            textDate.setVisibility(View.VISIBLE);
            timePicker.setVisibility(View.VISIBLE);

            day = datePicker.getDayOfMonth();
            month = datePicker.getMonth() + 1;
            year = datePicker.getYear();

            textDate.setText(day + "/" + month + "/" + year);
        }else{
            datePicker.setVisibility(View.VISIBLE);
            editTextPlannerName.setVisibility(View.GONE);
            textDate.setVisibility(View.GONE);
            timePicker.setVisibility(View.GONE);
        }
    }

    public boolean getVisibilityCalender(DatePicker dp){
        boolean visibility;
        int calendarVisibility = dp.getVisibility();
        if(calendarVisibility == 0) {
            visibility = true;
        }else{
            visibility = false;
        }
        return visibility;
    }


}
