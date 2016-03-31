package mobile.giis.app.DatabaseModals;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by coze on 3/31/16.
 */
public class ViewChartData {
    private Scheduled_Vaccination scheduled_vaccination;
    private List<ViewRow> viewRows = new ArrayList<>();

    public Scheduled_Vaccination getScheduled_vaccination() {
        return scheduled_vaccination;
    }

    public void setScheduled_vaccination(Scheduled_Vaccination scheduled_vaccination) {
        this.scheduled_vaccination = scheduled_vaccination;
    }

    public List<ViewRow> getViewRows() {
        return viewRows;
    }

    public void setViewRows(List<ViewRow> viewRows) {
        this.viewRows = viewRows;
    }
}
