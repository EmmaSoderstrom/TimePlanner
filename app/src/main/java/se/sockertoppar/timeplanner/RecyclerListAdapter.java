package se.sockertoppar.timeplanner;

import android.graphics.Color;
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

    public RecyclerListAdapter(ArrayList<Subjects> arrayString, myDbAdapterSubjects myDatabasHelperSubjects, TimePlannerActivity t) {
        //Tar bort alla sysslor i listan
        clearItemList();
        //Lägget till alla sysslor i listan från arrayList med sysslor från databasen
        mItems.addAll(arrayString);
        this.myDatabasHelperSubjects = myDatabasHelperSubjects;
        dialogConfirmDeleteSubject = new DialogConfirmDeleteSubject();
        this.timePlannerActivity = t;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_planner_item, parent, false);
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
        // TODO: 2017-08-28
        //slut tid för detta syssla
        //subjectEndtime.setText(mItems.get(position).?);
    }

    public void clearItemList(){
        mItems.clear();
    }

    @Override
    public void onItemDismiss(int position) {
        Log.d(TAG, "onItemDismiss: " + mItems.get(position).getName());

        Subjects removedSubject = mItems.get(position);


        myDatabasHelperSubjects.deleteById(mItems.get(position).getId());
        mItems.remove(position);
        notifyItemRemoved(position);

        timePlannerActivity.updateRecycleview();

        dialogConfirmDeleteSubject.showDialogConfirmDelete(timePlannerActivity, removedSubject, position);

    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Subjects prev = mItems.remove(fromPosition);
        mItems.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
        moveFromLastPos = fromPosition;
        moveToLastPos = toPosition;
        //Log.d(TAG, "onItemMove: " + toPosition + ", " + mItems.get(fromPosition).getName());
        //mItems.get(fromPosition).setPosition(String.valueOf(toPosition));
        //myDatabasHelperSubjects.updatePos(String.valueOf(mItems.get(fromPosition).getId()), String.valueOf(toPosition));
//        Log.d("tag", "moveFromLastPos: " + moveFromLastPos);
//        Log.d("tag", "moveToLastPos: " + moveToLastPos);
//        Log.d(TAG, "onItemMove: " + mItems.get(fromPosition).getName());
        //subject.setPosition(String.valueOf(mItems.indexOf(subject)));
    }

    public void updateList(List<Subjects> data) {
        //mData = data;
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
            //linerView = (linerView) itemView;
            linerView = (LinearLayout) itemView;
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
            //Log.d("tag", "onItemSelected: " + itemView.findViewById(R.id.subject_linerView));

        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);

//            Log.d("tag", "moveFromLastPos: " + moveFromLastPos);
//            Log.d("tag", "moveToLastPos: " + moveToLastPos);

//          TODO: 2017-08-25
            //ändra i databas också
            for (Subjects subject: mItems) {

//                if(moveFromLastPos < moveToLastPos) {
//                    Log.d("tag", "if : moveFromLastPos < moveToLastPos");
//                    subject.setPosition(String.valueOf(mItems.indexOf(subject)));
//                    int SubjectId = subject.getId();
//                    myDatabasHelperSubjects.updatePos(String.valueOf(SubjectId), String.valueOf(moveToLastPos));
//                }
                //int SubjectId = subject.getId();
                //myDatabasHelperSubjects.updatePos(String.valueOf(SubjectId), );


                //Log.d("tag", "onItemClear: for " + subject.getName() + " " + mItems.indexOf(subject));
            }
        }
    }
}
