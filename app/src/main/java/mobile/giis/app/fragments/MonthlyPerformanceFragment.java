/*******************************************************************************
 * <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *   ~ Copyright (C)AIRIS Solutions 2015 TIIS App - Tanzania Immunization Information System App
 *   ~
 *   ~    Licensed under the Apache License, Version 2.0 (the "License");
 *   ~    you may not use this file except in compliance with the License.
 *   ~    You may obtain a copy of the License at
 *   ~
 *   ~        http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~    Unless required by applicable law or agreed to in writing, software
 *   ~    distributed under the License is distributed on an "AS IS" BASIS,
 *   ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~    See the License for the specific language governing permissions and
 *   ~    limitations under the License.
 *   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
 ******************************************************************************/

package mobile.giis.app.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import mobile.giis.app.R;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.entity.ChartDataModel;
import mobile.giis.app.util.MyMarkerView;

public class MonthlyPerformanceFragment extends DialogFragment implements OnChartValueSelectedListener {


    BarChart mChart;
    BackboneApplication app;
    DatabaseHandler mydb;
    Button btnBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_performance, container, false);

        app = (BackboneApplication) getActivity().getApplication();
        mydb = app.getDatabaseInstance();

        initChart(view);
        renderChartData();

        return view;
    }


    private void initChart(View v){
        btnBack= (Button)v.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(MonthlyPerformanceFragment.this).commit();
            }
        });
        mChart = (BarChart) v.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDescription("");

//        mChart.setDrawBorders(true);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawBarShadow(false);

        mChart.setDrawGridBackground(false);
        mChart.setDescriptionTextSize(16f);
        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);

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

        // create 2 datasets with different types
        BarDataSet set1 = new BarDataSet(noVals, app.getString(R.string.no_of_children_immunized));
        // set1.setColors(ColorTemplate.createColors(getApplicationContext(),
        // ColorTemplate.FRESH_COLORS));
        set1.setColor(Color.rgb(109, 164, 213));
        BarDataSet set2 = new BarDataSet(cummNoVals, app.getString(R.string.cumm_no_children_immunized));
        set2.setColor(Color.rgb(235, 139, 75));

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);

        BarData data = new BarData(xVals, dataSets);
        data.setValueFormatter(new LargeValueFormatter());



        mChart.setData(data);
        mChart.invalidate();
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

