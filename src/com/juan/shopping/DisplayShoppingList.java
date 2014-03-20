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

import com.juan.shopping.sqlitehelper.ShoppingListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.Item;
import com.juan.shopping.sqlitemodel.Shopping_list_item;

public class DisplayShoppingList extends ListActivity {
	private List<Shopping_list_item> shoppingList;
	private List<String> names;
	private Shopping_list_item clickedItem;
	private NumberPicker np;
	private int clickedItemPosition;
	private ArrayAdapter<String> adapter;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		names = new ArrayList<String>();

		ShoppingListDatabaseHelper db;
		db = new ShoppingListDatabaseHelper(getApplicationContext());
		shoppingList = db.getAllItems();
		db.closeDB();

		StoreDatabaseHelper storeDb = new StoreDatabaseHelper(
				getApplicationContext());
		for (Shopping_list_item item : shoppingList) {
			// Add to a list to display
			Item storeItem = storeDb.getItem(item.getUPC());
			names.add(storeItem.getName());
		}
		storeDb.closeDB();
		adapter = new ArrayAdapter<String>(this,
				R.layout.shopping_list, R.id.itemName, names);

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		clickedItem = shoppingList.get(position);
		clickedItemPosition = position;

		Log.i(this.getClass().toString(),
				"Item UPC: " + shoppingList.get(position).getUPC()
						+ " was clicked");

		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		// Setup the popupView
		View popupView = layoutInflater.inflate(R.layout.popup_shopping_list,
				null);
		final PopupWindow popupWindow = new PopupWindow(popupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		np = (NumberPicker) popupView.findViewById(R.id.npNumberItemsShoppingList);
		
		Button btnDismiss = (Button) popupView.findViewById(R.id.bSaveCartItem);
		TextView tv = (TextView) popupView.findViewById(R.id.tvItemNameShoppingList);
		ImageView iv = (ImageView) popupView.findViewById(R.id.ivItemImageShoppingList);
		
		tv.setText(names.get(position));
		
		// Display the picture
		int imageId = getResources().getIdentifier("com.juan.shopping:drawable/upc" + clickedItem.getUPC(), null,null);
		iv.setImageResource(imageId);
		
		// Setup the number picker
		np.setMinValue(0);
		np.setMaxValue(99);
		np.setValue(clickedItem.getQuantity());
		np.setWrapSelectorWheel(false);

		// Button add the item and quantity to the shopping cart
		btnDismiss.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(this.getClass().toString(),
						"Item UPC: " + clickedItem.getUPC()
								+ " was updated in shopping list");
				ShoppingListDatabaseHelper db = new ShoppingListDatabaseHelper(
						getApplicationContext());
				if (np.getValue() == 0) {
					db.deleteItem(clickedItem.getUPC());
					shoppingList.remove(clickedItemPosition);
					names.remove(clickedItemPosition);
					adapter.notifyDataSetChanged();
				} else {
					db.updateItem(clickedItem.getUPC(), np.getValue());
					shoppingList.get(clickedItemPosition).setQuantity(
							np.getValue());
				}
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

}
