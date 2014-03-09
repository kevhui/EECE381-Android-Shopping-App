package com.juan.shopping;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
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
	    
	    List<String> categoryList = new ArrayList<String>();
	    
		DatabaseHelper db;
		db = new DatabaseHelper(getApplicationContext());
		List<Item> itemFilteredList = db.getAllItemsByCategory("Fruits");
		for (Item item : itemFilteredList) {
			categoryList.add(item.getCategory());
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
