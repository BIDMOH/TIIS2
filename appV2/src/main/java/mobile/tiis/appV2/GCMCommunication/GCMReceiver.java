package mobile.tiis.appv2.GCMCommunication;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMReceiver extends GCMBroadcastReceiver {

	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		return "mobile.tiis.appv2.GCMCommunication.GCMService";
	}
}
