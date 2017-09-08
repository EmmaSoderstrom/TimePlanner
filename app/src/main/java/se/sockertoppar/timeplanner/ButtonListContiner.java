package se.sockertoppar.timeplanner;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by User on 2017-08-18.
 */

public class ButtonListContiner extends ArrayAdapter<PlannerListObjekt> {

    private final Context context;
    private ArrayList<PlannerListObjekt> plannerListObjekt;
    MainActivity mainActivity;



    public ButtonListContiner(Context context, ArrayList<PlannerListObjekt> startPerson, MainActivity mainActivity) {
        super(context, 0, startPerson);

        this.context = context;
        plannerListObjekt = startPerson;
        this.mainActivity = mainActivity;


    }

    @Override
    public int getCount() {
        return plannerListObjekt.size();
    }

    @Override
    public PlannerListObjekt getItem(int position) {
        return plannerListObjekt.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.main_list_layout, null,true);
        } else {
            rowView = convertView;
        }


//        /**
//         * Gör om strängen med fullt datum och tid till long och date
//         */
//        long dateLong = Long.parseLong(plannerListObjekt.get(position).getDateTimeMillisek());
//        Date date = new Date(dateLong);
//
//        /**
//         * calender, för att kunna hämta datum och tid var för sig
//         */
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(dateLong);
//        int day = cal.get(Calendar.DAY_OF_MONTH);
//        int month = cal.get(Calendar.MONTH) + 1;
//        int year = cal.get(Calendar.YEAR);
//        // TODO: 2017-08-23
//        //om på engelska ändra timmarna till 12timmars klocka
//        int hour = cal.get(Calendar.HOUR_OF_DAY);
//        int minute = cal.get(Calendar.MINUTE);

        Calendar cal = Calendar.getInstance();
        long toDayMillisek = cal.getTimeInMillis();
        PlannerListObjekt object = plannerListObjekt.get(position);
        TextView name = (TextView) rowView.findViewById(R.id.object_name);

        //om någon object är aktiv
        if (toDayMillisek > Long.valueOf(object.getAlarmTime())
                && toDayMillisek < (Long.valueOf(object.getDateTimeMillisek()) )) {

            //ändra bakgrund
            rowView.setBackgroundResource(R.color.aktivSubject);

            name.setTextColor(ContextCompat.getColor(context, R.color.aktivSubjectName));

            MillisekFormatChanger millisekFormatChanger = new MillisekFormatChanger(String.valueOf(toDayMillisek));
            //kollar om larmet går igång och gör knapp synlig
            if(millisekFormatChanger.getTimeString(toDayMillisek)
                    .equals(millisekFormatChanger.getTimeString(Long.valueOf(object.getAlarmTime())))){
                Button turnOfAlarmButtom = (Button)mainActivity.findViewById(R.id.turn_of_alarm);
                turnOfAlarmButtom.setVisibility(View.VISIBLE);
            }

        }else if(toDayMillisek > Long.valueOf(object.getDateTimeMillisek())){
            rowView.setBackgroundResource(0);
            name.setTextColor(ContextCompat.getColor(context, R.color.textListName));
        }



        TextView objectName = (TextView)rowView.findViewById(R.id.object_name);
        TextView objectDate = (TextView)rowView.findViewById(R.id.object_date);
        TextView objectTime = (TextView)rowView.findViewById(R.id.object_time);
        objectName.setText(plannerListObjekt.get(position).getName());
        MillisekFormatChanger millisekFormatChanger = new MillisekFormatChanger(plannerListObjekt.get(position).getDateTimeMillisek());
        objectDate.setText(millisekFormatChanger.getDateString());
        objectTime.setText(millisekFormatChanger.getTimeString());

        return rowView;
    }






}