package mobile.tiis.app.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.app.R;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.entity.HealthFacilityBalance;
import mobile.tiis.app.fragments.StockAdjustmentFragment;

/**
 * Created by issymac on 09/02/16.
 */
public class StockAdjustmentListAdapter extends BaseAdapter {

    public Context context;

    LayoutInflater inflator;

    private List<HealthFacilityBalance> rowCollectorList;

    private List<String> reasons;

    ViewHolder viewHolder;

    public StockAdjustmentListAdapter(Context context,  List<HealthFacilityBalance> items , List<String> adjustmentReasons) {
        this.rowCollectorList   = items;
        this.reasons            = adjustmentReasons;
        this.context            = context;
        inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder{
        TextView tvItem, tvLotNumber, tvBalance;
        MaterialEditText metQuantity;
        MaterialSpinner reason;
    }

    @Override
    public int getCount() {
        return rowCollectorList.size();
    }

    @Override
    public Object getItem(int position) {
        return rowCollectorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final HealthFacilityBalance item = rowCollectorList.get(position);
        viewHolder = new ViewHolder();
        View rowView = convertView;
        if(rowView == null){
            rowView = inflator.inflate(R.layout.stock_adjustment_list_item, null);
            viewHolder.tvItem = (TextView) rowView.findViewById(R.id.item_name);
            viewHolder.tvItem.setTypeface(BackboneActivity.Rosario_Regular);
            viewHolder.tvLotNumber = (TextView) rowView.findViewById(R.id.lot_number);
            viewHolder.tvLotNumber.setTypeface(BackboneActivity.Rosario_Regular);
            viewHolder.tvBalance = (TextView) rowView.findViewById(R.id.balance);
            viewHolder.tvBalance.setTypeface(BackboneActivity.Rosario_Regular);
            viewHolder.metQuantity = (MaterialEditText) rowView.findViewById(R.id.met_quantity);
            viewHolder.reason = (MaterialSpinner)rowView.findViewById(R.id.reason_spinner);
            viewHolder.reason.setTag(-1);

            rowView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) rowView.getTag();
            
        }

        viewHolder.tvItem.setText(item.getItem_name());
        viewHolder.tvLotNumber.setText(item.getLot_number());
        viewHolder.tvBalance.setText(item.getBalance() + "");
        SingleTextViewAdapter spAdjustmentReasons = new SingleTextViewAdapter(context, R.layout.single_text_spinner_item_drop_down, reasons);
        viewHolder.reason.setAdapter(spAdjustmentReasons);
        viewHolder.reason.setSelection(Integer.parseInt(viewHolder.reason.getTag().toString()));

        viewHolder.metQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                item.setTempBalance(etQuantity.getText().toString());
                StockAdjustmentFragment.rowCollectorList.get(position).setTempBalance(viewHolder.metQuantity.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewHolder.reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
//                item.setSelectedAdjustmentReasonPosition(i);
                viewHolder.reason.setTag(i);
                StockAdjustmentFragment.rowCollectorList.get(position).setSelectedAdjustmentReasonPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rowView;
    }

}
