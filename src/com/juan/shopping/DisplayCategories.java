package com.juan.shopping;

import java.util.List;

import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DisplayCategories extends ListActivity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		StoreDatabaseHelper db;
		db = new StoreDatabaseHelper(getApplicationContext());
		List<String> categoryList = db.getAllCategories();
		db.closeDB();


		//Display the categories on the list View
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_categories, R.id.categoryName, categoryList);

		setListAdapter(adapter);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		Toast t;
		switch (item.getItemId()) {
		case R.id.action_search:
			 t = Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_LONG);
			t.show();
			return true;
		case R.id.action_settings:
			t = Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG);
			t.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
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
