package com.juan.shopping;

import java.util.List;

import com.juan.shopping.sqlitehelper.DatabaseHelper;
import com.juan.shopping.sqlitemodel.Item;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

	// Database Helper
	DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		db = new DatabaseHelper(getApplicationContext());

		// Creating items
		Item item1 = new Item("000000000001", "Oreos", "Cookies");
		Item item2 = new Item("000000000002", "Chocolate Chip", "Cookies");
		Item item3 = new Item("000000000003", "Butter Cookies", "Cookies");

		Item item4 = new Item("000000000004", "Coke", "Drinks");
		Item item5 = new Item("000000000005", "7-UP", "Drinks");
		Item item6 = new Item("000000000006", "Sprite", "Drinks");
	
		Item item7 = new Item("000000000007", "Apples", "Fruits");
		Item item8 = new Item("000000000008", "Bananas", "Fruits");
		Item item9 = new Item("000000000009", "Cantelopes", "Fruits");

		// Inserting items in db
		db.createItem(item1);
		db.createItem(item2);
		db.createItem(item3);
		db.createItem(item4);
		db.createItem(item5);
		db.createItem(item6);
		db.createItem(item7);
		db.createItem(item8);
		db.createItem(item9);


		Log.e("Item Count", "Item count: " + db.getItemCount());

		// Getting todos under "Fruits" tag name
		Log.d("Item", "Get Items under single Category name");

		List<Item> itemFilteredList = db.getAllItemsByCategory("Fruits");
		for (Item item : itemFilteredList) {
			Log.d("ToDo Watchlist", item.getName());
		}

		// Don't forget to close database connection
		db.closeDB();
		
	}
}
