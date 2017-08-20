package com.example.gianlu.fooddiary;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper app_db;
    Utils app_utils;

    RadioGroup radioGroupFoodType;
    EditText editTextFoodDescription;
    RatingBar ratingBarRating;
    Button buttonInsertData;
    Button buttonViewAll;
    Button buttonResetDb;
    Button buttonImportExportDB;
    Button sympAndTreatButton;
    Button setDate;
    Button buttonEsp;
    Button buttonCig;
    TextView textViewDate;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener dateListener;
    Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        app_db = new DatabaseHelper(this);
        app_utils = new Utils();


//        activity objects
        setDate = (Button) findViewById(R.id.buttonSetDate);
        editTextFoodDescription = (EditText)findViewById(R.id.editTextFoodDescription);
        buttonInsertData = (Button)findViewById(R.id.buttonInsertData);
        buttonViewAll = (Button)findViewById(R.id.buttonViewAll);
        buttonEsp = (Button)findViewById(R.id.buttonEsp);
        buttonCig = (Button)findViewById(R.id.buttonCig);
        buttonResetDb = (Button)findViewById(R.id.buttonResetDb);
        buttonImportExportDB = (Button)findViewById(R.id.buttonImportExportDB);
        ratingBarRating = (RatingBar)findViewById(R.id.ratingBarRating);
        radioGroupFoodType = (RadioGroup) findViewById(R.id.radioGroupFoodType);
        sympAndTreatButton = (Button) findViewById(R.id.buttonSympAndTreat);
        textViewDate = (TextView) findViewById(R.id.textViewDate);

        myCalendar = Calendar.getInstance();
        date = new Date();



        // SET DATE BUTTON
        setDate();
        // INSERT DATA BUTTON
        insertData();
        // INSERT ESPRESSO BUTTON
        insertEspresso();
        // INSERT CIGARETTE BUTTON
        insertCigarette();
        insertPeppino();
        // VIEW ALL BUTTON
        viewAllData();
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
    private void resetDate(){
        date = new Date();
        // Date format
        String myFormat = "EEE, d MMM";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(myFormat);
        textViewDate.setText(dateFormatter.format(date));
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
        resetDate();
        resetFoodTypeRadioButton();
        resetFoodTextBox();
        resetRatingBar();
    }



    // SET DATE
    public void setDate(){
        // Date format
        String myFormat = "EEE, d MMM";
        final SimpleDateFormat dateFormatter = new SimpleDateFormat(myFormat);

        // set today as default date
        textViewDate.setText(dateFormatter.format(date));
        // listen for date change
        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                date = myCalendar.getTime();
                textViewDate.setText(dateFormatter.format(date));
            }
        };

        setDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(MainActivity.this, dateListener, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });



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
    // INSERT HABIT ESPRESSO
    public void insertEspresso(){
        buttonEsp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //insert into sqlite DB
                        Date now = new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(now);
                        Integer date = dayMonthYearToDateInteger(
                                cal.get(Calendar.DAY_OF_MONTH),
                                cal.get(Calendar.MONTH) + 1, //Jan=0, Feb=1, etc
                                cal.get(Calendar.YEAR)
                        );
                        SimpleDateFormat date_formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
                        String datetime = date_formatter.format(now);

                        boolean isInserted = app_db.insertHabit(date,datetime, "espresso");
                        if (isInserted)
                            Toast.makeText(MainActivity.this, "Espresso Inserted", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Espresso Not Inserted", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    public void insertCigarette(){
        buttonCig.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //insert into sqlite DB
                        Date now = new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(now);
                        Integer date = dayMonthYearToDateInteger(
                                cal.get(Calendar.DAY_OF_MONTH),
                                cal.get(Calendar.MONTH) + 1, //Jan=0, Feb=1, etc
                                cal.get(Calendar.YEAR)
                        );
                        SimpleDateFormat date_formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
                        String datetime = date_formatter.format(now);

                        boolean isInserted = app_db.insertHabit(date,datetime, "cigarette");
                        if (isInserted)
                            Toast.makeText(MainActivity.this, "Cigarette Inserted", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Cigarette Not Inserted", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    public void insertPeppino(){
        buttonCig.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        //insert into sqlite DB
                        Date now = new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(now);
                        Integer date = dayMonthYearToDateInteger(
                                cal.get(Calendar.DAY_OF_MONTH),
                                cal.get(Calendar.MONTH) + 1, //Jan=0, Feb=1, etc
                                cal.get(Calendar.YEAR)
                        );
                        SimpleDateFormat date_formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
                        String datetime = date_formatter.format(now);

                        boolean isInserted = app_db.insertHabit(date,datetime, "peppino");
                        if (isInserted)
                            Toast.makeText(MainActivity.this, "Peppino :)", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Peppino Not Inserted", Toast.LENGTH_LONG).show();

                        return true;
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
                        app_db.truncateAllFactTables();
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
        Date today = new Date();
          if(date.after(today))
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
        //dateListener
        if (!dateIsValid()) {
            Toast.makeText(MainActivity.this, "Date must be minor or equal to today!", Toast.LENGTH_LONG).show();
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return dayMonthYearToDateInteger(
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1, //Jan=0, Feb=1, etc
                cal.get(Calendar.YEAR));
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
