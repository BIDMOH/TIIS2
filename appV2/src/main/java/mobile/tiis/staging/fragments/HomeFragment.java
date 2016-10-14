package mobile.tiis.staging.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import mobile.tiis.staging.AdministerVaccineOfflineRevisedActivity;
import mobile.tiis.staging.ChildDetailsActivity;
import mobile.tiis.staging.ChildSynchronizationListener;
import mobile.tiis.staging.HomeActivityRevised;
import mobile.tiis.staging.R;
import mobile.tiis.staging.SubClassed.mBarDataSet;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.database.SQLHandler;
import mobile.tiis.staging.entity.Child;
import mobile.tiis.staging.entity.HealthFacility;
import mobile.tiis.staging.entity.Stock;
import mobile.tiis.staging.helpers.Utils;
import mobile.tiis.staging.util.MyMarkerView;

/**
 *  Created by issymac on 26/01/16.
 */

public class HomeFragment extends android.support.v4.app.Fragment implements View.OnClickListener, ChildSynchronizationListener{
    private static final String TAG = HomeFragment.class.getSimpleName();
    protected BarChart mChart;
    protected ArrayList<BarEntry> entries;
    protected ArrayList<String> labels;
    protected BarDataSet dataset;
    protected BarData data;

    MaterialEditText txtBarcode;

    private float maximumIndex = 0;

    AlertDialog ad;

    HomeActivityRevised homeActivityRevised;

    ProgressDialog add;

    private String handleBarcode = "";

    int found = 0;

    int parseChildByBarcode = 0;

    private String origine;

    private RelativeLayout overlay;

    private ImageView ivCheckIn;

    private ImageButton scanUsingCamera;

    Button  searchButton;
    private Thread thread;
    public static final int[] VORDIPLOM_COLORS = {
            Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
            Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
    };
    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Intent childDetailsActivity = new Intent(HomeFragment.this.getActivity(), ChildDetailsActivity.class);
            childDetailsActivity.putExtra("barcode", handleBarcode);

            ChildDetailsFragment childDetailsFragment = new ChildDetailsFragment();
            Bundle args = new Bundle();
            args.putString("barcode", handleBarcode);
            childDetailsFragment.setArguments(args);

            switch (msg.what) {
                case 10:
                    add.show();
                    break;
                case 2:
                    //Toast.makeText(ScanHandlerActivity.this, "Status 2", Toast.LENGTH_SHORT).show();
                    //myHandler.sendEmptyMessage(10);
                    add.dismiss();

                    final AlertDialog ad2 = new AlertDialog.Builder(HomeFragment.this.getActivity()).create();
                    ad2.setTitle("Not Found");
                    ad2.setMessage("The inserted barcode does not belong to any child.\nPlease try registering child.");
                    ad2.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ad2.dismiss();
                        }
                    });
                    ad2.show();
                    break;
                case 3:
                    final AlertDialog ad3 = new AlertDialog.Builder(HomeFragment.this.getActivity()).create();
                    ad3.setTitle(getString(R.string.error));
                    ad3.setMessage(getString(R.string.network_connectivity));
                    ad3.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ad3.dismiss();
                        }
                    });
                    ad3.show();
                    //Toast.makeText(ScanHandlerActivity.this, "Status 3", Toast.LENGTH_SHORT).show();
                    //myHandler.sendEmptyMessage(10);
                    add.dismiss();
                    break;
                default:
                    found = 1;
                    //myHandler.sendEmptyMessage(10);
                    add.dismiss();
                    startActivity(childDetailsActivity);
//                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.replace(R.id.content_frame, childDetailsFragment, "detailsFrag");
//                    ft.addToBackStack("detailsFrag");
//                    ft.commit();

                    //Snackbar.make((FrameLayout)parent, "scan button pressed", Snackbar.LENGTH_LONG).show();
                    //Toast.makeText(ScanHandlerActivity.this, "Child found", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home, null);
        setUpView(root);
        initChart();
        renderChartData();

        BackboneApplication app = (BackboneApplication) HomeFragment.this.getActivity().getApplication();
        app.LAST_FRAGMENT_TITLE = getString(R.string.home);

        homeActivityRevised = (HomeActivityRevised) getActivity();

        Bundle extras = HomeFragment.this.getActivity().getIntent().getExtras();
        if (extras != null) {
            origine = extras.getString("origine");
        }

        txtBarcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, android.view.KeyEvent keyEvent) {
                if (keyEvent.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        //case android.view.KeyEvent.KEYCODE_DPAD_CENTER:
                        case android.view.KeyEvent.KEYCODE_ENTER:
                            if (Utils.isStringBlank(txtBarcode.getText().toString())) {
                                Toast.makeText(getActivity(), "No barcode inputted", Toast.LENGTH_LONG).show();
                            } else {
                                String contents = txtBarcode.getText().toString();
                                onBarcodeInput(contents);
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        txtBarcode.requestFocus();

        searchButton.setOnClickListener(this);
        scanUsingCamera.setOnClickListener(this);

        return root;
    }

    public boolean searchButtonClicked(){

        if(!(txtBarcode.getText().length() > 0)){
            txtBarcode.setError("Scan or Enter the barcode first");
            txtBarcode.setErrorColor(Color.RED);
            return false;
        }
        else{
            return true;
        }

    }

    public void setUpView(View v){
        mChart              = (BarChart)            v.findViewById(R.id.home_stock_chart);
        txtBarcode          = (MaterialEditText)    v.findViewById(R.id.met_home_barcode);
        searchButton        = (Button)              v.findViewById(R.id.search_btn);
        overlay             = (RelativeLayout)      v.findViewById(R.id.overlay);
        ivCheckIn           = (ImageView)           v.findViewById(R.id.iv_check_in);
        scanUsingCamera     = (ImageButton)         v.findViewById(R.id.scan_camera_button);
    }

    @Override
    public void onClick(View v) {


        //respond to click

        if (v.getId() == R.id.search_btn) {
            //log to see if it captures the user clicks
            Log.d("PATH", "Click is here... ");
            if(searchButtonClicked()){
                Log.d("PATH", "EditText is soo not empty... ");
                String contents = txtBarcode.getText().toString();
                onBarcodeInput(contents);
            }
        }

        if(v.getId() == R.id.scan_camera_button){
            Log.d("BAKTRAK", "Scan instantiated");
        }

    }

    private void initChart(){

        mChart.setDescription("");

//        mChart.setDrawBorders(true);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawBarShadow(false);

        mChart.setDrawGridBackground(false);

        mChart.setDrawValueAboveBar(true);


        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mChart.setMarkerView(mv);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setTextSize(18f);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(true);
        leftAxis.setDrawAxisLine(false); // no axis line
        leftAxis.setDrawGridLines(false); // no grid lines
        leftAxis.setDrawZeroLine(true); // draw a zero line
        mChart.getAxisRight().setEnabled(false); // no right axis


        XAxis yAxis = mChart.getXAxis();
        yAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        yAxis.setTextSize(10f);
        yAxis.setTextColor(Color.BLUE);
        yAxis.setDrawAxisLine(true);
        yAxis.setDrawGridLines(false);

        mChart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        mChart.animateY(2500);

        mChart.getLegend().setEnabled(false);


        mChart.getAxisRight().setEnabled(false);
    }

    public void renderChartData() {

        BackboneApplication app = (BackboneApplication) HomeFragment.this.getActivity().getApplication();
        DatabaseHandler mydb = app.getDatabaseInstance();
        List<Stock> listStock = mydb.getAllHealthFacilityBalance();
        ArrayList<String> xVals = new ArrayList<String>();

        ArrayList<BarEntry> balanceVals = new ArrayList<BarEntry>();
        ArrayList<BarEntry> reorderVals = new ArrayList<BarEntry>();
        ArrayList<Float> thresholds = new ArrayList<>();
        float maxValue = 0;
        for (int i = 0; i < listStock.size(); i++) {
            Stock item = listStock.get(i);
            xVals.add(item.getItem());

            float n = Integer.parseInt(item.getReorderQty());
            thresholds.add(n);

            if (n > maxValue){
                maxValue = n;
            }
            balanceVals.add(new BarEntry((float)item.getBalance(), i));

            reorderVals.add(new BarEntry(Float.parseFloat(item.getReorderQty()), i));
        }

        Log.e("sample", "maximum value is  : "+maxValue);


        mBarDataSet set1 = new mBarDataSet(balanceVals, app.getString(R.string.balance), thresholds);
        set1.setColors(new int[]{this.getActivity().getResources().getColor(R.color.red_500), this.getActivity().getResources().getColor(R.color.yellow_500), this.getActivity().getResources().getColor(R.color.green_500)});
        set1.setDrawValues(true);



        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
//        dataSets.add(set2);

        BarData data = new BarData(xVals, dataSets);
        data.setValueFormatter(new LargeValueFormatter());

        mChart.setData(data);
        mChart.animateY(2500);
        mChart.invalidate();
    }

    public void onBarcodeInput(final String barcode) {

        Log.d("PATH", "And This : "+barcode+" is the barcode");

        if (barcodeCheck(barcode)) {
            Log.d(TAG, "Passed the barcode Check");
            handleBarcode = barcode;
            final BackboneApplication app = (BackboneApplication) HomeFragment.this.getActivity().getApplication();
            Intent childDetailsActivity = new Intent(getContext(), ChildDetailsActivity.class);
            childDetailsActivity.putExtra("barcode", barcode);

            //check database for current barcode
            DatabaseHandler mydb = app.getDatabaseInstance();
            Child c = mydb.getChildByBarcode(barcode);

            if (c!=null) {
                Log.d(TAG, "child found locally");
                String village_id = c.getDomicileId();


                Cursor cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM place WHERE ID=?", new String[]{village_id});
                if (!(cursor.getCount() > 0)) {
                    pullPlaceFromServer task = new pullPlaceFromServer();
                    task.execute(village_id);
                }

                cursor.close();
                startActivity(childDetailsActivity);
            } else {
                if (Utils.isOnline(getContext())) {
                    add = new ProgressDialog(getContext());
                    add.setTitle(getString(R.string.searching_online));
                    add.setMessage("Barcode not found locally.\nPlease wait for server results ...");
                    add.setCanceledOnTouchOutside(false);
                    add.setCancelable(false);

                    myHandler.sendEmptyMessage(10);
                    //Parse child from server.
                    ChildSynchronization task = new ChildSynchronization(HomeFragment.this);
                    task.execute(barcode);
                } else {
                    Intent intent = new Intent(HomeFragment.this.getActivity(), AdministerVaccineOfflineRevisedActivity.class);
                    intent.putExtra("barcode", barcode);
                    startActivity(intent);
                }
            }
        } else {
            Toast.makeText(getContext(), "Wrong barcode format", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onTaskCompleted(int result) {
        parseChildByBarcode = result;
    }


    /**
     * This method is used to set an image resource to an image view and hide its container after some time has passed
     *
     * @param delay      the delay
     * @param resourceId the resourceId
     * @param iv         the imageView to which to set the resource
     */
    private void delayVisibilityGoneChange(final long delay, final int resourceId, final ImageView iv, final ViewGroup vg, final String alertTitle, final String alertContent) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageResource(resourceId);
                        }
                    });
                    sleep(delay);
                    HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog ad = new AlertDialog.Builder(getContext()).create();
                            ad.setTitle(alertTitle);
                            ad.setMessage(alertContent);
                            ad.setButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ad.dismiss();
                                }
                            });
                            ad.show();
                            vg.setVisibility(View.GONE);
                            iv.setImageResource(R.drawable.on_check_in);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private class ChildSynchronization extends AsyncTask<String, Void, Integer> {

        private ChildSynchronizationListener listener;

        public ChildSynchronization(ChildSynchronizationListener listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(String... params) {
            Log.d(TAG,"passing child from server");
            BackboneApplication application = (BackboneApplication) HomeFragment.this.getActivity().getApplication();
            int parseChildByBarcode = 0;
            String village_id, hf_id;
            for (String barcode : params) {
                parseChildByBarcode = application.parseChildCollectorSearchByBarcode(barcode);
                if (parseChildByBarcode != 2 && parseChildByBarcode != 3) {
                    DatabaseHandler db = application.getDatabaseInstance();

                    parseHFIDWhenNotInDb(db, application);

                    Cursor cursor = null;
                    cursor = db.getReadableDatabase().rawQuery("SELECT * FROM child WHERE BARCODE_ID=?", new String[]{barcode});
                    if (cursor.getCount() > 0) {
                        // used to fix the case of handler delivering maybe an empty message
                        handleBarcode = barcode;
                        cursor.moveToFirst();
                        village_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID));
                        hf_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.HEALTH_FACILITY_ID));

                        int found = 0;
                        List<HealthFacility> a = db.getAllHealthFacility();
                        for (HealthFacility b : a) {
                            if (b.getId().equalsIgnoreCase(hf_id)) {
                                found = 1;
                            }
                        }

                        if (found == 0) {
                            application.parseCustomHealthFacility(hf_id);
                        }

                        try {
                            if (village_id != null || !village_id.equalsIgnoreCase("0")) {
                                Log.d("Search. Parsing custom", village_id);
                                application.parsePlaceById(village_id);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return parseChildByBarcode;
        }

        @Override
        protected void onPostExecute(Integer result) {
            //super.onPostExecute(result);
            listener.onTaskCompleted(result);
            myHandler.sendEmptyMessage(result);
            try {
                txtBarcode.setText("");
            } catch (Exception e) {
            }
            //parseChildByBarcode = result;
        }
    }

    private class pullPlaceFromServer extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            BackboneApplication application = (BackboneApplication) HomeFragment.this.getActivity().getApplication();

            for (String id : params) {
                application.parsePlaceById(id);
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }
    }

    private void parseHFIDWhenNotInDb(DatabaseHandler db, BackboneApplication app){
        String hfidFoundInVaccEvOnlyAndNotInHealthFac = db.getHFIDFoundInVaccEvAndNotInHealthFac();
        if(hfidFoundInVaccEvOnlyAndNotInHealthFac != null){
            app.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
        }
    }

    public boolean barcodeCheck(String barcode) {
        Log.d("PATH", "Barcode has been checked");
        return true;
    }

    public Context getContext(){
        return HomeFragment.this.getActivity();
    }

}