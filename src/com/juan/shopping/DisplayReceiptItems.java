package com.juan.shopping;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juan.shopping.sqlitehelper.CheckoutListDatabaseHelper;
import com.juan.shopping.sqlitehelper.ShoppingListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.HistoryItem;
import com.juan.shopping.sqlitemodel.Item;

public class DisplayReceiptItems extends ListActivity {

	private List<HistoryItem> itemFilteredList;
	private List<String> names;
	private HistoryItem clickedItem;
	private NumberPicker np;
	private String date;
	private ArrayAdapter<String> adapter;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Intent intent = getIntent();
		date  = intent.getStringExtra("com.juan.shopping.DATE");

		setTitle(date);

		names = new ArrayList<String>();

		// Open database and query all items with a certain category
		CheckoutListDatabaseHelper cdb;

		cdb = new CheckoutListDatabaseHelper(getApplicationContext());
		itemFilteredList = cdb.getItemsByDate(date);
		cdb.closeDB();
		
		StoreDatabaseHelper db;
		db = new StoreDatabaseHelper(getApplicationContext());
		for (HistoryItem item : itemFilteredList) {
			names.add(db.getItem(item.getUPC()).getName());
		}
		db.closeDB();

		// Display the items
		adapter = new ArrayAdapter<String>(this,
				R.layout.history_list, R.id.historyList, names);

		setListAdapter(adapter);
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
