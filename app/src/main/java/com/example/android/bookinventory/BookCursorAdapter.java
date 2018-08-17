package com.example.android.bookinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookinventory.data.InventoryContract;

/**
 * Created by Maria on 16.08.2018.
 */

public class BookCursorAdapter extends CursorAdapter {


    TextView quantityView;
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list_books, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameView = view.findViewById(R.id.tv_product_name);
        TextView priceView = view.findViewById(R.id.tv_price);
        quantityView = view.findViewById(R.id.et_quantity);

        String name = cursor.getString(cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_NAME_BOOK));
        int price = cursor.getInt(cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_PRICE_BOOK));
        final int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.BookEntry.COLUMN_QUANT_BOOK));

        int id = cursor.getInt(cursor.getColumnIndex(InventoryContract.BookEntry._ID));
        final Uri uri = ContentUris.withAppendedId(InventoryContract.BookEntry.CONTENT_URI, id);

        Button saleButton = view.findViewById(R.id.sell_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity == 0) {
                    Toast.makeText(context,"Out of stock", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(InventoryContract.BookEntry.COLUMN_QUANT_BOOK, quantity - 1);
                    context.getContentResolver().update(uri, cv, null, null);

                }
            }
        });

        nameView.setText(name);
        priceView.setText(price + " lei");
        if(quantity == 0){
            quantityView.setText("Out of stock");
        } else {
            quantityView.setText(quantity + " items left");
        }


    }
}
