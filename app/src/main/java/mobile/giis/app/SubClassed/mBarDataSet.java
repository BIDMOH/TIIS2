package mobile.giis.app.SubClassed;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

/**
 * Created by issymac on 27/12/15.
 */
public class mBarDataSet extends BarDataSet {

    private float axisMaximumValue;

    public mBarDataSet(List<BarEntry> yVals, String label, float maxValue){
        super(yVals, label);
        axisMaximumValue = maxValue;
    }

    @Override
    public int getColor(int index) {
        if(getEntryForXIndex(index).getVal() < (0.20*axisMaximumValue)) // less than 25% red
            return mColors.get(0);
        else if(getEntryForXIndex(index).getVal() < (0.50*axisMaximumValue)) // less than 50% orange
            return mColors.get(1);
        else // less or equal to 100% Green
            return mColors.get(2);
    }

}
