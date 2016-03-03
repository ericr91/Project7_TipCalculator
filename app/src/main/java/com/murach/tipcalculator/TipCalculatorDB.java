package com.murach.tipcalculator;

/**
 * Created by Eric on 2/29/2016.
 */
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TipCalculatorDB {

    // database constants
    public static final String DB_NAME = "tiplist.db";
    public static final int    DB_VERSION = 1;
    
    // task table constants
    public static final String TIP_TABLE = "tip";

    public static final String _ID = "_id";
    public static final int    _ID_COL = 0;

    public static final String BILL_DATE = "list_id";
    public static final int    BILL_DATE_COL = 1;

    public static final String BILL_AMOUNT = "tip_name";
    public static final int    BILL_AMOUNT_COL = 2;

    public static final String TIP_PERCENT = "notes";
    public static final int    TIP_PERCENT_COL = 3;

    // CREATE and DROP TABLE statements
    public static final String CREATE_TIP_TABLE =
            "CREATE TABLE " + TIP_TABLE + " (" +
                    _ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    BILL_DATE    + " INTEGER NOT NULL, " +
                    BILL_AMOUNT       + " REAL NOT NULL, " +
                    TIP_PERCENT      + " REAL NOT NULL);";

    public static final String DROP_TIP_TABLE =
            "DROP TABLE IF EXISTS " + TIP_TABLE;

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TIP_TABLE);

            db.execSQL("INSERT INTO tip VALUES (NULL, 0, 10.89, .15)");
            db.execSQL("INSERT INTO tip VALUES (NULL, 0 ,32.34, .25)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("Tip list", "Upgrading db from version " + oldVersion +
                    " to " + newVersion);

            db.execSQL(TipCalculatorDB.DROP_TIP_TABLE);
            onCreate(db);
        }
    }
    // database and database helper objects
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    // constructor
    public TipCalculatorDB(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    // private methods
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null)
            db.close();
    }

    // public methods
    public ArrayList<Tip> getTips() {
        ArrayList<Tip> tips = new ArrayList<Tip>();
        openReadableDB();
        Cursor cursor = db.query(TIP_TABLE,
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Tip tip = new Tip();
            tip.setId(cursor.getLong(_ID_COL));
            tip.setDateMillis(cursor.getLong(BILL_DATE_COL));
            tip.setBillAmount(cursor.getFloat(BILL_AMOUNT_COL));
            tip.setTipPercent(cursor.getFloat(TIP_PERCENT_COL));

            tips.add(tip);
        }
        if (cursor != null)
            cursor.close();
        closeDB();

        return tips;
    }

    public void saveTips(long dateMillis, double billAmount, double tipPercent){
        openWriteableDB();
        db.execSQL("INSERT INTO tip VALUES (NULL, "+ dateMillis + ", " + billAmount + ", " + tipPercent + ")");
        closeDB();
    }

    public void deleteTipTable(){
        openWriteableDB();
        db.execSQL(DROP_TIP_TABLE);
        db.execSQL(CREATE_TIP_TABLE);
        db.execSQL("INSERT INTO tip VALUES (NULL, 0, 10.89, .15)");
        db.execSQL("INSERT INTO tip VALUES (NULL, 0 ,32.34, .25)");
        closeDB();
    }
}