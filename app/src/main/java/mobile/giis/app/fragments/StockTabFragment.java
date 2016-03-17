package mobile.giis.app.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import mobile.giis.app.R;
import mobile.giis.app.SubClassed.mBarDataSet;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.entity.Stock;
import mobile.giis.app.util.MyMarkerView;

/**
 * Created by issymac on 22/12/15.
 */
public class StockTabFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    protected BarChart mChart;
    protected ArrayList<BarEntry> entries;
    protected ArrayList<String> labels;
    protected mBarDataSet dataset;
    protected BarData data;

    private float maximumIndex = 0;

    public static StockTabFragment newInstance() {
        StockTabFragment f = new StockTabFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;

        root = inflater.inflate(R.layout.new_stock_chart_layout, null);

        //::..
        setUpViews(root);

        initChart();
        renderChartData();

//        createXYValues();

//        YAxis leftYAxis = mChart.getAxisLeft();
//        YAxis rightYAxis = mChart.getAxisRight();
//        leftYAxis.setAxisMaxValue(18f+1f);
//        rightYAxis.setAxisMaxValue(18f+1f);
//
//        dataset = new mBarDataSet(entries, "Stock Status", maximumIndex);
//        dataset.setColors(new int[]{this.getActivity().getResources().getColor(R.color.red_500), this.getActivity().getResources().getColor(R.color.orange_500), this.getActivity().getResources().getColor(R.color.green_500)});
//
//        data = new BarData(labels, dataset);
//        mChart.setData(data);
////        mChart.setDescription("# of Stocks");
//        mChart.animateY(500);

        return root;
    }

    private void setUpViews(View v){
        mChart = (BarChart) v.findViewById(R.id.stock_chart);
    }

    private void initChart(){
        mChart.setDescription("");
        //mChart.setDrawBorders(true);
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

        BackboneApplication app = (BackboneApplication) this.getActivity().getApplication();
        DatabaseHandler mydb = app.getDatabaseInstance();
        List<Stock> listStock = mydb.getAllHealthFacilityBalance();
        ArrayList<String> xVals = new ArrayList<String>();

        ArrayList<BarEntry> balanceVals = new ArrayList<BarEntry>();
        ArrayList<BarEntry> reorderVals = new ArrayList<BarEntry>();
        float maxValue = 0;
        for (int i = 0; i < listStock.size(); i++) {
            Stock item = listStock.get(i);
            xVals.add(item.getItem());
            if (item.getBalance() > maxValue){
                maxValue = item.getBalance();
            }
            balanceVals.add(new BarEntry((float)item.getBalance(), i));

            reorderVals.add(new BarEntry(Float.parseFloat(item.getReorderQty()), i));

        }

        // create 2 datasets with different types
        mBarDataSet set1 = new mBarDataSet(balanceVals, app.getString(R.string.balance), maxValue);
        // set1.setColors(ColorTemplate.createColors(getApplicationContext(),
        // ColorTemplate.FRESH_COLORS));
        set1.setColors(new int[]{this.getActivity().getResources().getColor(R.color.red_500), this.getActivity().getResources().getColor(R.color.yellow_500), this.getActivity().getResources().getColor(R.color.green_500)});
//        BarDataSet set2 = new BarDataSet(reorderVals, app.getString(R.string.reorder_qty));
//        set2.setColor(Color.rgb(235, 139, 75));

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);
//        dataSets.add(set2);

        BarData data = new BarData(xVals, dataSets);
        data.setValueFormatter(new LargeValueFormatter());

        mChart.setData(data);
        mChart.animateY(500);
        mChart.invalidate();
    }

}
