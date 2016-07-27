package mobile.tiis.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mobile.tiis.app.R;

/**
 * Created by issy on 7/25/16.
 */
public class SingleTextViewAdapterForVaccineLot extends ArrayAdapter<String> {
    List<String> items;
    List<String> lotBalance;
    Context act;

    public SingleTextViewAdapterForVaccineLot(Context context, int resource, List<String> items, List<String> lot_balance) {
        super(context, resource, items);
        this.items = items;
        this.lotBalance = lot_balance;
        act = context;
    }


    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        View view = new View(act, null);
        int balance = 0;

        try {
            balance = Integer.parseInt(lotBalance.get(position));
        }catch (Exception e){
            e.printStackTrace();
        }

        if (balance > 0){
            View rowView = convertView;
            LayoutInflater vi = (LayoutInflater) act.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = vi.inflate(R.layout.single_text_spinner_item_drop_down, null);

            TextView tvTitle =(TextView)rowView.findViewById(R.id.rowtext);
            tvTitle.setPadding(15, 10, 10, 5);
            tvTitle.setText(items.get(position));


            return rowView;
        }else {
            return view;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        int balance = 0;
        View view = new View(act, null);

        try {
            balance = Integer.parseInt(lotBalance.get(position));
        }catch (Exception e){
            e.printStackTrace();
        }

        if (balance > 0){
            View rowView = convertView;
            LayoutInflater vi = (LayoutInflater) act.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = vi.inflate(R.layout.single_text_spinner_view_item, null);

            TextView tvTitle = (TextView)rowView.findViewById(R.id.rowtext);
            tvTitle.setText(items.get(position));



            return rowView;
        }else{
             return view;
        }

    }
}