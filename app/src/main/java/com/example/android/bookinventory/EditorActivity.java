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
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookinventory.data.BookDbHelper;
import com.example.android.bookinventory.data.InventoryContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_EDITOR_LOADER = 0;
    String mName;
    String mPrice;
    String mQuantity;
    String mSupplier;
    String mPhoneNumber;
    int quantInt;
    int priceInt;
    private BookDbHelper dbHelper = new BookDbHelper(this);
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private TextView mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mPhoneNumberEditText;
    private Uri bookUri;
    private boolean mBookHasChanged;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };
    private Button mIncreaseButton;
    private Button mDecreaseButton;
    private Button mOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mOrderButton = findViewById(R.id.order_b);

        Intent intent = getIntent();
        bookUri = intent.getData();
        if (bookUri == null) {
            mOrderButton.setVisibility(View.INVISIBLE);
            setTitle("Add new book");
            invalidateOptionsMenu();
        } else {
            mOrderButton.setVisibility(View.VISIBLE);
            setTitle("Edit book");
            getLoaderManager().initLoader(BOOK_EDITOR_LOADER, null, this);
        }

        mNameEditText = findViewById(R.id.et_name);
        mPriceEditText = findViewById(R.id.et_price);
        mQuantityEditText = findViewById(R.id.et_quantity);
        mSupplierNameEditText = findViewById(R.id.et_name_supplier);
        mPhoneNumberEditText = findViewById(R.id.et_phone_number);
        mDecreaseButton = findViewById(R.id.b_decrease);
        mIncreaseButton = findViewById(R.id.b_increase);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mPhoneNumberEditText.setOnTouchListener(mTouchListener);

        mDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(mDecreaseButton);
            }
        });
        mIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(mIncreaseButton);
            }
        });
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneNumberEditText.getText().toString().trim();
                Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intentCall);
            }
        });

    }

    private void updateQuantity(View view) {
        int quant = 0;
        mQuantity = mQuantityEditText.getText().toString().trim();
        if (!mQuantity.isEmpty()) {
            quant = Integer.parseInt(mQuantity);
            switch (view.getId()) {
                case R.id.b_decrease:
                    if (quant > 0) {
                        quant--;
                    }
                    break;
                case R.id.b_increase:
                    quant++;
            }
        }
        mQuantityEditText.setText(String.valueOf(quant));
        mBookHasChanged = true;
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

    public void saveBook() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        mName = mNameEditText.getText().toString().trim();
        mSupplier = mSupplierNameEditText.getText().toString().trim();
        mPhoneNumber = mPhoneNumberEditText.getText().toString().trim();
        mPrice = mPriceEditText.getText().toString().trim();
        mQuantity = mQuantityEditText.getText().toString().trim();
        if (TextUtils.isEmpty(mName)
                && mPrice == null
                && mQuantity == null
                && TextUtils.isEmpty(mSupplier)
                && TextUtils.isEmpty(mPhoneNumber)) {
            Toast.makeText(this, "You have not completed any field!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mPrice)) {
            Toast.makeText(this, "Complete the price field",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            priceInt = Integer.parseInt(mPrice);
            contentValues.put(InventoryContract.BookEntry.COLUMN_PRICE_BOOK, priceInt);
        }

        if (TextUtils.isEmpty(mQuantity)) {
            Toast.makeText(this, "Complete the quantity field",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            quantInt = Integer.parseInt(mQuantity);
            contentValues.put(InventoryContract.BookEntry.COLUMN_QUANT_BOOK, quantInt);
        }

        contentValues.put(InventoryContract.BookEntry.COLUMN_NAME_BOOK, mName);
        contentValues.put(InventoryContract.BookEntry.COLUMN_SUPPLIER_NAME_BOOK, mSupplier);
        contentValues.put(InventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK, mPhoneNumber);

        if (bookUri == null) {
            Uri uri = getContentResolver().insert(InventoryContract.BookEntry.CONTENT_URI, contentValues);
            if (uri == null) {
                //Toast.makeText(this, "Insert item failed",
                //Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Insert item successful",
                        Toast.LENGTH_LONG).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(bookUri, contentValues, null, null);
            if (rowsAffected == 0) {
                //Toast.makeText(this, "Error updating the item",
                //Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Item updated",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    private void deleteBook() {
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
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(InventoryContract.BookEntry.COLUMN_NAME_BOOK);
            int priceColumnIndex = data.getColumnIndex(InventoryContract.BookEntry.COLUMN_PRICE_BOOK);
            int quantityColumnIndex = data.getColumnIndex(InventoryContract.BookEntry.COLUMN_QUANT_BOOK);
            int supplierColumnIndex = data.getColumnIndex(InventoryContract.BookEntry.COLUMN_SUPPLIER_NAME_BOOK);
            int phoneNumberColumnIndex = data.getColumnIndex(InventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK);

            mName = data.getString(nameColumnIndex);
            mPrice = data.getString(priceColumnIndex);
            mQuantity = data.getString(quantityColumnIndex);
            mSupplier = data.getString(supplierColumnIndex);
            mPhoneNumber = data.getString(phoneNumberColumnIndex);

            mNameEditText.setText(mName);
            mPriceEditText.setText(mPrice);
            mQuantityEditText.setText(mQuantity);
            mSupplierNameEditText.setText(mSupplier);
            mPhoneNumberEditText.setText(mPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText(null);
        mPriceEditText.setText(null);
        mQuantityEditText.setText(null);
        mSupplierNameEditText.setText(null);
        mPhoneNumberEditText.setText(null);
    }
}
