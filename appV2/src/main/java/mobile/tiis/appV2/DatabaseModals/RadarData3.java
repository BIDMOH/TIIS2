package mobile.tiis.appV2.DatabaseModals;


import io.realm.RealmObject;

/**
 * Demo class that encapsulates data stored in realm.io database.
 * This class represents data suitable for all chart-types.
 */
public class RadarData3 extends RealmObject {

    private float value;

    private int xIndex;

    private String xValue;


    public RadarData3() {

    }

    public RadarData3(float value, int xIndex, String xValue) {
        this.value = value;
        this.xIndex = xIndex;
        this.xValue = xValue;
    }


    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getxIndex() {
        return xIndex;
    }

    public void setxIndex(int xIndex) {
        this.xIndex = xIndex;
    }

    public String getxValue() {
        return xValue;
    }

    public void setxValue(String xValue) {
        this.xValue = xValue;
    }
}