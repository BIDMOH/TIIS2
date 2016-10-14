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

import android.app.IntentService;
import android.content.Intent;

import mobile.tiis.staging.base.BackboneApplication;

/**
 * Created by Rubin on 6/4/2015.
 */
public class WeeklySynchronizationService extends IntentService {

    public WeeklySynchronizationService() {
        super(WeeklySynchronizationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        BackboneApplication application = (BackboneApplication) getApplication();
        synchronized (application) {
            if(application.getLOGGED_IN_USER_ID()!=null && !application.getLOGGED_IN_USER_ID().equals("0")) {

                application.parseItem();
                application.parseDose();
                application.parseScheduledVaccination();
                application.parseStockAdjustmentReasons();
                application.parseNonVaccinationReason();
                application.parseAgeDefinitions();
            }
        }


        this.stopSelf();
    }
}