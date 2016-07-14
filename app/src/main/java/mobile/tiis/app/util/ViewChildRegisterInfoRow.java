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

package mobile.tiis.app.util;

import java.util.ArrayList;

import mobile.tiis.app.base.BackboneApplication;

/**
 * Created by Teodor on 3/8/2015.
 */
public class ViewChildRegisterInfoRow extends BackboneApplication{

    public int sn;
    public String childFirstName;
    public String childMiddleName;
    public String childSurname;
    public String birthdate;
    public String gender;
    public String motherFirstName;
    public String motherLastName;
    public String domicile;
    public String bcg;
    public String OPV0,OPV1,OPV2,OPV3;
    public String DTP1,DTP2,DTP3;
    public String Rota1,Rota2;
    public String Measles1,Measles2;
    public String PCV1,PCV2,PCV3;
    public String MeaslesRubella1,MeaslesRubella2;
}
