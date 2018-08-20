package com.arunika.grocerytracker;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TodayDecorator implements DayViewDecorator {

    private Context mainContext;

    public TodayDecorator(Context mainContext) {
        this.mainContext = mainContext;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(CalendarDay.today()); //check if date equal to today
    }
    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(ContextCompat.getDrawable(mainContext,R.drawable.selector)); //add yellow backdrop to that cell
    }



}
