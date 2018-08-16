package com.example.android.bookinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookinventory.data.BookDbHelper;
import com.example.android.bookinventory.data.InventoryContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_EDITOR_LOADER = 0;
    private BookDbHelper dbHelper = new BookDbHelper(this);
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private TextView mQuantityTextView;
    private EditText mSupplierNameEditText;
    private EditText mPhoneNumber;
    private Uri bookUri;
    private boolean mBookHasChanged;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        bookUri = intent.getData();
        if (bookUri == null){
            setTitle("Add new book");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit book");
            getLoaderManager().initLoader(BOOK_EDITOR_LOADER, null, this);
        }

        mNameEditText = findViewById(R.id.et_name);
        mPriceEditText = findViewById(R.id.et_price);
        mQuantityTextView = findViewById(R.id.tv_quantity);
        mSupplierNameEditText = findViewById(R.id.et_name_supplier);
        mPhoneNumber = findViewById(R.id.et_phone_number);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityTextView.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mPhoneNumber.setOnTouchListener(mTouchListener);

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (bookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void saveBook(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String name = mNameEditText.getText().toString().trim();
        String price =mPriceEditText.getText().toString().trim();
        String quantity = mQuantityTextView.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String phoneNumber = mPhoneNumber.getText().toString().trim();
        int priceInt = 0;
        if (!TextUtils.isEmpty(price)){
            priceInt = Integer.parseInt(price);
        }
        int quantityInt = 0;
        if(!TextUtils.isEmpty(quantity)){
            quantityInt = Integer.parseInt(quantity);
        }
        if(TextUtils.isEmpty(name)
                && TextUtils.isEmpty(price)
                && TextUtils.isEmpty(quantity)
                && TextUtils.isEmpty(supplierName)
                && TextUtils.isEmpty(phoneNumber)){
            return;
        }



        contentValues.put(InventoryContract.BookEntry.COLUMN_NAME_BOOK, name);
        contentValues.put(InventoryContract.BookEntry.COLUMN_PRICE_BOOK, priceInt);
        contentValues.put(InventoryContract.BookEntry.COLUMN_QUANT_BOOK, quantityInt);
        contentValues.put(InventoryContract.BookEntry.COLUMN_SUPPLIER_NAME_BOOK, supplierName);
        contentValues.put(InventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK, phoneNumber);
        if(bookUri == null){
            Uri uri = getContentResolver().insert(InventoryContract.BookEntry.CONTENT_URI, contentValues);
            if(uri == null) {
                Toast.makeText(this, "Insert item failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Insert pet successful",
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            int rowsAffected = getContentResolver().update(bookUri, contentValues, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Error updating the item",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item updated",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void deleteBook(){
        int rowDeleted;
        if (bookUri != null) {
            rowDeleted = getContentResolver().delete(bookUri, null, null);
        } else {
            rowDeleted = 0;
        }
        if (rowDeleted != 0) {
            Toast.makeText(this, "Item successfully deleted.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error deleting the item",
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.BookEntry._ID,
                InventoryContract.BookEntry.COLUMN_NAME_BOOK,
                InventoryContract.BookEntry.COLUMN_PRICE_BOOK,
                InventoryContract.BookEntry.COLUMN_QUANT_BOOK,
                InventoryContract.BookEntry.COLUMN_SUPPLIER_NAME_BOOK,
                InventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK};
        return new CursorLoader(this, bookUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null || data.getCount() < 1) {
            return;
        }
        if(data.moveToFirst()){
            int nameColumnIndex = data.getColumnIndex(InventoryContract.BookEntry.COLUMN_NAME_BOOK);
            int priceColumnIndex = data.getColumnIndex(InventoryContract.BookEntry.COLUMN_PRICE_BOOK);
            int quantityColumnIndex = data.getColumnIndex(InventoryContract.BookEntry.COLUMN_QUANT_BOOK);
            int supplierColumnIndex = data.getColumnIndex(InventoryContract.BookEntry.COLUMN_SUPPLIER_NAME_BOOK);
            int phoneNumberColumnIndex = data.getColumnIndex(InventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK);

            String name = data.getString(nameColumnIndex);
            int price = data.getInt(priceColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            String supplierName = data.getString(supplierColumnIndex);
            String phoneNumber = data.getString(phoneNumberColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(price + "");
            mQuantityTextView.setText(quantity + "");
            mSupplierNameEditText.setText(supplierName);
            mPhoneNumber.setText(phoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText(null);
        mPriceEditText.setText(null);
        mQuantityTextView.setText(null);
        mSupplierNameEditText.setText(null);
        mPhoneNumber.setText(null);
    }
}
