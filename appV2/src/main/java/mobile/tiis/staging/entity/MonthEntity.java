package mobile.tiis.staging.entity;

/**
 * Created by issy on 10/20/16.
 */

public class MonthEntity {

    private String month_number = "";
    private String month_name = "";
    private int year;

    public MonthEntity(String monthName, String monthNumber){
        this.month_name     = monthName;
        this.month_number   = monthNumber;
    }

    public String getMonth_number() {
        return month_number;
    }

    public void setMonth_number(String month_number) {
        this.month_number = month_number;
    }

    public String getMonth_name() {
        return month_name;
    }

    public void setMonth_name(String month_name) {
        this.month_name = month_name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
