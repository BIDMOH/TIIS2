package mobile.giis.app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mobile.giis.app.AdministerVaccineOfflineRevisedActivity;
import mobile.giis.app.ChildDetailsActivity;
import mobile.giis.app.ChildSynchronizationListener;
import mobile.giis.app.HomeActivity;
import mobile.giis.app.HomeActivityRevised;
import mobile.giis.app.R;
import mobile.giis.app.RegisterChildActivity;
import mobile.giis.app.ScanResultActivity;
import mobile.giis.app.SubClassed.mBarDataSet;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.entity.HealthFacility;
import mobile.giis.app.entity.Stock;
import mobile.giis.app.helpers.Utils;
import mobile.giis.app.util.MyMarkerView;

/**
 *  Created by issymac on 26/01/16.
 */

public class HomeFragment extends android.support.v4.app.Fragment implements View.OnClickListener, ChildSynchronizationListener{

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


        //respond to clicks
        if (v.getId() == R.id.manual_barcode_input_button) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(HomeFragment.this.getActivity());
            if (origine != null) {
                if (origine.equalsIgnoreCase(BackboneActivity.ACTIVITY_CHECK_IN)) {
                    BackboneApplication app = (BackboneApplication) HomeFragment.this.getActivity().getApplication();
                    app.setCurrentActivity(BackboneActivity.ACTIVITY_CHECK_IN);
                }
            }
            //TODO modified by coze
//            scanIntegrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            scanIntegrator.initiateScan();
        }

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
            IntentIntegrator scanIntegrator = IntentIntegrator.forSupportFragment(this);
            //TODO modified by coze
//            scanIntegrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            scanIntegrator.initiateScan();
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
            Log.d("PATH", "Passed the barcode Check");
            handleBarcode = barcode;
            final BackboneApplication app = (BackboneApplication) HomeFragment.this.getActivity().getApplication();

//                    ChildDetailsFragment childDetailsFragment = new ChildDetailsFragment();
//                    Bundle args = new Bundle();
//                    args.putString("barcode", barcode);
//                    childDetailsFragment.setArguments(args);

                    Intent childDetailsActivity = new Intent(getContext(), ChildDetailsActivity.class);
                    childDetailsActivity.putExtra("barcode", barcode);

                    //check database for current barcode
                    DatabaseHandler mydb = app.getDatabaseInstance();
                    Cursor cursor = null;
                    cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE BARCODE_ID=?", new String[]{barcode});
                    //Child found locally
                    if (cursor.getCount() > 0 && cursor != null) {


                        cursor.moveToFirst();
                        String[] columnNames = cursor.getColumnNames();
                        Log.d("BAKTRAK", columnNames[1]+"  ########### ");

                        String village_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID));

                        cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM place WHERE ID=?", new String[]{village_id});
                        if (!(cursor.getCount() > 0)) {
                            pullPlaceFromServer task = new pullPlaceFromServer();
                            task.execute(village_id);
                        }

                        startActivity(childDetailsActivity);
//                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        ft.replace(R.id.content_frame, childDetailsFragment, "detailsFrag");
//                        ft.addToBackStack(homeActivityRevised.CHILD_DETAILS_FRAGMENT);
//                        homeActivityRevised.currentFragment = homeActivityRevised.CHILD_DETAILS_FRAGMENT;
//                        ft.commit();

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
//                            final AlertDialog ad = new AlertDialog.Builder(this).create();
//                            ad.setTitle("Not Found");
//                            ad.setMessage("The inserted barcode does not belong to any child on the local database.\nPlease try scanning while" +
//                                    " device is online.");
//                            ad.setButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    ad.dismiss();
//                                }
//                            });
//                            ad.show();
                            //TODO: These are the new changes that are commented
//                            Toast.makeText(getContext(), "Barcode not Found, Connect to Internet", Toast.LENGTH_LONG).show();
//                            txtBarcode.setErrorColor(Color.RED);
//                            txtBarcode.setError("Barcode not Found");
//                            txtBarcode.requestFocus();

                        }
                    }
//                case BackboneActivity.ACTIVITY_CHECK_IN:
//                    //Do foreground processp
////                    overlay.setVisibility(View.VISIBLE);
//                    thread = new Thread() {
//                        @Override
//                        public void run() {
//
//                            synchronized (this) {
//                                try {
//                                    wait(2000);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());
//
//                                try {
//                                    dateNow = URLEncoder.encode(dateNow, "utf-8");
//                                } catch (UnsupportedEncodingException e) {
//                                    e.printStackTrace();
//                                }
//                                DatabaseHandler db = app.getDatabaseInstance();
//                                // check if child is in DB , if not than get child data from server
//                                if (!db.isChildInDB(barcode)) {
//                                    app.updateVaccinationQueue(barcode, app.getLOGGED_IN_USER_HF_ID(), dateNow, app.getLOGGED_IN_USER_ID());
//
//                                    app.registerAudit(BackboneApplication.CHILD_AUDIT, barcode, dateNow, app.getLOGGED_IN_USER_ID(), BackboneApplication.ACTION_CHECKIN);
//
//                                    int parseResult = app.parseChildCollectorSearchByBarcode(barcode);
//                                    if (parseResult == 2) {
//                                        HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                delayVisibilityGoneChange(2000, R.drawable.on_check_in_failed, ivCheckIn, overlay, getString(R.string.not_found), getString(R.string.barcode_does_not_exist));
//                                                txtBarcode.setText("");
//                                            }
//                                        });
//                                        return;
//                                    } else if (parseResult == 3) {
//                                        HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                delayVisibilityGoneChange(2000, R.drawable.on_check_in_failed, ivCheckIn, overlay, getString(R.string.msg_error), getString(R.string.error_retrieving_child_data));
//                                                txtBarcode.setText("");
//                                            }
//                                        });
//                                        return;
//                                    }
//
//                                    parseHFIDWhenNotInDb(db, app);
//
//
//                                }
//                                // this should never be null in this part of the app according to the lines of code above
//                                String childId = db.getChildIdByBarcode(barcode);
//                                if (db.isChildToBeAddedInVaccinationQueue(childId)) {
//
//                                    ContentValues cv = new ContentValues();
//                                    cv.put(SQLHandler.VaccinationQueueColumns.CHILD_ID, childId);
//                                    cv.put(SQLHandler.VaccinationQueueColumns.DATE, dateNow);
//
//                                    if (db.addChildToVaccinationQueue(cv) > -1) {
//                                        app.updateVaccinationQueue(barcode, app.getLOGGED_IN_USER_HF_ID(), dateNow, app.getLOGGED_IN_USER_ID());
//                                    }
//                                } else {
//                                    HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(getContext(), "Child was not to be added to queue", Toast.LENGTH_LONG).show();
//                                            overlay.setVisibility(View.GONE);
//                                            txtBarcode.setText("");
//                                        }
//                                    });
//                                }
//
//                                //
//
//                                app.registerAudit(BackboneApplication.CHILD_AUDIT, barcode, dateNow, app.getLOGGED_IN_USER_ID(), BackboneApplication.ACTION_CHECKIN);
//
//                                HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getContext(), "Child checkin finished", Toast.LENGTH_LONG).show();
////                                        overlay.setVisibility(View.GONE);
//                                        txtBarcode.setText("");
//                                    }
//                                });
//
//                            }
//                        }
//                    };
//
//                    thread.start();
//
//                    //Do the backgroud app logic
//                    break;
//                case BackboneActivity.ACTIVITY_REGISTER_CHILD_SCAN:
//                    Intent register_child_scan = new Intent(getContext(), RegisterChildActivity.class);
//                    register_child_scan.putExtra("result", barcode);
//                    startActivity(register_child_scan);
//                    break;
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e("onActivityResult", "onActivityResult");
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            if (scanContent != null) {
                BackboneApplication app = (BackboneApplication) HomeFragment.this.getActivity().getApplication();
                switch (app.getCurrentFragment()) {
                    case BackboneActivity.FRAGMENT_HOME:

                        Intent childDetailsActivity = new Intent(getContext(), ChildDetailsActivity.class);
                        childDetailsActivity.putExtra("barcode", scanContent);

//                        Intent scan = new Intent(HomeFragment.this.getActivity(), ScanResultActivity.class);
//                        scan.putExtra("barcode", scanContent);

                        DatabaseHandler mydb = app.getDatabaseInstance();
                        Cursor cursor = null;
                        cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE BARCODE_ID=?", new String[]{scanContent});
                        //Child found locally
                        if (cursor.getCount() > 0 && cursor != null) {


                            cursor.moveToFirst();
                            String village_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID));

                            if(village_id!=null)
                            { cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM place WHERE ID=?", new String[]{village_id});
                                if (!(cursor.getCount() > 0)) {
                                    pullPlaceFromServer task = new pullPlaceFromServer();
                                    task.execute(village_id);
                                }
                            }


                            startActivity(childDetailsActivity);
                        } else {
                            if (Utils.isOnline(HomeFragment.this.getActivity())) {
                                add = new ProgressDialog(HomeFragment.this.getActivity());
                                add.setTitle(getString(R.string.searching_online));
                                add.setMessage(getString(R.string.barcode_not_found_locally));
                                add.setCanceledOnTouchOutside(false);
                                add.setCancelable(false);

                                myHandler.sendEmptyMessage(10);
                                //Parse child from server.
                                ChildSynchronization task = new ChildSynchronization(this);
                                task.execute(scanContent);
                            } else {
//                                final AlertDialog ad = new AlertDialog.Builder(this).create();
//                                ad.setTitle("Not Found");
//                                ad.setMessage("The inserted barcode does not belong to any child on the local database.\nPlease try scanning while" +
//                                        " device is online.");
//                                ad.setButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        ad.dismiss();
//                                    }
//                                });
//                                ad.show();
//                                startActivity(scan);
                                Toast.makeText(getContext(), "Barcode not Found, Connect to Internet", Toast.LENGTH_LONG).show();
                                txtBarcode.setErrorColor(Color.RED);
                                txtBarcode.setError("Barcode not Found");
                                txtBarcode.requestFocus();

                            }
                        }
                        break;
//                    case ACTIVITY_CHECK_IN:
//                        onBarcodeInput(scanContent);
//                        break;
//                    case ACTIVITY_REGISTER_CHILD_SCAN:
//                        Intent register_child_scan = new Intent(getApplicationContext(), RegisterChildActivity.class);
//                        register_child_scan.putExtra("result", scanContent);
//                        startActivity(register_child_scan);
//                        break;
                }
            }
            //TODO check the Zxing library,check this link for solution of this bug: https://github.com/journeyapps/zxing-android-embedded/issues/42
        }


    }

}