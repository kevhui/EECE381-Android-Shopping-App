package com.juan.shopping;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
	private ImageView iv;
	JSONArray items = null;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Intent intent = getIntent();
		category = intent.getStringExtra("com.juan.shopping.CATEGORY");

		setTitle(category);

		categoryList = new ArrayList<String>();
		itemFilteredList = new ArrayList<Item>();

		// Open database and query all items with a certain category
		// StoreDatabaseHelper db;
		// db = new StoreDatabaseHelper(getApplicationContext());
		// itemFilteredList = db.getAllItemsByCategory(category);
		// for (Item item : itemFilteredList) {
		// categoryList.add(item.getName());
		// }
		// db.closeDB();

		// Display the items
		adapter = new ArrayAdapter<String>(this, R.layout.list_categories,
				R.id.categoryName, categoryList);

		setListAdapter(adapter);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		new GetItems().execute("http://162.243.133.20/category/"
				+ category.replaceAll(" ", "%20"));
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
		iv = (ImageView) popupView.findViewById(R.id.ivItemImage);

		// Display the name of the item clicked
		tv.setText(clickedItem.getName());

		// Setup the number picker
		np.setMinValue(0);
		np.setMaxValue(99);
		np.setValue(1);
		np.setWrapSelectorWheel(false);

		// Display the picture
		new GetImage().execute(clickedItem.getImage());

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

	private void popUpFilter() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Enter item name");

		// Get user input via EditText
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Filter",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						String filter = input.getText().toString();
						categoryList.clear();
						itemFilteredList.clear();

						// Open database and query all items with a certain
						// category and string
						StoreDatabaseHelper db;
						db = new StoreDatabaseHelper(getApplicationContext());
						itemFilteredList = db.getAllItemsByCategoryAndString(
								category, filter);
						for (Item i : itemFilteredList) {
							categoryList.add(i.getName());
						}
						db.closeDB();
						adapter.notifyDataSetChanged();
					}
				});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}

	class GetItems extends AsyncTask<String, Void, List<Item>> {

		// This is the "guts" of the asynchronus task. The code
		// in doInBackground will be executed in a separate thread

		@Override
		protected List<Item> doInBackground(String... url) {
			Log.i("MainActivity", "Inside the asynchronous task");

			List<Item> list_items = new ArrayList<Item>();

			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url[0]);

			Log.d("Response: ", "> " + jsonStr);

			Item tempItem;

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					// Getting JSON Array node
					items = jsonObj.getJSONArray("items");

					// loop though all items
					for (int i = 0; i < items.length(); i++) {
						JSONObject data = items.getJSONObject(i);

						tempItem = new Item();
						tempItem.setUpc(data.getString("upc"));
						tempItem.setName(data.getString("name"));
						tempItem.setDescription(data.getString("description"));
						tempItem.setPrice(data.getInt("price"));
						tempItem.setCategory(data.getString("category"));
						tempItem.setImage(data.getString("image"));
						Log.d("Response: ",
								"Adding to list: " + tempItem.getName());
						list_items.add(tempItem);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			for (Item i : list_items) {
				Log.d("Test", "Data is " + i.getName());
			}

			return list_items;
		}

		// This routine is called at the end of the task. This
		// routine is run as part of the main thread, so it can
		// update the GUI. The input parameter is automatically
		// set by the output parameter of doInBackground()

		@Override
		protected void onPostExecute(List<Item> result) {
			categoryList.clear();
			itemFilteredList.clear();

			for (Item i : result) {

				categoryList.add(i.getName());
				itemFilteredList.add(i);
			}
			adapter.notifyDataSetChanged();
		}
	}

	class GetImage extends AsyncTask<String, Void, Bitmap> {

		// This is the "guts" of the asynchronus task. The code
		// in doInBackground will be executed in a separate thread

		@Override
		protected Bitmap doInBackground(String... url_array) {
			URL url;
			Log.i("DisplayItem", "Inside the asynchronous task");

			try {
				url = new URL(url_array[0]);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();

				Log.i("DisplayItem", "Successfully opened the web page");

				InputStream input = connection.getInputStream();
				Bitmap bitmap = BitmapFactory.decodeStream(input);
				input.close();

				return bitmap;

			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

		}

		// This routine is called at the end of the task. This
		// routine is run as part of the main thread, so it can
		// update the GUI. The input parameter is automatically
		// set by the output parameter of doInBackground()

		@Override
		protected void onPostExecute(Bitmap result) {
			iv.setImageBitmap(result);
		}
	}

}
