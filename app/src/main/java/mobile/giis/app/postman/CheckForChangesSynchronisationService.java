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
import android.util.Log;

import mobile.giis.app.base.BackboneApplication;

/**
 * Created by utente1 on 4/8/2015.
 */
public class CheckForChangesSynchronisationService  extends IntentService {

    public CheckForChangesSynchronisationService() {
        super(CheckForChangesSynchronisationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("coze", "SynchronisationService2 started");
        BackboneApplication app = (BackboneApplication) getApplication();
        synchronized (app) {
            app.parseConfiguration();
            if(app.getLOGGED_IN_USER_ID()!=null && !app.getLOGGED_IN_USER_ID().equals("0")) {
                app.continuousModificationParser();
                app.intervalGetChildrenByHealthFacilitySinceLastLogin();
                app.loginRequest();
//                app.getGetChildByIdListSince();
                app.getVaccinationQueueByDateAndUser();
            }

            String placesFoundInChildOnlyAndNotInPlace = app.getDatabaseInstance().getDomicilesFoundInChildAndNotInPlace();
            if(placesFoundInChildOnlyAndNotInPlace != null){
                app.parsePlacesThatAreInChildAndNotInPlaces(placesFoundInChildOnlyAndNotInPlace);
            }

            String hfidFoundInVaccEvOnlyAndNotInHealthFac = app.getDatabaseInstance().getHFIDFoundInVaccEvAndNotInHealthFac();
            if(hfidFoundInVaccEvOnlyAndNotInHealthFac != null){
                app.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
            }
            app.parseStock();
        }

        this.stopSelf();
    }
}