package com.example.android.bookinventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookinventory.data.InventoryContract;

import org.w3c.dom.Text;

/**
 * Created by Maria on 16.08.2018.
 */

public class BookCursorAdapter extends CursorAdapter{

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list_books, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView =  view.findViewById(R.id.tv_product_name);
        TextView priceView = view.findViewById(R.id.tv_price);
        TextView quantityView = view.findViewById(R.id.tv_price);

        String name = cursor.getString(cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_NAME_BOOK));
        int price = cursor.getInt(cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_PRICE_BOOK));
        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_QUANT_BOOK));

        nameView.setText(name);
        priceView.setText(price + "");
        quantityView.setText(quantity + "");


    }
}
