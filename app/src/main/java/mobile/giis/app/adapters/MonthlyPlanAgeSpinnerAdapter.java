package mobile.giis.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mobile.giis.app.R;

/**
 * Created by issymac on 16/12/15.
 */
public class MonthlyPlanAgeSpinnerAdapter extends ArrayAdapter<String>{

    List<String> items;
    Context act;

    public MonthlyPlanAgeSpinnerAdapter(Context context, int resource, List<String> items) {
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        LayoutInflater vi = (LayoutInflater) act.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = vi.inflate(R.layout.single_text_spinner_view_item, null);

        TextView tvTitle = (TextView)rowView.findViewById(R.id.rowtext);
        tvTitle.setText(items.get(position));



        return rowView;

    }

}
