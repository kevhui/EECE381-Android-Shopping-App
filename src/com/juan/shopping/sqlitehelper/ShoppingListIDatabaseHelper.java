package com.juan.shopping.sqlitehelper;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.juan.shopping.sqlitemodel.Item;
import com.juan.shopping.sqlitemodel.Shopping_list_item;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ShoppingListIDatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "contactsManager";

	// Table Names
	private static final String TABLE_SHOPPING_LIST = "shoppinglist";

	// ITEMS Table - column names
	private static final String KEY_UPC = "UPC";
	private static final String KEY_QUANTITY = "quantity";

	// Item table create statement
	private static final String CREATE_TABLE_SHOPPING_LIST = "CREATE TABLE "
			+ TABLE_SHOPPING_LIST + "(" + KEY_UPC + " TEXT PRIMARY KEY,"
			+ KEY_QUANTITY	+ " INTEGER)";
	
	public ShoppingListIDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_SHOPPING_LIST);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST);
		// create new tables
		onCreate(db);
	}

	  //**************************************************************//
	 //***************** Shopping List table methods ****************//
	//**************************************************************//
	
	// Adding an item
		//TODO: Make it increment the quantity
	public void addItem(String UPC) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_UPC, UPC);
		values.put(KEY_QUANTITY, 1);

		db.insert(TABLE_SHOPPING_LIST, null, values);
	}


	// query the whole shopping list
	public List<Shopping_list_item> getAllItems(String category) {
		List<Shopping_list_item> shoppingList = new ArrayList<Shopping_list_item>();

		String selectQuery = "SELECT  * FROM " + TABLE_SHOPPING_LIST;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// loop through all rows and add to list
		if (c.moveToFirst()) {
			do {
				Shopping_list_item item = new Shopping_list_item();
				item.setUPC(c.getString((c.getColumnIndex(KEY_UPC))));
				item.setQuantity(c.getInt((c.getColumnIndex(KEY_QUANTITY))));
				// adding item to list
				shoppingList.add(item);
			} while (c.moveToNext());
		}

		return shoppingList;
	}

	// Getting number of items in shopping list
	public int getItemCount() {
		String countQuery = "SELECT  * FROM " + TABLE_SHOPPING_LIST;
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
		db.delete(TABLE_SHOPPING_LIST, KEY_UPC + " = ?",
				new String[] { upc });
	}
	

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * get datetime
	 * */
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}
