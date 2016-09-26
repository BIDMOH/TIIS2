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
import mobile.tiis.appV2.fragments.FragmentVaccineNameQuantity;

/**
 * Created by Rubin on 3/30/2015.
 */
public class AdapterVaccineNameQuantity extends ArrayAdapter<FragmentVaccineNameQuantity.VacineNameQuantity> {
    List<FragmentVaccineNameQuantity.VacineNameQuantity> items;
    Context ctx;

    public AdapterVaccineNameQuantity(Context ctx, int resource, List<FragmentVaccineNameQuantity.VacineNameQuantity> items) {
        super(ctx, resource, items);
        this.items = items;
        this.ctx = ctx;
    }

    static class ViewHolder {
        public TextView tvName, tvQuantity;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;
        final FragmentVaccineNameQuantity.VacineNameQuantity item = items.get(position);
        if (rowView == null) {
            LayoutInflater li = (LayoutInflater) ctx.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = li.inflate(R.layout.item_vaccine_name_quantity, parent,false);

            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) rowView.findViewById(R.id.tv_name);
            viewHolder.tvQuantity = (TextView) rowView.findViewById(R.id.tv_quantity);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        viewHolder.tvName.setText(item.getName());
        viewHolder.tvQuantity.setText(item.getQuantity()+"");

        return rowView;

    }
}