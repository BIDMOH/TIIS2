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

package mobile.tiis.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mobile.tiis.app.R;
import mobile.tiis.app.entity.ModelImmunizedChild;

/**
 * Created by Rubin on 4/30/2015.
 */
public class AdapterListImmunizedChildren  extends ArrayAdapter<ModelImmunizedChild> {
    List<ModelImmunizedChild> items;
    Context ctx;

    public AdapterListImmunizedChildren(Context ctx, int resource, List<ModelImmunizedChild> items) {
        super(ctx, resource, items);
        this.items = items;
        this.ctx = ctx;
    }

    static class ViewHolder {
        public TextView tvName,tvLastname,tvVaccine;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        ViewHolder viewHolder;
        final ModelImmunizedChild item = items.get(position);
        if (rowView == null) {
            LayoutInflater li = (LayoutInflater) ctx.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = li.inflate(R.layout.item_listview_vacc_children, null);


            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) rowView.findViewById(R.id.tv_name);
            viewHolder.tvLastname = (TextView)rowView.findViewById(R.id.tv_lastname);
            viewHolder.tvVaccine = (TextView)rowView.findViewById(R.id.tv_vacc);



            rowView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        viewHolder.tvName.setText(item.getName());
        viewHolder.tvName.setTextColor(0xff0000ff);
        viewHolder.tvName.setTypeface(Typeface.DEFAULT_BOLD);
        viewHolder.tvLastname.setText(item.getLastname());
        try {
            viewHolder.tvVaccine.setText(item.getVaccine());
        }catch (Exception e){e.printStackTrace();}

        return rowView;

    }
}
