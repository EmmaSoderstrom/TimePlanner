package se.sockertoppar.timeplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;


/**
 * Created by User on 2017-08-14.
 */

public class DialogConfirmDeleteSubject {

    String TAG = "tag";
    TimePlannerActivity timePlannerActivity;
    View diaView;
    boolean confirmation = false;


    public DialogConfirmDeleteSubject(){
        super();
    }

    public void showDialogConfirmDelete(final Context context, final Subjects removedSubjekt, final int position) {
        Log.d(TAG, "showDialogAddPlanner: " + context.getClass().getSimpleName());

        timePlannerActivity = (TimePlannerActivity) context;
        //context.getApplicationContext();

        AlertDialog.Builder builderConfirm = new AlertDialog.Builder(context);
        //builder1.setMessage(R.string.dialog_add_planner_message);

        String sPart1 = timePlannerActivity.getResources().getString(R.string.dialog_confirm_delete_title_part1);
        String sPart2 = timePlannerActivity.getResources().getString(R.string.dialog_confirm_delete_title_part2);
        builderConfirm.setTitle(sPart1 + " " + removedSubjekt.getName() + " " + sPart2);
        //builderConfirm.setCancelable(false);

        diaView = View.inflate(context, R.layout.dialog_confirm_delete, null);
        builderConfirm.setView(diaView);


        /**
         * Knappar på dialog
         */

        builderConfirm.setPositiveButton(
                "Ja",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("tag", "Dialog ja");
                        dialog.cancel();
                    }
                });
        builderConfirm.setNegativeButton(
                "Nej",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("tag", "Dialog nej");
                        timePlannerActivity.undoRemoveSubject(removedSubjekt, position);
                        dialog.cancel();
                    }
                });

        AlertDialog alertConfirm = builderConfirm.create();
        alertConfirm.show();
    }
}
