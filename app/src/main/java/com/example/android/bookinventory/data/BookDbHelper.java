package com.example.android.bookinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDbHelper extends SQLiteOpenHelper {

      private final static String DATABASE_NAME = "inventory.db";
      private final static int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + InventoryContract.BookEntry.TABLE_NAME + " ("
                + InventoryContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.BookEntry.COLUMN_NAME_BOOK + " TEXT NOT NULL, "
                + InventoryContract.BookEntry.COLUMN_PRICE_BOOK + " INTEGER, "
                + InventoryContract.BookEntry.COLUMN_QUANT_BOOK + " INTEGER DEFAULT 0, "
                + InventoryContract.BookEntry.COLUMN_SUPPLIER_NAME_BOOK + " TEXT NOT NULL DEFAULT 0, "
                + InventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
