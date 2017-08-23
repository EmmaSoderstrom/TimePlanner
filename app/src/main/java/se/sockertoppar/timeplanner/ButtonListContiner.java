package se.sockertoppar.timeplanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by User on 2017-08-18.
 */

public class ButtonListContiner extends ArrayAdapter<PlannerListObjekt> {

    private final Context context;
    private ArrayList<PlannerListObjekt> plannerListObjekt;



    public ButtonListContiner(Context context, ArrayList<PlannerListObjekt> startPerson) {
        super(context, 0, startPerson);

        this.context = context;
        plannerListObjekt = startPerson;

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
            //rowView = inflater.inflate(R.layout.list_buttons, null,true);
            rowView = inflater.inflate(R.layout.main_list_layout, null,true);
        } else {
            rowView = convertView;
        }

//        Button button = (Button)rowView.findViewById(R.id.list_Button);
//        button.setText(plannerListObjekt.get(position).getName() + ", "
//                + plannerListObjekt.get(position).getDate() + ", "
//                + plannerListObjekt.get(position).getTime());

        //Date date = new Date(Integer.valueOf(plannerListObjekt.get(position).getDateTimeMillisek()));
        //String myString = DateFormat.getDateInstance().format(Integer.valueOf(plannerListObjekt.get(position).getDateTimeMillisek()));

        /**
         * Gör om strängen med fullt datum och tid till long och date
         */
        long dateLong = Long.parseLong(plannerListObjekt.get(position).getDateTimeMillisek());
        Date date = new Date(dateLong);

        /**
         * calender, för att kunna hämta datum och tid var för sig
         */
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateLong);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        // TODO: 2017-08-23
        //om på engelska ändra timmarna till 12timmars klocka
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TextView button = (TextView)rowView.findViewById(R.id.mainListObjektText);
        button.setText(plannerListObjekt.get(position).getName() + ", "
                + day + "/" + month + "/" + year + ", "
                + hour + ":" + minute + ", "
                //+ plannerListObjekt.get(position).getDateTimeMillisek() + ", "
                //+ date
                );

//        TextView button = (TextView)rowView.findViewById(R.id.mainListObjektText);
//        button.setText(plannerListObjekt.get(position).getName() + ", "
//                + plannerListObjekt.get(position).getDate() + ", "
//                + plannerListObjekt.get(position).getTime() + ", "
//                + plannerListObjekt.get(position).getDateTimeMillisek() + ", "
//                + date);











        return rowView;
    }





}