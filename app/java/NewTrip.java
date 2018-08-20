package com.arunika.grocerytracker;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewTrip extends AppCompatActivity {

    private ArrayList<String> expenseList = new ArrayList<>(); //list of user selected checkboxes for expense type
    public static final String TRIP_PRICE= "com.arunika.GroceryTracker.TRIPPRICE"; //for sending trip PRICE back to main activity if trip saved
    public static final String TRIP_LIST= "com.arunika.GroceryTracker.TRIPLIST"; //for sending trip LIST back to main activity if trip saved
    public static final String TRIP_DATE= "com.arunika.GroceryTracker.TRIPDATE"; //for sending formatted trip date back to main activity if trip saved
    private String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        //get selected date from main activity
        Intent intent = getIntent();
        date = intent.getStringExtra(MainActivity.CURRENT_DATE);

        //initiate header textview to display selected date
        TextView textView = (TextView) findViewById(R.id.currentDateLabel);
        textView.setText(date);

        addAutoSuggestions();
    }

    public void addAutoSuggestions() {
        //suggestions for user input
        String[] expenseTypes = {"Rent", "Electric Bill", "Water Bill", "Gas (Car)", "Auto Insurance",
                "Medical Insurance", "Grocery", "Internet", "Phone"};
        //Creating the instance of ArrayAdapter containing list of suggestions
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, expenseTypes);

        for(int tag=1; tag<=5; tag++)
        {
            AutoCompleteTextView typeField = (AutoCompleteTextView) findViewById(getResources().getIdentifier("type" + tag, "id",
                    this.getPackageName()));
            typeField.setThreshold(1);//will start working from first character
            typeField.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        }
    }

    //user pressed SAVE
    public void onSaveEvent(View view) {

        double totalPrice = 0;
        boolean allGood = true;

        for(int i=1; i<=5; i++)
        {
            EditText typeField = (EditText) findViewById(getResources().getIdentifier("type" + i, "id",
                    this.getPackageName()));
            EditText amountField = (EditText) findViewById(getResources().getIdentifier("amount" + i, "id",
                    this.getPackageName()));
            String expenseType = typeField.getText().toString().trim();
            String amountText = amountField.getText().toString().trim();

            if((expenseType.length()!=0) && (amountText.length()!=0))
            {
                if(expenseType.indexOf(',') >= 0) {
                    Toast.makeText(this, "No commas or semicolons (, ;) allowed!", Toast.LENGTH_SHORT).show();
                    allGood=false;
                }

                totalPrice += Double.parseDouble(amountText);

                StringBuilder typeAndPrice = new StringBuilder(expenseType);
                typeAndPrice.append(" : $");
                typeAndPrice.append(amountText);

                expenseList.add(typeAndPrice.toString());
            }

            if(  (expenseType.length()!=0 && amountText.length()==0) ||
                 (amountText.length()!=0 && expenseType.length()==0)  )
               allGood = false;
        }

        //error if user didnt enter anything
        if(expenseList.size()==0 && allGood) {
            Toast.makeText(this, "Fill at least one expense type and its $ amount", Toast.LENGTH_SHORT).show();
            allGood = false;
        }
        //error if user didnt enter either price or expense type
        if(!allGood) {
            Toast.makeText(this, "Please enter both $ amount and expense type where applicable", Toast.LENGTH_SHORT).show();
        }


        //every expense type entered as corresponding $ amount
        if(allGood) {

            String dateAsString = formatDate(date);

            Intent returnIntent = new Intent();
            returnIntent.putExtra(TRIP_PRICE, Double.toString(totalPrice)); //send price to main activity
            returnIntent.putExtra(TRIP_LIST, expenseList.toString()); //send expenses to main activity
            returnIntent.putExtra(TRIP_DATE, dateAsString); //send trip date to main activity
            setResult(RESULT_OK, returnIntent);
            finish(); //exit, go back to main activity
        }
    }

    public void onCrossButton(View view) {

        Button deleteButton = (Button) findViewById(view.getId());
        String tag = deleteButton.getTag().toString();

        EditText typeField = (EditText) findViewById(getResources().getIdentifier("type" + tag, "id",
                this.getPackageName()));
        EditText amountField = (EditText) findViewById(getResources().getIdentifier("amount" + tag, "id",
                this.getPackageName()));
        String expenseType = typeField.getText().toString().trim();
        String amountText = amountField.getText().toString().trim();

        if(expenseType.length()!=0)
            typeField.setText("");
        if(amountText.length()!=0)
            amountField.setText("");

    }


    //cancel trip & return to main if user pressed CANCEL
    public void onCancelEvent(View view) {
        Toast.makeText(this, "Trip Discarded", Toast.LENGTH_SHORT).show();
        finish();
    }

    //cancel trip & return to main if user presses back button
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Trip Discarded", Toast.LENGTH_SHORT).show();
        finish();
    }

    public String formatDate(String dateAsString)  {

        SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd, yyyy (E)");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        StringBuilder result = new StringBuilder("");
        Date date;
        try {
            date = sdf1.parse(dateAsString);
            result.append(sdf2.format(date));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }





}
