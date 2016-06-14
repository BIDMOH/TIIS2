package mobile.tiis.app.fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mobile.tiis.app.R;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.entity.ChartDataModel;
import mobile.tiis.app.util.MyMarkerView;

/**
 * Created by issymac on 15/04/16.
 */
public class ImmunizationChartFragment extends Fragment implements OnChartValueSelectedListener {

    protected BarChart mChart;
    protected BarData data;
    private DatabaseHandler mydb;
    private BackboneApplication app;
    String dataFromDataPicker;
    ProgressBar loadingBar;
    CardView chartCard;

    public static ImmunizationChartFragment newInstance() {
        ImmunizationChartFragment f = new ImmunizationChartFragment();
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
        root = inflater.inflate(R.layout.immunization_chart_fragment, null);
        //::..
        setUpViews(root);

        app = (BackboneApplication) this.getActivity().getApplication();
        mydb = app.getDatabaseInstance();

        new stallRendering().execute();

        return root;
    }

    class stallRendering extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            chartCard.setVisibility(View.GONE);
            loadingBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            chartCard.setVisibility(View.VISIBLE);
            loadingBar.setVisibility(View.GONE);
            ImmunizationChartSnippets(dataFromDataPicker);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void setUpViews(View v){
        mChart                  = (BarChart)        v.findViewById(R.id.immunization_chart);
        chartCard               = (CardView)        v.findViewById(R.id.chart_card);
        loadingBar              = (ProgressBar)     v.findViewById(R.id.loading_bar);
    }

    public void ImmunizationChartSnippets(String mDate){
        mChart.setOnChartValueSelectedListener(ImmunizationChartFragment.this);
        mChart.setDescription("");
        mChart.setPinchZoom(false);
        mChart.setPinchZoom(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
        mChart.setDescriptionTextSize(16f);

        MyMarkerView mv = null;

        try {
             mv = new MyMarkerView(ImmunizationChartFragment.this.getActivity(), R.layout.custom_marker_view);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        mChart.setMarkerView(mv);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
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
        yAxis.setTextSize(14f);
        yAxis.setTextColor(Color.RED);
        yAxis.setDrawAxisLine(true);
        yAxis.setDrawGridLines(false);


        mChart.getAxisRight().setEnabled(false);


        renderChart();

    }

    private void  renderChart(){
        List<ChartDataModel> listItems = mydb.getMonthlyPerformance(app);
        ArrayList<String> xVals = new ArrayList<String>();

        ArrayList<BarEntry> noVals = new ArrayList<BarEntry>();
        ArrayList<BarEntry> cummNoVals = new ArrayList<BarEntry>();

        for (int i = 0; i < listItems.size(); i++) {
            ChartDataModel item = listItems.get(i);
            xVals.add(item.getLabel());

            noVals.add(new BarEntry((float)item.getValue(), i));

            cummNoVals.add(new BarEntry((float)summPreviousEntries(listItems, i), i));
        }

        BarDataSet set1 = new BarDataSet(noVals, app.getString(R.string.no_of_children_immunized));
        // set1.setColors(ColorTemplate.createColors(getApplicationContext(),
        // ColorTemplate.FRESH_COLORS));
        set1.setColor(Color.rgb(109, 164, 213));
        BarDataSet set2 = new BarDataSet(cummNoVals, app.getString(R.string.cumm_no_children_immunized));
        set2.setColor(Color.rgb(235, 139, 75));

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);

        BarData data = new BarData(xVals, dataSets);
        data.setValueFormatter(new LargeValueFormatter());

        mChart.setData(data);
        mChart.invalidate();

    }

    private String getYesterdayDateStringDashyyyyMMdd() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    private int summPreviousEntries(List<ChartDataModel> list , int index){
        int sum = 0;

        for(int i = 0 ; i <= index ; i++){
            sum += list.get(i).getValue();
        }
        return sum;
    }


    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
