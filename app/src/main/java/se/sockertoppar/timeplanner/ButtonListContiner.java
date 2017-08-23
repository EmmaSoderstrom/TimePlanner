package se.sockertoppar.timeplanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

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

        TextView button = (TextView)rowView.findViewById(R.id.mainListObjektText);
        button.setText(plannerListObjekt.get(position).getName() + ", "
                + plannerListObjekt.get(position).getDate() + ", "
                + plannerListObjekt.get(position).getTime());











        return rowView;
    }





}