package se.sockertoppar.timeplanner;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by User on 2017-08-18.
 */

public class PlannerListContiner extends ArrayAdapter<PlannerObjekt> {

    private final Context context;
    private ArrayList<PlannerObjekt> plannerListObjekt;
    MainActivity mainActivity;
    MillisekFormatChanger millisekFormatChanger;



    public PlannerListContiner(Context context, ArrayList<PlannerObjekt> startPerson, MainActivity mainActivity) {
        super(context, 0, startPerson);

        this.context = context;
        plannerListObjekt = startPerson;
        this.mainActivity = mainActivity;
        millisekFormatChanger = new MillisekFormatChanger();


    }

    @Override
    public int getCount() {
        return plannerListObjekt.size();
    }

    @Override
    public PlannerObjekt getItem(int position) {
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
        PlannerObjekt object = plannerListObjekt.get(position);
        TextView name = (TextView) rowView.findViewById(R.id.object_name);

        //om någon object är aktiv
        if (toDayMillisek > Long.valueOf(object.getAlarmTime())
                && toDayMillisek < (Long.valueOf(object.getDateTimeMillisek()) )) {

            //ändra bakgrund
            rowView.setBackgroundResource(R.color.aktivSubject);

            name.setTextColor(ContextCompat.getColor(context, R.color.aktivSubjectName));

            millisekFormatChanger.getDateString(String.valueOf(toDayMillisek));
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
        TextView objectStart = (TextView)rowView.findViewById(R.id.object_start);
        TextView objectEnd = (TextView)rowView.findViewById(R.id.object_end);
        objectName.setText(plannerListObjekt.get(position).getName());
        //MillisekFormatChanger millisekFormatChanger = new MillisekFormatChanger(plannerListObjekt.get(position).getDateTimeMillisek());
        //MillisekFormatChanger millisekFormatChanger = new MillisekFormatChanger(plannerListObjekt.get(position).getDateTimeMillisek());

        String startTime = millisekFormatChanger.getTimeString(plannerListObjekt.get(position).getAlarmTime());
        String startDate = millisekFormatChanger.getDateString(plannerListObjekt.get(position).getAlarmTime());
        String endTime = millisekFormatChanger.getTimeString(plannerListObjekt.get(position).getDateTimeMillisek());
        String endDate = millisekFormatChanger.getDateString(plannerListObjekt.get(position).getDateTimeMillisek());

        if(startTime.equals(endTime) && startDate.equals(endDate)) {
            objectStart.setText("");
        }else{
            objectStart.setText(millisekFormatChanger.getTimeString(plannerListObjekt.get(position).getAlarmTime())
                    + ", "
                    + millisekFormatChanger.getDateString(plannerListObjekt.get(position).getAlarmTime()));
        }
        objectEnd.setText(millisekFormatChanger.getTimeString(plannerListObjekt.get(position).getDateTimeMillisek())
                + ", "
                + millisekFormatChanger.getDateString(plannerListObjekt.get(position).getDateTimeMillisek()));

        return rowView;
    }






}