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

package mobile.tiis.appv2.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Melisa on 05/02/2015.
 */
public class Community {

    public static final String TAG_COMMUNITY = "Community";
    public static final String TAG_ID = "ID";
    public static final String TAG_NAME = "NAME";

    private String id;
    private String name;

    public Community(){}

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();

        map.put(TAG_ID, id);
        map.put(TAG_NAME, name);

        return map;
    }

    public String toString() {
        return name;
    }
}
