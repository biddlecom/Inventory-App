package com.example.android.v3_inventory_app_stage_2_biddlecom.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.v3_inventory_app_stage_2_biddlecom.data.BookMasterContract.BookMasterEntry;

public class BookMasterDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookMasterDbHelper.class.getSimpleName();

    //Create CONSTANTS for the database name and database version.
    private static final String DATABASE_NAME = "book_master.db";
    private static final int DATABASE_VERSION = 1;

    //The Book Master constructor.
    public BookMasterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Implement the onCreate method for when the database is first created.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOK_MASTER_TABLE = "CREATE TABLE " + BookMasterEntry.TABLE_NAME + " ("
                + BookMasterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookMasterEntry.COLUMN_BOOK_TITLE + " TEXT NOT NULL, "
                + BookMasterEntry.COLUMN_AUTHOR_FIRST_NAME + " TEXT NOT NULL, "
                + BookMasterEntry.COLUMN_AUTHOR_LAST_NAME + " TEXT NOT NULL, "
                + BookMasterEntry.COLUMN_ISBN + " TEXT NOT NULL, "  //13 digits
                + BookMasterEntry.COLUMN_PRICE + " TEXT NOT NULL, "
                + BookMasterEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + BookMasterEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookMasterEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL);";

        //Execute the SQL statement.
        db.execSQL(SQL_CREATE_BOOK_MASTER_TABLE);
    }

    //Implement the onUpgrade() method for when the schema of the database changes.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //The database is still at version one, so there is nothing to be done here.
    }
}