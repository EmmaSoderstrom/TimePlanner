package se.sockertoppar.timeplanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by User on 2017-09-05.
 */

public class AlarmtonePlayingService extends Service {

    private boolean isRunning;
    private Context context;
    MediaPlayer mMediaPlayer;
    private int startId;


    @Override
    public IBinder onBind(Intent intent){
        Log.d("Tag", "AlarmtonePlayingService IBinder");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        Notification mNotify  = new Notification.Builder(this)
                .setContentTitle("Nu startar alarmet" + "!")
                .setContentText("Tryck på mig!")
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        String state = intent.getExtras().getString("extra");

        Log.d("tag", "state  " + state);

        assert state != null;
        switch (state) {
            case "no":
                startId = 0;
                break;
            case "yes":
                startId = 1;
                break;
            default:
                startId = 0;
                break;
        }

        if(!this.isRunning && startId == 1){
            Log.d("tag", " alarm yes");

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), notification);
            mMediaPlayer.start();

            notificationManager.notify(0, mNotify);

            this.isRunning = true;
            this.startId = 0;

        }else if (!this.isRunning && startId == 0){
            Log.d("tag", " alarm defult");

            this.isRunning = false;
            this.startId = 0;

        }else if(this.isRunning && startId == 1){
            Log.d("tag", " alarm sista if");

            this.isRunning = true;
            this.startId = 0;

        }else{
            Log.d("tag", " alarm else");

            mMediaPlayer.stop();
            mMediaPlayer.reset();

            this.isRunning = false;
            this.startId = 0;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        Log.d("tag", "onDestroy: ");
        super.onDestroy();

        this.isRunning = false;
    }
}