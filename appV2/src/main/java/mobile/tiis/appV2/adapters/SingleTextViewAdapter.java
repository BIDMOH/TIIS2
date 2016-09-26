/*******************************************************************************
 * <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *   ~ Copyright (C)AIRIS Solutions 2015 TIIS App - Tanzania Immunization Information System App
 *   ~
 *   ~    Licensed under the Apache License, Version 2.0 (the "License");
 *   ~    you may not use this file except in compliance with the License.
 *   ~    You may obtain a copy of the License at
 *   ~
 *   ~        http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~    Unless required by applicable law or agreed to in writing, software
 *   ~    distributed under the License is distributed on an "AS IS" BASIS,
 *   ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~    See the License for the specific language governing permissions and
 *   ~    limitations under the License.
 *   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
 ******************************************************************************/

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
 * Created by Arinela on 3/8/2015.
 */
public class SingleTextViewAdapter extends ArrayAdapter<String> {
    List<String> items;
    Context act;

    public SingleTextViewAdapter(Context context, int resource, List<String> items) {
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