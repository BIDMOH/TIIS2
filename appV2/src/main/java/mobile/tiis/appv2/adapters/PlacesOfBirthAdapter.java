package mobile.tiis.appv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mobile.tiis.appv2.R;

/**
 * Created by issymac on 14/12/15.
 */
public class PlacesOfBirthAdapter extends ArrayAdapter<String> {
    List<String> items;
    Context act;

    public PlacesOfBirthAdapter(Context context, int resource, List<String> items) {
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