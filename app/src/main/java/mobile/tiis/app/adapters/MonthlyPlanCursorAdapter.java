package mobile.tiis.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import mobile.tiis.app.HomeActivityRevised;
import mobile.tiis.app.R;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;

/**
 * Created by issymac on 10/04/16.
 */
public class MonthlyPlanCursorAdapter extends CursorAdapter {

    public MonthlyPlanCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.vacination_queue_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        View convertView = view;

        TextView name       = (TextView) convertView.findViewById(R.id.vacc_txt_child_names);
        name.setTypeface(BackboneActivity.Rosario_Regular);
        TextView vaccine    = (TextView) convertView.findViewById(R.id.vaccine);
        vaccine.setTypeface(BackboneActivity.Rosario_Regular);
        TextView age        = (TextView) convertView.findViewById(R.id.age);
        age.setTypeface(BackboneActivity.Rosario_Regular);
        TextView date       = (TextView) convertView.findViewById(R.id.date);
        date.setTypeface(BackboneActivity.Rosario_Regular);

        HomeActivityRevised activity = (HomeActivityRevised) context;
        BackboneApplication app;
        app = (BackboneApplication) activity.getApplication();
        DatabaseHandler db = app.getDatabaseInstance();
        Cursor naming = null;
        naming = db.getReadableDatabase().rawQuery("SELECT FIRSTNAME1 , LASTNAME1,FIRSTNAME2 FROM child WHERE ID=?", new String[]{cursor.getString(cursor.getColumnIndex("CHILD_ID"))});
        if (naming != null) {
            if (naming.moveToFirst()) {
                name.setText(naming.getString(naming.getColumnIndex("FIRSTNAME1")) + " " + naming.getString(naming.getColumnIndex("FIRSTNAME2")) + " " + naming.getString(naming.getColumnIndex("LASTNAME1")));
            }
            naming.close();
        }

        vaccine.setText(cursor.getString(cursor.getColumnIndex("VACCINES")));
        age.setText(cursor.getString(cursor.getColumnIndex("SCHEDULE")));

        Date scheduled_date = BackboneActivity.dateParser(cursor.getString(cursor.getColumnIndex("SCHEDULED_DATE")));
        SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        date.setText(ft.format(scheduled_date));
    }

}
