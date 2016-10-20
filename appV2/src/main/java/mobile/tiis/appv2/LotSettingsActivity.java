package mobile.tiis.appv2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.database.DatabaseHandler;
import mobile.tiis.appv2.database.GIISContract;
import mobile.tiis.appv2.database.SQLHandler;
import mobile.tiis.appv2.entity.Stock;
import mobile.tiis.appv2.fragments.LotSelectionFragment;

import static mobile.tiis.appv2.base.BackboneActivity.Roboto_Regular;
import static mobile.tiis.appv2.base.BackboneApplication.TABLET_REGISTRATION_MODE_PREFERENCE_NAME;

public class LotSettingsActivity extends AppCompatActivity {
    private DatabaseHandler db;
    private LinearLayout itemsList;
    private static final String TAG = LotSettingsActivity.class.getSimpleName();
    private List<Stock> listAllStock,listStock;
    private LotSelectionFragment fragment;
    private GregorianCalendar gregorianCalendar;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        boolean isFromHome = getIntent().getBooleanExtra("isFromHomeActivity",false);

        editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();


        Calendar calendar = Calendar.getInstance();
        gregorianCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Set Lot Numbers");

        assert toolbar != null;
        BackboneApplication app = (BackboneApplication)getApplication();
        db  = app.getDatabaseInstance();
        listStock = db.getAvailableHealthFacilityBalance();
        listAllStock = db.getAllHealthFacilityBalance();
        itemsList = (LinearLayout)findViewById(R.id.list);

        if(!isFromHome) {
            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(TABLET_REGISTRATION_MODE_PREFERENCE_NAME, false) || checkIntegrity()) {
                Intent intent = new Intent(this, HomeActivityRevised.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }


        addViewsToTable();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkIntegrity() || PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(TABLET_REGISTRATION_MODE_PREFERENCE_NAME,false)){
                    Snackbar.make(view, "Lot Numbers Saved Successfully", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Intent intent = new Intent(LotSettingsActivity.this, HomeActivityRevised.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else{
                    addViewsToTable();
                    FlashDialogue flashDialogue = new FlashDialogue(LotSettingsActivity.this);
                    flashDialogue.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

            }
        });

        SwitchCompat setRegistrationMode = (SwitchCompat)findViewById(R.id.set_tablet_type);
        setRegistrationMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    findViewById(R.id.frameLayout).setVisibility(View.INVISIBLE);
                    findViewById(R.id.info).setVisibility(View.VISIBLE);
                    editor.putBoolean(TABLET_REGISTRATION_MODE_PREFERENCE_NAME, true);
                    editor.commit();
                }else{
                    findViewById(R.id.frameLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.info).setVisibility(View.INVISIBLE);
                    editor.putBoolean(TABLET_REGISTRATION_MODE_PREFERENCE_NAME, false);
                    editor.commit();
                }
            }
        });


        setRegistrationMode.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(TABLET_REGISTRATION_MODE_PREFERENCE_NAME, false));
    }


    public void addViewsToTable(){
        itemsList.removeAllViews();
        for (final Stock stock : listAllStock){

            View rowView = View.inflate(this, R.layout.view_lot_number_selection_item, null);

            TextView vaccineName = (TextView) rowView.findViewById(R.id.dosage_title);
            vaccineName.setTypeface(Roboto_Regular);
            vaccineName.setText(stock.getItem());

            if(stock.getBalance()<=0){
                TextView lot = (TextView)rowView.findViewById(R.id.add_lot);
                lot.setText("Out of Stock");
                lot.setTextColor(this.getResources().getColor(R.color.red_500));
            }else {
                Cursor c = db.getReadableDatabase().rawQuery("SELECT * FROM " + SQLHandler.Tables.ACTIVE_LOT_NUMBERS + " INNER JOIN health_facility_balance ON health_facility_balance.lot_id = active_lot_numbers.lot_id WHERE  active_lot_numbers.item = '" + stock.getItem() + "' AND " +
                        GIISContract.ActiveLotNumbersColumns.DATE + " = " + gregorianCalendar.getTimeInMillis() + " AND CAST(health_facility_balance.balance as REAL) > " + 0 + "", null);

                int count = c.getCount();

                for (int i = 0; i < count; i++) {
                    c.moveToPosition(i);
                    View lotView = View.inflate(this, R.layout.view_lot_item, null);
                    TextView v = (TextView) lotView.findViewById(R.id.name);
                    v.setText(c.getString(c.getColumnIndex(GIISContract.ActiveLotNumbersColumns.LOT_NUMBER)));
                    v.setTypeface(Roboto_Regular);

                    final String item = stock.getItem();
                    final String lotNum = c.getString(c.getColumnIndex("lot_number"));
                    final String lotId = c.getString(c.getColumnIndex(GIISContract.ActiveLotNumbersColumns.LOT_ID));
                    lotView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int result = db.getWritableDatabase().delete(SQLHandler.Tables.ACTIVE_LOT_NUMBERS,
                                    GIISContract.ActiveLotNumbersColumns.ITEM + " = '" + stock.getItem() + "' AND "
                                            + GIISContract.ActiveLotNumbersColumns.LOT_ID + " = '" + lotId + "' AND "
                                            + GIISContract.ActiveLotNumbersColumns.DATE + " = " + gregorianCalendar.getTimeInMillis(), null);
                            Log.d(TAG, "deleted lot no = " + lotNum + " result = " + result);
                            Log.d(TAG, "deleted where = " +
                                    GIISContract.ActiveLotNumbersColumns.ITEM + " = '" + stock.getItem() + "' AND "
                                    + GIISContract.ActiveLotNumbersColumns.LOT_ID + " = '" + lotId + "' AND "
                                    + GIISContract.ActiveLotNumbersColumns.DATE + " = " + gregorianCalendar.getTimeInMillis());

                            if (fragment != null) {
                                fragment.fillLots(item);
                            }
                            addViewsToTable();
                        }
                    });


                    ((LinearLayout) rowView.findViewById(R.id.added_lot_numbers)).addView(lotView);
                }

                rowView.findViewById(R.id.add_lot).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle arguments = new Bundle();
                        arguments.putString(LotSelectionFragment.ARG_ITEM, stock.getItem());
                        fragment = new LotSelectionFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    }
                });
            }
            itemsList.addView(rowView);

        }
    }

    public boolean expDateValue(Date expiry) {
        Date now = new Date();
        long diff = getDaysDifference(now, expiry);
        if (diff<0){
            return true;
        }
        else{
            return false;
        }
    }

    public static final long  getDaysDifference(Date d1, Date d2){
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
    }

    private boolean checkIntegrity(){
        for (Stock stock : listStock){
            Cursor c = db.getReadableDatabase().rawQuery("SELECT active_lot_numbers.lot_id FROM "+ SQLHandler.Tables.ACTIVE_LOT_NUMBERS+" INNER JOIN health_facility_balance ON health_facility_balance.lot_id = active_lot_numbers.lot_id WHERE active_lot_numbers.item = '"+stock.getItem()+"' AND "+
                    GIISContract.ActiveLotNumbersColumns.DATE+" = "+gregorianCalendar.getTimeInMillis()+" AND CAST(health_facility_balance.balance as REAL) > "+0+"",null);
            if(c.getCount()==0){
                return false;
            }
        }
        return true;
    }

    static class FlashDialogue extends AsyncTask<Void,Void,Void> {
        private Context context;
        Dialog dialog;

        public FlashDialogue(Context context){
            this.context = context;
            dialog = new Dialog(context);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View view = View.inflate(context, R.layout.custom_info_dialogue, null);
            ((TextView)view.findViewById(R.id.inform)).setText("Please add Lot Numbers to all \nrelevant doses before continuing");
            TextView info = (TextView) view.findViewById(R.id.inform);
            info.setTypeface(HomeActivityRevised.Rosario_Regular);
            dialog.setContentView(view);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.hide();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
