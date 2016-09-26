package mobile.tiis.staging.DatabaseModals;

/**
 * Created by coze on 3/31/16.
 */
public class ViewRow {
    private Dose dose;
    private int withinCatchmentMale;
    private int withinCatchmentFemale;
    private int outsideCatchmentMale;
    private int outsideCatchmentFemale;
    private int expectedCatchmentTotal;
    private int cummulativeTotal;

    public Dose getDose() {
        return dose;
    }

    public void setDose(Dose dose) {
        this.dose = dose;
    }

    public int getWithinCatchmentMale() {
        return withinCatchmentMale;
    }

    public void setWithinCatchmentMale(int withinCatchmentMale) {
        this.withinCatchmentMale = withinCatchmentMale;
    }

    public int getWithinCatchmentFemale() {
        return withinCatchmentFemale;
    }

    public void setWithinCatchmentFemale(int withinCatchmentFemale) {
        this.withinCatchmentFemale = withinCatchmentFemale;
    }

    public int getOutsideCatchmentMale() {
        return outsideCatchmentMale;
    }

    public void setOutsideCatchmentMale(int outsideCatchmentMale) {
        this.outsideCatchmentMale = outsideCatchmentMale;
    }

    public int getOutsideCatchmentFemale() {
        return outsideCatchmentFemale;
    }

    public void setOutsideCatchmentFemale(int outsideCatchmentFemale) {
        this.outsideCatchmentFemale = outsideCatchmentFemale;
    }

    public int getExpectedCatchmentTotal() {
        return expectedCatchmentTotal;
    }

    public void setExpectedCatchmentTotal(int expectedCatchmentTotal) {
        this.expectedCatchmentTotal = expectedCatchmentTotal;
    }

    public int getCummulativeTotal() {
        return cummulativeTotal;
    }

    public void setCummulativeTotal(int cummulativeTotal) {
        this.cummulativeTotal = cummulativeTotal;
    }
}
