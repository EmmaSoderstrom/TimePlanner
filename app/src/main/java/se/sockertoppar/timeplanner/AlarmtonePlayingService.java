package se.sockertoppar.timeplanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by User on 2017-09-05.
 */

public class AlarmtonePlayingService extends Service {

    private boolean isRunning;
    MediaPlayer mMediaPlayer;
    private int startId;

    String plannerObjectId;
    PlannerObjekt plannerListObjekt;
    String name = "";
    MillisekFormatChanger millisekFormatCanger;
    String endsInString;


    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);




        Intent intent1 = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        String state = intent.getExtras().getString("extra");

        String CurrentString = state;
        String[] separated = CurrentString.split(":");
        state = separated[0]; // get state
        plannerObjectId = separated[1]; // get id

        myDbAdapter myDatabasHelper = new myDbAdapter(this);
        if(state.equals("yes")) {
            plannerListObjekt = myDatabasHelper.getObjektById(plannerObjectId);
            name = plannerListObjekt.getName();
            millisekFormatCanger = new MillisekFormatChanger();
            long endsInMillisek = Long.valueOf(plannerListObjekt.getDateTimeMillisek()) - Long.valueOf(plannerListObjekt.getAlarmTime());
            endsInString = millisekFormatCanger.getTimeStringMH(endsInMillisek);
        }

        String title = this.getString(R.string.noti_title);
        String subtext = this.getString(R.string.noti_subtext);

        Notification mNotify  = new Notification.Builder(this)
                .setContentTitle(title + " " + name + "!")
                .setContentText(name + " " + subtext + endsInString)
                .setSmallIcon(R.drawable.notiicon)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

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

            // TODO: 2017-09-08
            // ändra till 1 minut
            int millisekToDelay = 30 * 1000; //1 minut

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    stopAlarm();
                }
            }, millisekToDelay);

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

    public void stopAlarm(){
        mMediaPlayer.stop();
        mMediaPlayer.reset();

        this.isRunning = false;
        this.startId = 0;
    }

    @Override
    public void onDestroy(){
        Log.d("tag", "onDestroy: ");
        super.onDestroy();

        this.isRunning = false;
    }
}
