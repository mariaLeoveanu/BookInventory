package com.example.android.bookinventory.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;



public class BookProvider extends ContentProvider{

    private BookDbHelper mBookHelper;
    private static final int BOOKS = 100;
    private static final int BOOKS_ID = 101;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_BOOKS, BOOKS);
        uriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_BOOKS + "/#", BOOKS_ID);
    }
    public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + InventoryContract.CONTENT_AUTHORITY + "/" + InventoryContract.PATH_BOOKS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + InventoryContract.CONTENT_AUTHORITY + "/" + InventoryContract.PATH_BOOKS;

    @Override
    public boolean onCreate() {
        mBookHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mBookHelper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch (match){
            case BOOKS:{
                cursor = db.query(InventoryContract.BookEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case  BOOKS_ID:{
                selection = InventoryContract.BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(InventoryContract.BookEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            }
            default: throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:{
                return insertBook(uri, values);
            }
            default: throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertBook (Uri uri, ContentValues values){
        SQLiteDatabase db = mBookHelper.getWritableDatabase();

        String name = values.getAsString(InventoryContract.BookEntry.COLUMN_NAME_BOOK);
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("Book needs a valid name");
        }

        Integer price = values.getAsInteger(InventoryContract.BookEntry.COLUMN_PRICE_BOOK);
        if(price == null || price < 0){
            throw new IllegalArgumentException("Book needs a valid price");
        }

        Integer quantity = values.getAsInteger(InventoryContract.BookEntry.COLUMN_QUANT_BOOK);
        if(quantity == null || quantity < 0){
            throw new IllegalArgumentException("Book needs a valid quantity");
        }

        String supplier = values.getAsString(InventoryContract.BookEntry.COLUMN_SUPPLIER_NAME_BOOK);
        if(supplier == null || supplier.isEmpty()){
            throw new IllegalArgumentException("Book needs a valid supplier name");
        }

        String supplierPhone = values.getAsString(InventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK);
        if(supplierPhone == null || supplierPhone.isEmpty()){
            throw new IllegalArgumentException("Book needs a valid supplier phone number");
        }
        long newRowId = db.insert(InventoryContract.BookEntry.TABLE_NAME, null,values);
        if(newRowId == -1){
            Log.e("BookProvider", "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mBookHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = uriMatcher.match(uri);
        switch (match){
            case BOOKS:
                rowsDeleted = db.delete(InventoryContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case BOOKS_ID:
                selection = InventoryContract.BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(InventoryContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook (uri, values, selection, selectionArgs);
            case BOOKS_ID:
                selection = InventoryContract.BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateBook (uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateBook (Uri uri, ContentValues values, String selection, String[] selectionArgs){
        SQLiteDatabase db = mBookHelper.getWritableDatabase();

        if(values.containsKey(InventoryContract.BookEntry.COLUMN_NAME_BOOK)
                && (values.getAsString(InventoryContract.BookEntry.COLUMN_NAME_BOOK) == null
                || values.getAsString(InventoryContract.BookEntry.COLUMN_NAME_BOOK).isEmpty())){
            throw new IllegalArgumentException("Book needs a valid name");
        }


        if(values.containsKey(InventoryContract.BookEntry.COLUMN_PRICE_BOOK)
                && (values.getAsInteger(InventoryContract.BookEntry.COLUMN_PRICE_BOOK) == null
                || values.getAsInteger(InventoryContract.BookEntry.COLUMN_PRICE_BOOK) < 0)){
            throw new IllegalArgumentException("Book needs a valid price");
        }

        if(values.containsKey(InventoryContract.BookEntry.COLUMN_QUANT_BOOK)
                && (values.getAsInteger(InventoryContract.BookEntry.COLUMN_QUANT_BOOK) == null
                || values.getAsInteger(InventoryContract.BookEntry.COLUMN_QUANT_BOOK) < 0)){
            throw new IllegalArgumentException("Book needs a valid quantity");
        }

        if(values.containsKey(InventoryContract.BookEntry.COLUMN_SUPPLIER_NAME_BOOK)
                && (values.getAsString(InventoryContract.BookEntry.COLUMN_SUPPLIER_NAME_BOOK) == null
                || values.getAsString(InventoryContract.BookEntry.COLUMN_SUPPLIER_NAME_BOOK).isEmpty())){
            throw new IllegalArgumentException("Book needs a valid supplier name");
        }

        if(values.containsKey(InventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK)
                && (values.getAsString(InventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK) == null
                || values.getAsString(InventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK).isEmpty()
                || values.getAsString(InventoryContract.BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER_BOOK).matches("[0-9]+"))){
            throw new IllegalArgumentException("Book needs a valid supplier phone number");
        }
        int rowsUpdated = db.update(InventoryContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowsUpdated != 0){
           getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
