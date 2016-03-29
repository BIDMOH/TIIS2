package mobile.giis.app.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

import mobile.giis.app.R;
import mobile.giis.app.SubClassed.mBarDataSet;
import mobile.giis.app.SubClassed.nBarDataSet;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.entity.ScheduledVaccination;
import mobile.giis.app.util.MyMarkerView;

/**
 *  Created by issymac::.. on 10/02/16 .
 */
public class VaccinationCoverageFragment extends android.support.v4.app.Fragment {

    private static final String ARG_POSITION = "position";

    protected BarChart mChart;
    protected ArrayList<BarEntry> entries;
    protected ArrayList<String> labels;
    protected mBarDataSet dataset;
    protected BarData data;

    public static VaccinationCoverageFragment newInstance() {
        VaccinationCoverageFragment f = new VaccinationCoverageFragment();
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
        root = inflater.inflate(R.layout.fragment_vaccination_coverage, null);

        //::..
        setUpViews(root);

//        createXYValues();
//
//        YAxis leftYAxis     = mChart.getAxisLeft();
//        YAxis rightYAxis    = mChart.getAxisRight();
//        leftYAxis   .setAxisMaxValue(100f+1f);
//        rightYAxis  .setAxisMaxValue(100f+1f);
//
//        dataset     = new mBarDataSet(entries, "Stock Status", maximumIndex);
//        dataset     .setColors(new int[]{this.getActivity().getResources().getColor(R.color.red_500), this.getActivity().getResources().getColor(R.color.orange_500), this.getActivity().getResources().getColor(R.color.green_500)});
//
//        data    = new BarData(labels, dataset);
//        mChart  .setData(data);
//        mChart  .animateY(500);

        initChart();
        renderChartData();

        return root;
    }

    private void setUpViews(View v){
        mChart              = (BarChart)        v.findViewById(R.id.vaccination_coverage_chart);
    }

    private void initChart(){

        mChart.setDescription("");

//        mChart.setDrawBorders(true);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawBarShadow(false);

        mChart.setDrawGridBackground(false);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(this.getActivity(), R.layout.custom_marker_view);

        // define an offset to change the original position of the marker
        // (optional)
        // mv.setOffsets(-mv.getMeasuredWidth() / 2, -mv.getMeasuredHeight());

        // set the marker to the chart
        mChart.setMarkerView(mv);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        l.setTextSize(18f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(25f);

        mChart.getAxisRight().setEnabled(false);
    }

    public void renderChartData() {

        BackboneApplication app = (BackboneApplication) VaccinationCoverageFragment.this.getActivity().getApplication();
        DatabaseHandler mydb = app.getDatabaseInstance();
        List<ScheduledVaccination> list = mydb.getAllScheduledVaccination();
        ArrayList<String> xVals = new ArrayList<String>();

        ArrayList<BarEntry> percentageVals = new ArrayList<BarEntry>();
        for (int i = 0; i < list.size(); i++) {
            ScheduledVaccination item = list.get(i);
            xVals.add(item.getName());
            percentageVals.add(new BarEntry((float)mydb.getCoveragePercentage(app.getLOGGED_IN_USER_HF_ID(),item.getId()), i));
        }

        nBarDataSet set1 = new nBarDataSet(percentageVals, app.getString(R.string.vaccination_coverage));
//        set1.setColor(Color.rgb(109, 164, 213));
        set1.setColors(new int[]{this.getActivity().getResources().getColor(R.color.red_500), this.getActivity().getResources().getColor(R.color.yellow_500), this.getActivity().getResources().getColor(R.color.green_500)});

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueFormatter(new LargeValueFormatter());



        mChart.setData(data);
        mChart.getAxis(YAxis.AxisDependency.LEFT).setAxisMaxValue(100f);
        mChart.getAxis(YAxis.AxisDependency.LEFT).setLabelCount(20, true);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.setPadding(0, (int) (getResources().getDimension(R.dimen.ten_dp_height)), 0, 0);
        //mChart.setVisibleYRange(100f , YAxis.AxisDependency.LEFT);
        mChart.animateY(500);
        mChart.invalidate();
    }

}
