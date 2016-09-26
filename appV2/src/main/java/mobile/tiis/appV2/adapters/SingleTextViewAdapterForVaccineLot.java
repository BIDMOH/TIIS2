package mobile.tiis.appV2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mobile.tiis.appV2.R;

/**
 * Created by issy on 7/25/16.
 */

public class SingleTextViewAdapterForVaccineLot extends ArrayAdapter<String> {
    List<String> items;
    Context act;

    public SingleTextViewAdapterForVaccineLot(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.items = items;
        act = context;
    }


    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {

        View rowView = convertView;
        LayoutInflater vi = (LayoutInflater) act.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = vi.inflate(R.layout.single_text_spinner_item_drop_down, null);

        TextView tvTitle =(TextView)rowView.findViewById(R.id.rowtext);
        tvTitle.setPadding(15, 10, 10, 5);
        tvTitle.setText(items.get(position));

        return rowView;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        LayoutInflater vi = (LayoutInflater) act.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = vi.inflate(R.layout.single_text_spinner_view_item, null);

        TextView tvTitle = (TextView)rowView.findViewById(R.id.rowtext);
        tvTitle.setText(items.get(position));

        return rowView;

    }
}