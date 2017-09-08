package se.sockertoppar.timeplanner;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

import se.sockertoppar.timeplanner.helper.ItemTouchHelperAdapter;
import se.sockertoppar.timeplanner.helper.ItemTouchHelperViewHolder;

/**
 * Created by User on 2017-08-25.
 */

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {

    String TAG = "tag";
    public static int moveFromLastPos = 0;
    public static int moveToLastPos = 0;

    public static List<Subjects> mItems = new ArrayList<>();
    static myDbAdapterSubjects myDatabasHelperSubjects;
    DialogConfirmDeleteSubject dialogConfirmDeleteSubject;
    TimePlannerActivity timePlannerActivity;
    PlannerListObjekt plannerListObjekt;


    public RecyclerListAdapter(ArrayList<Subjects> arrayString, myDbAdapterSubjects myDatabasHelperSubjects,
                               TimePlannerActivity timePlannerActivity, PlannerListObjekt plannerListObjekt) {
        //Tar bort alla sysslor i listan
        clearItemList();
        //Lägget till alla sysslor i listan från arrayList med sysslor från databasen
        mItems.addAll(arrayString);

        this.myDatabasHelperSubjects = myDatabasHelperSubjects;
        dialogConfirmDeleteSubject = new DialogConfirmDeleteSubject();
        this.timePlannerActivity = timePlannerActivity;
        this.plannerListObjekt = plannerListObjekt;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        TextView subjectName = (TextView)holder.linerView.findViewById(R.id.subject_name);
        TextView subjectTime = (TextView)holder.linerView.findViewById(R.id.subject_time);
        TextView subjectEndtime = (TextView)holder.linerView.findViewById(R.id.subject_endtime);
        subjectName.setText(mItems.get(position).getName());
        MillisekFormatChanger millisekFormatChanger = new MillisekFormatChanger(mItems.get(position).getTime());
        subjectTime.setText(millisekFormatChanger.getTimeStringMH());

        //skapar starttid till varje syssla
        String subjectToGiveTimePosition = mItems.get(position).getPosition();
        long totalTimeToGive = 0;
        for (Subjects subjectToGetTime : mItems) {
            if(Integer.valueOf(subjectToGiveTimePosition) < Integer.valueOf(subjectToGetTime.getPosition())){
                totalTimeToGive = totalTimeToGive + Long.valueOf(subjectToGetTime.getTime());
            }
        }
        long endTime = Long.valueOf(plannerListObjekt.getDateTimeMillisek());
        long startTimeOnSubject = endTime - totalTimeToGive - Long.valueOf(mItems.get(position).getTime());
        subjectEndtime.setText(millisekFormatChanger.getTimeString(startTimeOnSubject));
        timePlannerActivity.addStartTimeToSubject(position, startTimeOnSubject);
        timePlannerActivity.checkIfSubjectActiv();
    }

    public void clearItemList(){
        mItems.clear();
    }

    @Override
    public void onItemDismiss(int position) {
        Subjects removedSubject = mItems.get(position);

        myDatabasHelperSubjects.deleteById(mItems.get(position).getId());
        mItems.remove(position);
        notifyItemRemoved(position);

        timePlannerActivity.updateArrayListToRecycleview();
        dialogConfirmDeleteSubject.showDialogConfirmDelete(timePlannerActivity, removedSubject, position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Subjects prev = mItems.remove(fromPosition);
        mItems.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
        moveFromLastPos = fromPosition;
        moveToLastPos = toPosition;
    }

    public void updateList(List<Subjects> data) {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final LinearLayout linerView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            linerView = (LinearLayout) itemView;
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundResource(R.color.removeSubject);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
