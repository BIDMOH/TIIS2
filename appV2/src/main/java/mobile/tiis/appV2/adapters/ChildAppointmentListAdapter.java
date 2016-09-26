package mobile.tiis.appV2.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mobile.tiis.appV2.R;
import mobile.tiis.appV2.base.BackboneActivity;
import mobile.tiis.appV2.util.ViewAppointmentRow;

/**
 * Created by issymac on 03/03/16.
 */
public class ChildAppointmentListAdapter extends ArrayAdapter<ViewAppointmentRow> {

    private Context context;

    private List<ViewAppointmentRow> var;

    private LayoutInflater inflator;

    public ChildAppointmentListAdapter(Context context, int resource, List<ViewAppointmentRow> rows){
        super(context, resource, rows);
        this.context        = context;
        this.var            = rows;
        this.inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public String getAppointmentid(int position){
        return var.get(position).getAppointment_id();
    }

    @Override
    public int getCount() {
        return var.size();
    }

    @Override
    public ViewAppointmentRow getItem(int i) {
        return var.get(i);
    }

    @Override
    public long getItemId(int i) {
        long results = 0;
        try {
            results = Integer.parseInt(var.get(i).getAppointment_id());
        }catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }

    static class ViewHolder {
        public TextView tvVaccine, tvSchedule, getTvScheduleDate;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View convertView;
        convertView = view;

        final ViewAppointmentRow a = var.get(position);

        View rowView = convertView;
        final ViewHolder viewHolder;

        Log.d("ViewAppointment", "Adapter Row : " + position + "");

        if (rowView == null) {

            rowView = inflator.inflate(R.layout.appointment_list_item, null);

            viewHolder = new ViewHolder();
            viewHolder.tvVaccine            = (TextView) rowView.findViewById(R.id.appointment_list_item_vaccine);
            viewHolder.tvSchedule           = (TextView) rowView.findViewById(R.id.appointment_list_item_schedule);
            viewHolder.getTvScheduleDate    = (TextView) rowView.findViewById(R.id.appointment_list_item_schedule_date);

            rowView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.tvVaccine.setText(a.getVaccine_dose());
        viewHolder.tvSchedule.setText(a.getSchedule());

        Date scheduled_date = BackboneActivity.dateParser(a.getScheduled_date());
        SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");

        viewHolder.getTvScheduleDate.setText(ft.format(scheduled_date));

        return rowView;
    }

}
