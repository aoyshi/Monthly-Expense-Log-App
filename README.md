# Monthly-Expense-Log-App
A very straight-forward, simple Android app to log your daily expenses on a month-based overview, using Prolific Interactive's material calendar view UI API and Firebase.

![App Icon ScreenShot](https://github.com/aoyshi/Monthly-Expense-Log-App/blob/master/App_Icon_Pic.png)

1. Click on any day to Create a New Trip.
2. Today (Aug 20th) is highlighted with a yellow box. Total for the currently shown month is displayed at the top.

![App Home ScreenShot](https://github.com/aoyshi/Monthly-Expense-Log-App/blob/master/App_Pic_Today.png)

3. Select any day to view the breakdown of the total price for that day in the text area at the bottom. You may also choose to edit or delete the currently selected trip (highlighted with a yellow circle).

![App Home ScreenShot](https://github.com/aoyshi/Monthly-Expense-Log-App/blob/master/App_Pic_Selected_Date.png)

4. Create New Trip instance:

![App Home ScreenShot](https://github.com/aoyshi/Monthly-Expense-Log-App/blob/master/App_Pic_Create_Trip.png)


# References:

Thanks to Prolific Interactive (https://github.com/prolificinteractive/material-calendarview) for the material calendar view API that has been used extensively in this app to create the UI element of the calendar.

# Future Work:
This is a personal project still in the making as I learn the ropes of Android Development more and more every day. Some areas of  ongoing + future work need be mentioned:
1. Integrate multiple screen size support.
2. Optimize the start-up time (slow as of now because of fetching all saved records from remote firebase server): a possible solution might include changing the database to a faster, local one like SQLite and giving the user an option to save data to a remote server if desired.
3. Work on Database authentication (anonymous-auth and/or option to link Facebook account).
4. Build Test Cases for UI/logic.
5. Use Pro-Guard to secure the app.
