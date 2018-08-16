package com.example.android.bookinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.bookinventory.data.BookDbHelper;
import com.example.android.bookinventory.data.InventoryContract.BookEntry;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private BookDbHelper mBookDbHelper;
    BookCursorAdapter mAdapter;
    private static final int BOOK_LOADER = 0;
    public static final String DEFAULT_BOOK_NAME = "Jane Austen";
    public static final int DEFAULT_BOOK_PRICE = 29;
    public static final int DEFAULT_QUANTITY = 3;
    public static final String DEFAULT_SUPPLIER_NAME = "Penguin English Library";
    public static final String DEFAULT_PHONE_NUMBER = "45679362291";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        mAdapter = new BookCursorAdapter(this, null);
        mBookDbHelper = new BookDbHelper(this);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                intent.setData(ContentUris.withAppendedId(BookEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });
        listView.setEmptyView(findViewById(R.id.empty_title_text));
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    public void insertBook (){

        SQLiteDatabase db = mBookDbHelper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COLUMN_NAME_BOOK, DEFAULT_BOOK_NAME);
        contentValues.put(BookEntry.COLUMN_PRICE_BOOK, DEFAULT_BOOK_PRICE);
        contentValues.put(BookEntry.COLUMN_QUANT_BOOK, DEFAULT_QUANTITY);
        contentValues.put(BookEntry.COLUMN_SUPPLIER_NAME_BOOK, DEFAULT_SUPPLIER_NAME);
        contentValues.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK, DEFAULT_PHONE_NUMBER);

        getContentResolver().insert(BookEntry.CONTENT_URI, contentValues);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_default_data:
                insertBook();
                return true;
            case R.id.action_delete_all_entries:
                getContentResolver().delete(BookEntry.CONTENT_URI,null, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }





    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {BookEntry._ID,
                BookEntry.COLUMN_NAME_BOOK,
                BookEntry.COLUMN_PRICE_BOOK,
                BookEntry.COLUMN_QUANT_BOOK,
                BookEntry.COLUMN_SUPPLIER_NAME_BOOK,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK};
        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
