package mobile.tiis.appv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import mobile.tiis.appv2.R;
import mobile.tiis.appv2.entity.AefiListItem;

/**
 * Created by issymac on 09/03/16.
 */
public class AefiBottomListAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<AefiListItem> aefiItems;

    final SimpleDateFormat ft;

    public AefiBottomListAdapter(Context ctx, ArrayList<AefiListItem> aefiItems){
        this.context    = ctx;
        this.aefiItems  = aefiItems;
        ft = new SimpleDateFormat("dd-MMM-yyyy");
    }

    static class ViewHolder{
        TextView vaccineDoses, vaccineDate, healthFacility, AEFIDates, notes;
        CheckBox done,AEFI;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        ViewHolder viewHolder = new ViewHolder();
        AefiListItem item = aefiItems.get(i);

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(rowView == null){
            rowView = vi.inflate(R.layout.aefi_list_two_item, null);
            viewHolder.vaccineDoses = (TextView) rowView.findViewById(R.id.vacc_dose);
            viewHolder.vaccineDate = (TextView) rowView.findViewById(R.id.vaccine_date);
            viewHolder.healthFacility = (TextView) rowView.findViewById(R.id.health_facility);
            viewHolder.done         = (CheckBox) rowView.findViewById(R.id.done_checkbox);
            viewHolder.AEFI         = (CheckBox) rowView.findViewById(R.id.aefi_box);
            viewHolder.AEFIDates    = (TextView) rowView.findViewById(R.id.aefi_dates);
            viewHolder.notes        = (TextView) rowView.findViewById(R.id.notes);

            rowView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.vaccineDoses.setText(item.getVaccines());
//        viewHolder.vaccineDate.setText(ft.format(item.getVaccinationDate()));
        viewHolder.healthFacility.setText(item.getHealthFacilityName());

        viewHolder.done.setChecked(item.isDone());

        viewHolder.AEFI.setChecked(item.isAefi());
//        viewHolder.AEFIDates.setText(ft.format(item.getAefiDate()));
        viewHolder.notes.setText(item.getNotes());

        return rowView;
    }

    @Override
    public int getCount() {
        return aefiItems.size();
    }

    @Override
    public Object getItem(int i) {
        return aefiItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

}
