package mobile.tiis.staging.fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.staging.HomeActivityRevised;
import mobile.tiis.staging.R;
import mobile.tiis.staging.adapters.SingleTextViewAdapter;
import mobile.tiis.staging.adapters.StockAdjustmentListAdapter;
import mobile.tiis.staging.base.BackboneActivity;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.database.SQLHandler;
import mobile.tiis.staging.entity.AdjustmentReasons;
import mobile.tiis.staging.entity.HealthFacilityProofOfDelivery;

/**
 *  Created by issymac on 09/02/16.
 */
public class StockProofOfDeliveryFragment extends Fragment{
    private static final String TAG = StockProofOfDeliveryFragment.class.getSimpleName();

    private static final String ARG_POSITION = "position";

    StockAdjustmentListAdapter adapter;

    private BackboneApplication application;

    private DatabaseHandler database;
    public Dialog dialog;
    public TextView dialogueMessage, dialogueOKButton;

    public static List<HealthFacilityProofOfDelivery> rowCollectorList;
    private TableLayout stockHostTable;

    public static StockProofOfDeliveryFragment newInstance() {
        StockProofOfDeliveryFragment f = new StockProofOfDeliveryFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogueView   = LayoutInflater.from(getActivity()).inflate(R.layout.monthly_report_dialogue, null);
        dialog.setContentView(dialogueView);
        dialogueMessage     = (TextView) dialogueView.findViewById(R.id.mesage);
        dialogueMessage.setTypeface(HomeActivityRevised.Roboto_Light);
        dialogueOKButton    = (TextView) dialogueView.findViewById(R.id.tv_ok);
        dialogueOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_stock_proof_of_delivery, null);

        Button saveButton               = (Button) root.findViewById(R.id.save_btn);
        stockHostTable                  = (TableLayout) root.findViewById(R.id.stock_table_container);

        application = (BackboneApplication)getActivity().getApplication();
        database = application.getDatabaseInstance();

        rowCollectorList = getHealthFacilityStockDistributions();
        Log.d(TAG,"count = "+rowCollectorList.size());
        addViewsToTable();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButtonClicked();
            }
        });

        return root;
    }

    public void addViewsToTable(){
        stockHostTable.removeAllViews();
        for (final HealthFacilityProofOfDelivery HealthFacilityProofOfDelivery : rowCollectorList){

            View rowView = View.inflate(StockProofOfDeliveryFragment.this.getActivity(), R.layout.stock_proof_of_delivery_item, null);

            TextView vaccineName = (TextView) rowView.findViewById(R.id.item_name);
            vaccineName.setTypeface(BackboneActivity.Rosario_Regular);
            vaccineName.setText(HealthFacilityProofOfDelivery.getVaccineName());

            TextView vaccineLotNumber = (TextView) rowView.findViewById(R.id.lot_no);
            vaccineLotNumber.setTypeface(BackboneActivity.Rosario_Regular);
            vaccineLotNumber.setText(HealthFacilityProofOfDelivery.getLotNumber());

            TextView vacccineBalance = (TextView) rowView.findViewById(R.id.unit_of_measure);
            vacccineBalance.setTypeface(BackboneActivity.Rosario_Regular);
            vacccineBalance.setText(HealthFacilityProofOfDelivery.getUnitOfMeasure());


            stockHostTable.addView(rowView);

        }
    }



    private void saveButtonClicked(){

        boolean canContinue = true;
        boolean balanceNegative = false;

        int count = stockHostTable.getChildCount();
        // check if there are rows that have only qty or reason selected
        for (int i=0; i<count; i++) {
            if (((EditText)stockHostTable.getChildAt(i).findViewById(R.id.quantity_received)).getText().toString().equals("")) {
                canContinue = false;
                break;
            }
        }

        if (canContinue) {
            int counter = rowCollectorList.size();
            boolean success = true;
            for (int i=0;i<counter;i++) {
                HealthFacilityProofOfDelivery healthfacility = rowCollectorList.get(i);
                healthfacility.setQuantity(Integer.valueOf(((((EditText)stockHostTable.getChildAt(i).findViewById(R.id.quantity_received)).getText().toString()))));

                ContentValues values = new ContentValues();
                values.put(SQLHandler.StockDistributionsValuesColumns.QUANTITY,healthfacility.getQuantity());
                values.put(SQLHandler.StockDistributionsValuesColumns.STATUS,"RECEIVED");

                database.getWritableDatabase().update(SQLHandler.Tables.STOCK_DISTRIBUTIONS,values,
                        SQLHandler.StockDistributionsValuesColumns.FROM_HEALTH_FACILITY_ID + "= " + healthfacility.getFromHealthFacilityId() + " AND " +
                        SQLHandler.StockDistributionsValuesColumns.TO_HEALTH_FACILITY_ID + "= " + healthfacility.getToHealthFacilityId() + " AND " +
                        SQLHandler.StockDistributionsValuesColumns.PRODUCT_ID + "= " + healthfacility.getProductId()+ " AND " +
                        SQLHandler.StockDistributionsValuesColumns.LOT_ID + "= " + healthfacility.getLotId() + " AND " +
                        SQLHandler.StockDistributionsValuesColumns.ITEM_ID + "= " + healthfacility.getItemId() + " AND " +
                        SQLHandler.StockDistributionsValuesColumns.DISTRIBUTION_TYPE + "= '" + healthfacility.getDistributionType() + "' AND " +
                        SQLHandler.StockDistributionsValuesColumns.DISTRIBUTION_DATE + "= '" + healthfacility.getDistributionDate() + "'",null);

                Date date = BackboneActivity.dateParser(healthfacility.getDistributionDate());
                String distributionDate = null;
                try {
                    distributionDate = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(date), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                application.updateStockDistribution(healthfacility.getFromHealthFacilityId(),healthfacility.getToHealthFacilityId(),healthfacility.getProductId(),healthfacility.getLotId(),healthfacility.getItemId(),healthfacility.getDistributionType(),distributionDate,healthfacility.getQuantity(),"RECEIVED");
            }
            if (success) {
                sayThis(getResources().getString(R.string.saved_successfully),1);
            }
        } else {
            sayThis("Please fill all fields",1);
        }

        addViewsToTable();

    }

    public void showDialogWhenSavedSucc(String text) {

        Toast.makeText(StockProofOfDeliveryFragment.this.getActivity(), text, Toast.LENGTH_LONG).show();

    }

    private ArrayList<HealthFacilityProofOfDelivery> getHealthFacilityStockDistributions(){
        ArrayList<HealthFacilityProofOfDelivery> list = new ArrayList<>();
        Cursor cursor = null;
        cursor = database.getReadableDatabase().rawQuery("SELECT * FROM "+ SQLHandler.Tables.STOCK_DISTRIBUTIONS+" " +
                " JOIN item on "+SQLHandler.Tables.STOCK_DISTRIBUTIONS+"."+SQLHandler.StockDistributionsValuesColumns.ITEM_ID+" = item.ID  " +
                " JOIN item_lot ON "+SQLHandler.Tables.STOCK_DISTRIBUTIONS+"."+SQLHandler.StockDistributionsValuesColumns.LOT_ID+" =  item_lot.id  " +
                " WHERE status ='PENDING'", null);
        if(cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    HealthFacilityProofOfDelivery row = new HealthFacilityProofOfDelivery();
                    row.setFromHealthFacilityId(cursor.getInt(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.FROM_HEALTH_FACILITY_ID)));
                    row.setToHealthFacilityId(cursor.getInt(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.TO_HEALTH_FACILITY_ID)));
                    row.setDistributionDate(cursor.getString(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.DISTRIBUTION_DATE)));
                    row.setDistributionType(cursor.getString(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.DISTRIBUTION_TYPE)));
                    row.setItemId(cursor.getInt(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.ITEM_ID)));
                    row.setLotId(cursor.getInt(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.LOT_ID)));

                    row.setProductId(cursor.getInt(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.PRODUCT_ID)));
                    row.setProgramId(cursor.getInt(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.PROGRAM_ID)));
                    row.setQuantity(cursor.getInt(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.QUANTITY)));

                    row.setVvmStatus(cursor.getString(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.VVM_STATUS)));
                    row.setVimsLotId(cursor.getInt(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.VIMS_LOT_ID)));

                    row.setStatus(cursor.getString(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.STATUS)));
                    row.setUnitOfMeasure(cursor.getString(cursor.getColumnIndex(SQLHandler.StockDistributionsValuesColumns.UNIT_OF_MEASURE)));

                    row.setVaccineName(cursor.getString(cursor.getColumnIndex("NAME")));
                    row.setLotNumber(cursor.getString(cursor.getColumnIndex("lot_number")));


                    list.add(row);
                } while (cursor.moveToNext());
            }
        }

        return list;
    }


    public void sayThis(String message, int code){
        dialogueMessage.setText(message);
        if (code == 1){
            dialogueMessage.setTextColor(getResources().getColor(R.color.red_600));
        }else if (code == 2){
            dialogueMessage.setTextColor(getResources().getColor(R.color.green_600));
        }

        dialog.show();
    }


}
