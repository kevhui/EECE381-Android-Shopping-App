package com.juan.shopping;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juan.shopping.sqlitehelper.CheckoutListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.HistoryItem;
import com.juan.shopping.sqlitemodel.Item;

public class DisplayHistory extends ListActivity {
	private List<HistoryItem> checkoutList;
	private List<String> names;
	private HistoryItem clickedItem;
	private ArrayAdapter<String> adapter;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		names = new ArrayList<String>();

		CheckoutListDatabaseHelper db;
		db = new CheckoutListDatabaseHelper(getApplicationContext());
		checkoutList = db.getAllItems();
		db.closeDB();
		
		Log.d("DisplayHistory", "size: " + checkoutList.size());

		StoreDatabaseHelper storeDb = new StoreDatabaseHelper(
				getApplicationContext());
		

		for (HistoryItem item : checkoutList) {
			// Add to a list to display
			Item storeItem = storeDb.getItem(item.getUPC());
			names.add(storeItem.getName());
		}
		storeDb.closeDB();
		adapter = new ArrayAdapter<String>(this,
				R.layout.history, R.id.itemName, names);

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		clickedItem = checkoutList.get(position);

		Log.i("DisplayHistory",
				"Item UPC: " + checkoutList.get(position).getUPC()
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
		TextView tvI = (TextView) popupView.findViewById(R.id.tvItemRidCheckoutList);
		ImageView iv = (ImageView) popupView.findViewById(R.id.ivItemImageCheckoutList);
		
		Log.d("DisplayHistory",
				"setViews");
		tvN.setText("Name: " + names.get(position));
		tvQ.setText("Quantity: " + Integer.toString(checkoutList.get(position).getQuantity()));
		tvP.setText("Price: $" + String.format("%.2f", checkoutList.get(position).getPrice()));
		tvD.setText("Date: " + checkoutList.get(position).getDate());
		tvI.setText("Receipt Number: " + Integer.toString(checkoutList.get(position).getRid()));
		
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