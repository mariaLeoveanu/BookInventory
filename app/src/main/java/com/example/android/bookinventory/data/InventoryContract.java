package com.example.android.bookinventory.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract  {

    public InventoryContract(){}
    public static final String CONTENT_AUTHORITY = "com.example.android.bookinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "bookinventory";

    public static class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        public final static String TABLE_NAME = "books";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME_BOOK = "name";
        public final static String COLUMN_PRICE_BOOK = "price";
        public final static String COLUMN_QUANT_BOOK = "quantity";
        public final static String COLUMN_SUPPLIER_NAME_BOOK = "supplier_name";
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER_BOOK = "supplier_phone_number";

    }
}
