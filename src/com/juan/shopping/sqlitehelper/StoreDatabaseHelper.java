package com.juan.shopping.sqlitehelper;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	private static final String DATABASE_NAME = "walmart.db";

	// Database Path
	private static final String DATABASE_PATH = "/data/data/com.juan.shopping/databases/";
	
	// Table Names
	private static final String TABLE_STORE = "items";

	// ITEMS Table - column names
	private static final String KEY_UPC = "upc";
	private static final String KEY_NAME = "name";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_PRICE = "price";
	private static final String KEY_CATEGORY = "category";
	
	private final Context context;
	
	public StoreDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		createDataBase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE);
		// create new tables
		onCreate(db);
	}
	
	public void createDataBase() {
		boolean dbExist = context.getDatabasePath(DATABASE_NAME).exists();
		if (!dbExist) {
			this.getReadableDatabase();
			try {
				Log.e(this.getClass().toString(), "Going to copy");
				copyDataBase();
			} catch (IOException e) {
				Log.e(this.getClass().toString(), "Copying error");
				throw new Error("Error copying database!");
			}
		} else {
			Log.i(this.getClass().toString(), "Database already exists");
		}
	}

	private void copyDataBase() throws IOException {

        //Open your local db as the input stream
		Log.e(this.getClass().toString(), "Getting Assets");
        InputStream myInput = context.getAssets().open(DATABASE_NAME);
		Log.e(this.getClass().toString(), "Getting outfile path");
        // Path to the just created empty db
        String outFileName = DATABASE_PATH + DATABASE_NAME;
		Log.e(this.getClass().toString(), "Open empty db");
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
		Log.e(this.getClass().toString(), "copy");
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[2048];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
		Log.e(this.getClass().toString(), "finish copy");
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
	}


	  //************************************************************//
	 //******************** Store table methods *******************//
	//************************************************************//
	
	// Create an item
	public void createItem(Item item) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_UPC, item.getUpc());
		values.put(KEY_NAME, item.getName());
		values.put(KEY_CATEGORY, item.getCategory());

		db.insert(TABLE_STORE, null, values);
	}

	// query a single item
	public Item getItem(String upc) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_STORE + " WHERE "
				+ KEY_UPC + " = " + "'" + upc + "'";

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Item td = new Item();
		td.setUpc(c.getString(c.getColumnIndex(KEY_UPC)));
		td.setName((c.getString(c.getColumnIndex(KEY_NAME))));
		td.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
		td.setPrice(c.getString(c.getColumnIndex(KEY_PRICE)));
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
				td.setUpc(c.getString((c.getColumnIndex(KEY_UPC))));
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
		values.put(KEY_UPC, item.getUpc());
		values.put(KEY_NAME, item.getName());
		values.put(KEY_DESCRIPTION, item.getDescription());
		values.put(KEY_CATEGORY, item.getPrice());
		values.put(KEY_CATEGORY, item.getCategory());

		// updating row
		db.update(TABLE_STORE, values, KEY_UPC + " = ?",
				new String[] { String.valueOf(item.getUpc()) });
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

		String selectQuery = "SELECT DISTINCT category FROM items";
		
		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// loop through all rows and add to list
		if (c.moveToFirst()) {
			do {
				categoryList.add(c.getString(c.getColumnIndex("category")));
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
