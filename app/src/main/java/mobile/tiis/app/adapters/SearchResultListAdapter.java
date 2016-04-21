package mobile.tiis.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import mobile.tiis.app.R;

/**
 *  Created by issymac on 15/12/15.
 */
public class SearchResultListAdapter extends BaseAdapter {

    public Context context;

    LayoutInflater inflator;

    String[] objects;


    public SearchResultListAdapter(Context context, String names[]) {
        this.objects = names;
        inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.length;
    }

    @Override
    public Object getItem(int position) {
        return objects[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            convertView = inflator.inflate(R.layout.children_list_item, null);

        TextView childNames = (TextView) convertView.findViewById(R.id.txt_child_names);
        TextView snNumber = (TextView) convertView.findViewById(R.id.sn_number);
        childNames.setText(objects[position]);
        snNumber.setText(position+"");

        return convertView;
    }

}