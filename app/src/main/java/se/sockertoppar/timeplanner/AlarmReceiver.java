package se.sockertoppar.timeplanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by User on 2017-09-05.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("tag", "onReceive: ");

        String state = intent.getExtras().getString("extra");

        Intent serviceIntent = new Intent(context, AlarmtonePlayingService.class);
        serviceIntent.putExtra("extra", state);

        context.startService(serviceIntent);
    }
}
