package mobile.tiis.staging.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.staging.R;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.entity.NonVaccinationReason;
import mobile.tiis.staging.fragments.AdministerVaccineOfflineFragment.RowCollector;

/**
 * Created by issymac on 14/03/16.
 */
public class vaccinateOfflineListAdapter extends BaseAdapter {

    Context context;

    final SimpleDateFormat ft;

    private BackboneApplication application;
    private DatabaseHandler database;
    public ViewHolder viewHolder;

    List<String> reasons;
    private ArrayList<RowCollector> rowCollectors;

    public vaccinateOfflineListAdapter(Context ctx, ArrayList<RowCollector> rowCollectorsArraylist){
        context         = ctx;
        ft              = new SimpleDateFormat("dd-MMM-yyyy");
        rowCollectors   = rowCollectorsArraylist;
    }

    static class ViewHolder{
        TextView vaccines, vaccineDate;
        CheckBox done;
        MaterialSpinner vaccineLot, reasons;
        RowCollector rowCollector;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = null;
        final RowCollector rowCollector = rowCollectors.get(i);
        viewHolder = new ViewHolder();

        if(view == null){
            LayoutInflater vi = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = vi.inflate(R.layout.vaccine_offline_list_item, null);
            viewHolder.rowCollector =  rowCollector;
            viewHolder.vaccines     = (TextView) rowView.findViewById(R.id.vaccine_name);
            viewHolder.vaccineLot   = (MaterialSpinner) rowView.findViewById(R.id.vaccine_lot_spinner);
            viewHolder.vaccineDate  = (TextView) rowView.findViewById(R.id.vaccination_date);
            viewHolder.done         = (CheckBox) rowView.findViewById(R.id.done_checkbox);
            viewHolder.reasons      = (MaterialSpinner) rowView.findViewById(R.id.reason_spinner);
            SingleTextViewAdapter statusAdapter = new SingleTextViewAdapter(context, R.layout.single_text_spinner_item_drop_down, viewHolder.rowCollector.getVaccine_lot_names_list());
            final SingleTextViewAdapter statusAdapterNonVaccinationReason = new SingleTextViewAdapter(context, R.layout.single_text_spinner_item_drop_down, viewHolder.rowCollector.getNon_vac_reason_list());
            Date dNow = new Date();
            viewHolder.rowCollector.setVaccination_date(dNow);

            viewHolder.rowCollector.setVaccination_done_status("false");
            viewHolder.done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    viewHolder.rowCollector.setVaccination_done_status(String.valueOf(b));
                    if (!b) {
                        viewHolder.reasons.setVisibility(View.VISIBLE);
                        //VaccineLotColumn.setSelection(0);
                        viewHolder.rowCollector.setNonvaccination_reason_position(0);

                    }
                    if (b) {
                        viewHolder.reasons.setVisibility(View.GONE);
                        viewHolder.rowCollector.setNon_vac_reason("-1");
                    }
                }
            });

            /**
             * Setting the reasons in the spinners begins here
             */
            viewHolder.reasons.setAdapter(statusAdapterNonVaccinationReason);
            viewHolder.rowCollector.setNonvaccination_reason_position(0);
            viewHolder.rowCollector.setNon_vac_reason("0");
            //viewHolder.reasons.setSelection(viewHolder.rowCollector.getNonvaccination_reason_position());

            viewHolder.reasons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (position != -1){

                        viewHolder.rowCollector.setNonvaccination_reason_position(position);
                        for (NonVaccinationReason a : viewHolder.rowCollector.getNon_vaccination_reason_list_with_additions()) {
                            if (statusAdapterNonVaccinationReason.getItem(position).equalsIgnoreCase(a.getName())) {
                                viewHolder.rowCollector.setNon_vac_reason(a.getId());
                            }
                        }

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //no changes
                }

            });
            /**
             * Setting the reasons in the spinners ENDS here
             */

            viewHolder.vaccineLot.setAdapter(statusAdapter);
            if (viewHolder.rowCollector.getVaccine_lot_names_list().size() > 2) {
                viewHolder.rowCollector.setVaccine_lot_current_position(2);
                //viewHolder.vaccineLot.setSelection(2);
            } else {
                //viewHolder.vaccineLot.setSelection(1);
                viewHolder.rowCollector.setVaccine_lot_current_position(1);
            }
            viewHolder.vaccineLot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    //viewHolder.vaccineLot.setSelection(position);
                    viewHolder.rowCollector.setVaccine_lot_current_position(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //no changes
                }

            });

            rowView.setTag(viewHolder);
        }else {
            rowView = view;
        }

        viewHolder = (ViewHolder) rowView.getTag();
        viewHolder.vaccines.setText(viewHolder.rowCollector.getScheduled_vaccination_name());
        viewHolder.done.setButtonDrawable(R.drawable.checkbox);
        viewHolder.vaccineLot.setSelection(viewHolder.rowCollector.getVaccine_lot_current_position());
        viewHolder.reasons.setSelection(viewHolder.rowCollector.getNonvaccination_reason_position());
        viewHolder.vaccineDate.setText(ft.format(viewHolder.rowCollector.getVaccination_date()));

        return rowView;
    }

    @Override
    public int getCount() {
        return rowCollectors.size();
    }

    @Override
    public Object getItem(int i) {
        return rowCollectors.get(i);
    }

    @Override
    public long getItemId(int i) {
        long val = 0;

        try {
            val = Integer.parseInt(rowCollectors.get(i).getScheduled_vaccination_item_id());
        }catch (Exception e){
            e.printStackTrace();
        }

        return val;
    }
}
