package com.juan.shopping.sqlitehelper;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.juan.shopping.ServiceHandler;
import com.juan.shopping.sqlitemodel.HistoryItem;
import com.juan.shopping.sqlitemodel.Item;

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
	private static final String KEY_RID = "rid";

	// Item table create statement
	private static final String CREATE_TABLE_CHECKOUT_LIST = "CREATE TABLE "
			+ TABLE_CHECKOUT_LIST + "(" + KEY_UPC + " TEXT,"
			+ KEY_QUANTITY	+ " INTEGER," + KEY_PRICE + " REAL," + KEY_DATE + " TEXT," + KEY_RID + " INTEGER PRIMARY KEY)";
	
	JSONArray item = null;
	
	private List<Item> itemList;
	
	public CheckoutListDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		itemList = new ArrayList<Item>();
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
		values.put(KEY_RID, item.getRid());

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
	values.put(KEY_RID, item.getRid());

	db.update(TABLE_CHECKOUT_LIST, values, KEY_UPC + " = ?",
			new String[] { String.valueOf(item.getUPC()) });
	//db.insert(TABLE_SHOPPING_LIST, null, values);
	Log.i(this.getClass().toString(), "Item updated in checkout list");
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
				item.setRid(c.getInt(c.getColumnIndex(KEY_RID)));
				// adding item to list
				checkoutList.add(item);
			} while (c.moveToNext());
		}

		return checkoutList;
	}
	
	public List<String> getAllDates() {
		List<String> checkoutDates = new ArrayList<String>();

		String selectQuery = "SELECT DISTINCT " + KEY_DATE + " FROM " + TABLE_CHECKOUT_LIST;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// loop through all rows and add to list
		if (c.moveToFirst()) {
			do {
				checkoutDates.add(c.getString(c.getColumnIndex(KEY_DATE)));
			} while (c.moveToNext());
		}

		return checkoutDates;
	}

	public List<HistoryItem> getItemsByDate(String date) {
		List<HistoryItem> checkoutList = new ArrayList<HistoryItem>();

		String selectQuery = "SELECT * FROM " + TABLE_CHECKOUT_LIST + " WHERE " + KEY_DATE + " = '" + date + "'";

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
				item.setRid(c.getInt(c.getColumnIndex(KEY_RID)));
				// adding item to list
				checkoutList.add(item);
			} while (c.moveToNext());
		}

		return checkoutList;
	}
	
	public List<String> getAllDates() {
		List<String> checkoutDates = new ArrayList<String>();

		String selectQuery = "SELECT DISTINCT " + KEY_DATE + " FROM " + TABLE_CHECKOUT_LIST;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// loop through all rows and add to list
		if (c.moveToFirst()) {
			do {
				checkoutDates.add(c.getString(c.getColumnIndex(KEY_DATE)));
			} while (c.moveToNext());
		}

		return checkoutDates;
	}
	
	public List<HistoryItem> getItemsByDate(String date) {
		List<HistoryItem> checkoutList = new ArrayList<HistoryItem>();

		String selectQuery = "SELECT * FROM " + TABLE_CHECKOUT_LIST + " WHERE " + KEY_DATE + " = '" + date + "'";

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
				item.setRid(c.getInt(c.getColumnIndex(KEY_RID)));
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
	
	public int getRid(){
		String query = "SELECT MAX(rid) FROM " + TABLE_CHECKOUT_LIST;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		cursor.moveToFirst();
		int max = cursor.getInt(0);
		
		return max;
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

	
	class GetItems extends AsyncTask<List<String>, Void, List<Item>> {

		// This is the "guts" of the asynchronus task. The code
		// in doInBackground will be executed in a separate thread

		@Override
		protected List<Item> doInBackground(List<String>... list_upc) {
			Log.i("MainActivity", "Inside the asynchronous task");

			List<Item> list_items = new ArrayList<Item>();

			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			for ( List<String> u : list_upc){
				// Making a request to url and getting response
				String jsonStr = sh.makeServiceCall("http://162.243.133.20/items/"+ u);
	
				Log.d("Response: ", "> " + jsonStr);
	
				if (jsonStr != null) {
					try {
						JSONObject jsonObj = new JSONObject(jsonStr);
	
						// Getting JSON Array node
						item = jsonObj.getJSONArray("items");
	
						// loop through all items
							JSONObject data = item.getJSONObject(0);
	
							Item tempItem = new Item();
							tempItem.setUpc(data.getString("upc"));
							tempItem.setName(data.getString("name"));
							tempItem.setDescription(data.getString("description"));
							tempItem.setPrice(data.getInt("price"));
							tempItem.setCategory(data.getString("category"));
							tempItem.setImage(data.getString("image"));
							Log.d("Response: ",
									"Adding to list: " + tempItem.getName());
							list_items.add(tempItem);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.e("ServiceHandler", "Couldn't get any data from the url");
				}

			}
			return list_items;
		}

		// This routine is called at the end of the task. This
		// routine is run as part of the main thread, so it can
		// update the GUI. The input parameter is automatically
		// set by the output parameter of doInBackground()

		@Override
		protected void onPostExecute(List<Item> result) {
			itemList.clear();
			for (Item i : result) {
				itemList.add(i);
			}

		}
	}
}
