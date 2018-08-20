package com.arunika.grocerytracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.style.LineBackgroundSpan;

public class CalendarViewSpan implements LineBackgroundSpan {

    String text;
    Context context;

    public CalendarViewSpan(String text, Context context) {

        this.text = text;
        this.context = context;
    }

    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom,
                               CharSequence text, int start, int end, int lnum) {
        text = this.text;

        //set padding so that text is aligned in middle of cell (under date)
        switch(text.length()) {
            case 4: text = "   "+text;
            break;
            case 5: text = "  "+text;
            break;
            case 6: text = " "+text;
        }


        int color = ContextCompat.getColor(context, R.color.colorText);
        p.setColor(color);
        c.drawText(String.valueOf(text), (start+end)/2, bottom + 15, p);
    }
}
