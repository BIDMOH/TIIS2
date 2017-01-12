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

package mobile.tiis.staging.entity;

/**
 * Created by Coze on 29/10/2016.
 */
public class HealthFacilityProofOfDelivery {

    private String DistributionDate;
    private String DistributionType;
    private int FromHealthFacilityId;
    private int StockDistributionId;
    private int ToHealthFacilityId;
    private int ItemId;
    private int LotId;
    private int ProductId;
    private int ProgramId;
    private int Quantity;
    private String Status;
    private int VimsLotId;
    private String VvmStatus;
    private String vaccineName;
    private String lotNumber;
    private String unitOfMeasure;
    private int dosesPerDispensingUnit;

    public String getDistributionDate() {
        return DistributionDate;
    }

    public void setDistributionDate(String distributionDate) {
        DistributionDate = distributionDate;
    }

    public String getDistributionType() {
        return DistributionType;
    }

    public void setDistributionType(String distributionType) {
        DistributionType = distributionType;
    }

    public int getFromHealthFacilityId() {
        return FromHealthFacilityId;
    }

    public void setFromHealthFacilityId(int fromHealthFacilityId) {
        FromHealthFacilityId = fromHealthFacilityId;
    }

    public int getToHealthFacilityId() {
        return ToHealthFacilityId;
    }

    public void setToHealthFacilityId(int toHealthFacilityId) {
        ToHealthFacilityId = toHealthFacilityId;
    }

    public int getItemId() {
        return ItemId;
    }

    public void setItemId(int itemId) {
        ItemId = itemId;
    }

    public int getLotId() {
        return LotId;
    }

    public void setLotId(int lotId) {
        LotId = lotId;
    }

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int productId) {
        ProductId = productId;
    }

    public int getProgramId() {
        return ProgramId;
    }

    public void setProgramId(int programId) {
        ProgramId = programId;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getVimsLotId() {
        return VimsLotId;
    }

    public void setVimsLotId(int vimsLotId) {
        VimsLotId = vimsLotId;
    }

    public String getVvmStatus() {
        return VvmStatus;
    }

    public void setVvmStatus(String vvmStatus) {
        VvmStatus = vvmStatus;
    }

    public HealthFacilityProofOfDelivery() {
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public int getStockDistributionId() {
        return StockDistributionId;
    }

    public void setStockDistributionId(int stockDistributionId) {
        StockDistributionId = stockDistributionId;
    }

    public int getDosesPerDispensingUnit() {
        return dosesPerDispensingUnit;
    }

    public void setDosesPerDispensingUnit(int dosesPerDispensingUnit) {
        this.dosesPerDispensingUnit = dosesPerDispensingUnit;
    }
}
