package mobile.tiis.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import mobile.tiis.app.R;
import mobile.tiis.app.entity.ImmunizationCardItem;

/**
 * Created by issymac on 10/03/16.
 */
public class ImmunizationCardAdapter extends BaseAdapter {

    private ArrayList<ImmunizationCardItem> items;

    private Context context;

    public ImmunizationCardAdapter(Context ctx, ArrayList<ImmunizationCardItem> listItems){
        this.items      = listItems;
        this.context    = ctx;
    }

    static class ViewHolder{
        TextView vaccineDoses, vaccineDate, healthFacility, vaccineLot, reasons;
        CheckBox done;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        ViewHolder viewHolder = new ViewHolder();
        ImmunizationCardItem item = items.get(i);

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(rowView == null){
            rowView = vi.inflate(R.layout.immunization_card_list_item, null);

            viewHolder.vaccineDoses = (TextView) rowView.findViewById(R.id.imm_card_vacc_dose_txt);
            viewHolder.vaccineLot   = (TextView) rowView.findViewById(R.id.imm_card_vacc_lot_txt);
            viewHolder.healthFacility = (TextView) rowView.findViewById(R.id.imm_card_health_facilty_txt);
            viewHolder.vaccineDate  = (TextView) rowView.findViewById(R.id.imm_card_vacc_date_txt);
            viewHolder.done         = (CheckBox) rowView.findViewById(R.id.imm_card_done_chk);
            viewHolder.done         .setEnabled(false);
            viewHolder.reasons      = (TextView) rowView.findViewById(R.id.imm_card_non_vac_reason_txt);

            rowView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.vaccineDoses.setText(item.getVacineDose());
        viewHolder.vaccineLot.setText(item.getVaccineLot());
        viewHolder.healthFacility.setText(item.getHealthCenterName());
        viewHolder.reasons.setText(item.getNonVaccinaitonReason());
        viewHolder.done.setChecked(item.isDone());
        viewHolder.done.setButtonDrawable(R.drawable.checkbox);
        if (item.getVaccinationDate() != null){
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            viewHolder.vaccineDate.setText(format.format(item.getVaccinationDate()));
        }

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
        return Integer.parseInt(items.get(i).getAppointementId());
    }
}
