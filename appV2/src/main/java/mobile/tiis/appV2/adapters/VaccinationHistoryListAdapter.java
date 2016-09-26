package mobile.tiis.appV2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mobile.tiis.appV2.R;
import mobile.tiis.appV2.base.BackboneActivity;
import mobile.tiis.appV2.base.BackboneApplication;
import mobile.tiis.appV2.database.DatabaseHandler;
import mobile.tiis.appV2.util.ViewAppointmentRow;

/**
 * Created by issymac on 26/01/16.
 */
public class VaccinationHistoryListAdapter extends BaseAdapter {

    public Context context;

    LayoutInflater inflator;

    private String[] objects;

    private ArrayList<ViewAppointmentRow> var;

    private BackboneApplication app;


    public VaccinationHistoryListAdapter(Context context, ArrayList<ViewAppointmentRow> var, BackboneApplication application) {
        app = application;
        inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.var = var;
        this.context = context;
    }

    @Override
    public int getCount() {
        return var.size();
    }

    @Override
    public Object getItem(int position) {
        return var.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflator.inflate(R.layout.vaccination_history_item, null);

            //convertView.setTag(viewHolder);

        } else {

        }

        TextView antigen = (TextView) convertView.findViewById(R.id.antigen_value);
        TextView week0 = (TextView) convertView.findViewById(R.id.week_o_value);
        TextView week2 = (TextView) convertView.findViewById(R.id.week_2_value);
        TextView month1 = (TextView) convertView.findViewById(R.id.month_1_value);
        TextView week6 = (TextView) convertView.findViewById(R.id.week_6_value);
        TextView week10 = (TextView) convertView.findViewById(R.id.week_10_value);
        TextView week14 = (TextView) convertView.findViewById(R.id.week_14_value);
        TextView month9 = (TextView) convertView.findViewById(R.id.month_9_value);
        TextView month18= (TextView) convertView.findViewById(R.id.month_18_value);
        TextView month21= (TextView) convertView.findViewById(R.id.month_21_value);


//        getSchedule
        antigen.setText(var.get(position).getVaccine_dose());

        Date scheduled_date = BackboneActivity.dateParser(var.get(position).getScheduled_date());
        SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");

        DatabaseHandler mydb = app.getDatabaseInstance();

        switch (var.get(position).getSchedule()){
            case "At birth":
                week0.setText(ft.format(scheduled_date));
                break;
            case "2 weeks":
                week2.setText(ft.format(scheduled_date));
                break;
            case "1 Month":
                month1.setText(ft.format(scheduled_date));
                break;
            case "6 weeks":
                week6.setText(ft.format(scheduled_date));
                break;
            case "10 weeks":
                week10.setText(ft.format(scheduled_date));
                break;
            case "14 weeks":
                week14.setText(ft.format(scheduled_date));
                break;
            case "9 Months":
                month9.setText(ft.format(scheduled_date));
                break;
            case "18 Months":
                month18.setText(ft.format(scheduled_date));
                break;
            case "21 Months":
                month21.setText(ft.format(scheduled_date));
                break;
        }

        return convertView;
    }

}
