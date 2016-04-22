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

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import mobile.tiis.app.R;
import mobile.tiis.app.entity.ImmunizationCardItem;

/**
 * Created by Rubin on 5/27/2015.
 */
public class ImmunizationCardAdapterForLinearLayout {


    public ImmunizationCardAdapterForLinearLayout(Activity context, LinearLayout ln, List<ImmunizationCardItem> items) {
        for(int i = 0;i<items.size();i++){
            ln.addView(getView(context,items.get(i),i));
        }
    }


    public View getView(Activity  context,ImmunizationCardItem items,int position)
    {
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View  rowView = vi.inflate(R.layout.item_immunization_card, null);

        LinearLayout llItem =(LinearLayout)rowView.findViewById(R.id.ll_immuniz_card_item);
        TextView vaccinationDose = (TextView) rowView.findViewById(R.id.vaccine_dose_item);
        TextView healthCenter = (TextView) rowView.findViewById(R.id.health_center_name_item);
        TextView vaccinationDate = (TextView) rowView.findViewById(R.id.vaccination_date_item);
        TextView nonVaccinationReason = (TextView) rowView.findViewById(R.id.non_vaccinattion_reason_item);
        CheckBox done = (CheckBox) rowView.findViewById(R.id.chk_done_item);

        if(position%2==0){
            llItem.setBackgroundColor(context.getResources().getColor(R.color.grid_row));
        }

        vaccinationDose.setText(items.getVacineDose());
        healthCenter.setText(items.getHealthCenterName());
        if(items.getVaccinationDate()!=null) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            String test = format.format(items.getVaccinationDate());
            vaccinationDate.setText(format.format(items.getVaccinationDate()));
        }
        nonVaccinationReason.setText(items.getNonVaccinaitonReason());
        if(items.getNonVaccinaitonReason()!=null && !items.getNonVaccinaitonReason().isEmpty())
            vaccinationDate.setVisibility(View.INVISIBLE);
        if(items.isDone()) {
            done.setChecked(true);
        }else{
            if(items.getNonVaccinaitonReason()==null||items.getNonVaccinaitonReason().isEmpty()){
                healthCenter.setVisibility(View.INVISIBLE);
                vaccinationDate.setVisibility(View.INVISIBLE);
            }
        }

        return rowView;

    }
}