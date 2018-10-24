package com.example.android.v3_inventory_app_stage_2_biddlecom.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.example.android.v3_inventory_app_stage_2_biddlecom.data.BookMasterContract.BookMasterEntry;

/**
 * ContentProvider for the Book Master app.
 */
public class BookMasterProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the "books" table.
     */
    private static final int BOOKS = 100;

    /**
     * URI matcher code for the content URI for a single book in the "books" table.
     */
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to be returned for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Static initializer.  This is run the first time anything is called from this class.
     */
    static {
        //The calls to addURI() go here, for all the content URI patterns that the provider should
        //recognize.  All paths added to the UriMatcher have a corresponding code to return when
        //a match is found.
        sUriMatcher.addURI(BookMasterContract.CONTENT_AUTHORITY, BookMasterContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookMasterContract.CONTENT_AUTHORITY, BookMasterContract.PATH_BOOKS
                + "/#", BOOK_ID);
    }

    /**
     * Database helper object.
     */
    private BookMasterDbHelper mBookMasterDbHelper;

    /**
     * Initialize the Provider and the Database Helper Object.
     */
    @Override
    public boolean onCreate() {
        mBookMasterDbHelper = new BookMasterDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI.  Use the given projection, selection,
     * selection arguments and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        //Get a readable database.
        SQLiteDatabase database = mBookMasterDbHelper.getReadableDatabase();

        //This Cursor will hold the result of the query.
        Cursor cursor;

        //Figure out if the URI Matcher can match the URI to a specific code.
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                //For the BOOKS code, query the books table directly with the given projection,
                //selection, selection arguments and sort order.  The cursor could contain multiple
                //rows of the books table.
                cursor = database.query(BookMasterEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                //For the BOOK_ID code, extract out the ID from the URI.
                //For an example URI such as:
                //content://com.example.android.v3_inventory_app_stage_2_biddlecom/books/5
                //The selection will be "_id=?" and the selection argument will be a String array
                //containing the actual ID of 5 (in this case).
                //
                //For every "?" in the selection, we need to have an element in the selection
                //arguments that fills in the "?".  Since we have one question mark in the selection
                //we have one string in the selection arguments' String array.
                selection = BookMasterEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                //This will perform a query on the books table to return a Cursor containing
                //that row of the table.
                cursor = database.query(BookMasterEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknonwn URI " + uri);
        }

        /**
         * Set a notification URI on the cursor, so we know what content URI the cursor was created
         * for.  If the data at this URI changes, then we know we need to update the cursor.
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        //Return the cursor.
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a new book into the database with the given contentValues.  Return the new content
     * URI for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        //Check that the TITLE is not null.
        String title = values.getAsString(BookMasterEntry.COLUMN_BOOK_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Book requires a Title.");
        }

        //Check that the AUTHOR FIRST NAME is not null.
        String authorFirstName = values.getAsString(BookMasterEntry.COLUMN_AUTHOR_FIRST_NAME);
        if (authorFirstName == null) {
            throw new IllegalArgumentException("Book requires an Author First Name.");
        }

        //Check that the AUTHOR LAST NAME is not null.
        String authorLastName = values.getAsString(BookMasterEntry.COLUMN_AUTHOR_LAST_NAME);
        if (authorLastName == null) {
            throw new IllegalArgumentException("Book requires an Author Last Name.");
        }

        //Check that the ISBN is not null.
        String isbn = values.getAsString(BookMasterEntry.COLUMN_ISBN);
        if (isbn == null) {
            throw new IllegalArgumentException("Book requires an ISBN number");
        }

        //Check that the PRICE is not null.
        String price = values.getAsString(BookMasterEntry.COLUMN_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Book requires a Price.");
        }

        //Check that the QUANTITY is not null.
        Integer quantity = values.getAsInteger(BookMasterEntry.COLUMN_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Book requires a Quantity");
        }

        //Check that the SUPPLIER NAME is not null.
        String supplierName = values.getAsString(BookMasterEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Book requires a Supplier Name.");
        }

        //Check that the SUPPLIER PHONE NUMBER is not null.
        String supplierPhoneNumber = values.getAsString(BookMasterEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNumber == null) {
            throw new IllegalArgumentException("Book requires a Supplier Phone Number.");
        }

        //Gets the database in write mode.
        SQLiteDatabase database = mBookMasterDbHelper.getWritableDatabase();

        //Insert the new book with the given values.
        long id = database.insert(BookMasterEntry.TABLE_NAME, null, values);

        //If the ID is -1, then the insertion failed. Pop up a toast message and return null.
        if (id == -1) {
            Toast.makeText(getContext(), "Sorry, the book was not inserted into database.\n" +
                    "Please try again. " + String.valueOf(id), Toast.LENGTH_LONG).show();
            return null;
        }

        // Notify all listeners that the data has changed for the book content URI.
        getContext().getContentResolver().notifyChange(uri, null);

        //Once we know the ID of the new row in the table, return the new URI with the ID (of the
        //newly inserted row) appended to the end of it.
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                //For the BOOK_ID code, extract out the ID from the URI, so we know which row to
                //update.  Selection will be the "_id=?" and the selection arguments will be a
                //String array containing the actual ID.
                selection = BookMasterEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values.  Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books in the
     * database).  Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //If the BookMasterEntry#COLUMN_BOOK_TITLE key is present,
        if (values.containsKey(BookMasterEntry.COLUMN_BOOK_TITLE)) {
            //Check that the name is not null.
            String title = values.getAsString(BookMasterEntry.COLUMN_BOOK_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Book requires a Title.");
            }
        }

        //If the BookMasterEntry#COLUMN_AUTHOR_FIRST_NAME key is present,
        if (values.containsKey(BookMasterEntry.COLUMN_AUTHOR_FIRST_NAME)) {
            //Check that the AUTHOR FIRST NAME is not null.
            String authorFirstName = values.getAsString(BookMasterEntry.COLUMN_AUTHOR_FIRST_NAME);
            if (authorFirstName == null) {
                throw new IllegalArgumentException("Book requires an Author First Name.");
            }
        }

        //If the BookMasterEntry#COLUMN_AUTHOR_LAST_NAME key is present,
        if (values.containsKey(BookMasterEntry.COLUMN_AUTHOR_LAST_NAME)) {
            //Check that the AUTHOR LAST NAME is not null.
            String authorLastName = values.getAsString(BookMasterEntry.COLUMN_AUTHOR_LAST_NAME);
            if (authorLastName == null) {
                throw new IllegalArgumentException("Book requires an Author Last Name.");
            }
        }

        //If the BookMasterEntry#COLUMN_ISBN key is present,
        if (values.containsKey(BookMasterEntry.COLUMN_ISBN)) {
            //Check that the ISBN is not null.
            String isbn = values.getAsString(BookMasterEntry.COLUMN_ISBN);
            if (isbn == null) {
                throw new IllegalArgumentException("Book requires an ISBN number");
            }
        }

        //If the BookMasterEntry#COLUMN_PRICE key is present,
        if (values.containsKey(BookMasterEntry.COLUMN_PRICE)) {
            //Check that the PRICE is not null.
            String price = values.getAsString(BookMasterEntry.COLUMN_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Book requires a Price.");
            }
        }

        //If the BookMasterEntry#COLUMN_QUANTITY key is present,
        if (values.containsKey(BookMasterEntry.COLUMN_QUANTITY)) {
            //Check that the QUANTITY is not null.
            Integer quantity = values.getAsInteger(BookMasterEntry.COLUMN_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Book requires a Quantity");
            }
        }

        //If the BookMasterEntry#COLUMN_SUPPLIER_NAME key is present,
        if (values.containsKey(BookMasterEntry.COLUMN_SUPPLIER_NAME)) {
            //Check that the SUPPLIER NAME is not null.
            String supplierName = values.getAsString(BookMasterEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Book requires a Supplier Name.");
            }
        }

        //If the BookMasterEntry#COLUMN_SUPPLIER_PHONE_NUMBER key is present,
        if (values.containsKey(BookMasterEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            //Check that the SUPPLIER PHONE NUMBER is not null.
            String supplierPhoneNumber = values.getAsString(BookMasterEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNumber == null) {
                throw new IllegalArgumentException("Book requires a Supplier Phone Number.");
            }
        }

        //If there are no values to update, then don't try to update the database.
        if (values.size() == 0) {
            return 0;
        }

        //Otherwise get a writable database to update the data.
        SQLiteDatabase database = mBookMasterDbHelper.getWritableDatabase();

        //Perform the update on the database and get the number of rows affected.
        int rowsUpdated = database.update(BookMasterEntry.TABLE_NAME, values, selection, selectionArgs);

        //If one or more rows was updated, then notify all listeners that the data at the given
        //URI has changed.
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Return the number of rows updated.
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //Get a writable database.
        SQLiteDatabase database = mBookMasterDbHelper.getWritableDatabase();

        //Track the number of rows that were deleted.
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                //Delete all rows that match the selection and selectionArgs.
                rowsDeleted = database.delete(BookMasterEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                //Delete a single row given by the ID in the URI.
                selection = BookMasterEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookMasterEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        //If one or more rows was deleted, then notify all listeners that the data at the given
        //URI has changed.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Return the number of rows deleted.
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookMasterEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookMasterEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + "with match " + match);
        }
    }
}