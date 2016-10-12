package mobile.tiis.appv2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobile.tiis.appv2.HomeActivityRevised;
import mobile.tiis.appv2.R;
import mobile.tiis.appv2.base.BackboneActivity;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.database.DatabaseHandler;
import mobile.tiis.appv2.util.ViewAppointmentRow;

/**
 * Created by issymac on 16/12/15.
 */
public class VaccinationQueueListAdapter extends BaseAdapter {

    public Context context;

    LayoutInflater inflator;

    ArrayList<ViewAppointmentRow> objects;

    BackboneApplication app;


    public VaccinationQueueListAdapter(Context context, ArrayList<ViewAppointmentRow> names) {
        this.objects = names;
        this.context = context;
        inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflator.inflate(R.layout.vacination_queue_list_item, null);

        TextView name       = (TextView) convertView.findViewById(R.id.vacc_txt_child_names);
        name.setTypeface(BackboneActivity.Rosario_Regular);
        TextView vaccine    = (TextView) convertView.findViewById(R.id.vaccine);
        vaccine.setTypeface(BackboneActivity.Rosario_Regular);
        TextView age        = (TextView) convertView.findViewById(R.id.age);
        age.setTypeface(BackboneActivity.Rosario_Regular);
        TextView date       = (TextView) convertView.findViewById(R.id.date);
        date.setTypeface(BackboneActivity.Rosario_Regular);

        HomeActivityRevised activity = (HomeActivityRevised) context;
        app = (BackboneApplication) activity.getApplication();
        DatabaseHandler db = app.getDatabaseInstance();
        Cursor naming = null;
        naming = db.getReadableDatabase().rawQuery("SELECT FIRSTNAME1 , LASTNAME1,FIRSTNAME2 FROM child WHERE ID=?", new String[]{objects.get(position).getChild_id()});
        if (naming != null) {
            if (naming.moveToFirst()) {
                name.setText(naming.getString(naming.getColumnIndex("FIRSTNAME1")) + " " + naming.getString(naming.getColumnIndex("FIRSTNAME2")) + " " + naming.getString(naming.getColumnIndex("LASTNAME1")));
            }
        }

        vaccine.setText(objects.get(position).getVaccine_dose());
        age.setText(objects.get(position).getSchedule());

        Date scheduled_date = BackboneActivity.dateParser(objects.get(position).getScheduled_date());
        SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        date.setText(ft.format(scheduled_date));


//        TextView childNames = (TextView) convertView.findViewById(R.id.vacc_txt_child_names);
//        childNames.setText(objects.get(position).getChild_id());

        return convertView;
    }

    public void updateReceiptsList(List<ViewAppointmentRow> newlist) {
        objects.clear();
        objects.addAll(newlist);
        this.notifyDataSetChanged();
    }

}