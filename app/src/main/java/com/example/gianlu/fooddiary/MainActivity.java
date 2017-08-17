package com.example.gianlu.fooddiary;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;
import android.database.Cursor;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper app_db;
    Utils app_utils;

    RadioGroup radioGroupFoodType;
    EditText editTextFoodDescription;
    DatePicker datePicker;
    RatingBar ratingBarRating;
    Button buttonInsertData;
    Button buttonViewAll;
    Button buttonResetDb;
    Button buttonImportExportDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        app_db = new DatabaseHelper(this);
        app_utils = new Utils();

//        activity objects
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        editTextFoodDescription = (EditText)findViewById(R.id.editTextFoodDescription);
        buttonInsertData = (Button)findViewById(R.id.buttonInsertData);
        buttonViewAll = (Button)findViewById(R.id.buttonViewAll);
        buttonResetDb = (Button)findViewById(R.id.buttonResetDb);
        buttonImportExportDB = (Button)findViewById(R.id.buttonImportExportDB);
        ratingBarRating = (RatingBar)findViewById(R.id.ratingBarRating);
        radioGroupFoodType = (RadioGroup) findViewById(R.id.radioGroupFoodType);


        // INSERT DATA BUTTON
        insertData();
        // VIEW ALL BUTTON
        viewAllDateNew();
        // CLEAR DB BUTTON - NORMAL CLICK (delete user data) LONG PRESS (delete file DB)
        deleteUserData();
        deleteFileDb();
        // IMPORT EXPORT DB BUTTON - NORMAL CLICK (export DB to Download folder) LONG PRESS (import DB from Download folder)
        exportDbFileToDownloadFoler(this);
        //IMPORT EXPORT DB BUTTON - LONG PRESS (pick a DB file from SD and overwrite app_db)
        importDbFile(this);
    }

    //ACTIVITY
    //show messages
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    //Reset activity objects
    private void resetDayPicker(){
        Date today = new Date();
        SimpleDateFormat format_year = new SimpleDateFormat("yyyy");
        SimpleDateFormat format_month = new SimpleDateFormat("MM") ;
        SimpleDateFormat format_day = new SimpleDateFormat("dd");

        String year_str = format_year.format(today);
        String month_str = format_month.format(today);
        String day_str = format_day.format(today);
        datePicker.updateDate(
                Integer.parseInt(year_str),
                Integer.parseInt(month_str) - 1, //JAN=0, FEB=1, ...
                Integer.parseInt(day_str)
        );
    }
    private void resetFoodTypeRadioButton(){
        radioGroupFoodType.clearCheck();
    }
    private void resetFoodTextBox(){
        editTextFoodDescription.setText("");
    }
    private void resetRatingBar(){
        ratingBarRating.setRating(0);
    }
    private void resetMainActivity(){
        resetDayPicker();
        resetFoodTypeRadioButton();
        resetFoodTextBox();
        resetRatingBar();
    }

    // INSERT DATA
    public void insertData() {
        buttonInsertData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Validate input data
                        if (!allFieldsAreValid())
                            return;

                        //insert into sqlite DB
                        boolean isInserted = app_db.insertFood(dbPickedDate(), dbFood(), dbFoodType(), dbRating());
                        if (isInserted)
                            Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Data Not Inserted", Toast.LENGTH_LONG).show();
                        resetMainActivity();
                    }
                }
        );
    }
    // VIEW ALL DATA
    public void viewAllData(){
        buttonViewAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Cursor res = app_db.getAllDataRaw();
                        if (res.getCount() == 0){
                            // show message
                            showMessage("DB Empty", "Nothing found");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();

                        Integer dateDb;
                        String foodDescriptionDb, foodTypeDb;
                        Float ratingDb;

                        while (res.moveToNext()) {
                            dateDb = Integer.parseInt(res.getString(0));
                            foodDescriptionDb = res.getString(1);
                            foodTypeDb = res.getString(2);
                            ratingDb = Float.parseFloat(res.getString(3));

                            buffer.append(
                                    convertDateDbEntryToPrintable(dateDb) + "           " +
                                    foodTypeDb + "     " +
                                    convertRatingDbEntryToPRintable(ratingDb) + "\n"
                            );
                            buffer.append(foodDescriptionDb + "\n\n");
                        }
                        // show all data
                        showMessage("Data", buffer.toString());
                    }
                }
        );
    }



    public void viewAllDateNew(){
        buttonViewAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        StringBuffer buffer = new StringBuffer();

                        Integer dateDb;
                        String foodDescriptionDb, foodTypeDb;
                        Float ratingDb;

                        Cursor res_dates = app_db.getLastNDates("31");
                        if (res_dates.getCount() == 0){
                            // show message
                            showMessage("DB Empty", "Nothing found");
                            return;
                        }

                        while (res_dates.moveToNext()) {
                            dateDb = Integer.parseInt(res_dates.getString(0));
                            buffer.append("*************  " + convertDateDbEntryToPrintable(dateDb) + "  *************\n");

                            Cursor res_data_for_a_day = app_db.getAllDataForASpecificDay(res_dates.getString(0));

                            while (res_data_for_a_day.moveToNext()) {

                                foodDescriptionDb = res_data_for_a_day.getString(0);
                                foodTypeDb = res_data_for_a_day.getString(1);
                                ratingDb = Float.parseFloat(res_data_for_a_day.getString(2));

                                buffer.append("-- " + foodTypeDb + " ---- (" + convertRatingDbEntryToPRintable(ratingDb) + ") ---------------\n");
                                buffer.append( foodDescriptionDb + "\n");
                            }
                            buffer.append( "*****************************************\n\n");
                        }

                        // show all data
                        showMessage("Data", buffer.toString());
                    }
                }
        );
    }





    //DELETE USER DATA FROM FACT TABLE
    public void deleteUserData(){
        buttonResetDb.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        app_db.truncateFactTable();
                        Toast.makeText(MainActivity.this, "DB Cleansed", Toast.LENGTH_LONG).show();
                        resetMainActivity();
                    }
                }
        );
    }
    //DELETE FILE DB
    public void deleteFileDb(){
        buttonResetDb.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        app_db.dropDb(getApplicationContext());
                        Toast.makeText(MainActivity.this, "DB File Deleted. Restart the App to refresh the data", Toast.LENGTH_LONG).show();
                        resetMainActivity();
                        return true;
                    }
                }
        );
    }
    //EXPORT DB TO DOWNLOAD FOLDER
    public void exportDbFileToDownloadFoler(final Activity curr_activity){
        buttonImportExportDB.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        try {
                            app_utils.copyAppDbToDownloadFolder(curr_activity, getApplicationContext(), app_db.getDatabaseName());
                            Toast.makeText(MainActivity.this, "DB File copied to Download folder", Toast.LENGTH_LONG).show();
                            return;
                        } catch (IOException e) {
                            Toast.makeText(MainActivity.this, "Error exporting to Download folder", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
    //IMPORT DB FILE
    public void importDbFile(final Activity curr_activity){
        buttonImportExportDB.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        try {
                            app_utils.importAppDbFromFile(curr_activity, getApplicationContext(), app_db.getDatabaseName());
                            Toast.makeText(MainActivity.this, "DB File imported", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error importing DB", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                }
        );
    }


    //DATA MANIPULATION
    //Check user entries
    private boolean dateIsValid(){
        //check date
        Date today = new Date();
        Date pickedDate = null;
        Integer pickedDate_int = dayMonthYearToDateInteger(
                datePicker.getDayOfMonth(),
                datePicker.getMonth() + 1, //JAN=1, FEB=2, ...
                datePicker.getYear()
        );
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        try {
            pickedDate = formatter.parse(pickedDate_int.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(pickedDate.after(today))
            return false;

        return true;
    }
    private boolean foodTypeIsValid(){
        if (radioGroupFoodType.getCheckedRadioButtonId() == -1)
            return false;
        return true;

    }
    private boolean foodIsValid() {
        if (editTextFoodDescription.getText().toString().isEmpty())
            return false;
        return true;
    }
    private boolean ratingIsValid(){
        if (ratingBarRating.getRating() == 0)
            return false;
        return true;
    }
    private boolean allFieldsAreValid(){
        //date
        if (!dateIsValid()) {
            Toast.makeText(MainActivity.this, "Date must be minor or equal to today!", Toast.LENGTH_LONG).show();
            resetDayPicker();
            return false;
        }
        //food type
        if (!foodTypeIsValid()){
            Toast.makeText(MainActivity.this, "Pick a food type!", Toast.LENGTH_LONG).show();
            resetFoodTypeRadioButton();
            return false;
        }
        //food text box
        if (!foodIsValid()) {
            Toast.makeText(MainActivity.this, "Insert a description for what you've eaten!", Toast.LENGTH_LONG).show();
            resetFoodTextBox();
            return false;
        }
        //rating
        if (!ratingIsValid()){
            Toast.makeText(MainActivity.this, "Give a rating between 1 (worst case) and 5 (best case)!", Toast.LENGTH_LONG).show();
            resetRatingBar();
            return false;
        }
        return true;
    }
    //Parse data
    private Integer dayMonthYearToDateInteger(Integer day, Integer month, Integer year){
        String day_str, month_str, year_str, date_str;

        if (day.toString().length() == 1)
            day_str = "0" + day.toString();
        else
            day_str = day.toString();

        if (month.toString().length() == 1)
            month_str = "0" + month.toString();
        else
            month_str = month.toString();

        year_str = year.toString();
        date_str = year_str + month_str + day_str;

        return Integer.parseInt(date_str);
    }
    private String parseDate(Integer date_int) throws ParseException{
        SimpleDateFormat formatter_input = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatter_output = new SimpleDateFormat("EEE, d MMM");
        Date date = formatter_input.parse(date_int.toString());
        String date_str = formatter_output.format(date);
        return date_str;
    }
    private String convertDateDbEntryToPrintable(Integer date_int){
        String date_str = "Date Error";
        try {
            date_str = parseDate(date_int);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date_str;
    }
    private String convertRatingDbEntryToPRintable(Float rating){
        if (rating %1 == 0)
            return "Rating: " + rating.intValue() + "/5";
        return "Rating: " + rating + " /5";
    }
    //Convert user entries to DB entries
    private Integer dbPickedDate() {
        Integer pickedDate = dayMonthYearToDateInteger(
                datePicker.getDayOfMonth(),// 1, 2, 3, ...
                datePicker.getMonth() + 1, // JAN=0, Feb=1, ...
                datePicker.getYear()       // 2017, 2018, ...
        );
        return pickedDate;
    }
    private String dbFoodType(){
        //retrieve radiobutton selection
        Integer radioButtonCheckedID = radioGroupFoodType.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = (RadioButton)findViewById(radioButtonCheckedID);
        return selectedRadioButton.getText().toString();
    }
    private String dbFood(){
        return editTextFoodDescription.getText().toString().trim();
    }
    private Float dbRating(){
        return ratingBarRating.getRating();
    }

}
