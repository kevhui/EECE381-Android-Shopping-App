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
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juan.shopping.sqlitehelper.CheckoutListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.ExpensiveListItem;
import com.juan.shopping.sqlitemodel.HistoryItem;

public class DisplayReceiptExpensiveItems extends ListActivity {

	private List<HistoryItem> itemFilteredList;
	private List<String> names;
	private HistoryItem clickedItem;
	private ExpensiveListItem eItem;
	private String expensive;
	private String total;
	private ArrayAdapter<String> adapter;
	ListView list;
	TextView tv;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.expensive_list);

		Intent intent = getIntent();
		expensive  = intent.getStringExtra("com.juan.shopping.EXPENSIVE");
		total = intent.getStringExtra("com.juan.shopping.TOTAL");

		setTitle(expensive);

		names = new ArrayList<String>();

		// Open database and query all items with a certain category
		CheckoutListDatabaseHelper cdb;

		cdb = new CheckoutListDatabaseHelper(getApplicationContext());
		itemFilteredList = cdb.getItemsByDate(expensive);
		cdb.closeDB();
		
		StoreDatabaseHelper db;
		db = new StoreDatabaseHelper(getApplicationContext());
		for (HistoryItem item : itemFilteredList) {
			names.add(db.getItem(item.getUPC()).getName());
		}
		db.closeDB();
		
		list = (ListView) findViewById(android.R.id.list);
		tv = (TextView) findViewById(R.id.TOTAL_PRICE_EXPENSIVE);
		tv.setText("TOTAL: " + total);

		// Display the items
		adapter = new ArrayAdapter<String>(this,
				R.layout.list_items, R.id.itemName, names);

		list.setAdapter(adapter);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		clickedItem = itemFilteredList.get(position);

		Log.i("DisplayHistory",
				"Item UPC: " + itemFilteredList.get(position).getUPC()
						+ " was clicked");

		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		// Setup the popupView
		View popupView = layoutInflater.inflate(R.layout.popup_history,
				null);
		final PopupWindow popupWindow = new PopupWindow(popupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		Log.d("DisplayHistory",
				"views");
		TextView tvN = (TextView) popupView.findViewById(R.id.tvItemNameCheckoutList);
		TextView tvQ = (TextView) popupView.findViewById(R.id.tvItemQuantityCheckoutList);
		TextView tvP = (TextView) popupView.findViewById(R.id.tvItemPriceCheckoutList);
		TextView tvD = (TextView) popupView.findViewById(R.id.tvItemDateCheckoutList);
		ImageView iv = (ImageView) popupView.findViewById(R.id.ivItemImageCheckoutList);
		
		Log.d("DisplayHistory",
				"setViews");
		tvN.setText("Name: " + names.get(position));
		tvQ.setText("Quantity: " + Integer.toString(itemFilteredList.get(position).getQuantity()));
		tvP.setText("Price: $" + String.format("%.2f", itemFilteredList.get(position).getPrice()));
		tvD.setText("Date: " + itemFilteredList.get(position).getDate());
		
		Log.d("DisplayHistory",
				"display pic");
		// Display the picture
		int imageId = getResources().getIdentifier("com.juan.shopping:drawable/upc" + clickedItem.getUPC(), null,null);
		iv.setImageResource(imageId);

		// Disable background clicking
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new Drawable() {

			@Override
			public void setColorFilter(ColorFilter cf) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setAlpha(int alpha) {
				// TODO Auto-generated method stub

			}

			@Override
			public int getOpacity() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void draw(Canvas canvas) {
				// TODO Auto-generated method stub

			}
		});

		// Show the popUp
		popupWindow.update();
		popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
	}
}
