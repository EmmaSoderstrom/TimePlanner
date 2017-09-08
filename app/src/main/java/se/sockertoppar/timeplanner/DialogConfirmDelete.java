package se.sockertoppar.timeplanner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;


/**
 * Created by User on 2017-08-14.
 */

public class DialogConfirmDelete {

    String TAG = "tag";
    MainActivity mainActivity;
    View diaView;


    public DialogConfirmDelete(){
        super();
    }

    public void showDialogConfirmDelete(final Context context, String name, final int objektId) {

        mainActivity = (MainActivity) context;

        AlertDialog.Builder builderConfirm = new AlertDialog.Builder(context);
        //builder1.setMessage(R.string.dialog_add_planner_message);

        String sPart1 = mainActivity.getResources().getString(R.string.dialog_confirm_delete_title_part1);
        String sPart2 = mainActivity.getResources().getString(R.string.dialog_confirm_delete_title_part2);
        builderConfirm.setTitle(sPart1 + " " + name + " " + sPart2);
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
                        mainActivity.deleteObjektInDatabas(objektId);
                        dialog.cancel();
                    }
                });
        builderConfirm.setNegativeButton(
                "Nej",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("tag", "Dialog nej");
                        dialog.cancel();
                    }
                });

        AlertDialog alertConfirm = builderConfirm.create();
        alertConfirm.show();
    }
}
