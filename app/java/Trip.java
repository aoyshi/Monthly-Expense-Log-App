package com.arunika.grocerytracker;

public class Trip {

    String date;
    double price;
    String expensesList;

    public Trip() {
        //Default constructor required for calls to DataSnapshot.getValue
    }

    public Trip(String date, double price, String expensesList) {
        this.date = date;
        this.price = price;
        this.expensesList = expensesList;
    }

    public String getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    public String getExpensesList() {
        return expensesList;
    }

    public String getTripSummary() {

        StringBuilder summary = new StringBuilder(Double.toString(price));
        summary.append("=");
        summary.append(expensesList);

        return summary.toString();
    }

}
