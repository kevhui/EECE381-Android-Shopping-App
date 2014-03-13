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

		
		StoreDatabaseHelper db;
		db = new StoreDatabaseHelper(getApplicationContext());
		List<String> categoryList = db.getAllCategories();
		// Don't forget to close database connection
		db.closeDB();


		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_categories, R.id.categoryName, categoryList);

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	    Intent intent = new Intent(this, DisplayItems.class);
	    String message = l.getItemAtPosition(position).toString();
	    intent.putExtra("com.juan.shopping.CATEGORY", message);
	    startActivity(intent);
	}
}
