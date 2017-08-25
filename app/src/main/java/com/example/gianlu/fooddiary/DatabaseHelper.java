package com.example.gianlu.fooddiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by gianlu on 06/08/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // DB VARIABLES
    public static final String DATABASE_NAME = "app_db.db";

    //FACT TABLE FOOD VARIABLES
    public static final String FACT_FOOD_TABLE_NAME = "fact_food";
    public static final String F_FOOD_COL_ID = "ID";
    public static final String F_FOOD_COL_DATE = "DATE";
    public static final String F_FOOD_COL_FOOD = "FOOD";
    public static final String F_FOOD_COL_FOOD_TYPE = "FOOD_TYPE";
    public static final String F_FOOD_COL_RATING = "RATING";

    //DIMENSION FOOD TYPE VARIABLES
    public static final String DIM_FOOD_TYPE_TABLE_NAME = "dim_food_type";
    public static final String D_COL_FOOD_TYPE = "FOOD_TYPE";
    public static final String D_COL_FOOD_TYPE_ORDER = "FOOD_TYPE_ORDER";

    //FACT TABLE HABITS VARIABLES
    public static final String FACT_HABITS_TABLE_NAME = "fact_habits";
    public static final String F_HABITS_COL_ID = "ID";
    public static final String F_HABITS_COL_DATE = "DATE";
    public static final String F_HABITS_COL_DATETIME = "DATETIME";
    public static final String F_HABITS_COL_HABIT = "HABIT";

    //FACT TABLE SYMPTOMS VARIABLES
    public static final String FACT_SYMPTOMS_TABLE_NAME = "fact_symptoms";
    public static final String F_SYMPTOMS_COL_ID = "ID";
    public static final String F_SYMPTOMS_COL_DATE = "DATE";
    public static final String F_SYMPTOMS_COL_DATETIME = "DATETIME";
    public static final String F_SYMPTOMS_COL_SYMPTOM = "SYMPTOM";
    public static final String F_SYMPTOMS_COL_VALUE = "VALUE_1_TO_3";

    //FACT TABLE TREATMENTS VARIABLES
    public static final String FACT_TREATMENTS_TABLE_NAME = "fact_treatments";
    public static final String F_TREATMENTS_COL_ID = "ID";
    public static final String F_TREATMENTS_COL_DATE = "DATE";
    public static final String F_TREATMENTS_COL_DATETIME = "DATETIME";
    public static final String F_TREATMENTS_COL_TREATMENT = "TREATMENT";
    public static final String F_TREATMENTS_COL_VALUE = "VALUE_MG_ML";



    //constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null,1);
        }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Fact Table Food
        db.execSQL("CREATE TABLE " + FACT_FOOD_TABLE_NAME + " (" +
                F_FOOD_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                F_FOOD_COL_DATE + " INTEGER NOT NULL," +
                F_FOOD_COL_FOOD + " TEXT NOT NULL," +
                F_FOOD_COL_FOOD_TYPE + " TEXT NOT NULL," +
                F_FOOD_COL_RATING + " REAL NOT NULL" +
                ")"
        );

        // Create Fact Table Habits
        db.execSQL("CREATE TABLE " + FACT_HABITS_TABLE_NAME + " (" +
                F_HABITS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                F_HABITS_COL_DATE + " INTEGER NOT NULL," +
                F_HABITS_COL_DATETIME + " TEXT NOT NULL," +
                F_HABITS_COL_HABIT + " TEXT NOT NULL" +
                ")"
        );

        // Create Fact Table Symptoms
        db.execSQL("CREATE TABLE " + FACT_SYMPTOMS_TABLE_NAME + " (" +
                F_SYMPTOMS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                F_SYMPTOMS_COL_DATE + " INTEGER NOT NULL," +
                F_SYMPTOMS_COL_DATETIME + " TEXT NOT NULL," +
                F_SYMPTOMS_COL_SYMPTOM + " TEXT NOT NULL," +
                F_SYMPTOMS_COL_VALUE + " INTEGER NOT NULL" +
                ")"
        );

        // Create Fact Table Treatments
        db.execSQL("CREATE TABLE " + FACT_TREATMENTS_TABLE_NAME + " (" +
                F_TREATMENTS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                F_TREATMENTS_COL_DATE + " INTEGER NOT NULL," +
                F_TREATMENTS_COL_DATETIME + " TEXT NOT NULL," +
                F_TREATMENTS_COL_TREATMENT + " TEXT NOT NULL," +
                F_TREATMENTS_COL_VALUE + " INTEGER NOT NULL" +
                ")"
        );

        // Create Dimension Food Type
        db.execSQL("CREATE TABLE " + DIM_FOOD_TYPE_TABLE_NAME + " (" +
                D_COL_FOOD_TYPE + " TEXT NOT NULL," +
                D_COL_FOOD_TYPE_ORDER + " INTEGER NOT NULL" +
                ")"
        );
        // Insert Static Values Into Dim_Food_Type
        db.execSQL("INSERT INTO " + DIM_FOOD_TYPE_TABLE_NAME + " VALUES ('Breakfast', 1)");
        db.execSQL("INSERT INTO " + DIM_FOOD_TYPE_TABLE_NAME + " VALUES ('Lunch', 2)");
        db.execSQL("INSERT INTO " + DIM_FOOD_TYPE_TABLE_NAME + " VALUES ('Dinner', 3)");
        db.execSQL("INSERT INTO " + DIM_FOOD_TYPE_TABLE_NAME + " VALUES ('Other', 4)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE " + FACT_FOOD_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertFood(Integer date, String food, String food_type, Float rating){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(F_FOOD_COL_DATE, date);
        contentValues.put(F_FOOD_COL_FOOD, food);
        contentValues.put(F_FOOD_COL_FOOD_TYPE, food_type);
        contentValues.put(F_FOOD_COL_RATING, rating);

        long result = db.insert(FACT_FOOD_TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;
        else
            Log.d("myTag", "-------------- NEW RECORD INSERTED ------------" );
        return true;
    }
    public boolean insertHabit(Integer date, String datetime, String habit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(F_HABITS_COL_DATE, date);
        contentValues.put(F_HABITS_COL_DATETIME, datetime);
        contentValues.put(F_HABITS_COL_HABIT, habit);

        long result = db.insert(FACT_HABITS_TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;
        else
            Log.d("myTag", "-------------- NEW HABIT INSERTED ------------" );
        return true;
    };
    public boolean insertSymptom(Integer date, String datetime, String symptom, Integer value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(F_SYMPTOMS_COL_DATE, date);
        contentValues.put(F_SYMPTOMS_COL_DATETIME, datetime);
        contentValues.put(F_SYMPTOMS_COL_SYMPTOM, symptom);
        contentValues.put(F_SYMPTOMS_COL_VALUE, value);

        long result = db.insert(FACT_SYMPTOMS_TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;
        else
            Log.d("myTag", "-------------- NEW SYMPTOM INSERTED ------------" );
        return true;
    }
    public boolean insertTreatment(Integer date, String datetime, String tretment, Integer value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(F_TREATMENTS_COL_DATE, date);
        contentValues.put(F_TREATMENTS_COL_DATETIME, datetime);
        contentValues.put(F_TREATMENTS_COL_TREATMENT, tretment);
        contentValues.put(F_TREATMENTS_COL_VALUE, value);

        long result = db.insert(FACT_TREATMENTS_TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;
        else
            Log.d("myTag", "-------------- NEW TREATMENT INSERTED ------------" );
        return true;
    }
    public void runSqlStatement(String sqlStatement){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(sqlStatement);
        this.close();
    }
    public Cursor getLastNDates(String n){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT DISTINCT " +
                        F_FOOD_COL_DATE + " " +
                        "FROM " + FACT_FOOD_TABLE_NAME + " " +
                        "ORDER BY " + F_FOOD_COL_DATE + " DESC LIMIT " + n,

                null);
        return res;
    }
    public Cursor getAllDataForASpecificDay(String day){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT " +
                        "f." + F_FOOD_COL_FOOD + ", " +
                        "f." + F_FOOD_COL_FOOD_TYPE + ", " +
                        "f." + F_FOOD_COL_RATING + " " +
                        "FROM " + FACT_FOOD_TABLE_NAME + " AS f " +
                        "LEFT JOIN " + DIM_FOOD_TYPE_TABLE_NAME + " AS dim_ft " +
                        "ON f." + F_FOOD_COL_FOOD_TYPE + " = dim_ft." + D_COL_FOOD_TYPE + " " +
                        "WHERE f.DATE = " + day.toString() + " " +
                        "ORDER BY dim_ft." + D_COL_FOOD_TYPE_ORDER + " DESC",

                null);
        return res;
    }



    public void truncateAllFactTables(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + FACT_FOOD_TABLE_NAME + "; VACUUM;");
        db.execSQL("DELETE FROM " + FACT_HABITS_TABLE_NAME + "; VACUUM;");
    }
    public void dropDb(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }


}