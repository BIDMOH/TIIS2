/*******************************************************************************
 * <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *   ~ Copyright (C)AIRIS Solutions 2015 TIIS App - Tanzania Immunization Information System App
 *   ~
 *   ~    Licensed under the Apache License, Version 2.0 (the "License");
 *   ~    you may not use this file except in compliance with the License.
 *   ~    You may obtain a copy of the License at
 *   ~
 *   ~        http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~    Unless required by applicable law or agreed to in writing, software
 *   ~    distributed under the License is distributed on an "AS IS" BASIS,
 *   ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~    See the License for the specific language governing permissions and
 *   ~    limitations under the License.
 *   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
 ******************************************************************************/

package mobile.tiis.staging.postman;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

import mobile.tiis.staging.helpers.Utils;

/**
 * Created by Rubin on 3/18/2015.
 */
public class RoutineAlarmReceiver extends WakefulBroadcastReceiver {
    // The staging's AlarmManager, which provides access to the system alarm services.
    private static AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private static PendingIntent alarmIntent, checkForChangesInChildPI , weeklyUpdateBaseTables;

    /**
     * Sets a repeating alarm that runs once every 5 minutes When the
     * alarm fires, the staging broadcasts an Intent to this WakefulBroadcastReceiver.
     *
     * @param context
     */
    public static void setPostmanAlarm(Context context) {
        if (alarmMgr == null)
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RoutineAlarmReceiver.class);
        intent.putExtra("setPostmanAlarm", true);
        alarmIntent = PendingIntent.getBroadcast(context, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 30000, 30000, alarmIntent);
    }

    // BEGIN_INCLUDE(set_alarm)

    /**
     * This part starts the service that checks every 10 minutes for changes to child data
     *
     * @param context
     */
    public static void setAlarmCheckForChangesInChild(Context context) {
        if (alarmMgr == null)
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent childChanges = new Intent(context, RoutineAlarmReceiver.class);
        childChanges.putExtra("childChanges", true);
        checkForChangesInChildPI = PendingIntent.getBroadcast(context, 222, childChanges, PendingIntent.FLAG_UPDATE_CURRENT); //
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + (5*60000), (5*60000), checkForChangesInChildPI);
    }

    /**
     * This part starts the service that checks every 7 days for changes to doses, nonvacinationreasons and tables like these
     *
     * @param context
     */
    public static void setAlarmWeeklyUpdateBaseTables(Context context) {
        if (alarmMgr == null)
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent childChanges = new Intent(context, RoutineAlarmReceiver.class);
        childChanges.putExtra("weeklyUpdateBaseTables", true);
        weeklyUpdateBaseTables = PendingIntent.getBroadcast(context, 333, childChanges, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 604800000, 604800000, weeklyUpdateBaseTables);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getBooleanExtra("childChanges", false)) {
            if (Utils.isOnline(context)) {
                Intent i = new Intent(context, CheckForChangesSynchronisationService.class);
                startWakefulService(context, i);
            }
        }

        if (intent.getBooleanExtra("setPostmanAlarm", false)) {
            // this happens everytime a broadcast is received , without needing to categorise if the event comes from network or something else
            if (Utils.isOnline(context)) {
                Log.d("WOWHITS", "Broadcast Received Syncronization of the service starts.........");
                Intent i = new Intent(context, SynchronisationService.class);
                startWakefulService(context, i);
            }
        }

        if (intent.getBooleanExtra("weeklyUpdateBaseTables", false)) {
            // this happens everytime a broadcast is received , without needing to categorise if the event comes from network or something else
            if (Utils.isOnline(context)) {
                Intent i = new Intent(context, WeeklySynchronizationService.class);
                startWakefulService(context, i);
            }
        }
    }

    /**
     * Cancels the alarm.
     *
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public static void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
        if (checkForChangesInChildPI != null) {
            alarmMgr.cancel(checkForChangesInChildPI);
        }
    }
}