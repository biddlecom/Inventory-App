# Book Inventory App

This app is a simple app that will allow the user to input common data about a book and save it on their device.  The book data that can be saved includes:
-Book Title
-Author Name (first and last)
-ISBN (International Standard Book Number)
-Book Price
-Quantity
-Supplier Name
-Supplier Phone Number

All this information will be stored in an SQLite database on the users device.

There is a “Contact Supplier” button that will launch the phone app by default.  The user can choose a different app (like WhatsApp, Google Duo, Skype, etc) on their device to complete this action if they so choose.

Once a user saves a book into the database, it will show up on the main screen.  The main screen will display the book title, price and quantity.  There is a convenient “Sell Item” button that will decrease the quantity by one each time it is clicked.  When the quantity reaches zero it will turn the background color around that specific book to red, letting the user know that they are out of that book and need to order more copies.

The user has the ability to delete one specific book from the inventory as well as an option to mass delete every book in the inventory if they so desire.

NOTE- this app was made as part of a Udacity course and in it’s current state is not meant for a real world application.  It does function as designed and can be used, but it is not a polished app.

# Installation and Usage

This app was written in Java using Android Studio.  It was tested and works on the most recent version of Android Studio 4.0.

The Android libraries in this app have not been upgraded to Androidx as of yet.
