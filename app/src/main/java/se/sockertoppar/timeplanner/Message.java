package se.sockertoppar.timeplanner;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by User on 2017-08-15.
 */

public class Message {

    public static void message(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
