package mobile.tiis.appV2.entity;

/**
 * Created by issymac on 15/03/16.
 */
public class NewChartDataTable {

    private String label;

    private String name;

    private String dosenumber;

    private int value;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosenumber() {
        return dosenumber;
    }

    public void setDosenumber(String dosenumber) {
        this.dosenumber = dosenumber;
    }
}
