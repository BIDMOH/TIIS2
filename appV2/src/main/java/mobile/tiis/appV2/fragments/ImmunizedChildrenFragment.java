package mobile.tiis.appv2.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import mobile.tiis.appv2.CustomViews.NestedListView;
import mobile.tiis.appv2.R;
import mobile.tiis.appv2.SubClassed.mBarDataSet;
import mobile.tiis.appv2.adapters.ImmunizationsListAdapter;
import mobile.tiis.appv2.adapters.ImmunizedChildrenListAdapter;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.database.DatabaseHandler;
import mobile.tiis.appv2.entity.ChartDataModel;
import mobile.tiis.appv2.entity.ModelImmunizedChild;
import mobile.tiis.appv2.entity.NewChartDataTable;

/**
 *  Created by issymac on 09/02/16.
 */
public class ImmunizedChildrenFragment extends Fragment implements OnChartValueSelectedListener ,  DatePickerDialog.OnDateSetListener{
    private static final String ARG_POSITION = "position";
    protected BarChart mChart;
    protected ArrayList<BarEntry> entries;
    protected ArrayList<String> labels;
    protected mBarDataSet dataset;
    protected BarData data;
    private float maximumIndex = 0;
    private NestedListView immChildrenList, immunizationsList;
    private ImmunizationsListAdapter immAdapter;
    private ImmunizedChildrenListAdapter immChildAdapter;
    private DatabaseHandler mydb;
    private BackboneApplication app;
    RelativeLayout emptyImmunizedChildren, emptyImmunizations;
    TextView dateText;
    String dataFromDataPicker;
    List<ChartDataModel> listImmun;
    List<NewChartDataTable> listOfImmunizations;
    List<ModelImmunizedChild> listOfImmunizedChildren;
    List<String> listofCodes;

    View immunizationsHeaderView;

    View immChildrenListHeader;

    CardView dateTextCardWrapper;

    public static ImmunizedChildrenFragment newInstance() {
        ImmunizedChildrenFragment f = new ImmunizedChildrenFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        root = inflater.inflate(R.layout.immunized_children_fragment, null);
        //::..
        setUpViews(root);
//        createXYValues();

        immunizationsHeaderView   = inflater.inflate(R.layout.immunizations_list_title, null);
        immChildrenListHeader = inflater.inflate(R.layout.imm_children_list_item_title, null);

        app = (BackboneApplication) this.getActivity().getApplication();
        mydb = app.getDatabaseInstance();

        dataFromDataPicker = getYesterdayDateStringDashyyyyMMdd();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        String text ="";
        try {
            date = format.parse(dataFromDataPicker);
            format = new SimpleDateFormat("dd MMM yyyy");
            text = format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateText.setText("Date: " + text);

        ImmunizedChildrenSnippets(dataFromDataPicker);

        ImmunizationsSnippets(dataFromDataPicker);

        dateTextCardWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });

        return root;
    }

    private void setUpViews(View v){
        immChildrenList         = (NestedListView)  v.findViewById(R.id.immunized_children_list);
        immunizationsList       = (NestedListView)  v.findViewById(R.id.immunizations_list);
        emptyImmunizedChildren  = (RelativeLayout) v.findViewById(R.id.rl_empty_immunized_children);
        emptyImmunizedChildren  .setVisibility(View.GONE);
        emptyImmunizations      = (RelativeLayout) v.findViewById(R.id.rl_empty_immunization);
        emptyImmunizations      .setVisibility(View.GONE);

        dateText                = (TextView) v.findViewById(R.id.date_text);
        dateTextCardWrapper     = (CardView) v.findViewById(R.id.date_text_card_wrap);

    }

    public void pickDate(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ImmunizedChildrenFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setAccentColor(Color.DKGRAY);
        dpd.show(this.getActivity().getFragmentManager(), "DatePickerDialogue");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = new GregorianCalendar();
        cal.set(year, (monthOfYear), dayOfMonth);


        String displayDate = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        date = format.format(cal.getTime());
        format = new SimpleDateFormat("dd MMM yyyy");
        displayDate = format.format(cal.getTime());
        dateText.setText(displayDate);

        refreshImmunizationList(date);
        refreshImmunizedChildren(date);
//        renderChart();

    }

    public void ImmunizedChildrenSnippets(String mDate){

        listOfImmunizedChildren = mydb.getImmunizedChildren(mDate, app);

        if(!(listOfImmunizedChildren.size() > 0)){
            emptyImmunizedChildren.setVisibility(View.VISIBLE);
        }else{
            emptyImmunizedChildren.setVisibility(View.GONE);
        }
        Log.d("coze","refreshing snippet");
        immChildAdapter                 = new ImmunizedChildrenListAdapter(ImmunizedChildrenFragment.this.getActivity(), listOfImmunizedChildren);

        setListViewHeightBasedOnChildren(immChildrenList);
        immChildrenList     .addHeaderView(immChildrenListHeader);
        immChildrenList     .setAdapter(immChildAdapter);

    }

    public void ImmunizationsSnippets(String mDate){
        listOfImmunizations = new ArrayList<>();
        listOfImmunizations = mydb.getImmunizationsNew(mDate, app);

        listofCodes = new ArrayList<>();
        if (listOfImmunizations != null || !listOfImmunizations.isEmpty()){
            for(NewChartDataTable a : listOfImmunizations){
                if(listofCodes.contains(a.getLabel()));
                else {
                    listofCodes.add(a.getLabel());
                }
            }

        }

        listImmun   =   mydb.getImmunizations(mDate, app);

        setListViewHeightBasedOnChildren(immunizationsList);
        if(listImmun.isEmpty()){
            emptyImmunizations.setVisibility(View.VISIBLE);
        }else {
            emptyImmunizations  .setVisibility(View.GONE);
            immAdapter          = new ImmunizationsListAdapter(ImmunizedChildrenFragment.this.getActivity(), listofCodes, listOfImmunizations);
            immunizationsList   .setAdapter(immAdapter);

//            AdapterImmunizations adapter = new AdapterImmunizations(getActivity(), R.layout.item_listview_immunizations, listImmun);
//            immunizationsList   .setAdapter(immAdapter);

        }

    }

    private void refreshImmunizedChildren(String mDate){
        Log.d("coze","refreshing");
        listOfImmunizedChildren = mydb.getImmunizedChildren(mDate ,app);
        if(!(listOfImmunizedChildren.size() > 0)){
            emptyImmunizedChildren.setVisibility(View.VISIBLE);
        }else{
            emptyImmunizedChildren.setVisibility(View.GONE);
        }
        immChildAdapter                 = new ImmunizedChildrenListAdapter(ImmunizedChildrenFragment.this.getActivity(), listOfImmunizedChildren);
        immChildrenList.setAdapter(null);
        immChildrenList.setAdapter(immChildAdapter);

    }

    private void refreshImmunizationList(String mDate){

        listOfImmunizations = new ArrayList<>();
        listOfImmunizations = mydb.getImmunizationsNew(mDate, app);

        listofCodes = new ArrayList<>();
        if (listOfImmunizations != null || !listOfImmunizations.isEmpty()){
            for(NewChartDataTable a : listOfImmunizations){
                if(listofCodes.contains(a.getLabel()));
                else {
                    listofCodes.add(a.getLabel());
                }
            }
        }

        if(listImmun.isEmpty()){
            emptyImmunizations.setVisibility(View.VISIBLE);
        }else {
            emptyImmunizations  .setVisibility(View.GONE);

            immAdapter          = new ImmunizationsListAdapter(ImmunizedChildrenFragment.this.getActivity(), listofCodes, listOfImmunizations);
            immunizationsList   .setAdapter(null);
            immunizationsList   .setAdapter(immAdapter);

//            AdapterImmunizations adapter = new AdapterImmunizations(getActivity(), R.layout.item_listview_immunizations, listImmun);
//            immunizationsList   .setAdapter(immAdapter);

        }
    }

//    private void  renderChart(){
//        List<ChartDataModel> listItems = mydb.getMonthlyPerformance(appv2);
//        ArrayList<String> xVals = new ArrayList<String>();
//
//        ArrayList<BarEntry> noVals = new ArrayList<BarEntry>();
//        ArrayList<BarEntry> cummNoVals = new ArrayList<BarEntry>();
//
//        for (int i = 0; i < listItems.size(); i++) {
//            ChartDataModel item = listItems.get(i);
//            xVals.add(item.getLabel());
//
//            noVals.add(new BarEntry((float)item.getValue(), i));
//
//            cummNoVals.add(new BarEntry((float)summPreviousEntries(listItems, i), i));
//        }
//
//        BarDataSet set1 = new BarDataSet(noVals, appv2.getString(R.string.no_of_children_immunized));
//        // set1.setColors(ColorTemplate.createColors(getApplicationContext(),
//        // ColorTemplate.FRESH_COLORS));
//        set1.setColor(Color.rgb(109, 164, 213));
//        BarDataSet set2 = new BarDataSet(cummNoVals, appv2.getString(R.string.cumm_no_children_immunized));
//        set2.setColor(Color.rgb(235, 139, 75));
//
//        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
//        dataSets.add(set1);
//        dataSets.add(set2);
//
//        BarData data = new BarData(xVals, dataSets);
//        data.setValueFormatter(new LargeValueFormatter());
//
//        mChart.setData(data);
//        mChart.invalidate();
//
//    }

//    private void createXYValues(){
//
//        entries = new ArrayList<>();
//        entries.add(new BarEntry(21f, 0));
//        entries.add(new BarEntry(21f, 1));
//        entries.add(new BarEntry(70f, 2));
//        entries.add(new BarEntry(91f, 3));
//
//        //According to the maximum value of the Y axis set the maximumIndex value
//        maximumIndex = 18;
//
//        labels = new ArrayList<String>();
//        labels.add("January");
//        labels.add("February");
//        labels.add("March");
//        labels.add("April");
//
//    }

    private String getYesterdayDateStringDashyyyyMMdd() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private int summPreviousEntries(List<ChartDataModel> list , int index){
        int sum = 0;

        for(int i = 0 ; i <= index ; i++){
            sum += list.get(i).getValue();
        }
        return sum;
    }

}
