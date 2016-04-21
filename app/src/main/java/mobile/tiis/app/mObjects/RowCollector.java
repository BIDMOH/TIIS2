package mobile.tiis.app.mObjects;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by issymac on 02/03/16.
 */
public class RowCollector {


    //Holding values from Scheduled Vaccination Table
    private String scheduled_vaccination_name;
    private String scheduled_vaccination_id;
    private String scheduled_vaccination_item_id;

    //Holding Values from Item Lot Table
    private Map vaccine_lot_id_name_map;
    private List<String> vaccine_lot_names_list;
    private int vaccine_lot_current_position;

    private Date vaccination_date;

    //Holds status Done / NotDone
    private String vaccination_done_status = "false";

    private int nonvaccination_reason_position;
    private String non_vac_reason = "";


    public RowCollector() {

    }

    public String getScheduled_vaccination_name() {
        return scheduled_vaccination_name;
    }

    public void setScheduled_vaccination_name(String scheduled_vaccination_name) {
        this.scheduled_vaccination_name = scheduled_vaccination_name;
    }

    public String getScheduled_vaccination_item_id() {
        return scheduled_vaccination_item_id;
    }

    public void setScheduled_vaccination_item_id(String scheduled_vaccination_item_id) {
        this.scheduled_vaccination_item_id = scheduled_vaccination_item_id;
    }

    public String getScheduled_vaccination_id() {
        return scheduled_vaccination_id;
    }

    public void setScheduled_vaccination_id(String scheduled_vaccination_id) {
        this.scheduled_vaccination_id = scheduled_vaccination_id;
    }


    public Map getVaccine_lot_id_name_map() {
        return vaccine_lot_id_name_map;
    }

    public void setVaccine_lot_id_name_map(Map vaccine_lot_id_name_map) {
        this.vaccine_lot_id_name_map = vaccine_lot_id_name_map;
    }

    public List<String> getVaccine_lot_names_list() {
        return vaccine_lot_names_list;
    }

    public void setVaccine_lot_names_list(List<String> vaccine_lot_names_list) {
        this.vaccine_lot_names_list = vaccine_lot_names_list;
    }

    public int getVaccine_lot_current_position() {
        return vaccine_lot_current_position;
    }

    public void setVaccine_lot_current_position(int vaccine_lot_current_position) {
        this.vaccine_lot_current_position = vaccine_lot_current_position;
    }


    public Date getVaccination_date() {
        return vaccination_date;
    }

    public void setVaccination_date(Date vaccination_date) {
        this.vaccination_date = vaccination_date;
    }


    public String getVaccination_done_status() {
        return vaccination_done_status;
    }

    public void setVaccination_done_status(String vaccination_done_status) {
        this.vaccination_done_status = vaccination_done_status;
    }

    public int getNonvaccination_reason_position() {
        return nonvaccination_reason_position;
    }

    public void setNonvaccination_reason_position(int nonvaccination_reason_position) {
        this.nonvaccination_reason_position = nonvaccination_reason_position;
    }


    public String getNon_vac_reason() {
        return non_vac_reason;
    }

    public void setNon_vac_reason(String non_vac_reason) {
        this.non_vac_reason = non_vac_reason;
    }

}
