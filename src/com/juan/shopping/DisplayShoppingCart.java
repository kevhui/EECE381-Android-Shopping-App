package com.juan.shopping;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.juan.shopping.sqlitehelper.ShoppingListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.Item;
import com.juan.shopping.sqlitemodel.Shopping_list_item;

public class DisplayShoppingCart extends ListActivity{
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		List<String> names = new ArrayList<String>();
		
		ShoppingListDatabaseHelper db;
		db = new ShoppingListDatabaseHelper(getApplicationContext());
		List<Shopping_list_item> shoppingList = db.getAllItems();
		db.closeDB();

		StoreDatabaseHelper storeDb = new StoreDatabaseHelper(getApplicationContext());
		for(Shopping_list_item item:shoppingList){
			Item storeItem = storeDb.getItem(item.getUPC());
			names.add(storeItem.getName());
		}
		storeDb.closeDB();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.shopping_list, R.id.itemName, names);

		setListAdapter(adapter);
	}

}
