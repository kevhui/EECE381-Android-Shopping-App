package com.juan.shopping.sqlitehelper;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.juan.shopping.sqlitemodel.Item;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StoreDatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "contactsManager";

	// Table Names
	private static final String TABLE_STORE = "store";

	// ITEMS Table - column names
	private static final String KEY_UPC = "UPC";
	private static final String KEY_NAME = "name";
	private static final String KEY_CATEGORY = "category";

	// Item table create statement
	private static final String CREATE_TABLE_STORE = "CREATE TABLE "
			+ TABLE_STORE + "(" + KEY_UPC + " TEXT PRIMARY KEY," + KEY_NAME
			+ " TEXT," + KEY_CATEGORY + " TEXT" + ")";
	
	public StoreDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_STORE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE);
		// create new tables
		onCreate(db);
	}

	  //************************************************************//
	 //******************** Store table methods *******************//
	//************************************************************//
	
	// Create an item
	public void createItem(Item item) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_UPC, item.getUPC());
		values.put(KEY_NAME, item.getName());
		values.put(KEY_CATEGORY, item.getCategory());

		db.insert(TABLE_STORE, null, values);
	}

	// query a single item
	public Item getItem(long item_upc) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_STORE + " WHERE "
				+ KEY_UPC + " = " + item_upc;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Item td = new Item();
		td.setUPC(c.getString(c.getColumnIndex(KEY_UPC)));
		td.setName((c.getString(c.getColumnIndex(KEY_NAME))));
		td.setCategory((c.getString(c.getColumnIndex(KEY_CATEGORY))));
		
		return td;
	}

	// query all items with a certain category
	public List<Item> getAllItemsByCategory(String category) {
		List<Item> items = new ArrayList<Item>();

		String selectQuery = "SELECT  * FROM " + TABLE_STORE
				+ " WHERE "	+ KEY_CATEGORY + " = " + "'" + category + "'";

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// loop through all rows and add to list
		if (c.moveToFirst()) {
			do {
				Item td = new Item();
				td.setUPC(c.getString((c.getColumnIndex(KEY_UPC))));
				td.setName((c.getString(c.getColumnIndex(KEY_NAME))));
				td.setCategory(c.getString(c.getColumnIndex(KEY_CATEGORY)));

				// adding item to list
				items.add(td);
			} while (c.moveToNext());
		}

		return items;
	}

	// Getting number of items in store
	public int getItemCount() {
		String countQuery = "SELECT  * FROM " + TABLE_STORE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	// updating an item
	public void updateItem(Item item) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_UPC, item.getUPC());
		values.put(KEY_NAME, item.getName());
		values.put(KEY_CATEGORY, item.getCategory());

		// updating row
		db.update(TABLE_STORE, values, KEY_UPC + " = ?",
				new String[] { String.valueOf(item.getUPC()) });
	}

	// Deleting an item
	public void deleteItem(String upc) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_STORE, KEY_UPC + " = ?",
				new String[] { upc });
	}
	
	// query all categories
	public List<String> getAllCategories() {
		List<String> categoryList = new ArrayList<String>();

		String selectQuery = "SELECT  DISTINCT " + KEY_CATEGORY 
				+ " FROM " + TABLE_STORE;
		
		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// loop through all rows and add to list
		if (c.moveToFirst()) {
			do {
				categoryList.add(c.getString(c.getColumnIndex(KEY_CATEGORY)));
			} while (c.moveToNext());
		}

		return categoryList;
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
