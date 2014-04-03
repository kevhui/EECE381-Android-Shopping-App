package com.juan.shopping.sqlitehelper;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.juan.shopping.sqlitemodel.HistoryItem;

public class CheckoutListDatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "CheckoutList";

	// Table Names
	private static final String TABLE_CHECKOUT_LIST = "checkoutList";

	// ITEMS Table - column names
	private static final String KEY_UPC = "upc";
	private static final String KEY_PRICE = "price";
	private static final String KEY_DATE = "date";
	private static final String KEY_QUANTITY = "quantity";

	// Item table create statement
	private static final String CREATE_TABLE_CHECKOUT_LIST = "CREATE TABLE "
			+ TABLE_CHECKOUT_LIST + "(" + KEY_UPC + " TEXT,"
			+ KEY_QUANTITY	+ " INTEGER," + KEY_PRICE + " REAL," + KEY_DATE + " TEXT," + "PRIMARY KEY( " + KEY_UPC + "," + KEY_DATE + "))";
	
	public CheckoutListDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_CHECKOUT_LIST);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKOUT_LIST);
		// create new tables
		onCreate(db);
	}

	  //**************************************************************//
	 //***************** Shopping List table methods ****************//
	//**************************************************************//
	
	// Add an item
	public void addItem(HistoryItem item) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_UPC, item.getUPC());
		values.put(KEY_QUANTITY, item.getQuantity());
		values.put(KEY_PRICE, item.getPrice());
		values.put(KEY_DATE, item.getDate());

		db.insert(TABLE_CHECKOUT_LIST, null, values);
	}
	
	// Update a row in the shopping list
	public void updateItem(HistoryItem item) {

	SQLiteDatabase db = this.getWritableDatabase();

	ContentValues values = new ContentValues();
	values.put(KEY_UPC, item.getUPC());
	values.put(KEY_QUANTITY, item.getQuantity());
	values.put(KEY_PRICE, item.getPrice());
	values.put(KEY_DATE, item.getDate());

	db.update(TABLE_CHECKOUT_LIST, values, KEY_UPC + " = ?",
			new String[] { String.valueOf(item.getUPC()) });
	//db.insert(TABLE_SHOPPING_LIST, null, values);
	Log.i(this.getClass().toString(), "Item updated in shopping list");
}

	// query a single item
//	public Item checkItemExists(String upc) {
//		SQLiteDatabase db = this.getReadableDatabase();
//
//		String selectQuery = "SELECT  * FROM " + TABLE_CHECKOUT_LIST + " WHERE "
//				+ KEY_UPC + " = " + "'" + upc + "'";
//
//		Log.e(LOG, selectQuery);
//
//		Cursor c = db.rawQuery(selectQuery, null);
//
//		if (c != null)
//			c.moveToFirst();
//		//c.get
//		
//	}

	// query the whole shopping list
	public List<HistoryItem> getAllItems() {
		List<HistoryItem> checkoutList = new ArrayList<HistoryItem>();

		String selectQuery = "SELECT  * FROM " + TABLE_CHECKOUT_LIST;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// loop through all rows and add to list
		if (c.moveToFirst()) {
			do {
				HistoryItem item = new HistoryItem();
				item.setQuantity(c.getInt(c.getColumnIndex(KEY_QUANTITY)));
				item.setUPC(c.getString(c.getColumnIndex(KEY_UPC)));
				item.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
				item.setPrice(c.getFloat(c.getColumnIndex(KEY_PRICE)));
				// adding item to list
				checkoutList.add(item);
			} while (c.moveToNext());
		}

		return checkoutList;
	}

	// Getting number of items in shopping list
	public int getItemCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CHECKOUT_LIST;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	// Deleting an item
	public void deleteItem(String upc) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CHECKOUT_LIST, KEY_UPC + " = ?",
				new String[] { upc });
	}
	

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

}
