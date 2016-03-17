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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import mobile.giis.app.R;
import mobile.giis.app.entity.HealthFacilityBalance;

/**
 * Created by utente1 on 6/3/2015.
 */
public class AdapterStockAdjustmentReasons {

    List<String> adjustmentReasons;
    Activity context;
    public AdapterStockAdjustmentReasons(Activity context, LinearLayout ln, List<HealthFacilityBalance> items , List<String> adjustmentReasons) {
        this.adjustmentReasons = adjustmentReasons;
        this.context= context;
        for (int i = 0; i < items.size(); i++) {
            ln.addView(getView(context, items.get(i)));
        }
    }


    public View getView(Activity ctx, final HealthFacilityBalance item) {

        LayoutInflater li = LayoutInflater.from(ctx);// ctx.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = li.inflate(R.layout.item_stock_adjustment_reasons, null);

        TextView tvItem = (TextView) rowView.findViewById(R.id.tv_item);
        TextView tvlot_number = (TextView) rowView.findViewById(R.id.tv_lot_number);
        TextView tvbalance = (TextView) rowView.findViewById(R.id.tv_balance);
        final EditText etQuantity = (EditText) rowView.findViewById(R.id.et_quantity);
        Spinner spReasons = (Spinner)rowView.findViewById(R.id.sp_reason);


        tvItem.setText(item.getItem_name().toString());
        tvlot_number.setText(item.getLot_number());
        tvbalance.setText(item.getBalance() + "");
        SingleTextViewAdapter spAdjustmentReasons = new SingleTextViewAdapter(ctx, R.layout.single_text_spinner_item_drop_down, adjustmentReasons);
        spReasons.setAdapter(spAdjustmentReasons);
        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.setTempBalance(etQuantity.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spReasons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item.setSelectedAdjustmentReasonPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rowView;

    }
}