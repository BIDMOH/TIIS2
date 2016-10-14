package mobile.tiis.staging.GCMCommunication;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMReceiver extends GCMBroadcastReceiver {

	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		return "mobile.tiis.staging.GCMCommunication.GCMService";
	}
}
