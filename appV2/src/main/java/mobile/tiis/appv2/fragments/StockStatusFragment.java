package mobile.tiis.appv2.fragments;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.trello.rxlifecycle.components.support.RxFragment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.appv2.R;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.database.DatabaseHandler;
import mobile.tiis.appv2.database.SQLHandler;
import mobile.tiis.appv2.entity.StockStatusEntity;
import mobile.tiis.appv2.util.BackgroundThread;
import mobile.tiis.appv2.adapters.ReportPeriodSpinnerAdapter;
import mobile.tiis.appv2.entity.MonthYearPair;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;


/**
 *  Created by issy on 10/18/16.
 */

public class StockStatusFragment extends RxFragment {

    //UI Elements
    private TableLayout stockStatusTable;
    private MaterialSpinner reportingPeriod;
    private Button generateReport;
    private  TextView healthFacility;

    List<String> monthYear = new ArrayList<>();
    List<MonthYearPair> monthYearPairs = new ArrayList<>();
    MonthYearPair selectedMonth;
    List<StockStatusEntity> stockStatusEntities = new ArrayList<>();

    private Looper backgroundLooper;

    BackboneApplication app;
    DatabaseHandler databaseHandler;

    public static StockStatusFragment newInstance() {
        StockStatusFragment f = new StockStatusFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    ProgressDialog ringProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        int mYear = cal.get(Calendar.YEAR);

        cal.add(Calendar.YEAR, -1);
        int lastYear = cal.get(Calendar.YEAR);

        int[] years = {mYear, lastYear};

        for (int year: years){
            for (int i = 11; i>=0; i--){
                if(year==mYear){
                    Calendar c= Calendar.getInstance();
                    int currentMonth =  c.get(Calendar.MONTH);
                    if(currentMonth >= i){
                        monthYearPairs.add(new MonthYearPair(i+1, year));
                    }
                }else {
                    monthYearPairs.add(new MonthYearPair(i + 1, year));
                }
            }
        }

        app = (BackboneApplication) StockStatusFragment.this.getActivity().getApplication();
        databaseHandler = app.getDatabaseInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        root = inflater.inflate(R.layout.stock_status_report, null);
        setUpViews(root);

        final ReportPeriodSpinnerAdapter adapter   = new ReportPeriodSpinnerAdapter(StockStatusFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, monthYearPairs);
        reportingPeriod.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = calendar.get(Calendar.MONTH);
        thisMonth+=1;

        String month = databaseHandler.getCurrentMonthName(app);

        for (int i = 0; i<monthYearPairs.size(); i++){
            if (monthYearPairs.get(i).equals(new MonthYearPair(thisMonth, thisYear))){
                reportingPeriod.setSelection(i+1);
                selectedMonth = monthYearPairs.get(i);
            }
        }

        reportingPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = (MonthYearPair) adapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        healthFacility.setText(app.getDatabaseInstance().getHealthCenterName(app.getLOGGED_IN_USER_HF_ID()));

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();

        generateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStockInformationFromServer();
            }
        });

        return root;

    }

    public void getStockInformationFromServer(){

        ringProgressDialog = ProgressDialog.show(StockStatusFragment.this.getActivity(), "Please wait ...", "Loading Report ...", true);

        Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                String fromDate, toDate, reportingMonth;
                Date startDate, endDate;
                fromDate = "";
                toDate  = "";
                reportingMonth = "";
                Pair<Date, Date> starEndDatePair = getDateRange();
                startDate   = starEndDatePair.first;
                endDate     = starEndDatePair.second;
                reportingMonth = selectedMonth.getMonthyear().first+" "+selectedMonth.getMonthyear().second;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                fromDate    = dateFormat.format(startDate);
                toDate      = dateFormat.format(endDate);

                Log.d("monthstartandenddate", fromDate+" to "+toDate);

                // Do some long running operation
                // This should query Stock Information and Save it to the Stock Information Table

                app.parseStockStatusInformation(fromDate, toDate, reportingMonth);

                return Observable.just(true);
            }
        })// Run on a background thread
                .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Boolean>bindToLifecycle())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        //After completing the background job do something about the results here
                        beginGeneratingReport();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("", "onError()", e);
                    }

                    @Override
                    public void onNext(Boolean string) {
                        Log.d("", "onNext(" + string + ")");
                    }
                });
    }

    public Pair<Date, Date> getDateRange() {
        Date begining, end;

        {
            Calendar calendar = getCalendarForNow();
            calendar.set(Calendar.MONTH, selectedMonth.getMonthyear().first-1);
            calendar.set(Calendar.YEAR, selectedMonth.getMonthyear().second);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            begining = calendar.getTime();
        }

        {
            Calendar calendar = getCalendarForNow();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndofDay(calendar);
            end = calendar.getTime();
        }

        return Pair.create(begining, end);
    }

    private static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    public void beginGeneratingReport(){
//        selectedMonth = selectedMonth.getMonthyear().first+" "+selectedMonth.getMonthyear().second;
        stockStatusEntities = getDataFromStockStatusTable();

        stockStatusTable.removeAllViews();

        for (StockStatusEntity stockStatusEntity : stockStatusEntities){

            View view = LayoutInflater.from(StockStatusFragment.this.getActivity()).inflate(R.layout.stock_status_list_item, null);

            TextView itemName = (TextView) view.findViewById(R.id.antigen);
            TextView dosesReceived = (TextView) view.findViewById(R.id.doses_received);
            TextView discardedUnopened = (TextView) view.findViewById(R.id.discarded_unopened);
            TextView openedDoses = (TextView) view.findViewById(R.id.opened_doses);
            TextView immunizedChildren = (TextView) view.findViewById(R.id.immunized_children);
            TextView openingBalance = (TextView) view.findViewById(R.id.opening_balance);
            TextView closingBalance = (TextView) view.findViewById(R.id.closing_balance);
            TextView usageRate = (TextView) view.findViewById(R.id.usage_rate);
            TextView wastageRate = (TextView) view.findViewById(R.id.wastage_rate);

            itemName.setText(stockStatusEntity.getAntigen());
            dosesReceived.setText(stockStatusEntity.getDosesReceived());
            discardedUnopened.setText(stockStatusEntity.getDosesDiscardedUnopened());
            openedDoses.setText(stockStatusEntity.getDosesDiscardedOpened());
            immunizedChildren.setText(stockStatusEntity.getChildrenImmunized());
            openingBalance.setText(stockStatusEntity.getOpeningBalance());
            closingBalance.setText(stockStatusEntity.getStockOnHand());
            try {
                usageRate.setText(round(Double.parseDouble(stockStatusEntity.getUsageRate()),2)+"");
            }catch (Exception e){
                e.printStackTrace();
                usageRate.setText(stockStatusEntity.getUsageRate());
            }

            try {
                wastageRate.setText(round(Double.parseDouble(stockStatusEntity.getWastageRate()),2)+"");
            }catch (Exception e){
                e.printStackTrace();
                wastageRate.setText(stockStatusEntity.getWastageRate());
            }



            stockStatusTable.addView(view);
        }

        ringProgressDialog.hide();
        stockStatusTable.setVisibility(View.VISIBLE);

    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public List<StockStatusEntity> getDataFromStockStatusTable(){

        List<StockStatusEntity> entities = new ArrayList<>();

        String query =  "";
        query   = "SELECT * FROM "+ SQLHandler.Tables.STOCK_STATUS_REPORT+" WHERE "+ SQLHandler.StockStatusColumns.REPORTED_MONTH+ " = '"
                +(selectedMonth.getMonthyear().first)+" "+selectedMonth.getMonthyear().second+"'";
        Cursor cursor = databaseHandler.getReadableDatabase().rawQuery(query, null);
        Log.d("e", "Cursor count = "+cursor.getCount());
        if (cursor.getCount() > 0){
            if (cursor.moveToFirst()){
                do {
                    StockStatusEntity row = new StockStatusEntity();
                    row.setAntigen(cursor.getString(cursor.getColumnIndex(SQLHandler.StockStatusColumns.ITEM_NAME)));
                    row.setDosesDiscardedUnopened(cursor.getString(cursor.getColumnIndex(SQLHandler.StockStatusColumns.DISCARDED_UNOPENED)));
                    row.setDosesReceived(cursor.getString(cursor.getColumnIndex(SQLHandler.StockStatusColumns.DOSES_RECEIVED)));
                    row.setOpeningBalance(cursor.getString(cursor.getColumnIndex(SQLHandler.StockStatusColumns.OPPENING_BALANCE)));
                    row.setStockOnHand(cursor.getString(cursor.getColumnIndex(SQLHandler.StockStatusColumns.CLOSING_BALANCE)));
                    row.setDosesDiscardedOpened(cursor.getString(cursor.getColumnIndex(SQLHandler.StockStatusColumns.DISCARDED_OPENED)));
                    row.setChildrenImmunized(cursor.getString(cursor.getColumnIndex(SQLHandler.StockStatusColumns.IMMUNIZED_CHILDREN)));

                    int discardedOpened = cursor.getInt(cursor.getColumnIndex(SQLHandler.StockStatusColumns.DISCARDED_OPENED));
                    int discardedUnopened = cursor.getInt(cursor.getColumnIndex(SQLHandler.StockStatusColumns.DISCARDED_UNOPENED));
                    int imunizedchildren = cursor.getInt(cursor.getColumnIndex(SQLHandler.StockStatusColumns.IMMUNIZED_CHILDREN));

                    row.setUsageRate((imunizedchildren*100.0/(discardedOpened+discardedUnopened))+"");
                    row.setWastageRate((100 -(imunizedchildren*100.0/(discardedOpened+discardedUnopened)))+"");
                    entities.add(row);
                }while (cursor.moveToNext());
            }
        }

        return entities;
    }

    public void setUpViews(View v){
        stockStatusTable    = (TableLayout) v.findViewById(R.id.stock_status_table);
        stockStatusTable    .setVisibility(View.GONE);
        reportingPeriod     = (MaterialSpinner) v.findViewById(R.id.mon_year_spiner);
        generateReport      = (Button) v.findViewById(R.id.generate_report);

        healthFacility  = (TextView) v.findViewById(R.id.hf_value);
    }



}