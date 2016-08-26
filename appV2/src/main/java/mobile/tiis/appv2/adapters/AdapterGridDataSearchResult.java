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

package mobile.tiis.appv2.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mobile.tiis.appv2.R;
import mobile.tiis.appv2.database.DatabaseHandler;
import mobile.tiis.appv2.entity.Child;
import mobile.tiis.appv2.fragments.SearchChildFragment;

/**
 * Created by Arinela on 13/3/2015.
 */
 public class AdapterGridDataSearchResult extends BaseAdapter {

    List<Child> items;
    Context ctx;
    DatabaseHandler mydb;
    int snCount = Integer.parseInt(SearchChildFragment.currentCount);

    public AdapterGridDataSearchResult(Context ctx, List<Child> items,DatabaseHandler mydb, String currentCount) {
        this.items = items;
        this.ctx = ctx;
        this.mydb = mydb;
        Log.d("coze adpter", " number of count is : "+currentCount);
    }

    static class ViewHolder {
        public TextView tvName,tvMotherName,tvDateOfBirth,tvGender,tvVillage,tvHealthFacility, tvSnNumber;

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
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void updateReceiptsList(List<Child> newlist) {
        items.clear();
        items.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        View rowView = convertView;
        ViewHolder viewHolder;
        final Child item = items.get(position);
        if (rowView == null) {
            LayoutInflater li = (LayoutInflater) ctx.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = li.inflate(R.layout.search_screen_children_list_item, null);

            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) rowView.findViewById(R.id.txt_child_names);
            viewHolder.tvMotherName = (TextView) rowView.findViewById(R.id.txt_mother_names);
            viewHolder.tvDateOfBirth = (TextView) rowView.findViewById(R.id.txt_child_dob);
            viewHolder.tvGender = (TextView) rowView.findViewById(R.id.txt_child_gender);
            viewHolder.tvVillage = (TextView) rowView.findViewById(R.id.txt_child_village_domicile);
            viewHolder.tvHealthFacility = (TextView) rowView.findViewById(R.id.txt_child_health_facility);
            viewHolder.tvSnNumber   = (TextView) rowView.findViewById(R.id.sn_number);

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
        viewHolder.tvDateOfBirth.setTextColor(ctx.getResources().getColor(R.color.black));
        viewHolder.tvGender.setText(item.getGender());
        viewHolder.tvGender.setTextColor(ctx.getResources().getColor(R.color.black));
        viewHolder.tvVillage.setText(item.getDomicile());
        viewHolder.tvVillage.setTextColor(ctx.getResources().getColor(R.color.black));
        viewHolder.tvHealthFacility.setText(item.getHealthcenter());
        viewHolder.tvHealthFacility.setTextColor(ctx.getResources().getColor(R.color.black));
        viewHolder.tvSnNumber.setText((snCount+position+1)+"");

    return rowView;

    }

}