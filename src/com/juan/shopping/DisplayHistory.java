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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juan.shopping.sqlitehelper.CheckoutListDatabaseHelper;
import com.juan.shopping.sqlitehelper.ShoppingListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.Item;
import com.juan.shopping.sqlitemodel.Shopping_list_item;
import com.juan.shopping.sqlitemodel.historyItem;

public class DisplayHistory extends ListActivity {
	private List<historyItem> checkoutList;
	private List<String> names;
	private List<Integer> quantity;
	private List<Float> price;
	private List<String> date;
	private historyItem clickedItem;
	private ArrayAdapter<String> adapter;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		names = new ArrayList<String>();

		CheckoutListDatabaseHelper db;
		db = new CheckoutListDatabaseHelper(getApplicationContext());
		checkoutList = db.getAllItems();
		db.closeDB();

		StoreDatabaseHelper storeDb = new StoreDatabaseHelper(
				getApplicationContext());
		for (historyItem item : checkoutList) {
			// Add to a list to display
			Item storeItem = storeDb.getItem(item.getUPC());
			names.add(storeItem.getName());
			quantity.add(item.getQuantity());
			price.add(item.getPrice());
			date.add(item.getDate());
		}
		storeDb.closeDB();
		adapter = new ArrayAdapter<String>(this,
				R.layout.history, R.id.itemName, names);

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		clickedItem = checkoutList.get(position);

		Log.i(this.getClass().toString(),
				"Item UPC: " + checkoutList.get(position).getUPC()
						+ " was clicked");

		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		// Setup the popupView
		View popupView = layoutInflater.inflate(R.layout.popup_shopping_list,
				null);
		final PopupWindow popupWindow = new PopupWindow(popupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		TextView tvN = (TextView) popupView.findViewById(R.id.tvItemNameCheckoutList);
		TextView tvQ = (TextView) popupView.findViewById(R.id.tvItemQuantityCheckoutList);
		TextView tvP = (TextView) popupView.findViewById(R.id.tvItemPriceCheckoutList);
		TextView tvD = (TextView) popupView.findViewById(R.id.tvItemDateCheckoutList);
		ImageView iv = (ImageView) popupView.findViewById(R.id.ivItemImageCheckoutList);
		
		tvN.setText(names.get(position));
		tvQ.setText(quantity.get(position));
		tvP.setText("$" + String.format("%.2f", price.get(position).toString()));
		tvD.setText(date.get(position));
		
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