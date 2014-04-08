package com.juan.shopping;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
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
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juan.shopping.DisplayReceiptDateItems.GetImage;
import com.juan.shopping.sqlitehelper.CheckoutListDatabaseHelper;
import com.juan.shopping.sqlitemodel.ExpensiveListItem;
import com.juan.shopping.sqlitemodel.HistoryItem;
import com.juan.shopping.sqlitemodel.Item;

public class DisplayReceiptExpensiveItems extends ListActivity {

	private List<HistoryItem> itemFilteredList;
	private List<String> names;
	private List<String> upcList;
	private HistoryItem clickedItem;
	private ExpensiveListItem eItem;
	private String expensive;
	private String total;
	private ArrayAdapter<String> adapter;
	ListView list;
	TextView tv;
	JSONObject item = null;
	ImageView iv;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.expensive_list);

		Intent intent = getIntent();
		expensive  = intent.getStringExtra("com.juan.shopping.EXPENSIVE");
		total = intent.getStringExtra("com.juan.shopping.TOTAL");

		setTitle(expensive);

		names = new ArrayList<String>();
		upcList = new ArrayList<String>();
		// Open database and query all items with a certain category
		CheckoutListDatabaseHelper cdb;

		cdb = new CheckoutListDatabaseHelper(getApplicationContext());
		itemFilteredList = cdb.getItemsByDate(expensive);
		cdb.closeDB();
		
		//StoreDatabaseHelper db;
		//db = new StoreDatabaseHelper(getApplicationContext());
		for (HistoryItem item : itemFilteredList) {
			upcList.add(item.getUPC());
			//names.add(db.getItem(item.getUPC()).getName());
		}
		//db.closeDB();
		
		list = (ListView) findViewById(android.R.id.list);
		tv = (TextView) findViewById(R.id.TOTAL_PRICE_EXPENSIVE);
		tv.setText("TOTAL: " + total);

		// Display the items
		adapter = new ArrayAdapter<String>(this,
				R.layout.list_items, R.id.itemName, names);

		list.setAdapter(adapter);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		new GetItems().execute(upcList);
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
		iv = (ImageView) popupView.findViewById(R.id.ivItemImageCheckoutList);
		
		Log.d("DisplayHistory",
				"setViews");
		tvN.setText("Name: " + names.get(position));
		tvQ.setText("Quantity: " + Integer.toString(itemFilteredList.get(position).getQuantity()));
		tvP.setText("Price: $" + String.format("%.2f", itemFilteredList.get(position).getPrice()));
		tvD.setText("Date: " + itemFilteredList.get(position).getDate());
		
		Log.d("DisplayHistory",
				"display pic");
		// Display the picture
		//int imageId = getResources().getIdentifier("com.juan.shopping:drawable/upc" + clickedItem.getUPC(), null,null);
		//iv.setImageResource(imageId);
		new GetImage().execute(clickedItem.getUPC());

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
	
	class GetItems extends AsyncTask<List<String>, Void, List<Item>> {

		// This is the "guts" of the asynchronus task. The code
		// in doInBackground will be executed in a separate thread

		@Override
		protected List<Item> doInBackground(List<String>... upcList) {
			Log.i("MainActivity", "Inside the asynchronous task");

			List<Item> list_items = new ArrayList<Item>();

			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			for (int i = 0; i < upcList[0].size(); i++) {
				// Making a request to url and getting response
				String jsonStr = sh
						.makeServiceCall("http://162.243.133.20/items/"
								+ upcList[0].get(i));

				Log.d("Response: ", "> " + jsonStr);

				Item tempItem;

				if (jsonStr != null) {
					try {
						JSONObject jsonObj = new JSONObject(jsonStr);

						// Getting JSON Array node
						item = jsonObj.getJSONObject("item");
						
						//JSONObject data = item.getJSONObject(0);

						tempItem = new Item();
						tempItem.setUpc(item.getString("upc"));
						tempItem.setName(item.getString("name"));
						tempItem.setDescription(item.getString("description"));
						tempItem.setPrice(item.getInt("price"));
						tempItem.setCategory(item.getString("category"));
						tempItem.setImage(item.getString("image"));
						Log.d("Response: ","Adding to list: " + tempItem.getName());
						list_items.add(tempItem);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.e("ServiceHandler",
							"Couldn't get any data from the url");
				}

			}

			return list_items;
		}

		// This routine is called at the end of the task. This
		// routine is run as part of the main thread, so it can
		// update the GUI. The input parameter is automatically
		// set by the output parameter of doInBackground()

		@Override
		protected void onPostExecute(List<Item> result) {
			names.clear();

			for (Item i : result) {
				names.add(i.getName());
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	class GetImage extends AsyncTask<String, Void, Bitmap> {

		// This is the "guts" of the asynchronus task. The code
		// in doInBackground will be executed in a separate thread

		@Override
		protected Bitmap doInBackground(String... upc) {
		
			Log.i("MainActivity", "Inside the asynchronous task");

			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall("http://162.243.133.20/items/"+upc[0]);

			Log.d("Response: ", "> " + jsonStr);

			Item tempItem  = new Item();

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					JSONObject data = jsonObj.getJSONObject("item");

					tempItem = new Item();
					Log.d("Response: ", "Setting the item.image");
					tempItem.setUpc(data.getString("upc"));
					tempItem.setName(data.getString("name"));
					tempItem.setDescription(data.getString("description"));
					tempItem.setPrice(data.getInt("price"));
					tempItem.setCategory(data.getString("category"));
					tempItem.setImage(data.getString("image"));
					Log.d("Response: ", "Adding to list: " + tempItem.getName());

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			
			URL url;
			Log.i("DisplayItem", "Inside the asynchronous task");

			
			
			try {
				url = new URL(tempItem.getImage());
				Log.i("Image URL", tempItem.getImage());
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
