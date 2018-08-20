package com.arunika.grocerytracker;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CustomDayDecorator implements DayViewDecorator {

    private String thisDateString; //event date to be decorated
    private double price;
    private Context mainContext;

    public CustomDayDecorator(String thisDateString, double price, Context mainContext) {
        this.thisDateString = thisDateString;
        this.price = price;
        this.mainContext = mainContext;

        //round total to 2dp
        this.price = Math.round(this.price * 100.0) / 100.0;
    }

    public double getPrice() {
        return this.price;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {

        //convert calendarDay to string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String calendarDayString = sdf.format(day.getCalendar().getTime());

        return (calendarDayString).equals(thisDateString); //compare with event date to be decorated
    }
    @Override
    public void decorate(DayViewFacade view) {
        CalendarViewSpan cvs = new CalendarViewSpan("$"+Double.toString(this.price), mainContext); //write price underneath date
        view.addSpan(cvs); //add pink color text of price underneath date
        view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(mainContext, R.color.colorDates))); //set color of date to black (bc previous line changes it to pink)
    }



}
