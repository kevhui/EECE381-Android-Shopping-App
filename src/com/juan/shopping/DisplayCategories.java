package com.juan.shopping;

import java.util.List;

import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DisplayCategories extends ListActivity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
<<<<<<< HEAD
		
		//Open database and query all the categories
=======

>>>>>>> checkout
		StoreDatabaseHelper db;
		db = new StoreDatabaseHelper(getApplicationContext());
		List<String> categoryList = db.getAllCategories();
		db.closeDB();


		//Display the categories on the list View
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_categories, R.id.categoryName, categoryList);

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	    //TODO: change form making new activity to re-populating listView
		
		//Creates a new listView to displayed items 
		Intent intent = new Intent(this, DisplayItems.class);
	    String message = l.getItemAtPosition(position).toString();
	    intent.putExtra("com.juan.shopping.CATEGORY", message);
	    startActivity(intent);
	}
}
