package mobile.giis.app.adapters;
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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mobile.giis.app.AdministerVaccines2;
import mobile.giis.app.ChildDetailsActivity;
import mobile.giis.app.R;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.entity.AdministerVaccinesModel;
import mobile.giis.app.entity.NonVaccinationReason;
import mobile.giis.app.fragments.AdministerVaccineFragment;

/**
 *  ReCreated by issymac on 03/03/16.
 *  Created by utente1 on 5/13/2015.
 */
public class VaccineDoseListAdapter extends ArrayAdapter<AdministerVaccinesModel> implements DatePickerDialog.OnDateSetListener {
    List<AdministerVaccinesModel> items;
    Context ctx;
    int vac_lot_pos;
    private BackboneApplication app;
    private DatabaseHandler dbh;
    String dob_st;
    Date new_date = new Date();
    boolean correctDateSelected = false;
    public final SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
    private TextView tempHoldingTextView;
    private AdministerVaccinesModel tempHoldingVaccineModel;

    public VaccineDoseListAdapter(Context ctx, int resource, List<AdministerVaccinesModel> items,String dob_st,int vac_lot_pos) {
        super(ctx, resource, items);
        this.items = items;
        this.ctx = ctx;
        this.dob_st = dob_st;
        this.vac_lot_pos = vac_lot_pos;
    }

    public static final long getDaysDifference(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);

        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.HOUR,0);
        c1.set(Calendar.MINUTE,0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);

        c2.set(Calendar.HOUR_OF_DAY, 0);
        c2.set(Calendar.HOUR, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);
        c2.set(Calendar.MILLISECOND, 0);

        long diff = c2.getTimeInMillis() - c1.getTimeInMillis();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return Math.abs(difference);
    }

    static class ViewHolder {
        public TextView tvDose,tvVaccineDate;
        public Spinner spVaccLot,spReason;
        public CheckBox chDone;
        public View view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        final ViewHolder viewHolder;
        final AdministerVaccinesModel item = items.get(position);

        if (rowView == null) {

            LayoutInflater li = (LayoutInflater) ctx.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = li.inflate(R.layout.vaccine_dose_quantity_item, null);

            viewHolder                  = new ViewHolder();
            viewHolder.tvDose           = (TextView) rowView.findViewById(R.id.dose);
            viewHolder.tvVaccineDate    = (TextView)rowView.findViewById(R.id.vaccine_date);
            viewHolder.spVaccLot        = (Spinner)rowView.findViewById(R.id.lot_spinner);
            viewHolder.spReason         = (Spinner)rowView.findViewById(R.id.non_vacc_reason_spinner);
            viewHolder.chDone           = (CheckBox)rowView.findViewById(R.id.vaccine_administered_done_checkbox);
            viewHolder.view             = (View) rowView.findViewById(R.id.split_dose);

            rowView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.tvDose.setText(item.getDoseName());
        viewHolder.tvVaccineDate.setText(item.getTime());

        //TODO : Add the material design date picker instead of the current date picker
        viewHolder.tvVaccineDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempHoldingTextView = viewHolder.tvVaccineDate;
                tempHoldingVaccineModel = item;
                pickDate();
//                setdates(viewHolder.tvVaccineDate, item);
                Log.d("Time after show done", item.getTime());
            }
        });

        //Call methods to initialize the spinners and the checkbox values
        setSpinnerVoccLot(item, viewHolder);
        setSpinnerReason(item, viewHolder);
        checkBoxDone(item, viewHolder);

        return rowView;

    }

    public void pickDate(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog reaction_start_date_picker = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        Date bdate = BackboneActivity.dateParser(dob_st);


        Log.d("coze", "my date is " + bdate.getTime());

        Calendar dob=Calendar.getInstance();
        dob.setTimeInMillis(bdate.getTime());
        reaction_start_date_picker.setMinDate(dob);



        reaction_start_date_picker.show(((Activity) ctx).getFragmentManager(), "DatePickerDialogue");




    }

    public Date setdates(final TextView a, final AdministerVaccinesModel coll){
        final SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        if (getDaysDifference(new_date, coll.getTime2()) > 0) {
            coll.setTime(ft.format(new_date));
            a.setText(ft.format(new_date));
            coll.setTime2(new_date);
            int cc = 0;
            if (coll.getStarter_row()) {
                for (AdministerVaccinesModel others : items) {
                    others.setTime(ft.format(new_date));
                    others.setTime2(new_date);
                }
            }
        }else {
            coll.setTime(ft.format(coll.getTime2()));
            a.setText(ft.format(coll.getTime2()));
            coll.setTime2(coll.getTime2());
            if (coll.getStarter_row()) {
                for (AdministerVaccinesModel others : items) {
                    others.setTime(ft.format(coll.getTime2()));
                    others.setTime2(coll.getTime2());
                }
            }
        }
        notifyDataSetChanged();
        return new_date;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        final SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        Date bdate = BackboneActivity.dateParser(dob_st);

        Calendar cal = new GregorianCalendar();
        cal.set(year, (monthOfYear), dayOfMonth);
        new_date = cal.getTime();

        try {
            new_date = ft.parse(ft.format(new_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("WOWHITS", "##################### New Date is  :  "+ft.format(new_date));
        Log.d("WOWHITS", "##################### DOB is  :  " + ft.format(bdate));

        if (new_date.before(bdate)){
            AdministerVaccineFragment.correnctDateSelected = false;
        }else{
            AdministerVaccineFragment.correnctDateSelected = true;
        }

        setdates(tempHoldingTextView, tempHoldingVaccineModel);

    }

    public void setSpinnerVoccLot(final AdministerVaccinesModel items,final ViewHolder viewHolder){
        SingleTextViewAdapter statusAdapter = new SingleTextViewAdapter(ctx, R.layout.single_text_spinner_item_drop_down, items.getVaccine_lot_list());
        viewHolder.spVaccLot.setAdapter(statusAdapter);
        if (items.getVaccine_lot_list().size() > 2) {
            viewHolder.spVaccLot.setSelection(2);
            items.setVaccination_lot_pos(2);
            //setting the id of vaccine lot
            items.setVaccination_lot(items.getVaccine_lot_map().get(items.getVaccine_lot_list().get(2)).toString());
            Log.d("RowCollId", items.getVaccination_lot());
        } else {
            viewHolder.spVaccLot.setSelection(1);
            items.setVaccination_lot_pos(1);
            items.setVaccination_lot(items.getVaccine_lot_map().get(items.getVaccine_lot_list().get(1)).toString());
            Log.d("RowCollId", items.getVaccination_lot());
        }

        //rowCollector.setVaccination_lot_pos(1);
        vac_lot_pos = 1;
        viewHolder.spVaccLot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                viewHolder.spVaccLot.setSelection(position);
                vac_lot_pos = position;
                items.setVaccination_lot_pos(position);
                items.setVaccination_lot(items.getVaccine_lot_map().get(items.getVaccine_lot_list().get(position)).toString());
                Log.d("RowCollId", items.getVaccination_lot());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //no changes
            }

        });
    }


    public void setSpinnerReason(final AdministerVaccinesModel item,final ViewHolder viewHolder){

        app = (BackboneApplication) ctx.getApplicationContext();
        dbh = app.getDatabaseInstance();
        //NonVaccinationReason Column Spinner
        List<String> reasons = new ArrayList<String>();
        reasons.add("----");
        for (NonVaccinationReason nvElement : dbh.getAllNonvaccinationReasons()) {
            reasons.add(nvElement.getName());
        }


        final List<NonVaccinationReason> non_vaccination_reason_list_with_additions = dbh.getAllNonvaccinationReasons();
        NonVaccinationReason empty = new NonVaccinationReason();
        empty.setName("----");
        empty.setId("0");
        empty.setKeepChildDue("false");
        non_vaccination_reason_list_with_additions.add(empty);


        final SingleTextViewAdapter statusAdapterNonVaccinationReason = new SingleTextViewAdapter(ctx, R.layout.single_text_spinner_item_drop_down, reasons);
        viewHolder.spReason.setAdapter(statusAdapterNonVaccinationReason);
        //item.setNon_vac_reason_pos(0);
        viewHolder.spReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                viewHolder.spReason.setSelection(position);
                item.setNon_vac_reason_pos(position);
                for(NonVaccinationReason a : non_vaccination_reason_list_with_additions)
                {
                    if (statusAdapterNonVaccinationReason.getItem(position).equalsIgnoreCase(a.getName())){
                        item.setNon_vac_reason(a.getId());
                        item.setKeep_child_due(Boolean.parseBoolean(a.getKeepChildDue()));
                    }
                }

//                    if (position == 0) {
//                        rowCollector.setNon_vac_reason("0");
//                    }
//                    if (position == 1) {
//                        rowCollector.setNon_vac_reason("29");
//                    }
//                    if (position == 2) {
//                        rowCollector.setNon_vac_reason("30");
//                    }
//                    if (position == 3) {
//                        rowCollector.setNon_vac_reason("31");
//                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //no changes
            }

        });
        viewHolder.spReason.setSelection(item.getNon_vac_reason_pos());
        if(item.getStatus().equals(String.valueOf(true)))viewHolder.spReason.setVisibility(View.GONE);
        viewHolder.spReason.setMinimumWidth(220);
    }

//    public Date show(final TextView a, final AdministerVaccinesModel coll) {
//        final Dialog d = new Dialog(ctx);
//        d.setTitle("Date Picker");
//        d.setContentView(R.layout.birthdate_picker);
//        Button btnSet = (Button) d.findViewById(R.id.button1);
//        Button btnCancel = (Button) d.findViewById(R.id.button2);
//        final DatePicker dp = (DatePicker) d.findViewById(R.id.datePicker);
//        dp.setMaxDate(new Date().getTime());
//        final SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
//        try {
//            Date date = ft.parse(dob_st);
//            dp.setMinDate(date.getTime());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        btnSet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                //setText(Integer.toString(dp.getDayOfMonth()) + "/" + Integer.toString(dp.getMonth() + 1) + "/" + Integer.toString(dp.getYear()));
//                calendar.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
//                new_date = calendar.getTime();
//                //Date dNow = new Date();
//                if (AdministerVaccines2.getDaysDifference(new_date, coll.getTime2()) > 0) {
//                    coll.setTime(ft.format(new_date));
//                    a.setText(ft.format(new_date));
//                    coll.setTime2(new_date);
//                    int cc = 0;
//                    if (coll.getStarter_row()) {
//                        for (AdministerVaccinesModel others : items) {
//                            others.setTime(ft.format(new_date));
//                            others.setTime2(new_date);
//
//                        }
//                    }
//                } else {
//                    coll.setTime(ft.format(coll.getTime2()));
//                    a.setText(ft.format(coll.getTime2()));
//                    coll.setTime2(coll.getTime2());
//                    if (coll.getStarter_row()) {
//                        for (AdministerVaccinesModel others : items) {
//                            others.setTime(ft.format(coll.getTime2()));
//                            others.setTime2(coll.getTime2());
//                        }
//                    }
//                }
//                notifyDataSetChanged();
//
//                d.dismiss();
//            }
//        });
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                d.dismiss(); // dismiss the dialog
//            }
//        });
//
//        d.show();
//        return new_date;
//    }

    public void checkBoxDone(final AdministerVaccinesModel item,final ViewHolder viewHolder){


        viewHolder.chDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.setStatus(String.valueOf(b));
                //Toast.makeText(AdministerVaccinesActivity.this, "Value changed to " + b, Toast.LENGTH_SHORT).show();
                if (!b) {
                    viewHolder.spReason.setVisibility(View.VISIBLE);
                    viewHolder.spVaccLot.setSelection(0);
                    viewHolder.view.setVisibility(View.VISIBLE);
                }
                if (b) {
                    viewHolder.spReason.setVisibility(View.GONE);
                    item.setNon_vac_reason("-1");
                    viewHolder.view.setVisibility(View.GONE);
                }
            }
        });
        viewHolder.chDone.setChecked(Boolean.parseBoolean(item.getStatus()));
    }

    public List<AdministerVaccinesModel> getItems() {
        return items;
    }

    public void setItems(List<AdministerVaccinesModel> items) {
        this.items = items;
    }
}

