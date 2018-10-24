package com.example.android.v3_inventory_app_stage_2_biddlecom;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.v3_inventory_app_stage_2_biddlecom.data.BookMasterContract.BookMasterEntry;

/**
 * Allows the user to create a new book or edit an existing one.
 */
public class BookMasterEditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the Book data loader.
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Variable that keeps track of all valid data.
     */
    private boolean allDataIsValid = false;

    /**
     * Content URI for the existing book (null if it's a new book).
     */
    private Uri mCurrentBookUri;

    /**
     * EditText field to enter the title of the book.
     */
    private EditText mTitleEditText;

    /**
     * EditText field to enter the Author's First Name.
     */
    private EditText mAuthorFirstNameEditText;

    /**
     * EditText field to enter the Author's Last Name.
     */
    private EditText mAuthorLastNameEditText;

    /**
     * EditText field to enter the ISBN of the book.
     */
    private EditText mIsbnEditText;

    /**
     * EditText field to enter the Price of the book.
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the quantity of the book.
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the Supplier name.
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the Supplier Phone Number.
     */
    private EditText mSupplierPhoneNumberEditText;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false).
     */
    private boolean mBookHasBeenEdited = false;

    /**
     * OnTouchListener that listens for any touches from the user on any View to indicate that they
     * are modifying the view, and we can change the mBookHasBeenEdited to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasBeenEdited = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_book_master);

        //Examine the intent that was used to launch this activity, in order to figure out if we're
        //creating a new book or editing an existing one.
        final Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        //If the intent DOES NOT contain a book URI, then we know that we are creating a new book.
        if (mCurrentBookUri == null) {
            //Then this is a new book and we can change our app bar to say "Add A New Book".
            setTitle(getString(R.string.editor_activity_add_a_new_book));
            //Invalidate the options menu so that the "Delete" menu option can be hidden.
            //It doesn't make sense to delete a book that hasn't been created yet.
            invalidateOptionsMenu();
        } else {
            //This is an exiting book, so change the app bar to say "Edit An Existing Book".
            setTitle(getString(R.string.editor_activity_edit_an_existing_book));

            //Initialize a loader to read the pet data from the database and display the current
            //values in the editor.
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        //Find all the relevant views that we need to read user input from.
        mTitleEditText = findViewById(R.id.edit_title_text_view);
        mAuthorFirstNameEditText = findViewById(R.id.edit_author_first_name_text_view);
        mAuthorLastNameEditText = findViewById(R.id.edit_author_last_name_text_view);
        mIsbnEditText = findViewById(R.id.edit_isbn_text_view);
        mPriceEditText = findViewById(R.id.edit_price_text_view);
        mQuantityEditText = findViewById(R.id.edit_quantity_text_view);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name_text_view);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_supplier_phone_number_text_view);

        //Set OnTouchListeners on all the fields so that we know if the user has touched or
        //modified any of the fields.  This way we will know if there are any unsaved changes
        //or not, if the user tries to back out of the activity before saving the book.
        mTitleEditText.setOnTouchListener(mTouchListener);
        mAuthorFirstNameEditText.setOnTouchListener(mTouchListener);
        mAuthorLastNameEditText.setOnTouchListener(mTouchListener);
        mIsbnEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

        //Find and initialize the MINUS button.
        final Button minusButton = findViewById(R.id.editor_minus_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = 0;
                //Get the quantity and toss it to a String.
                String quantityString = mQuantityEditText.getText().toString();
                //Check to make sure the String is not empty.
                if (!TextUtils.isEmpty(quantityString)) {
                    quantity = Integer.parseInt(quantityString);
                    if (quantity > 0) {
                        //Decrease the quantity by one each time the minus button is pressed.
                        quantity--;
                    }
                    mQuantityEditText.setText(String.valueOf(quantity));
                }
            }
        });

        //Find and initialize the PLUS button.
        final Button plusButton = findViewById(R.id.editor_plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = 0;
                //Get the quantity and toss it to a String.
                String quantityString = mQuantityEditText.getText().toString();
                //Check to make sure the String is not empty.
                if (!TextUtils.isEmpty(quantityString)) {
                    quantity = Integer.parseInt(quantityString);
                    if (quantity >= 0) {
                        //Increase the quantity by one each time the plus button is pressed.
                        quantity++;
                    }
                    mQuantityEditText.setText(String.valueOf(quantity));
                }
            }
        });

        //Find and initialize the CONTACT SUPPLIER button.
        final Button contactSupplierButton = findViewById(R.id.supplier_contact_button);
        contactSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the text from the SupplierPhoneNumberEditText and toss it to a String.
                String supplierPhoneString = mSupplierPhoneNumberEditText.getText().toString();
                //Execute the intent, so that Android can open a suitable app to handle the
                //phone call.
                Intent supplierIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                        supplierPhoneString, null));
                if (supplierIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(supplierIntent);
                }
            }
        });
    }

    /**
     * Get user input from the editor and save it into the database.
     */
    private void saveBook() {
        //Read the data from the input fields.
        //Use trim to eliminate the the white space from the beginning or ending of the input fields.
        String titleString = mTitleEditText.getText().toString().trim();
        String authorFirstNameString = mAuthorFirstNameEditText.getText().toString().trim();
        String authorLastNameString = mAuthorLastNameEditText.getText().toString().trim();
        String isbnString = mIsbnEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText().toString().trim();

        //Check to see if this is supposed to be a new book and also check to see if the fields are empty.
        //If the user clicks SAVE without entering any data in the EditText fields, then return
        //without saving so that a "blank" book is not entered into the database.
        if (mCurrentBookUri == null && TextUtils.isEmpty(titleString) || TextUtils.isEmpty(authorFirstNameString)
                || TextUtils.isEmpty(authorLastNameString) || TextUtils.isEmpty(isbnString)
                || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(supplierNameString) || TextUtils.isEmpty(supplierPhoneNumberString)) {
            Toast.makeText(this, getString(R.string.editor_activity_save_button_add_info), Toast.LENGTH_LONG).show();
            allDataIsValid = false;
        } else {
            allDataIsValid = true;
        }

        if (allDataIsValid) {
            //Create a ContentValues object where column names are the keys and the book attributes from
            //the editor are the values.
            ContentValues values = new ContentValues();
            values.put(BookMasterEntry.COLUMN_BOOK_TITLE, titleString);
            values.put(BookMasterEntry.COLUMN_AUTHOR_FIRST_NAME, authorFirstNameString);
            values.put(BookMasterEntry.COLUMN_AUTHOR_LAST_NAME, authorLastNameString);
            values.put(BookMasterEntry.COLUMN_ISBN, isbnString);
            values.put(BookMasterEntry.COLUMN_PRICE, priceString);
            values.put(BookMasterEntry.COLUMN_QUANTITY, quantityString);
            values.put(BookMasterEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
            values.put(BookMasterEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

            //If the TITLE is not provided by the user, the app will generate "INVALID BOOK TITLE".
            //This will be a "flag" in the system that a valid TITLE was not entered for the book,
            //and that a valid TITLE needs to be entered for this book.
            String title = getResources().getString(R.string.unknown_book_title);
            if (!TextUtils.isEmpty(titleString)) {
                title = String.valueOf(titleString);
            }
            values.put(BookMasterEntry.COLUMN_BOOK_TITLE, title);

            //Determine if this is a new or existing book by checking if mCurrentBookUri is null or not.
            if (mCurrentBookUri == null) {
                //This is a NEW book, and insert it into the provider.  Return the content URI for the
                //new book.
                Uri newUri = getContentResolver().insert(BookMasterEntry.CONTENT_URI, values);

                //Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    //If the new content URI is null, then there was an error with the insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                            Toast.LENGTH_LONG).show();
                } else {
                    //The insertion was successful.
                    Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                            Toast.LENGTH_LONG).show();
                }
            } else {
                //This is an EXISTING book, so update the book with the content URI: mCurrentBookUri and
                //pass in the new ContentValues.  Pass in "null" for the selection and selectionArgs
                //because mCurrentBookUri will already identify the correct row in the database that we
                //want to modify.
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

                //Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    //If no rows were affected (updated) then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update_book_failed),
                            Toast.LENGTH_LONG).show();
                } else {
                    //The update was successful.
                    Toast.makeText(this, getString(R.string.editor_update_book_successful),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Inflate the menu_editor.xml options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu options from the res/menu/menu_editor.xml file.
        //This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after InvalidateOptionsMenu(), so that the menu can be updated.
     * (Some menu items can be hidden or made visible with this method.)
     */
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //If this is a new pet then hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem deleteMenuItem = menu.findItem(R.id.action_delete_book);
            deleteMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //User clicked on a menu item in the app bar overflow menu.
        switch (item.getItemId()) {
            //Respond to a click on the "Save" menu option.
            case R.id.action_save_book:
                //Save book to the database.
                saveBook();
                if (allDataIsValid) {
                    //Exit activity.
                    finish();
                }
                return true;

            //Respond to a click on the "Delete" menu option.
            case R.id.action_delete_book:
                showDeleteConfirmationDialog();
                return true;

            //Respond to a click on the "Up arrow" button in the app bar.
            case android.R.id.home:
                //If the book hasn't changed, continue with navigating up to the parent activity
                //which is, BookMasterMainActivity.
                if (!mBookHasBeenEdited) {
                    NavUtils.navigateUpFromSameTask(BookMasterEditorActivity.this);
                    return true;
                }

                //Otherwise, if there are unsaved changes, setup a dialog to warn the user.  Create
                //a click listener to handle the user confirming that changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //User clicked the "Discard" button, navigate to the parent activity.
                        NavUtils.navigateUpFromSameTask(BookMasterEditorActivity.this);
                    }
                };

                //Show the dialog that the user has unsaved changes.
                showUnsavedChangesConfirmationDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        //If the book hasn't changed then continue with handling the back button press.
        if (!mBookHasBeenEdited) {
            super.onBackPressed();
            return;
        }

        //Otherwise, if there are unsaved changes, setup a dialog to warn the user.  Create a click
        //listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                //User clicks the "Discard" button, close the current activity.
                finish();
            }
        };

        //Show the dialog that the user has unsaved changes.
        showUnsavedChangesConfirmationDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Since the editor shows all the book attributes, define a projection that contains all of
        //the columns in the books table.
        String[] projection = {
                BookMasterEntry._ID,
                BookMasterEntry.COLUMN_BOOK_TITLE,
                BookMasterEntry.COLUMN_AUTHOR_FIRST_NAME,
                BookMasterEntry.COLUMN_AUTHOR_LAST_NAME,
                BookMasterEntry.COLUMN_ISBN,
                BookMasterEntry.COLUMN_PRICE,
                BookMasterEntry.COLUMN_QUANTITY,
                BookMasterEntry.COLUMN_SUPPLIER_NAME,
                BookMasterEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        //This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,   //Parent activity context.
                mCurrentBookUri,                //Query the content URI for the book.
                projection,                     //Columns to include in the resulting Cursor.
                null,                  //No selection clause.
                null,               //No selection Arguments.
                null);                 //Default sort order.
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Get out early if the cursor is null or if there is less than 1 row in the cursor.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        //Proceed with moving to the first row of the cursor and reading data from it.
        if (cursor.moveToFirst()) {
            //Find the columns of the book attributes that we are interested in.
            int titleColumnIndex = cursor.getColumnIndex(BookMasterEntry.COLUMN_BOOK_TITLE);
            int authorFirstNameColumnIndex = cursor.getColumnIndex(BookMasterEntry.COLUMN_AUTHOR_FIRST_NAME);
            int authorLastNameColumnIndex = cursor.getColumnIndex(BookMasterEntry.COLUMN_AUTHOR_LAST_NAME);
            int isbnColumnIndex = cursor.getColumnIndex(BookMasterEntry.COLUMN_ISBN);
            int priceColumnIndex = cursor.getColumnIndex(BookMasterEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookMasterEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookMasterEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookMasterEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            //Extract out the value from the Cursor for the given column index.
            String title = cursor.getString(titleColumnIndex);
            String authorFirstName = cursor.getString(authorFirstNameColumnIndex);
            String authorLastName = cursor.getString(authorLastNameColumnIndex);
            String isbn = cursor.getString(isbnColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

            //Update the views on the screen with the values from the database.
            mTitleEditText.setText(title);
            mAuthorFirstNameEditText.setText(authorFirstName);
            mAuthorLastNameEditText.setText(authorLastName);
            mIsbnEditText.setText(isbn);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneNumberEditText.setText(supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //If the loader is invalidated, clear out all the data from the input fields.
        mTitleEditText.setText("");
        mAuthorFirstNameEditText.setText("");
        mAuthorLastNameEditText.setText("");
        mIsbnEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneNumberEditText.setText("");
    }

    /**
     * Show a dialog that warns the user that they have unsaved changes that will be lost if they
     * continue to leave before saving their work.
     */
    private void showUnsavedChangesConfirmationDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        //Create an AlertDialog.Builder and set the message, and the click listeners for the positive
        //and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.discard_changes, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //User clicked the "Keep Editing" button, so dismiss the dialog
                //and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Create and show the AlertDialog.
        AlertDialog unsavedChangesDialog = builder.create();
        unsavedChangesDialog.show();
    }

    /**
     * Show a dialog that warns the user that they are about to delete a book.
     */
    private void showDeleteConfirmationDialog() {
        //Create an AlertDialog.Builder and set the message, and the click listeners for the positive
        //and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_alert_dialog_message);
        builder.setPositiveButton(R.string.delete_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //User clicked on the "Cancel" button, so dismiss the dialog and continue editing
                //the book.
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
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        //Only perform the deletion if it is an existing book.
        if (mCurrentBookUri != null) {
            //Call the ContentResolver to delete the book at the given content URI.  Pass in null for
            //the selection and selectionArgs because the mCurrentBookUri content URI already identifies
            //the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            //Show a toast message depending on whether or not the deletion was successful.
            if (rowsDeleted == 0) {
                //If no rows were deleted then there was an error with the deletion.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                //The book was successfully deleted.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_LONG).show();
            }
        }
        //Close the activity.
        finish();
    }
}