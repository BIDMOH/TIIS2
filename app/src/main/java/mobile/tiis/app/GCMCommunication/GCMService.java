package mobile.tiis.app.GCMCommunication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;

import java.io.Serializable;
import java.util.Random;

import mobile.tiis.app.HomeActivityRevised;
import mobile.tiis.app.R;
import mobile.tiis.app.base.BackboneApplication;

public class GCMService extends GCMBaseIntentService {
    private String tone;
    private LocalBroadcastManager broadcaster;
    static final public String SynchronisationService_RESULT = "mobile,giis.app.CheckForChangesSynchronisationService.REQUEST_PROCESSED";
    static final public String SynchronisationService_MESSAGE = "mobile,giis.app.CheckForChangesSynchronisationService..MSG";


    private static final String TAG = "GCMService";
	public GCMService() {
        super(CommonUtilities.SENDER_ID);
        this.tone="";
	}
	
	@Override
	protected void onRegistered(Context context, String regId) {
		Log.d(TAG, "Sending Registration Id to TIIS server = "+regId);
	}
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            //should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

	@Override
	protected void onUnregistered(Context context, String regId) {
		Log.d(TAG, "Device unregistered");
        ServerUtilities.unregister(context, regId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
        Log.d(TAG, "message received");

        BackboneApplication application = (BackboneApplication) getApplication();
        String childId = intent.getStringExtra("message");
        synchronized (application) {
            application.parseChildById(childId);
        }


//        createNotification(context,childId);
        sendResult(childId);


	}

    public void sendResult(String message) {
        try {
            Intent intent = new Intent(SynchronisationService_RESULT);
            if (message != null)
                intent.putExtra(SynchronisationService_MESSAGE, message);
            broadcaster.sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.d(TAG, "Received deleted messages notification, count: " + total);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.d(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.d(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

    /**
     * Issues a notification to inform the user that server has sent a message.
     */



    public static void createNotification(Context context, String message) {
        Log.d(TAG, "creating notification");

        Intent intent;
        PendingIntent pIntent;

        int notificationId = new Random().nextInt(1000);
        Log.d(TAG, "Creating notification with ID == " + notificationId);

        Log.d(TAG,"the activity is not running");
        intent = new Intent(context, HomeActivityRevised.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);


        long[] pattern = {500,500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(alarmSound == null){
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }

        Notification noti;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            noti= new Notification.Builder(context)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText("TIIS test notification")
                    .setSmallIcon(R.drawable.launcher_icon)
                    .setColor(context.getResources().getColor(R.color.green_500))
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    .setVibrate(pattern)
                    .setSound(alarmSound)
                    .build();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                noti= new Notification.Builder(context)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText("TIIS test notification.")
                        .setSmallIcon(R.drawable.launcher_icon)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .setStyle(new Notification.BigTextStyle().bigText(message))
                        .setVibrate(pattern)
                        .setSound(alarmSound)
                        .build();
            }else{
                noti= new Notification(R.drawable.launcher_icon,message,Notification.FLAG_AUTO_CANCEL);
            }
        }


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;


        Log.d(TAG, "setting notification id == "+notificationId);
        notificationManager.notify(notificationId, noti);

    }



}
