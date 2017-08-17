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

    //FACT TABLE VARIABLES
    public static final String FACT_TABLE_NAME = "fact";
    public static final String F_COL_ID = "ID";
    public static final String F_COL_DATE = "DATE";
    public static final String F_COL_FOOD = "FOOD";
    public static final String F_COL_FOOD_TYPE = "FOOD_TYPE";
    public static final String F_COL_RATING = "RATING";

    //DIMENSION FOOD TYPE VARIABLES
    public static final String DIM_FOOD_TYPE_TABLE_NAME = "dim_food_type";
    public static final String D_COL_FOOD_TYPE = "FOOD_TYPE";
    public static final String D_COL_FOOD_TYPE_ORDER = "FOOD_TYPE_ORDER";

    //constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null,1);
        }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Fact Table
        db.execSQL("CREATE TABLE " + FACT_TABLE_NAME + " (" +
                F_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                F_COL_DATE + " INTEGER NOT NULL," +
                F_COL_FOOD + " TEXT NOT NULL," +
                F_COL_FOOD_TYPE + " TEXT NOT NULL," +
                F_COL_RATING + " REAL NOT NULL" +
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
        db.execSQL("DROP TABLE " + FACT_TABLE_NAME);
        onCreate(db);
    }


    public boolean insertFood(Integer date, String food, String food_type, Float rating){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(F_COL_DATE, date);
        contentValues.put(F_COL_FOOD, food);
        contentValues.put(F_COL_FOOD_TYPE, food_type);
        contentValues.put(F_COL_RATING, rating);

        long result = db.insert(FACT_TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;
        else
            Log.d("myTag", "-------------- NEW RECORD INSERTED ------------" );
        return true;

    }
    public Cursor getAllDataRaw(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT " +
                                    "f." + F_COL_DATE + ", " +
                                    "f." + F_COL_FOOD + ", " +
                                    "f." + F_COL_FOOD_TYPE + ", " +
                                    "f." + F_COL_RATING + " " +
                                 "FROM " + FACT_TABLE_NAME + " AS f " +
                                 "LEFT JOIN " + DIM_FOOD_TYPE_TABLE_NAME + " AS dim_ft " +
                                    "ON f." + F_COL_FOOD_TYPE + " = dim_ft." + D_COL_FOOD_TYPE + " " +
                                 "ORDER BY f." + F_COL_DATE + " DESC, dim_ft." + D_COL_FOOD_TYPE_ORDER + " DESC",

                                null);
        return res;
    }

    public Cursor getLastNDates(String n){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT DISTINCT " +
                        F_COL_DATE + " " +
                        "FROM " + FACT_TABLE_NAME + " " +
                        "ORDER BY " + F_COL_DATE + " DESC LIMIT " + n,

                null);
        return res;
    }
    public Cursor getAllDataForASpecificDay(String day){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT " +
                        "f." + F_COL_FOOD + ", " +
                        "f." + F_COL_FOOD_TYPE + ", " +
                        "f." + F_COL_RATING + " " +
                        "FROM " + FACT_TABLE_NAME + " AS f " +
                        "LEFT JOIN " + DIM_FOOD_TYPE_TABLE_NAME + " AS dim_ft " +
                        "ON f." + F_COL_FOOD_TYPE + " = dim_ft." + D_COL_FOOD_TYPE + " " +
                        "WHERE f.DATE = " + day.toString() + " " +
                        "ORDER BY dim_ft." + D_COL_FOOD_TYPE_ORDER + " DESC",

                null);
        return res;
    }

    public void runSqlStatement(String sqlStatement){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(sqlStatement);
        this.close();
    }

    public void truncateFactTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + FACT_TABLE_NAME + "; VACUUM;");
    }
    public void dropDb(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }


}