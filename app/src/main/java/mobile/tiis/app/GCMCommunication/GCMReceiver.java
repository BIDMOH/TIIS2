package mobile.tiis.app.GCMCommunication;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMReceiver extends GCMBroadcastReceiver {

	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		return "mobile.tiis.app.GCMCommunication.GCMService";
	}
}
