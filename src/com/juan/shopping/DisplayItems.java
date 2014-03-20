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
import com.juan.shopping.sqlitehelper.ShoppingListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.Item;

public class DisplayItems extends ListActivity {

	private List<Item> itemFilteredList;
	private Item clickedItem;
	private NumberPicker np;
	private List<String> categoryList;
	private String category;
	private ArrayAdapter<String> adapter;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Intent intent = getIntent();
		category  = intent.getStringExtra("com.juan.shopping.CATEGORY");

		setTitle(category);

		categoryList = new ArrayList<String>();

		// Open database and query all items with a certain category
		StoreDatabaseHelper db;
		db = new StoreDatabaseHelper(getApplicationContext());
		itemFilteredList = db.getAllItemsByCategory(category);
		for (Item item : itemFilteredList) {
			categoryList.add(item.getName());
		}
		db.closeDB();

		// Display the items
		adapter = new ArrayAdapter<String>(this,
				R.layout.list_categories, R.id.categoryName, categoryList);

		setListAdapter(adapter);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			popUpFilter();
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
		clickedItem = itemFilteredList.get(position);

		Log.i(this.getClass().toString(), "Item: " + clickedItem
				+ "was clicked");

		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		// Setup the popupView
		View popupView = layoutInflater.inflate(R.layout.popup_add_item, null);
		final PopupWindow popupWindow = new PopupWindow(popupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		np = (NumberPicker) popupView.findViewById(R.id.npNumberItems);
		Button btnDismiss = (Button) popupView.findViewById(R.id.bAddToCart);
		TextView tv = (TextView) popupView.findViewById(R.id.tvItemNameAddItem);
		ImageView iv = (ImageView) popupView.findViewById(R.id.ivItemImage);

		// Display the name of the item clicked
		tv.setText(clickedItem.getName());

		// Setup the number picker
		np.setMinValue(0);
		np.setMaxValue(99);
		np.setValue(1);
		np.setWrapSelectorWheel(false);

		// Display the picture
		int imageId = getResources().getIdentifier("com.juan.shopping:drawable/upc" + clickedItem.getUpc(), null,null);
		iv.setImageResource(imageId);

		// Button add the item and quantity to the shopping cart
		btnDismiss.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(this.getClass().toString(), "Item: " + clickedItem
						+ "was added to shopping list");

				ShoppingListDatabaseHelper db = new ShoppingListDatabaseHelper(
						getApplicationContext());
				db.addItem(clickedItem.getUpc(), np.getValue());
				db.closeDB();
				popupWindow.dismiss();
			}
		});

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
	
	private void popUpFilter(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Enter item name");
		
		// Get user input via EditText 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
		  String filter = input.getText().toString();
			categoryList.clear();
			itemFilteredList.clear();
			
			// Open database and query all items with a certain category and string
			StoreDatabaseHelper db;
			db = new StoreDatabaseHelper(getApplicationContext());
			itemFilteredList = db.getAllItemsByCategoryAndString(category, filter);
			for (Item i : itemFilteredList) {
				categoryList.add(i.getName());
			}
			db.closeDB();
			adapter.notifyDataSetChanged();
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  @Override
		public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}

}
