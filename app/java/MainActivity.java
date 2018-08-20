package com.arunika.grocerytracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String CURRENT_DATE = "com.arunika.GroceryTracker.DATE"; //pass selected date to new trip activity
    public static final String CURRENT_PRICE = "com.arunika.GroceryTracker.PRICE"; //pass PRICE of current trip to edit trip activity
    public static final String CURRENT_LIST = "com.arunika.GroceryTracker.LIST"; //pass PRICE of current trip to edit trip activity

    private HashMap<String,String> eventDates; //store all event dates and trip summary for each date
    private HashMap<String,CustomDayDecorator> decoratorList;
    private Context context; //save main activity context
    private DayViewDecorator todayDecorator;

    private MaterialCalendarView calendarView; //reference to calendar
    private DatabaseReference databaseTrips; //reference to db

    private TextView priceBreakDownDisplay; //for displaying items at the bottom

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        eventDates = new HashMap<>();
        decoratorList = new HashMap<>();

        //initialize textview reference to display item list break down (bottom)
        priceBreakDownDisplay = (TextView) findViewById(R.id.priceBreakDownDisplay);
        priceBreakDownDisplay.setMovementMethod(new ScrollingMovementMethod());

        //initialize firebase db reference
        databaseTrips = FirebaseDatabase.getInstance().getReference("trips");

        //get reference to material calendar view
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2018, 5, 1))
                .setMaximumDate(CalendarDay.from(2019, 5, 31)) //months numbered 0->11
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        //highlight current day (today) with yellow box
        todayDecorator = new TodayDecorator(context);
        calendarView.addDecorator(todayDecorator);

        //get all event dates from database and store in eventDates, once, upon app open
        databaseTrips.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //iterate through all database entries
                for(DataSnapshot tripSnapshot : dataSnapshot.getChildren())
                {
                    Trip trip = tripSnapshot.getValue(Trip.class); //get trip object
                    eventDates.put(trip.getDate(), trip.getTripSummary()); //save all event dates & trip summaries in global hashmap
                    //create decorator for each day, add price underneath
                    CustomDayDecorator decorator = new CustomDayDecorator(trip.getDate(),trip.getPrice(), context);
                    calendarView.addDecorator(decorator);
                    decoratorList.put(trip.getDate(),decorator); // save decorators and event dates in hashmap
                }
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
                String currentMonth = sdf.format(currentTime);
                updateMonthlyPrice(currentMonth);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //failed post, display log message
                Toast.makeText(context, "Error fetching saved trips from database", Toast.LENGTH_SHORT).show();
            }
        });

        //upon clicking any date
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull final CalendarDay date, boolean selected) {

                //initiate new dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(context); //instantiate dialog
                priceBreakDownDisplay.setText("");
                /**
                 * LIMIT ONE EVENT PER DATE ONLY
                 * check if selected date already exists in eventDates list
                 */
                //convert current selected day calendarDay obj to string (to compare against hashmap saved string dates)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                final String dateAsString = sdf.format(date.getCalendar().getTime());

                //if this date already has saved event:
                if(eventDates.containsKey(dateAsString))
                {
                    /**
                     * show trip details in dialog message
                     * edit/remove option
                     */
                    double price = decoratorList.get(dateAsString).getPrice();

                    updateItemList(dateAsString);

                    builder.setMessage("Current Trip Total: $" + price)
                            .setCancelable(true)
                            .setNeutralButton(
                                    "Edit",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //if edit
                                            /**
                                             * open new trip with preloaded data of saved trip
                                             */
                                            String tripSummary = eventDates.get(dateAsString);
                                            String price = tripSummary.split("=")[0];
                                            String expenseList = tripSummary.split("=")[1];

                                            String dateString = formatDate(date); //convert CalendarDay to string
                                            Intent intent = new Intent(context, EditTrip.class);
                                            intent.putExtra(CURRENT_DATE, dateString); //send formatted date to EDIT trip
                                            intent.putExtra(CURRENT_PRICE, price); //send saved trip price to edit trip for displaying in price input field
                                            intent.putExtra(CURRENT_LIST, expenseList); //send current saved trip list
                                            startActivityForResult(intent,2); // calls onActivityResult when NewTrip finishes

                                        }
                                    })
                            .setNegativeButton(
                                    "Remove",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //if remove
                                            /**
                                             * delete database entry
                                             * delete date from eventDates
                                             */
                                            databaseTrips.child(dateAsString).removeValue();
                                            eventDates.remove(dateAsString);

                                            calendarView.removeDecorator(decoratorList.get(dateAsString));

                                            updateMonthlyPrice(dateAsString.substring(0,7));
                                            priceBreakDownDisplay.setText("");

                                        }
                                    })
                            .setPositiveButton(
                                    "Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                }

                else //(date has no event yet)
                {
                    //create new entry dialog
                     builder.setMessage("Create New Trip?")
                            .setCancelable(true)
                            .setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            /**
                                             * Start new activity NewTrip
                                             */
                                            String dateString = formatDate(date); //convert CalendarDay to string
                                            Intent intent = new Intent(context, NewTrip.class);
                                            intent.putExtra(CURRENT_DATE, dateString); //send formatted date to new trip
                                            startActivityForResult(intent,1); // calls onActivityResult when NewTrip finishes
                                        }
                                    })
                            .setNegativeButton(
                                    "Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                }

                //build and show the dialog
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                //Do something like this
                Date dateTemp = date.getDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                String currentMonth = sdf.format(dateTemp);

                updateMonthlyPrice(currentMonth);
            }
        });




    }//end onCreate

    //displays currently selected day's item list at the bottom
    public void updateItemList(String currentEventDate) {

        priceBreakDownDisplay = (TextView) findViewById(R.id.priceBreakDownDisplay);
        //get trip summary for this event date
        String tripSummary = eventDates.get(currentEventDate);
        //extract item list
        String itemListWithBrackets = tripSummary.split("=")[1];
        String itemList = itemListWithBrackets.substring(1,itemListWithBrackets.length()-1); //remove [a,b,c] brackets
        String items[] = itemList.split(",");

        StringBuilder display = new StringBuilder("");

        for(String item : items)
        {
            display.append(item.trim());
            display.append("\n");
        }



        priceBreakDownDisplay.setText(display.toString());

    }


    //takes month part of date as argument ( eg 23/11/18 - give 11 to this function as parameter (date.substring(0,7))
    public void updateMonthlyPrice(String currentMonth) {
        double monthlyTotal = 0;

        //loop through every saved event in eventDates
        for (HashMap.Entry<String, String> entry : eventDates.entrySet()) {
            String thisDate = entry.getKey();

            //if event date falls within currently visible month
            if(thisDate.substring(0,7).equals(currentMonth))
            {
                //get value (trip summary)
                String thisTripSummary = entry.getValue();
                //extract price
                String price = thisTripSummary.split("=")[0];
                //add price to counter
                monthlyTotal += Double.parseDouble(price);
            }
        }

        //round total to 2dp
        monthlyTotal = Math.round(monthlyTotal * 100.0) / 100.0;

        //display total
        TextView priceDisplay = (TextView) findViewById(R.id.priceDisplay);
        priceDisplay.setText("$"+monthlyTotal);
    }


    //called when newTrip activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //called when NewTrip finishes

        //if user finished out of newTrip
        if (requestCode == 1) {
            //user hit save, and all submitted info was correct
            if(resultCode == RESULT_OK){
                String tripPrice = data.getStringExtra(NewTrip.TRIP_PRICE); //get trip price from newTrip activity
                String tripList = data.getStringExtra(NewTrip.TRIP_LIST); //get trip item list
                String tripDate = data.getStringExtra(NewTrip.TRIP_DATE); //get trip date

                //create new trip object
                Trip trip = new Trip(tripDate, Double.parseDouble(tripPrice), tripList);

                //save trip to database (event date as primary key)
                databaseTrips.child(tripDate).setValue(trip);

                //save new trip date & summary in eventDate hashmap
                eventDates.put(tripDate, trip.getTripSummary());

                //create decorator for day
                CustomDayDecorator decorator = new CustomDayDecorator(trip.getDate(),trip.getPrice(), context);
                calendarView.addDecorator(decorator); //show decorator on calendar
                decoratorList.put(trip.getDate(),decorator); // save decorators and event dates in hashmap

                updateMonthlyPrice(tripDate.substring(0,7));
                updateItemList(tripDate);

                Toast.makeText(context, "Trip Saved", Toast.LENGTH_SHORT).show();

            }
        }

        //if user finished out of editTrip
        if (requestCode == 2) {
            //user hit save, and all submitted info was correct
            if(resultCode == RESULT_OK){
                String tripPrice = data.getStringExtra(EditTrip.EDIT_TRIP_PRICE); //get trip price from newTrip activity
                String tripList = data.getStringExtra(EditTrip.EDIT_TRIP_LIST); //get trip item list
                String tripDate = data.getStringExtra(EditTrip.EDIT_TRIP_DATE); //get trip date

                //create new trip object
                Trip trip = new Trip(tripDate, Double.parseDouble(tripPrice), tripList);

                //save trip to database (event date as primary key, overwrite old value for this day)
                databaseTrips.child(tripDate).setValue(trip);

                //save edited trip date & summary in eventDate hashmap
                eventDates.put(tripDate, trip.getTripSummary());

                //remove old decorator from calendar
                calendarView.removeDecorator(decoratorList.get(tripDate));

                //create new decorator with updated price
                CustomDayDecorator updatedDecorator = new CustomDayDecorator(tripDate, trip.getPrice(), context);
                //add updated decorator to hashmap (overwrites old one)
                decoratorList.put(tripDate, updatedDecorator);
                //display new updated decorator
                calendarView.addDecorator(updatedDecorator);

                Toast.makeText(context, "Changes Saved", Toast.LENGTH_SHORT).show();

                updateMonthlyPrice(tripDate.substring(0,7));
                updateItemList(tripDate);

            }
        }
    }



    //convert calendarDay to string for displaying on title header for newTrip activity
    private String formatDate(CalendarDay date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy (E)");
        String result = "";
        try {
            result = formatter.format(date.getDate());
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error converting date format", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

}


