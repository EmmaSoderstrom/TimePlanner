package se.sockertoppar.timeplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by User on 2017-08-14.
 */

public class DialogChangePlanner {

    String TAG = "tag";
    //final PlannerObjekt plannerObjekt;
    View diaView;
    LinearLayout linearLayoutTime;
    EditText editTextPlannerName;
    TextView textDate;

    DatePicker datePicker;
    int day;
    int month;
    int year;



    public DialogChangePlanner(){
        super();
    }

    public void showDialogChangePlanner(final Context context, final TimePlannerActivity timePlannerActivity,
                                        final PlannerObjekt plannerObjekt) {

        AlertDialog.Builder builderAddPlanner = new AlertDialog.Builder(context);
        builderAddPlanner.setTitle(R.string.dialog_change_planner_title);
        //builderAddPlanner.setMessage(R.string.dialog_add_planner_message);
        //builderAddPlanner.setCancelable(false);

        //this.plannerObjekt = plannerObjekt;

        diaView = View.inflate(context, R.layout.dialog_change_planner, null);
        builderAddPlanner.setView(diaView);

        linearLayoutTime = (LinearLayout)diaView.findViewById(R.id.time_layout);
        editTextPlannerName = (EditText)diaView.findViewById(R.id.editTextPlannerName);
        editTextPlannerName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        datePicker = (DatePicker) diaView.findViewById(R.id.calendarView);

        /**
         * Sätta edit text till plannerarens namn
         */
        editTextPlannerName.setText(plannerObjekt.getName());

        /**
         * Datums text
         */
        textDate = (TextView) diaView.findViewById(R.id.textDate);
        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                showCalendar();
                InputMethodManager imm = (InputMethodManager)timePlannerActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        /**
         * Hämtar dagens datum och sätter datumstexten till datumet
         */
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(plannerObjekt.dateTimeMillisek));
        day = cal.get(Calendar.DATE);
        month = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);

        final String date = day + "/" + month + "/" + year;
        textDate.setText(date);

        //Datum på datePicker
        datePicker.init(year, month - 1, day, null);

        /**
         * Sätter tid
         */
        final TimePicker timePicker = (TimePicker) diaView.findViewById(R.id.timePicker);
        // TODO: 2017-08-14
        //KOlla vilken inställning tellefonen her och set rätt format på am/pm
        timePicker.setIs24HourView(true); // to set 24 hours mode
        //timePicker.setIs24HourView(false); // to set 12 hours mode
        MillisekFormatChanger millisekFormatChanger = new MillisekFormatChanger();

        timePicker.setCurrentHour(millisekFormatChanger.getTimeStringIntHour(Long.valueOf(plannerObjekt.getDateTimeMillisek())));
        timePicker.setCurrentMinute(millisekFormatChanger.getTimeStringIntMin(Long.valueOf(plannerObjekt.getDateTimeMillisek())));


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
                boolean visibility = getVisibilityCalender(datePicker);
                if(visibility) {
                    showCalendar();
                }else{

                    EditText editTextPlannerName = (EditText)diaView.findViewById(R.id.editTextPlannerName);
                    int textLength = editTextPlannerName.getText().length();

                    if(textLength > 0) {
                        String plannerName = editTextPlannerName.getText().toString();
                        int plannerTimeH = (int)timePicker.getCurrentHour();
                        int plannerTimeM = (int)timePicker.getCurrentMinute();


                        day = datePicker.getDayOfMonth();
                        month = datePicker.getMonth() + 1;
                        year = datePicker.getYear();

                        String strDate = year + "-" + month + "-" + day + " "
                                + plannerTimeH + ":" + plannerTimeM + ":" + "00";               // tex. "2017-01-07 10:11:23"

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

                        myDbAdapter myDatabasHelper = new myDbAdapter(timePlannerActivity);
                        myDatabasHelper.updateNameTimeDate(String.valueOf(plannerObjekt.getId()),
                                plannerName, String.valueOf(plannerDateTimeMillisek));

                        timePlannerActivity.upDatePage(String.valueOf(plannerObjekt.getId()));
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
        boolean visibility = getVisibilityCalender(datePicker);
        if(visibility) {
            datePicker.setVisibility(View.GONE);
            linearLayoutTime.setVisibility(View.VISIBLE);

            day = datePicker.getDayOfMonth();
            month = datePicker.getMonth() + 1;
            year = datePicker.getYear();

            textDate.setText(day + "/" + month + "/" + year);
        }else{
            datePicker.setVisibility(View.VISIBLE);
            linearLayoutTime.setVisibility(View.GONE);
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
