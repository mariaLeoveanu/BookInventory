package com.example.android.bookinventory.data;

import android.provider.BaseColumns;

public class InventoryContract  {

    public InventoryContract(){}

    public class BookEntry implements BaseColumns
    {
        public final static String TABLE_NAME = "books";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME_BOOK = "name";
        public final static String COLUMN_PRICE_BOOK = "price";
        public final static String COLUMN_QUANT_BOOK = "quantity";
        public final static String COLUMN_SUPPLIER_NAME_BOOK = "supplier_name";
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER_BOOK = "supplier_phone_number";

    }
}
