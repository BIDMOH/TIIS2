package mobile.tiis.staging.SubClassed;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

/**
 * Created by issymac on 16/03/16.
 */
public class nBarDataSet extends BarDataSet {

    public nBarDataSet(List<BarEntry> yVals, String label){
        super(yVals, label);
    }

    @Override
    public int getColor(int index) {
        if(getEntryForXIndex(index).getVal() < (80f)) // less than 25% red
            return mColors.get(0);
        else if(getEntryForXIndex(index).getVal() < (90f)) // less than 50% orange
            return mColors.get(1);
        else // less or equal to 100% Green
            return mColors.get(2);
    }


}
