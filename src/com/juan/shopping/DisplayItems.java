package com.juan.shopping;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.juan.shopping.sqlitehelper.ShoppingListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.Item;

public class DisplayItems extends ListActivity{
	
	private List<Item> itemFilteredList;
	
	@Override	
	  public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    
	    Intent intent = getIntent();
	    String category = intent.getStringExtra("com.juan.shopping.CATEGORY");
	    
	    List<String> categoryList = new ArrayList<String>();
	    
		StoreDatabaseHelper db;
		db = new StoreDatabaseHelper(getApplicationContext());
		itemFilteredList = db.getAllItemsByCategory(category);
		for (Item item : itemFilteredList) {
			categoryList.add(item.getName());
		}
		db.closeDB();
		
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		        R.layout.list_categories, R.id.categoryName, categoryList);
		
	    setListAdapter(adapter);
	  }
	
	  @Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
		Item item = itemFilteredList.get(position);
	    Toast.makeText(this, item.getName() + " Added to shopping list", Toast.LENGTH_LONG).show();
	    
		ShoppingListDatabaseHelper db = new ShoppingListDatabaseHelper(getApplicationContext());
		db.addItem(item.getUpc(), 1);
		db.closeDB();
	  }
}
