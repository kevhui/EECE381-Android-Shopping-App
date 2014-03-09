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

import com.juan.shopping.sqlitehelper.DatabaseHelper;
import com.juan.shopping.sqlitemodel.Item;

public class DisplayItems extends ListActivity{
	@Override
	  public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    
	    Intent intent = getIntent();
	    String category = intent.getStringExtra("com.juan.shopping.CATEGORY");
	    
	    List<String> categoryList = new ArrayList<String>();
	    
		DatabaseHelper db;
		db = new DatabaseHelper(getApplicationContext());
		List<Item> itemFilteredList = db.getAllItemsByCategory(category);
		for (Item item : itemFilteredList) {
			categoryList.add(item.getName());
		}
		// Don't forget to close database connection
		db.closeDB();
		
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		        R.layout.list_categories, R.id.categoryName, categoryList);
		
	    setListAdapter(adapter);
	  }

	  @Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
	    String item = (String) getListAdapter().getItem(position);
	    Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
	  }
}
