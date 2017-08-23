package se.sockertoppar.timeplanner;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String TAG = "tag";
    myDbAdapter helper;

    DialogAddPlanner dialogAddPlanner;
    DialogConfirmDelete dialogConfirmDelete;
    MainActivity mainActivity;
    Context context;

    LayoutInflater inflater;
    LinearLayout linearLayoutListContiner;

    ButtonListContiner adapter;
    ButtonListContiner2 adapter2;
    ListView listView = null;

    ArrayList<String> buttonStringArray = new ArrayList<String>();
    ArrayList<PlannerListObjekt> arrayListButtonObjekt = new ArrayList<PlannerListObjekt>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new myDbAdapter(this);

        dialogAddPlanner = new DialogAddPlanner();
        dialogConfirmDelete = new DialogConfirmDelete();
        mainActivity = this;
        context = this;

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linearLayoutListContiner = (LinearLayout)findViewById(R.id.linearLayout_list_continer);

        viewdata();
        setUpButtonList();
    }

    public void onClickAddPlanner(View view){
        Log.d(TAG, "onClickAddPlanner: ");
        dialogAddPlanner.showDialogAddPlanner(this, this);
    }

    public void onClickTextDate(View view){
        Log.d(TAG, "onClickTextDate: ");
        dialogAddPlanner.showCalendar();
    }

    public void saveNewTimePlanner(String plannerName, String plannerDate, int plannerTimeH, int plannerTimeM, String plannerDateTimeMillisek){
        Log.d(TAG, "saveNewTimePlanner: " + plannerName + ", " + plannerDate + ", " + plannerTimeH + ":" + plannerTimeM);
        helper.insertData(this, plannerName, plannerDate, (plannerTimeH + ":" + plannerTimeM), plannerDateTimeMillisek);
        setUpButtonList();
    }

    public void viewdata(){
        String data = helper.getData(this);
        Message.message(this,data);
    }

    public void deleteObjektInDatabas(int id) {
        Log.d(TAG, "delete: " + id);
        helper.delete(id);
        setUpButtonList();
    }

    public void clearButtonArrayList(){
        arrayListButtonObjekt.clear();
    }

    public void setUpButtonList(){

        arrayListButtonObjekt = helper.getDataToButton(this);
        Log.d(TAG, "setUpButtonList: " + arrayListButtonObjekt);

        if (adapter == null) {
            adapter = new ButtonListContiner(this, arrayListButtonObjekt);

            listView = (ListView) findViewById(R.id.list_view_button);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemClick: " + arrayListButtonObjekt.get(position).getName()
                            + ", id, " + arrayListButtonObjekt.get(position).getId());
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemLongClick: " + arrayListButtonObjekt.get(position).getName()
                            + ", id, " + arrayListButtonObjekt.get(position).getId());
                    dialogConfirmDelete.showDialogConfirmDelete(context, mainActivity,
                            arrayListButtonObjekt.get(position).getName(),
                            arrayListButtonObjekt.get(position).getId());

                    return false;
                }
            });

        }else {
            Log.d("tag", "notifyDataSetChanged");
            adapter.notifyDataSetChanged();
        }
    }

}
