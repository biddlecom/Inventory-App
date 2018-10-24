package com.example.android.v3_inventory_app_stage_2_biddlecom;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.v3_inventory_app_stage_2_biddlecom.data.BookMasterContract.BookMasterEntry;

/**
 * Displays a list of books that were entered and stored in the app.
 */
public class BookMasterMainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;

    BookMasterCursorAdapter mBookCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_book_master);

        //Set up the Floating Action Button to open the EditorActivity.
        FloatingActionButton fab = findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookMasterMainActivity.this,
                        BookMasterEditorActivity.class);
                startActivity(intent);
            }
        });

        //Find the ListView that will be populated with the Book data.
        ListView bookListView = findViewById(R.id.list_view);

        //Find and set an Empty View on the ListView so that it only shows when the ListView has
        //zero items to display.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        //Setup an Adapter to create a list item for each row of book data in the cursor.
        //There is no book data yet (until the loader finishes) so pass in null for the cursor.
        mBookCursorAdapter = new BookMasterCursorAdapter(this, null);
        bookListView.setAdapter(mBookCursorAdapter);

        //Setup the item click listener.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Create a new intent to go to the EditorActivity.
                Intent intent = new Intent(BookMasterMainActivity.this,
                        BookMasterEditorActivity.class);

                //Form the content URI that represents the specific pet that was clicked on by
                //appending the "id" (passed as input to this method) on the
                //BookMasterEntry#CONTENT_URI.
                //For example, the URI would be:
                //"content://com.example.android.v3_inventory_app_stage_2_biddlecom/books/5"
                //If the book with the ID of 5 was clicked on.
                Uri currentBookUri = ContentUris.withAppendedId(BookMasterEntry.CONTENT_URI, id);

                //Set the URI on the data field of the intent.
                intent.setData(currentBookUri);

                //Launch the EditorActivity to display the data for the current book.
                startActivity(intent);
            }
        });

        //Kick off the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    /**
     * Helper method to insert hard coded book data into the database.  This is for debugging
     * purposes and to allow you to populate the Listview much faster.
     */
    private void insertDummyBookData() {
        ContentValues values = new ContentValues();
        values.put(BookMasterEntry.COLUMN_BOOK_TITLE, "How To Archer");
        values.put(BookMasterEntry.COLUMN_AUTHOR_FIRST_NAME, "Sterling");
        values.put(BookMasterEntry.COLUMN_AUTHOR_LAST_NAME, "Archer");
        values.put(BookMasterEntry.COLUMN_ISBN, "9780062066312");
        values.put(BookMasterEntry.COLUMN_PRICE, "17.99");
        values.put(BookMasterEntry.COLUMN_QUANTITY, "3");
        values.put(BookMasterEntry.COLUMN_SUPPLIER_NAME, "Dey St.");
        values.put(BookMasterEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "111-222-3333");

        Uri newUri = getContentResolver().insert(BookMasterEntry.CONTENT_URI, values);
    }

    /**
     * Show a dialog that warns the user that they are about to delete all of the books in the list.
     */
    private void showDeleteAllConfirmationDialog() {
        //Create an AlertDialog.Builder and set the message, and the click listeners for the positive
        //and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_books_alert_dialog_message);
        builder.setPositiveButton(R.string.delete_all_books_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //User clicked the "Delete" button, so show the second alert message to make sure
                //that is what the user intends to do, because this could be a catastrophic event
                //if the user hits "Delete All" by mistake.
                showSecondDeleteAllConfirmationDialog();
            }
        });
        builder.setNegativeButton(R.string.cancel_delete_all_books_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //User clicked on the "Cancel" button, so dismiss the dialog and return to the Activity.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Create and show the AlertDialog.
        AlertDialog deleteAlertDialog = builder.create();
        deleteAlertDialog.show();
    }

    /**
     * Show a second dialog that warns the user that they are about to delete all of the books in the list.
     */
    private void showSecondDeleteAllConfirmationDialog() {
        //Create an AlertDialog.Builder and set the message, and the click listeners for the positive
        //and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_books_second_alert_dialog_message);
        builder.setPositiveButton(R.string.delete_all_books_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //User clicked the "Delete" button, so delete all the books in the list.
                deleteAllBooks();
            }
        });
        builder.setNegativeButton(R.string.cancel_delete_all_books_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //User clicked on the "Cancel" button, so dismiss the dialog and return to the Activity.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Create and show the AlertDialog.
        AlertDialog deleteAlertDialog = builder.create();
        deleteAlertDialog.show();
    }

    /**
     * Helper method to delete all the books in the database.
     */
    private void deleteAllBooks() {
        //Call the ContentResolver to delete all the books at the given CONTENT_URI.  Pass in null
        //for the selection and selectionArgs because the CONTENT_URI defines all the books that
        //we want to delete.
        int rowsDeleted = getContentResolver().delete(BookMasterEntry.CONTENT_URI, null, null);

        //Show a toast message detailing whether or not the deletion was successful.
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.delete_all_unsuccessful),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_all_successful),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu options for the res/menu/menu_main.xml file.
        //This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //User clicked on a menu item in the app bar overflow menu.
        switch (item.getItemId()) {
            //Respond to a click on the "Insert Dummy Data" menu option.
            case R.id.action_insert_dummy_data:
                insertDummyBookData();
                return true;
            //Respond to a click on the "Delete All Books" menu option.
            case R.id.action_delete_all_books:
                showDeleteAllConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection that specifies which columns of the database we care about.
        String[] projection = {
                BookMasterEntry._ID,
                BookMasterEntry.COLUMN_BOOK_TITLE,
                BookMasterEntry.COLUMN_PRICE,
                BookMasterEntry.COLUMN_QUANTITY};

        //This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,   //Parent activity context
                BookMasterEntry.CONTENT_URI,    //Provider content URI to query
                projection,                     //The columns to include in the resulting Cursor
                null,                  //No selection clause
                null,               //No selection arguments
                null);                 //Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Update the BookMasterCursorAdapter with the new Cursor containing updated book data.
        mBookCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Callback called when the data needs to be deleted.
        mBookCursorAdapter.swapCursor(null);
    }
}