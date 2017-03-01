package mobile.tiis.app.entity;

import android.content.Intent;
import android.util.Pair;

/**
 * Created by issy on 11/28/16.
 */

public class MonthYearPair {

    Pair<Integer, Integer> monthyear;

    public MonthYearPair(){}

    public MonthYearPair(int month, int year){
        this.monthyear = new Pair<>(month, year);
    }

    public Pair<Integer, Integer> getMonthyear() {
        return monthyear;
    }

    public void setMonthyear(Pair<Integer, Integer> monthyear) {
        this.monthyear = monthyear;
    }

}
