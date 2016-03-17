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
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mobile.giis.app.R;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.entity.HealthFacilityBalance;
import mobile.giis.app.util.Constants;

/**
 * Created by Rubin on 6/1/2015.
 */
public class AdapterHealthFacilityBalance   {

    public AdapterHealthFacilityBalance(Activity context, LinearLayout ln, List<HealthFacilityBalance> items) {
        for(int i = 0;i<items.size();i++){
            ln.addView(getView(context,items.get(i),i));
        }
    }





    public View getView(Activity  ctx, final HealthFacilityBalance  item,int position)
    {

            LayoutInflater li = (LayoutInflater) ctx.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = li.inflate(R.layout.item_list_view_health_facility_balance, null);

        TextView tvItem = (TextView) rowView.findViewById(R.id.tv_item);
        TextView tvgtin = (TextView)rowView.findViewById(R.id.tv_gtin);
        tvgtin.setVisibility(View.GONE);
        TextView tvlot_number = (TextView)rowView.findViewById(R.id.tv_lot_number);
        TextView tvexpire_date = (TextView)rowView.findViewById(R.id.tv_expire_date);
        TextView tvbalance = (TextView)rowView.findViewById(R.id.tv_balance);
        final EditText etQuantity = (EditText)rowView.findViewById(R.id.et_quantity);


        tvItem.setText(item.getItem_name().toString());
        tvgtin.setText(item.getGtin());
        tvlot_number.setText(item.getLot_number());
        SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        Log.d("Setting exp date", ft.format(BackboneActivity.dateParser(item.getExpire_date())));
        if(calculateDateDiffToExpiryRowColor(BackboneActivity.dateParser(item.getExpire_date()))){
          tvexpire_date.setBackgroundColor(Color.YELLOW);
        }
       tvexpire_date.setText(ft.format(BackboneActivity.dateParser(item.getExpire_date())));
       tvbalance.setText(item.getBalance() + "");
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

        return rowView;

    }

    public boolean calculateDateDiffToExpiryRowColor(Date expiry) {
        Date now = new Date();
        long diff = getDaysDifference(now, expiry);
        return diff < Constants.LimitNumberOfDaysBeforeExpireVal;
    }

    public static final long  getDaysDifference(Date d1, Date d2){
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
    }
}