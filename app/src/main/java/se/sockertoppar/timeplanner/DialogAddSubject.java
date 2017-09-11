package se.sockertoppar.timeplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by User on 2017-08-14.
 */

public class DialogAddSubject {

    String TAG = "tag";
    View diaView;


    public DialogAddSubject(){
        super();
    }

    public void showDialogAddSubject(final Context context, final TimePlannerActivity timePlannerActivity) {

        AlertDialog.Builder builderAddPlanner = new AlertDialog.Builder(context);

        builderAddPlanner.setTitle(R.string.dialog_add_subject_title);
        builderAddPlanner.setMessage(R.string.dialog_add_subject_subtext);
        //builderAddPlanner.setCancelable(false);

        diaView = View.inflate(context, R.layout.dialog_add_subject, null);
        builderAddPlanner.setView(diaView);

        EditText editTextSubjectName = (EditText)diaView.findViewById(R.id.editTextSubjectName);
        editTextSubjectName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        final TimePicker timePicker = (TimePicker)diaView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(0);
        timePicker.setCurrentMinute(0);

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

        final AlertDialog aletAddSubject = builderAddPlanner.create();
        aletAddSubject.show();

        aletAddSubject.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag", "Dialog Klar");

                EditText editTextSubjectName = (EditText)diaView.findViewById(R.id.editTextSubjectName);
                int textLength = editTextSubjectName.getText().length();
                int subjectTimeH = (int)timePicker.getCurrentHour();
                Log.d(TAG, "subjectTimeH: " + subjectTimeH);

                int subjectTimeM = (int)timePicker.getCurrentMinute();

                if(textLength > 0 && (subjectTimeH > 0 || subjectTimeM > 0)) {
                    String subjectName = editTextSubjectName.getText().toString();
//                    long subjectTimeTotal= TimeUnit.HOURS.toMillis(subjectTimeH) + TimeUnit.MINUTES.toMillis(subjectTimeM);
                    long subjectTimeTotal = (subjectTimeH * 60 * 60 * 1000) + (subjectTimeM * 60 * 1000);
                    MillisekFormatChanger millisek = new MillisekFormatChanger();
                    Log.d(TAG, "onClick: " + subjectTimeTotal);
                    Log.d(TAG, "onClick: " + millisek.getTimeStringMH(subjectTimeTotal));

                    timePlannerActivity.addSubjektToDatabas(subjectName, String.valueOf(subjectTimeTotal));
                    timePlannerActivity.updateArrayListToRecycleview();
                    //timePlannerActivity.checkIfSubjectActiv();
                    aletAddSubject.dismiss();

                }else{
                    Toast.makeText(context, (R.string.dialog_no_name_and_time_message), Toast.LENGTH_LONG).show();
                }
            }
        });

//        aletAddSubject.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
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
//                    aletAddSubject.dismiss();
//
//                }
//            }
//        });

    }
}
