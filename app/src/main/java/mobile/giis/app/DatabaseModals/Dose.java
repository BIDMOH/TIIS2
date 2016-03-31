package mobile.giis.app.DatabaseModals;

/**
 * Created by Ilakoze on 5/21/2015.
 */
public class Dose extends Modal {
    private int id;
    private String fullname;
    private String dose_number;
    private String scheduled_vaccination_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDose_number() {
        return dose_number;
    }

    public void setDose_number(String dose_number) {
        this.dose_number = dose_number;
    }

    public String getScheduled_vaccination_id() {
        return scheduled_vaccination_id;
    }

    public void setScheduled_vaccination_id(String scheduled_vaccination_id) {
        this.scheduled_vaccination_id = scheduled_vaccination_id;
    }
}
