package mobile.tiis.staging.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mobile.tiis.staging.R;

/**
 * Created by issy on 11/28/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mobile.tiis.staging.R;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.entity.MonthYearPair;

public class ReportPeriodSpinnerAdapter extends ArrayAdapter<MonthYearPair>{

    List<MonthYearPair> items;
    Context act;
    BackboneApplication application;

    public ReportPeriodSpinnerAdapter(Context context, int resource, List<MonthYearPair> items) {
        super(context, resource, items);
        this.items = items;
        act = context;
        application = (BackboneApplication) act.getApplicationContext();
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        LayoutInflater vi = (LayoutInflater) act.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = vi.inflate(R.layout.single_text_spinner_item_drop_down, null);

        TextView tvTitle =(TextView)rowView.findViewById(R.id.rowtext);
        tvTitle.setText(application.getDatabaseInstance().getMonthNameFromNumber(items.get(position).getMonthyear().first+"", application)+ " "+items.get(position).getMonthyear().second);


        return rowView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        LayoutInflater vi = (LayoutInflater) act.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = vi.inflate(R.layout.single_text_spinner_view_item, null);

        TextView tvTitle = (TextView)rowView.findViewById(R.id.rowtext);
        tvTitle.setText(application.getDatabaseInstance().getMonthNameFromNumber(items.get(position).getMonthyear().first+"", application)+ " "+items.get(position).getMonthyear().second);

        return rowView;

    }

}