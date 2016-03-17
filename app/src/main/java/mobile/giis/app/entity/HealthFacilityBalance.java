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

package mobile.giis.app.entity;

/**
 * Created by Rubin on 6/1/2015.
 */
public class HealthFacilityBalance {

    private String item_name;
    private String gtin;
    private String lot_number;
    private String expire_date;
    private int balance;
    private String lot_id;
    private int selectedAdjustmentReasonPosition = 0;
    private String tempBalance = "";
//        private String reorder_qty;


    public HealthFacilityBalance() {
    }

    public HealthFacilityBalance(String item_name, String tempBalance, int selectedAdjustmentReasonPosition, String lot_id, int balance, String expire_date, String lot_number, String gtin) {
        this.item_name = item_name;
        this.tempBalance = tempBalance;
        this.selectedAdjustmentReasonPosition = selectedAdjustmentReasonPosition;
        this.lot_id = lot_id;
        this.balance = balance;
        this.expire_date = expire_date;
        this.lot_number = lot_number;
        this.gtin = gtin;
    }
    public int getSelectedAdjustmentReasonPosition() {
        return selectedAdjustmentReasonPosition;
    }

    public void setSelectedAdjustmentReasonPosition(int selectedAdjustmentReasonPosition) {
        this.selectedAdjustmentReasonPosition = selectedAdjustmentReasonPosition;
    }
    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getGtin() {
        return gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    public String getLot_number() {
        return lot_number;
    }

    public void setLot_number(String lot_number) {
        this.lot_number = lot_number;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getLot_id() {
        return lot_id;
    }

    public void setLot_id(String lot_id) {
        this.lot_id = lot_id;
    }

    public String getTempBalance() {
        return tempBalance;
    }

    public void setTempBalance(String tempBalance) {
        this.tempBalance = tempBalance;
    }
//
//        public String getReorder_qty() {
//            return reorder_qty;
//        }
//
//        public void setReorder_qty(String reorder_qty) {
//            this.reorder_qty = reorder_qty;
//        }
}
