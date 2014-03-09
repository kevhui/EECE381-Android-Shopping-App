package com.juan.shopping;

import java.util.ArrayList;
import java.util.List;

import com.juan.shopping.sqlitehelper.DatabaseHelper;
import com.juan.shopping.sqlitemodel.Item;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DisplayCategories extends ListActivity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		DatabaseHelper db;
		db = new DatabaseHelper(getApplicationContext());
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
