package com.example.android.v3_inventory_app_stage_2_biddlecom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.v3_inventory_app_stage_2_biddlecom.data.BookMasterContract.BookMasterEntry;

/**
 * The BookMasterCursorAdapter is an adapter for the ListView that uses a Cursor of book data
 * as its data source.  The Adapter knows how to create list items for each row of book data
 * in the Cursor.
 */
public class BookMasterCursorAdapter extends CursorAdapter {

    /**
     * Construct a new Cursor Adapter.
     *
     * @param context is the context.
     * @param cursor  The cursor from which to get the data.
     */
    public BookMasterCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);  //zero flags.
    }

    /**
     * Makes a new blank list item view.  No data is set (or bound) to the views yet.
     *
     * @param context is the app context.
     * @param cursor  is the cursor from which to get the data.  The cursor is already moved to
     *                the correct position.
     * @param parent  is the parent to which the new view is attached to.
     * @return is the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate a list item view using the layout specified in book_list_item.xml.
        return LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by the cursor) to the given
     * list item layout.  For example, the book title can be set in the title TextView
     * (ID: book_title_text_view) in the book_list_item.xml layout.
     *
     * @param view    is the existing view that was returned earlier by the newView() method.
     * @param context is the app context.
     * @param cursor  is the cursor from which to get the data.  The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        //Find the individual views that we want to modify in the book list item layout.
        TextView titleTextView = view.findViewById(R.id.book_title_text_view);
        TextView priceTextView = view.findViewById(R.id.book_price_number_text_view);

        //Find the columns of the book attributes that we are interested in.
        int titleColumnIndex = cursor.getColumnIndex(BookMasterEntry.COLUMN_BOOK_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(BookMasterEntry.COLUMN_PRICE);

        //Read the book attributes from the cursor for the current book.
        String bookTitle = cursor.getString(titleColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);

        //If the book title is an empty string or null, then use some default text that says:
        //"Unknown Title", so the book_title_text_view isn't blank.
        if (TextUtils.isEmpty(bookTitle)) {
            bookTitle = context.getString(R.string.unknown_title);
        }

        //If the book price is an empty string or null, then use some default text that says:
        //"Unknown Price", so the book_price_text_view isn't blank.
        if (TextUtils.isEmpty(bookPrice)) {
            bookPrice = context.getString(R.string.unknown_price);
        }

        //Update the TextViews with the attributes of the current book.
        titleTextView.setText(bookTitle);
        priceTextView.setText(bookPrice);

        /**
         * Find and initialize the QUANTITY EditText view, the SELL ITEM button (to decrease the
         * quantity by one, and the RelativeLayout (used to turn the background red when the
         * quantity reaches zero, AKA- SOLD OUT).
         */
        final TextView quantityTextView = view.findViewById(R.id.book_quantity_number_text_view);
        final Button saleItemButton = view.findViewById(R.id.sell_item_button);
        final RelativeLayout colorLayoutBackground = view.findViewById(R.id.for_color_background_relative_layout);

        int bookQuantity = 0;

        //Find the column of the book attribute (QUANTITY).
        bookQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookMasterEntry.COLUMN_QUANTITY));

        final BookQuantityClass bookQuantityClass = new BookQuantityClass();
        bookQuantityClass.setBookQuantity(bookQuantity);
        if (bookQuantity == 0) {
            //Disable the saleItemButton (SELL ITEM button).
            saleItemButton.setEnabled(false);
            //Change the saleItemButton (SELL ITEM button) to "SOLD OUT".
            saleItemButton.setText(R.string.sold_out);
            //Change the saleItemButton (SELL ITEM button) "SOLD OUT" text to red.
            saleItemButton.setTextColor(Color.rgb(220, 0, 0));
            //Change the colorLayoutBackground (the RelativeLayout) background color to red.
            colorLayoutBackground.setBackgroundColor(Color.rgb(215, 50, 50));
        } else {
            //Enable the saleItemButton (SELL ITEM button).
            saleItemButton.setEnabled(true);
            //Change the saleItemButton (SELL ITEM button) to "SELL ITEM".
            saleItemButton.setText(R.string.button_sell_item);
            //Change the saleItemButton (SELL ITEM button) "SELL ITEM" text to green.
            saleItemButton.setTextColor(Color.rgb(30, 164, 34));
            //Change the colorLayoutBackground (the RelativeLayout) background color to white.
            colorLayoutBackground.setBackgroundColor(Color.rgb(255, 255, 255));
        }

        saleItemButton.setOnClickListener(new View.OnClickListener() {
            int currentBookId = cursor.getInt(cursor.getColumnIndex(BookMasterEntry._ID));
            Uri contentUri = Uri.withAppendedPath(BookMasterEntry.CONTENT_URI, Integer.toString(currentBookId));

            @Override
            public void onClick(View view) {
                // Create a ContentValues object.
                ContentValues values = new ContentValues();
                int bookQuantityValue = bookQuantityClass.getBookQuantity();
                //Decrease the quantity by one each time the button is clicked.
                bookQuantityValue--;

                //Put the new quantity value into the quantity column.
                values.put(BookMasterEntry.COLUMN_QUANTITY, bookQuantityValue);
                bookQuantityClass.setBookQuantity(bookQuantityValue);

                // Insert a new row for the book into the provider using the ContentResolver.
                // Use the {@link BookEntry#CONTENT_URI} to indicate that we want to insert
                // into the books database table.
                // Receive the new content URI that will allow us to access the book's data in the future.
                int rowsUpdatedId = context.getContentResolver().update(contentUri, values, null, null);

                if (rowsUpdatedId == 0) {
                    throw new IllegalArgumentException(context.getString(R.string.editor_update_book_failed));
                }
            }
        });

        quantityTextView.setText(String.valueOf(bookQuantityClass.getBookQuantity()));
    }

    static class BookQuantityClass {

        int bookQuantity;

        private int getBookQuantity() {
            return bookQuantity;
        }

        private void setBookQuantity(int bookQuantity) {
            this.bookQuantity = bookQuantity;
        }
    }
}