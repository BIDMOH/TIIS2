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

package mobile.giis.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import mobile.giis.app.R;
import mobile.giis.app.entity.AefiListItem;

/**
 * Created by Olsi on 24/03/2015.
 */
public class AefiListAdapter extends ArrayAdapter<AefiListItem> {
    List<AefiListItem> items;
    Activity act;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    public AefiListAdapter(Activity context, int resource, List<AefiListItem> items) {
        super(context, resource, items);
        this.items = items;
        act = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        LayoutInflater vi = (LayoutInflater) act.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = vi.inflate(R.layout.item_aefi, null);

        LinearLayout llItem =(LinearLayout)rowView.findViewById(R.id.ll_aefi_item);
        TextView vaccinnes = (TextView) rowView.findViewById(R.id.vaccines_aefi_item);
        TextView healthCenter = (TextView) rowView.findViewById(R.id.health_center_name_aefi_item);
        TextView vaccinationDate = (TextView) rowView.findViewById(R.id.vaccination_date_aefi_item);
        TextView aefiDate = (TextView) rowView.findViewById(R.id.txt_aefi_date_aefi_item);
        CheckBox done = (CheckBox) rowView.findViewById(R.id.chk_done_aefi_item);
        CheckBox aefi = (CheckBox) rowView.findViewById(R.id.chk_aefidone_aefi_item);
        TextView notes = (TextView) rowView.findViewById(R.id.txt_notes_aefi_item);

        AefiListItem item = items.get(position);
        if(position%2==0){
            llItem.setBackgroundColor(getContext().getResources().getColor(R.color.grid_row));
        }

        vaccinnes.setText(item.getVaccines());
        healthCenter.setText(item.getHealthFacilityName());
        notes.setText(item.getNotes());

        if(item.getVaccinationDate()!=null) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            vaccinationDate.setText(format.format(item.getVaccinationDate()));
        }

        if(item.getAefiDate()!=null) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            aefiDate.setText(format.format(item.getAefiDate()));
        }

        if(item.isDone()) {
            done.setChecked(true);
        }
        if(item.isAefi()) {
            aefi.setChecked(true);
        }
        
        return rowView;

    }
}