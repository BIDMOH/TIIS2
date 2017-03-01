package mobile.tiis.appv2.GCMCommunication;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	
	// give your server registration url here
    public  static final String SERVER_URL = "https://ec2-54-187-21-117.us-west-2.compute.amazonaws.com/SVC/r";
    public  static final String SERVER_REGISTER_GCM = "https://ec2-54-187-21-117.us-west-2.compute.amazonaws.com/SVC/";

    // Google project id
    // FOR LIVE SERVER
    public static final String SENDER_ID = "967487253557";

    /**
     * Tag used on log messages.
     */
    static final String TAG = CommonUtilities.class.getSimpleName();


    public static final String DISPLAY_MESSAGE_ACTION ="mobile.tiis.appv2.DISPLAY_MESSAGE";
    public static final String DISPLAY_POSTMAN_COUNT_ACTION ="mobile.tiis.appv2.POSTMAN_COUNT";

    public static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
