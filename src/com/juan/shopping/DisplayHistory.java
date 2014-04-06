package com.juan.shopping;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juan.shopping.sqlitehelper.CheckoutListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.HistoryItem;
import com.juan.shopping.sqlitemodel.Item;

public class DisplayHistory extends ListActivity {
	
	private HistoryItem clickedItem;
	private ArrayAdapter<String> adapter;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		CheckoutListDatabaseHelper db;
		db = new CheckoutListDatabaseHelper(getApplicationContext());
		List<String> datesList = db.getAllDates();
		db.closeDB();
		
		Log.d("DisplayHistory", "size: " + datesList.size());

		adapter = new ArrayAdapter<String>(this,
				R.layout.history_category, R.id.historyCategory, datesList);

		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.checkout_reports, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	    //TODO: change form making new activity to re-populating listView
		
		//Creates a new listView to displayed items 
		Intent intent = new Intent(this, DisplayReceiptItems.class);
	    String message = l.getItemAtPosition(position).toString();
	    intent.putExtra("com.juan.shopping.DATE", message);
	    startActivity(intent);
	}

}