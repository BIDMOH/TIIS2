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

package mobile.tiis.app.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Melisa on 08/02/2015.
 */
public class BoundedDatePickerDialog extends DatePickerDialog {

    private final Calendar calendar;
    private final SimpleDateFormat dateFormat;

    private int minYear;
    private int minMonth;
    private int minDay;

    private int maxYear;
    private int maxMonth;
    private int maxDay;

    public BoundedDatePickerDialog(Context context, OnDateSetListener listener,
                                   int year, int monthOfYear, int dayOfMonth, Date lowerBoundDate,
                                   Date upperBoundDate) {
        super(context, listener, year, monthOfYear, dayOfMonth);

        calendar = Calendar.getInstance();

        if (upperBoundDate != null) {
            calendar.setTime(upperBoundDate);
        } else {
            calendar.setTime(new Date());
        }
        maxYear = calendar.get(Calendar.YEAR);
        maxMonth = calendar.get(Calendar.MONTH);
        maxDay = calendar.get(Calendar.DATE);

        if (lowerBoundDate != null) {
            calendar.setTime(lowerBoundDate);
            minYear = calendar.get(Calendar.YEAR);
            minMonth = calendar.get(Calendar.MONTH);
            minDay = calendar.get(Calendar.DATE);
        }

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        dateFormat = new SimpleDateFormat(Utils.DEFAULT_DATE_PATTERN);
        setTitle(dateFormat.format(calendar.getTime()));
    }

    public BoundedDatePickerDialog(Context context, OnDateSetListener listener,
                                   int year, int monthOfYear, int dayOfMonth) {
        this(context, listener, year, monthOfYear, dayOfMonth, null, null);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        boolean beforeMinDate = false;
        boolean afterMaxDate = false;

        if (year < minYear) {
            beforeMinDate = true;
        } else if (year == minYear) {
            if (month < minMonth) {
                beforeMinDate = true;
            } else if (month == minMonth) {
                if (day < minDay) {
                    beforeMinDate = true;
                }
            }
        }

        if (!beforeMinDate) {
            if (year > maxYear) {
                afterMaxDate = true;
            } else if (year == maxYear) {
                if (month > maxMonth) {
                    afterMaxDate = true;
                } else if (month == maxMonth) {
                    if (day > maxDay) {
                        afterMaxDate = true;
                    }
                }
            }
        }

        if (beforeMinDate || afterMaxDate) {
            if (beforeMinDate) {
                year = minYear;
                month = minMonth;
                day = minDay;
            } else {
                year = maxYear;
                month = maxMonth;
                day = maxDay;
            }
            view.updateDate(year, month, day);
        }

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        setTitle(dateFormat.format(calendar.getTime()));
    }
}

