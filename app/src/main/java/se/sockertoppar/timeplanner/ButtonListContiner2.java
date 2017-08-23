package se.sockertoppar.timeplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by User on 2017-08-18.
 */

public class ButtonListContiner2 extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<String> plannerListObjekt;



    public ButtonListContiner2(Context context, ArrayList<String> startPerson) {
        super(context, 0, startPerson);

        this.context = context;
        plannerListObjekt = startPerson;

    }

    @Override
    public int getCount() {
        return plannerListObjekt.size();
    }

    @Override
    public String getItem(int position) {
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
            rowView = inflater.inflate(R.layout.list_buttons, null,true);
        } else {
            rowView = convertView;
        }

        Button button = (Button)rowView.findViewById(R.id.list_Button);
        button.setText(plannerListObjekt.get(position));













        return rowView;
    }





}