package mobile.tiis.appV2.SubClassed;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

/**
 * Created by issymac on 27/12/15.
 */
public class mBarDataSet extends BarDataSet {

    private List<Float> axisMaximumValue;

    public mBarDataSet(List<BarEntry> yVals, String label, List<Float> maxValue){
        super(yVals, label);
        axisMaximumValue = maxValue;
    }

    @Override
    public int getColor(int index) {
        float reorder = axisMaximumValue.get(index);
        float safety = reorder * 2;
        if(getEntryForXIndex(index).getVal() < (reorder)) // less than 25% red
            return mColors.get(0);
        else if(getEntryForXIndex(index).getVal() < (safety)) // less than 50% orange
            return mColors.get(1);
        else // less or equal to 100% Green
            return mColors.get(2);
    }

}
