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
import android.widget.Button;
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

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Intent intent = getIntent();
		String category = intent.getStringExtra("com.juan.shopping.CATEGORY");

		List<String> categoryList = new ArrayList<String>();

		//Open database and query all items with a certain category
		StoreDatabaseHelper db;
		db = new StoreDatabaseHelper(getApplicationContext());
		itemFilteredList = db.getAllItemsByCategory(category);
		for (Item item : itemFilteredList) {
			categoryList.add(item.getName());
		}
		db.closeDB();

		//Display the items
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_categories, R.id.categoryName, categoryList);

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		clickedItem = itemFilteredList.get(position);
		
		Log.i(this.getClass().toString(), "Item: " + clickedItem + "was clicked");


		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		//Setup the popupView
		View popupView = layoutInflater.inflate(R.layout.popup_add_item, null);
		final PopupWindow popupWindow = new PopupWindow(popupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		np = (NumberPicker) popupView.findViewById(R.id.npNumberItems);
		Button btnDismiss = (Button) popupView.findViewById(R.id.bAddToCart);
		TextView tv = (TextView) popupView.findViewById(R.id.tvItemNameAddItem);
		
		//Display the name of the item clicked
		tv.setText(clickedItem.getName());
		
		//Setup the number picker
		np.setMinValue(0);
        np.setMaxValue(99);
        np.setValue(1);
        np.setWrapSelectorWheel(false); 

        //Button add the item and quantity to the shopping cart
		btnDismiss.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Log.i(this.getClass().toString(), "Item: " + clickedItem + "was added to shopping list");
				
				ShoppingListDatabaseHelper db = new ShoppingListDatabaseHelper(
						getApplicationContext());
				db.addItem(clickedItem.getUpc(), np.getValue());
				db.closeDB();
				popupWindow.dismiss();
			}
		});


		//Disable background clicking
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

		//Show the popUp
		popupWindow.update();
		popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
	}

}
