package mobile.tiis.staging.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import mobile.tiis.staging.LotSettingsActivity;
import mobile.tiis.staging.R;
import mobile.tiis.staging.base.BackboneActivity;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.database.GIISContract;
import mobile.tiis.staging.database.SQLHandler;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link LotSettingsActivity}
 * in two-pane mode (on tablets)
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    private static final String TAG  = ItemDetailFragment.class.getSimpleName();
    public static final String ARG_ITEM = "item";
    private String mItem;
    private List<LotNumber> lots = new ArrayList<>();
    private LinearLayout itemsList;
    private DatabaseHandler db;
    private View rootView;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = getArguments().getString(ARG_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.item_detail, container, false);

        BackboneApplication app = (BackboneApplication)getActivity().getApplication();
        db  = app.getDatabaseInstance();

        itemsList = (LinearLayout)rootView.findViewById(R.id.list);

        fillLots(mItem);

        Button saveButton = (Button)rootView.findViewById(R.id.save_btn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0;i<itemsList.getChildCount();i++){
                    Log.d(TAG,"selected item");
                    View v = itemsList.getChildAt(i);
                    if(((CheckBox)v.findViewById(R.id.lot_number)).isChecked()){

                        Log.d(TAG,"selected item position = "+i);
                        Calendar c = Calendar.getInstance();
                        GregorianCalendar d = new GregorianCalendar(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
                        db.saveActiveLotNumber(lots.get(i).id,lots.get(i).name,mItem,d.getTimeInMillis());

                    }
                }

                ((LotSettingsActivity)getActivity()).addViewsToTable();
                fillLots(mItem);

            }
        });

        return rootView;
    }

    public void getLotNumbers(String item){
        lots.clear();
        Calendar c = Calendar.getInstance();
        GregorianCalendar d = new GregorianCalendar(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        String activeLotQuery = "SELECT lot_id FROM "+ SQLHandler.Tables.ACTIVE_LOT_NUMBERS +" where item = '"+item+"' AND "+ GIISContract.ActiveLotNumbersColumns.DATE+" = "+d.getTimeInMillis();
        Cursor activeLotCursor = db.getReadableDatabase().rawQuery(activeLotQuery,null);
        int count = activeLotCursor.getCount();


        String lot_ids="";
        for (int i=0;i<count;i++){
            activeLotCursor.moveToPosition(i);
            lot_ids+="'"+activeLotCursor.getString(0)+"',";
        }
        lot_ids=lot_ids.replaceFirst(".$","");

        Log.d(TAG,"already activated lots  = "+lot_ids);

        String selectQuery = "SELECT lot_id, lot_number, balance , GtinIsActive, LotIsActive FROM "+ SQLHandler.Tables.HEALTH_FACILITY_BALANCE +" where item = '"+item+"' AND balance > 0 AND datetime(substr(expire_date,7,10), 'unixepoch') >= datetime('now') AND lot_id NOT IN ("+lot_ids+")" ;
        Cursor lotNumbersCUrsor = db.getReadableDatabase().rawQuery(selectQuery,null);
        int size = lotNumbersCUrsor.getCount();

        for (int i=0;i<size;i++){
            lotNumbersCUrsor.moveToPosition(i);

            LotNumber lotNumber = new LotNumber();
            lotNumber.name = lotNumbersCUrsor.getString(lotNumbersCUrsor.getColumnIndex("lot_number"));
            lotNumber.id = lotNumbersCUrsor.getString(lotNumbersCUrsor.getColumnIndex("lot_id"));
            lotNumber.balance = lotNumbersCUrsor.getString(lotNumbersCUrsor.getColumnIndex("balance"));
            lots.add(lotNumber);
        }
    }

    public void fillLots(String mItem){
        //used to update the dose when this function is called from the activity
        this.mItem = mItem;
        getLotNumbers(mItem);
        itemsList.removeAllViews();
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.title)).setText("Select "+mItem+" Lot Numbers that will be used today");
        }

        if(lots.size()==0){
            rootView.findViewById(R.id.available_lots).setVisibility(View.GONE);
            ((TextView)rootView.findViewById(R.id.no_available_lots)).setText("No Available lot numbers for \n"+mItem);
        }else {
            rootView.findViewById(R.id.available_lots).setVisibility(View.VISIBLE);
            for (final LotNumber lot : lots) {

                View lotNumberItem = View.inflate(getContext(), R.layout.view_lot_number_item, null);

                TextView lotNumber = (TextView) lotNumberItem.findViewById(R.id.lot_name);
                lotNumber.setTypeface(BackboneActivity.Rosario_Regular);
                lotNumber.setText(lot.name);


                TextView lotNumberBalance = (TextView) lotNumberItem.findViewById(R.id.exp_date);
                lotNumberBalance.setTypeface(BackboneActivity.Rosario_Regular);
                lotNumberBalance.setText("" +
                        "Balance : " + lot.balance);

                itemsList.addView(lotNumberItem);

            }
        }

    }

    private static class LotNumber{
        public String name,id,balance;
    }

}