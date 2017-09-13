package se.sockertoppar.timeplanner;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
    static TimePlannerActivity timePlannerActivity;
    PlannerObjekt plannerListObjekt;
    MillisekFormatChanger millisekFormatChanger;
    static RecyclerView recycleView;

    static RecyclerListAdapter recyclerListAdapter;


    public RecyclerListAdapter(ArrayList<Subjects> arrayString, myDbAdapterSubjects myDatabasHelperSubjects,
                               TimePlannerActivity timePlannerActivity, PlannerObjekt plannerListObjekt, RecyclerView recycleView) {
        //Tar bort alla sysslor i listan
        clearItemList();
        //Lägget till alla sysslor i listan från arrayList med sysslor från databasen
        mItems.addAll(arrayString);

        this.myDatabasHelperSubjects = myDatabasHelperSubjects;
        dialogConfirmDeleteSubject = new DialogConfirmDeleteSubject();
        this.timePlannerActivity = timePlannerActivity;
        this.plannerListObjekt = plannerListObjekt;
        this.recycleView = recycleView;

        recyclerListAdapter = this;

        millisekFormatChanger = new MillisekFormatChanger();
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
        subjectTime.setText(millisekFormatChanger.getTimeStringMH(Long.valueOf(mItems.get(position).getTime())));

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
    }

    public void clearItemList(){
        mItems.clear();
    }

    public void updateItemList(){
        clearItemList();
        mItems.addAll(myDatabasHelperSubjects.getDataToSubjectsList(timePlannerActivity, String.valueOf(plannerListObjekt.getId())));
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
        Log.d(TAG, "onItemMove: " + fromPosition + " to " + toPosition);
        Subjects prev = mItems.remove(fromPosition);
        mItems.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
        moveFromLastPos = fromPosition;
        moveToLastPos = toPosition;

        //ändrar positioner på sysslorna som flyttas i databasen
        if(fromPosition < toPosition) {
            myDatabasHelperSubjects.updatePos(String.valueOf(mItems.get(fromPosition).getId()), String.valueOf((toPosition + 1)));
            myDatabasHelperSubjects.updatePos(String.valueOf(mItems.get(toPosition).getId()), String.valueOf((fromPosition + 1)));
        }else{
            myDatabasHelperSubjects.updatePos(String.valueOf(mItems.get(fromPosition).getId()), String.valueOf((fromPosition + 1)));
            myDatabasHelperSubjects.updatePos(String.valueOf(mItems.get(toPosition).getId()), String.valueOf((toPosition + 1)));
        }

        updateItemList();
    }

    public void updateList(List<Subjects> data) {
        notifyDataSetChanged();
        timePlannerActivity.setMinutsToDelayTimerCheckIfSubjectActiv();
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

            // TODO: 2017-09-13
            //Det var en nöd lösning då de gröna färgen på den markrade sysslan blikar
            // å fladrar konstigt när man flyttat klart syssor
            int childCount = recycleView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = recycleView.getChildAt(i);
                TextView subjectName = (TextView) child.findViewById(R.id.subject_name);

                    child.setBackgroundColor(0);
                    subjectName.setTextColor(ContextCompat.getColor(timePlannerActivity, R.color.textListName));

            }

            itemView.setBackgroundResource(R.color.removeSubject);
            TextView nameText = (TextView)itemView.findViewById(R.id.subject_name);
            nameText.setTextColor(ContextCompat.getColor(timePlannerActivity, (R.color.textListName)));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
            try {
                recyclerListAdapter.updateList(mItems);
            }catch (Exception e){
                Log.d("tag", "onItemClear: Exception ERROR " + e);
            }

        }
    }
}
