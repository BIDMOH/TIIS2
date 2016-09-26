package mobile.tiis.appV2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by rbasha on 11/10/15.
 */
public class ChildCollector2 {


    private List<Child> childList;
    private List<VaccinationAppointment> vaList;
    private List<VaccinationEvent> veList;

    @JsonProperty("childList")
    public List<Child> getChildList() {
        return childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }

    @JsonProperty("veList")
    public List<VaccinationEvent> getVeList() {
        return veList;
    }

    public void setVeList(List<VaccinationEvent> veList) {
        this.veList = veList;
    }

    @JsonProperty("vaList")
    public List<VaccinationAppointment> getVaList() {
        return vaList;
    }

    public void setVaList(List<VaccinationAppointment> vaList) {
        this.vaList = vaList;
    }
}
