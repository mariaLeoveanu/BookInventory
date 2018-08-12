package com.example.android.bookinventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bookinventory.data.BookDbHelper;
import com.example.android.bookinventory.data.InventoryContract.BookEntry;


public class MainActivity extends AppCompatActivity {

    BookDbHelper mBookDbHelper;
    Button mTestButton;
    TextView mBookList;
    public static final String DEFAULT_BOOK_NAME = "The murders in the Rue Morgue and other tales";
    public static final int DEFAULT_BOOK_PRICE = 29;
    public static final int DEFAULT_QUANTITY = 3;
    public static final String DEFAULT_SUPPLIER_NAME = "Penguin English Library";
    public static final String DEFAULT_PHONE_NUMBER = "45679362291";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookDbHelper = new BookDbHelper(this);
        mTestButton = findViewById(R.id.test_b);
        mBookList = findViewById(R.id.book_text);

        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertBook();
                displayDatabaseInfo();
            }
        });

    }

    public void insertBook (){

        SQLiteDatabase db = mBookDbHelper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COLUMN_NAME_BOOK, DEFAULT_BOOK_NAME);
        contentValues.put(BookEntry.COLUMN_PRICE_BOOK, DEFAULT_BOOK_PRICE);
        contentValues.put(BookEntry.COLUMN_QUANT_BOOK, DEFAULT_QUANTITY);
        contentValues.put(BookEntry.COLUMN_SUPPLIER_NAME_BOOK, DEFAULT_SUPPLIER_NAME);
        contentValues.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK, DEFAULT_PHONE_NUMBER);

        db.insert(BookEntry.TABLE_NAME, null, contentValues);
    }

    public void displayDatabaseInfo (){

        SQLiteDatabase db = mBookDbHelper.getReadableDatabase();

        String [] projection = {BookEntry._ID,
                BookEntry.COLUMN_NAME_BOOK,
                BookEntry.COLUMN_PRICE_BOOK,
                BookEntry.COLUMN_QUANT_BOOK,
                BookEntry.COLUMN_SUPPLIER_NAME_BOOK,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK};
        Cursor cursor = db.query(BookEntry.TABLE_NAME, projection, null, null, null, null, null);

            mBookList.setText(getString(R.string.num_rows_message) + getString(R.string.space) + cursor.getCount() + getString(R.string.space)
                     + getString(R.string.books) + getString(R.string.newlines));
            mBookList.append(BookEntry._ID + " - " +
                         BookEntry.COLUMN_NAME_BOOK + " - " +
                         BookEntry.COLUMN_PRICE_BOOK + " - " +
                         BookEntry.COLUMN_QUANT_BOOK + " - " +
                         BookEntry.COLUMN_SUPPLIER_NAME_BOOK + " - " +
                         BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK + '\n');

        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME_BOOK);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE_BOOK);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANT_BOOK);
        int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME_BOOK);
        int phoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK);

        while (cursor.moveToNext()) {
            int currentID = cursor.getInt(idColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int supplierName = cursor.getInt(supplierColumnIndex);
            String phoneNumber = cursor.getString(phoneNumberColumnIndex);


            // Display the values from each column of the current row in the cursor in the TextView
            mBookList.append(("\n" + currentID + " - " +
                    name + " - " +
                    price + " - " +
                    quantity + " - " +
                    supplierName + " - " +
                    phoneNumber));
        }

        cursor.close();
    }


        @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }


}
