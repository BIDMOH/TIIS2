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

import com.fasterxml.jackson.annotation.JsonProperty;

public class Stock {

    private long balance;
    private String expireDate;
    private String gtin;
    private String item;
    private String lotId;
    private String lotNumber;
    private String reorderQty;
    private String gtinIsActive;
    private String lotIsActive;

    public Stock(){}

    @JsonProperty("Balance")
    public long getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @JsonProperty("ExpireDate")
    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    @JsonProperty("Gtin")
    public String getGtin() {
        return gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    @JsonProperty("Item")
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @JsonProperty("LotId")
    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    @JsonProperty("LotNumber")
    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    @JsonProperty("ReorderQty")
    public String getReorderQty() {
        return reorderQty;
    }

    public void setReorderQty(String reorderQty) {
        this.reorderQty = reorderQty;
    }

    @JsonProperty("Gtin_IsActive")
    public String getGtinIsActive() {
        return gtinIsActive;
    }

    public void setGtinIsActive(String gtinIsActive) {
        this.gtinIsActive = gtinIsActive;
    }

    @JsonProperty("Lot_IsActive")
    public String getLotIsActive() {
        return lotIsActive;
    }

    public void setLotIsActive(String lotIsActive) {
        this.lotIsActive = lotIsActive;
    }

    //Check for usage

    public static final String TAG_ITEM = "Item";
    public static final String TAG_ID = "ID";
    public static final String TAG_ITEM_CATEGORY_ID = "ITEM_CATEGORY_ID";
    public static final String TAG_NAME = "NAME";
    public static final String TAG_CODE = "CODE";
    public static final String TAG_ENTRY_DATE = "ENTRY_DATE";
    public static final String TAG_EXIT_DATE = "EXIT_DATE";


}
