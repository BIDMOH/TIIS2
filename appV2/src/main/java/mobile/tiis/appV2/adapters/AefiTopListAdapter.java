package mobile.tiis.appV2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.tiis.appV2.R;
import mobile.tiis.appV2.entity.AefiListItem;

/**
 * Created by issymac on 09/03/16.
 */
public class AefiTopListAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<AefiListItem> items;

    public AefiTopListAdapter(Context ctx, ArrayList<AefiListItem> lastAppointementAefiList){
        this.context = ctx;
        this.items = lastAppointementAefiList;
    }

    static class ViewHolder{
        TextView vaccineDoses, vaccineDate, healthFacility;
        CheckBox done;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        ViewHolder viewHolder = new ViewHolder();
        AefiListItem item = items.get(i);
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(rowView == null){
            rowView = vi.inflate(R.layout.aefi_list_one_item, null);

            viewHolder.vaccineDoses = (TextView) rowView.findViewById(R.id.top_vaccine_dose);
            viewHolder.vaccineDate = (TextView) rowView.findViewById(R.id.top_vaccine_date);
            viewHolder.healthFacility = (TextView) rowView.findViewById(R.id.top_health_facility);
            viewHolder.done         = (CheckBox) rowView.findViewById(R.id.top_done_checkbox);

            rowView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.vaccineDoses.setText(item.getVaccines());

        return rowView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
