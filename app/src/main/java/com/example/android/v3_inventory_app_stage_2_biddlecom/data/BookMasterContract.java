package com.example.android.v3_inventory_app_stage_2_biddlecom.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookMasterContract {

    /**
     * The "Content Authority" is a name for the entire Content Provider.  A convenient string to
     * use for the Content Authority is the package name for this app which will be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.v3_inventory_app_stage_2_biddlecom";

    /**
     * Use the CONTENT_AUTHORITY to create the base of all URIs which all apps will use to
     * contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Append to the BASE_CONTENT_URI for possible valid URI paths.
     * For example, content://com.example.android.v3_inventory_app_stage_2_biddlecom/books is a
     * valid path for looking at book data.
     * However, com.example.android.v3_inventory_app_stage_2_biddlecom/employees is not a valid
     * path and will fail because the Content Provider hasn't been given any information on
     * what to do with "employees".
     */
    public static final String PATH_BOOKS = "books";

    //To prevent someone from accidentally instantiating the contract class, give it an
    //empty constructor.
    private BookMasterContract() {
    }

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry into the table represents a single book.
     */
    public static abstract class BookMasterEntry implements BaseColumns {

        /**
         * The Content URI to access the book data in the Provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the #CONTENT_URI for a list of books
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the #CONTENT_URI for a single pet
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * Name of the database table for the books
         */
        public static final String TABLE_NAME = "books";

        /**
         * Title of the book
         * Type: TEXT
         */
        public static final String COLUMN_BOOK_TITLE = "book_title";

        /**
         * Authors first name
         * Type: TEXT
         */
        public static final String COLUMN_AUTHOR_FIRST_NAME = "author_first_name";

        /**
         * Authors last name
         * Type: TEXT
         */
        public static final String COLUMN_AUTHOR_LAST_NAME = "author_last_name";

        /**
         * International Standard Book Number (ISBN) of the book
         * Type: TEXT
         */
        public static final String COLUMN_ISBN = "isbn_number";

        /**
         * Price of the book
         * Type: TEXT
         */
        public static final String COLUMN_PRICE = "price";

        /**
         * Quantity of the book
         * Type: INTEGER
         */
        public static final String COLUMN_QUANTITY = "quantity";

        /**
         * Name of the book supplier
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";

        /**
         * Phone number of the book supplier
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}