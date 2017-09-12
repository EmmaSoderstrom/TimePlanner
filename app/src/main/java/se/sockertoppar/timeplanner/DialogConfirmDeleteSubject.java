package se.sockertoppar.timeplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


/**
 * Created by User on 2017-08-14.
 */

public class DialogConfirmDeleteSubject {

    String TAG = "tag";
    TimePlannerActivity timePlannerActivity;
    View diaView;


    public DialogConfirmDeleteSubject(){
        super();
    }

    public void showDialogConfirmDelete(final Context context, final Subjects removedSubjekt, final int position) {
        timePlannerActivity = (TimePlannerActivity) context;

        AlertDialog.Builder builderConfirm = new AlertDialog.Builder(context);

        String sPart1 = timePlannerActivity.getResources().getString(R.string.dialog_confirm_delete_title_part1);
        String sPart2 = timePlannerActivity.getResources().getString(R.string.dialog_confirm_delete_title_part2);
        builderConfirm.setTitle(sPart1 + " " + removedSubjekt.getName() + sPart2);
        String subPart1 = timePlannerActivity.getResources().getString(R.string.dialog_confirm_delete_subtext_part1);
        String subPart2 = timePlannerActivity.getResources().getString(R.string.dialog_confirm_delete_subtext_part2);
        //builderConfirm.setMessage(subPart1 + " " + removedSubjekt.getName() + " " + subPart2);
        //builderConfirm.setCancelable(false);

        diaView = View.inflate(context, R.layout.dialog_confirm_delete, null);
        builderConfirm.setView(diaView);

        TextView contentText = (TextView) diaView.findViewById(R.id.content_text);
        contentText.setText(subPart1 + " " + removedSubjekt.getName() + subPart2);


        /**
         * Knappar på dialog
         */

        builderConfirm.setPositiveButton(
                "Ja",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("tag", "Dialog ja");
                        timePlannerActivity.changeAlarmTime();
                        dialog.cancel();
                    }
                });
        builderConfirm.setNegativeButton(
                "Nej",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("tag", "Dialog nej");
                        timePlannerActivity.undoRemoveSubject(removedSubjekt, position);
                        timePlannerActivity.changeAlarmTime();
                        dialog.cancel();
                    }
                });

        AlertDialog alertConfirm = builderConfirm.create();
        alertConfirm.show();
    }
}
