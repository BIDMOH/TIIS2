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

package mobile.giis.app.postman;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.helpers.Utils;

/**
 * Created by Rubin on 3/18/2015.
 * subclass for handling asynchronous task requests in
 * a service on a separate thread.
 */
public class SynchronizationService2 extends IntentService {

    private static final String TAG = "SynchronisationService";

    public SynchronizationService2() {
        super(SynchronizationService2.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG,"SynchronisationService2 started");
        if (!DatabaseHandler.dbPreinstalled) {
            BackboneApplication application = (BackboneApplication) getApplication();

            application.parsePlace();
            application.parseBirthplace();
            application.parseStatus();
            application.parseWeight();
            application.parseNonVaccinationReason();
            application.parseAgeDefinitions();
            application.parseItem();
            application.parseScheduledVaccination();
            application.parseDose();
            application.parseHealthFacility();
            application.parseItemLots();
            application.parseStock();
            application.parseStockAdjustmentReasons();


            String hfidFoundInVaccEvOnlyAndNotInHealthFac = application.getDatabaseInstance().getHFIDFoundInVaccEvAndNotInHealthFac();
            if (hfidFoundInVaccEvOnlyAndNotInHealthFac != null) {
                application.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
            }

            try {
                application.parseChildCollector(); // old service
//                    application.parseChildCollector2(); // new service
            }catch (Exception e){
                e.printStackTrace();
            }


            String placesFoundInChildOnlyAndNotInPlace = application.getDatabaseInstance().getDomicilesFoundInChildAndNotInPlace();
            if(placesFoundInChildOnlyAndNotInPlace != null){
                application.parsePlacesThatAreInChildAndNotInPlaces(placesFoundInChildOnlyAndNotInPlace);
            }

        }
        BackboneApplication app = (BackboneApplication) getApplication();
        app.setMainSyncronizationNeededStatus(false);

        Log.d(TAG,"sychronization finished");
        Log.d(TAG,"stopping service");

        this.stopSelf();
    }
}