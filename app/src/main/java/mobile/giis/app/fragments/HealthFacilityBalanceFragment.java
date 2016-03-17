package mobile.giis.app.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mobile.giis.app.R;
import mobile.giis.app.adapters.HealthFacilityBalanceListAdapter;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.entity.HealthFacilityBalance;

/**
 * Created by issymac on 09/02/16.
 */
public class HealthFacilityBalanceFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    private List<HealthFacilityBalance> rowCollectorList;

    HealthFacilityBalanceListAdapter adapter;

    private BackboneApplication application;

    private DatabaseHandler database;

    public static HealthFacilityBalanceFragment newInstance() {
        HealthFacilityBalanceFragment f = new HealthFacilityBalanceFragment();
        Bundle b = new Bundle();
//        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_health_facility_balance, null);
        setUpView(root);

        application = (BackboneApplication) this.getActivity().getApplication();
        database = application.getDatabaseInstance();
        rowCollectorList = getHealthFacilityBalanceRows();

        adapter = new HealthFacilityBalanceListAdapter(HealthFacilityBalanceFragment.this.getActivity(), rowCollectorList);

        ListView stockAdjustmentList    = (ListView) root.findViewById(R.id.health_facility_balance_list);
//        View v                          = inflater.inflate(R.layout.health_facility_balance_list_item_title, null);

        TextView nameTitle       = (TextView) root.findViewById(R.id.txt_name_title);
        nameTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView lotNumberTitle = (TextView) root.findViewById(R.id.txt_lot_number_title);
        lotNumberTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView expiryDateTitle = (TextView) root.findViewById(R.id.txt_exp_date_title);
        expiryDateTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView balanceTitle = (TextView) root.findViewById(R.id.txt_balance_title);
        balanceTitle.setTypeface(BackboneActivity.Rosario_Regular);

//        stockAdjustmentList.addHeaderView(v);
        stockAdjustmentList.setAdapter(adapter);

        return root;
    }

    public void setUpView(View v){

    }

    private ArrayList<HealthFacilityBalance> getHealthFacilityBalanceRows(){
        ArrayList<HealthFacilityBalance> list = new ArrayList<>();
        Cursor cursor = null;
        cursor = database.getReadableDatabase().rawQuery("SELECT * FROM health_facility_balance where GtinIsActive='true' AND LotIsActive='true'  AND datetime(substr(expire_date,7,10), 'unixepoch') >= datetime('now')  ", null);
        if(cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    HealthFacilityBalance row = new HealthFacilityBalance();
                    row.setBalance(cursor.getInt(cursor.getColumnIndex("balance")));
                    Log.d("balance", row.getBalance() + "");
                    row.setExpire_date(cursor.getString(cursor.getColumnIndex("expire_date")));
                    Log.d("expdate", row.getExpire_date());
                    row.setGtin(cursor.getString(cursor.getColumnIndex("gtin")));
                    Log.d("gtin", row.getGtin());

                    row.setLot_number(cursor.getString(cursor.getColumnIndex("lot_number")));
                    Log.d("lot_number", row.getLot_number());
                    row.setLot_id(cursor.getString(cursor.getColumnIndex("lot_id")));
                    Log.d("lot_id", row.getLot_id());
                    row.setItem_name(cursor.getString(cursor.getColumnIndex("item")));
                    Log.d("item", row.getItem_name());
//
//                    row.setItem_name(cursor.getString(cursor.getColumnIndex("reorder_qty")));
//                    Log.d("reorder_qty", row.getReorder_qty());
                    list.add(row);
                } while (cursor.moveToNext());
            }
        }

        return list;
    }

}
