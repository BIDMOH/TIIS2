package mobile.tiis.appV2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mobile.tiis.appV2.R;
import mobile.tiis.appV2.entity.NewChartDataTable;

/**
 * Created by issymac on 10/02/16.
 */
public class ImmunizationsListAdapter extends BaseAdapter {

    public Context context;

    LayoutInflater inflator;

    List<String> items;

    List<NewChartDataTable> newChartDataTables;

    public ImmunizationsListAdapter(Context context, List<String> items, List<NewChartDataTable> newItems) {
        this.items      = items;
        this.newChartDataTables = newItems;
        inflator        = (LayoutInflater) context
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        List<String> children = new ArrayList<>();
        List<String> childreValues = new ArrayList<>();

        for (NewChartDataTable a : newChartDataTables){
            if(a.getLabel().equals(items.get(position))){
                children.add(a.getDosenumber());
                childreValues.add(a.getValue()+"");
            }
        }

        convertView = inflator.inflate(R.layout.immunizations_list_item, null);

        TextView antigen    = (TextView) convertView.findViewById(R.id.txt_antigen);
        TextView model0     = (TextView) convertView.findViewById(R.id.model_0);
        TextView model1     = (TextView) convertView.findViewById(R.id.model_1);
        TextView model2     = (TextView) convertView.findViewById(R.id.model_2);
        TextView model3     = (TextView) convertView.findViewById(R.id.model_3);
        TextView number     = (TextView) convertView.findViewById(R.id.txt_number);

        antigen.setText(items.get(position));
        int totalCounts = 0;
        for (int i=0; i<children.size(); i++){
            int a = Integer.parseInt(childreValues.get(i));
            totalCounts = totalCounts + a;
            if(!(items.get(position).equals("BCG"))){
                switch (children.get(i)){
                    case "0":
                        model0.setText(childreValues.get(i));
                        break;
                    case "1":
                        model1.setText(childreValues.get(i));
                        break;
                    case "2":
                        model2.setText(childreValues.get(i));
                        break;
                    case "3":
                        model3.setText(childreValues.get(i));
                        break;
                }
            }
            else{
                model0.setText(childreValues.get(i));
            }
        }
        number.setText(totalCounts+"");

        return convertView;
    }

}
