package mobile.tiis.appV2.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mobile.tiis.appV2.R;
import mobile.tiis.appV2.base.BackboneActivity;
import mobile.tiis.appV2.entity.HealthFacilityBalance;
import mobile.tiis.appV2.util.Constants;

/**
 * Created by issymac on 09/02/16.
 */
public class HealthFacilityBalanceListAdapter extends BaseAdapter {

    public Context context;

    LayoutInflater inflator;

    String[] objects;

    private List<HealthFacilityBalance> items;

    public HealthFacilityBalanceListAdapter(Context context, List<HealthFacilityBalance> rowCollectorList) {
        this.items = rowCollectorList;
        inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        TextView name, lotNumber, expiryDate, balance;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HealthFacilityBalance item = items.get(position);

        ViewHolder viewHolder = new ViewHolder();
        View rowView;
        rowView = convertView;

        if (rowView == null){

            rowView = inflator.inflate(R.layout.health_facility_balance_list_item, null);
            viewHolder.name = (TextView) rowView.findViewById(R.id.item_name);
            viewHolder.name.setTypeface(BackboneActivity.Rosario_Regular);
            viewHolder.lotNumber = (TextView) rowView.findViewById(R.id.lot_number);
            viewHolder.lotNumber.setTypeface(BackboneActivity.Rosario_Regular);
            viewHolder.expiryDate = (TextView) rowView.findViewById(R.id.expiry_date);
            viewHolder.expiryDate.setTypeface(BackboneActivity.Rosario_Regular);
            viewHolder.balance = (TextView) rowView.findViewById(R.id.balance);
            viewHolder.balance.setTypeface(BackboneActivity.Rosario_Regular);

            rowView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.name.setText(item.getItem_name().toString());
        viewHolder.lotNumber.setText(item.getLot_number());
        SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        Log.d("Setting exp date", ft.format(BackboneActivity.dateParser(item.getExpire_date())));
        if(calculateDateDiffToExpiryRowColor(BackboneActivity.dateParser(item.getExpire_date()))){
            viewHolder.expiryDate.setBackgroundColor(Color.YELLOW);
        }
        viewHolder.expiryDate.setText(ft.format(BackboneActivity.dateParser(item.getExpire_date())));
        viewHolder.balance.setText(item.getBalance()+"");
        return rowView;
    }

    public boolean calculateDateDiffToExpiryRowColor(Date expiry) {
        Date now = new Date();
        long diff = getDaysDifference(now, expiry);
        return diff < Constants.LimitNumberOfDaysBeforeExpireVal;
    }

    public static final long  getDaysDifference(Date d1, Date d2){
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
    }

}
