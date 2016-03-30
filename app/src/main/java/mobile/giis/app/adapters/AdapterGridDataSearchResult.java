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

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mobile.giis.app.ChildDetailsActivity;
import mobile.giis.app.R;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.entity.Child;

/**
 * Created by Arinela on 13/3/2015.
 */
 public class AdapterGridDataSearchResult extends ArrayAdapter<Child> {
    List<Child> items;
    Context ctx;
    DatabaseHandler mydb;

    public AdapterGridDataSearchResult(Context ctx, int resource, List<Child> items,DatabaseHandler mydb) {
        super(ctx, resource, items);
        this.items = items;
        this.ctx = ctx;
        this.mydb = mydb;
        Log.d("coze adpter", " Size of list is : "+items.size());
    }

    static class ViewHolder {
        public TextView tvName,tvMotherName,tvDateOfBirth,tvGender,tvVillage,tvHealthFacility;

    }

    public void replaceData(List<Child> items){
        this.items = items;
    }

    public String getBarcode(int position){
        return items.get(position).getBarcodeID();
    }

    public String getChildId(int position){
        return items.get(position).getSystemId();
    }

    public String getChildid(int position){
        return items.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        ViewHolder viewHolder;
        final Child item = items.get(position);
        if (rowView == null) {
            LayoutInflater li = (LayoutInflater) ctx.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = li.inflate(R.layout.children_list_item, null);


            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) rowView.findViewById(R.id.txt_child_names);
            viewHolder.tvMotherName = (TextView) rowView.findViewById(R.id.txt_mother_names);
            viewHolder.tvDateOfBirth = (TextView) rowView.findViewById(R.id.txt_child_dob);
            viewHolder.tvGender = (TextView) rowView.findViewById(R.id.txt_child_gender);
            viewHolder.tvVillage = (TextView) rowView.findViewById(R.id.txt_child_village_domicile);
            viewHolder.tvHealthFacility = (TextView) rowView.findViewById(R.id.txt_child_health_facility);


            rowView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }


        viewHolder.tvName.setText(item.getFirstname1() + " " + ((item.getFirstname2() != null) ? item.getFirstname2() : "") + " " + item.getLastname1());
        viewHolder.tvName.setTextColor(0xff0000ff);
        viewHolder.tvName.setTypeface(Typeface.DEFAULT_BOLD);
        viewHolder.tvMotherName.setText(item.getMotherFirstname() + " " + item.getMotherLastname());
        viewHolder.tvMotherName.setTextColor(0xff0000ff);
        viewHolder.tvMotherName.setTypeface(Typeface.DEFAULT_BOLD);
        String dateString = new SimpleDateFormat("dd-MMM-yyyy").format(new Date(Long.parseLong(item.getBirthdate().toString())));
        viewHolder.tvDateOfBirth.setText(dateString);
        viewHolder.tvGender.setText(item.getGender());
        viewHolder.tvVillage.setText(item.getDomicile());
        viewHolder.tvHealthFacility.setText(item.getHealthcenter());
        Log.d("coze adpter", "facility One = " + item.getHealthcenter());
        Log.d("coze adpter", "facility Two = " + mydb.getHealthCenterName(item.getHealthcenter()));

//        rowView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!(position == -1)) {
//                    Intent childDetailsActivity = new Intent(ctx, ChildDetailsActivity.class);
//                    childDetailsActivity.putExtra("barcode", item.getBarcodeID());
//                    ctx.startActivity(childDetailsActivity);
//                }
//            }
//        });

    return rowView;

    }
}