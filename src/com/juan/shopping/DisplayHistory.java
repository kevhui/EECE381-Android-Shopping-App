package com.juan.shopping;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juan.shopping.sqlitehelper.CheckoutListDatabaseHelper;
import com.juan.shopping.sqlitemodel.AverageListItem;
import com.juan.shopping.sqlitemodel.ExpensiveListItem;
import com.juan.shopping.sqlitemodel.Item;
import com.juan.shopping.sqlitemodel.PopularItem;

public class DisplayHistory extends ListActivity {

	private PopularItem pclickedItem;
	private AverageListItem aclickedItem;
	private ArrayAdapter<String> adapter;
	private List<String> datesList;
	private int flagview;
	private List<ExpensiveListItem> expenses;
	private List<AverageListItem> averages;
	private List<PopularItem> popular;
	private List<String> names;
	private List<String> upcList;
	JSONObject item = null;
	ImageView iv;
	ImageView aiv;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		flagview = 2;
		expenses = new ArrayList<ExpensiveListItem>();
		averages = new ArrayList<AverageListItem>();
		popular = new ArrayList<PopularItem>();
		names = new ArrayList<String>();
		datesList = new ArrayList<String>();
		upcList = new ArrayList<String>();

		CheckoutListDatabaseHelper db;
		db = new CheckoutListDatabaseHelper(getApplicationContext());
		datesList = db.getAllDates();
		db.closeDB();

		adapter = new ArrayAdapter<String>(this, R.layout.history_category,
				R.id.historyCategory, datesList);

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO: change form making new activity to re-populating listView
		Intent intent;
		String message;
		LayoutInflater layoutInflater;
		View popupView;
		final PopupWindow popupWindow;
		switch (flagview) {
		case 0:
			pclickedItem = popular.get(position);
			Log.d("DisplayHistory","position " + position);

			layoutInflater = (LayoutInflater) getBaseContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);

			// Setup the popupView
			popupView = layoutInflater.inflate(R.layout.popup_popular, null);
			popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			TextView tvN = (TextView) popupView
					.findViewById(R.id.tvItemNamePopular);
			TextView tvQ = (TextView) popupView
					.findViewById(R.id.tvItemQuantityPopular);
			iv = (ImageView) popupView
					.findViewById(R.id.ivItemImagePopular);

			tvN.setText("Name: " + names.get(position));
			tvQ.setText("Quantity: "
					+ Integer.toString(popular.get(position).getQuantity()));

			// Display the picture
			//int imageId = getResources().getIdentifier(
			//		"com.juan.shopping:drawable/upc" + pclickedItem.getUPC(),
			//		null, null);
			//iv.setImageResource(imageId);
			new GetImage().execute(pclickedItem.getUPC());

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
			break;
		case 1:
			aclickedItem = averages.get(position);

			layoutInflater = (LayoutInflater) getBaseContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);

			// Setup the popupView
			popupView = layoutInflater.inflate(R.layout.popup_average, null);
			popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			TextView atvN = (TextView) popupView
					.findViewById(R.id.tvItemNameAverage);
			TextView atvQ = (TextView) popupView
					.findViewById(R.id.tvItemQuantityAverage);
			TextView atvP = (TextView) popupView
					.findViewById(R.id.tvItemPriceAverage);
			aiv = (ImageView) popupView
					.findViewById(R.id.ivItemImageAverage);

			atvN.setText("Name: " + names.get(position));
			atvQ.setText("Average Quantity: "
					+ String.format("%.2f", averages.get(position)
							.getQuantity()));
			atvP.setText("Average Price: "
					+ "$"
					+ String.format("%.2f", averages.get(position)
							.getAveragePrice()));

			// Display the picture
			//int aimageId = getResources().getIdentifier(
			//		"com.juan.shopping:drawable/upc" + aclickedItem.getUPC(),
			//		null, null);
			//aiv.setImageResource(aimageId);
			new GetImage().execute(aclickedItem.getUPC());

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
			break;
		case 2:
			intent = new Intent(this, DisplayReceiptDateItems.class);
			message = l.getItemAtPosition(position).toString();
			intent.putExtra("com.juan.shopping.DATE", message);
			startActivity(intent);
			break;
		case 3:
			intent = new Intent(this, DisplayReceiptExpensiveItems.class);
			message = l.getItemAtPosition(position).toString();
			float eprice = expenses.get(position).getTotalPrice();
			String price = "$" + String.format("%.2f", eprice);
			intent.putExtra("com.juan.shopping.EXPENSIVE", message);
			intent.putExtra("com.juan.shopping.TOTAL", price);
			startActivity(intent);
			break;
		default:

			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.checkout_reports, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_popular:
			flagview = 0;
			popular.clear();
			optionsPopular();
			return true;
		case R.id.action_average_price:
			flagview = 1;
			averages.clear();
			averages = optionsAverage();
			return true;
		case R.id.action_date:
			flagview = 2;
			optionsDate();
			return true;
		case R.id.action_expensive:
			flagview = 3;
			expenses.clear();
			expenses = optionsItemExpenses();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("unchecked")
	private void optionsPopular() {
		names.clear();
		upcList.clear();
		CheckoutListDatabaseHelper cdb;
		cdb = new CheckoutListDatabaseHelper(getApplicationContext());
		popular = cdb.getItemByPopularity();
		cdb.closeDB();
		PopularItem pItem = new PopularItem();
		
		TreeMap<Integer, String> map = new TreeMap<Integer, String>();
		
		for(PopularItem item : popular){
			map.put(item.getQuantity(), item.getUPC());
		}
		
		NavigableMap sortedPopular = map.descendingMap();

		Set<Integer> keys = sortedPopular.keySet();
		Iterator<Integer> it = keys.iterator();
		popular.clear();
		while(it.hasNext()){
			Integer next = it.next();
			upcList.add(sortedPopular.get(next).toString());
			pItem = new PopularItem(sortedPopular.get(next).toString(), next);
			Log.d("DisplayHistory","quantity: " + next);
			Log.d("DisplayHistory","upc: " + pItem.getUPC());
			popular.add(pItem);
		}
		// StoreDatabaseHelper db;
		// db = new StoreDatabaseHelper(getApplicationContext());
//		for (PopularItem item : popular) {
//			upcList.add(item.getUPC());
//		}
		// db.closeDB();
		//
		// adapter = new ArrayAdapter<String>(this,
		// R.layout.history_category, R.id.historyCategory, names);
		//
		// setListAdapter(adapter);
		// adapter.notifyDataSetChanged();
		//
		new GetItems().execute(upcList);
	}

	@SuppressWarnings("unchecked")
	private List<AverageListItem> optionsAverage() {
		names.clear();
		upcList.clear();
		CheckoutListDatabaseHelper cdb;
		cdb = new CheckoutListDatabaseHelper(getApplicationContext());
		averages = cdb.getItemByAverage();
		cdb.closeDB();

		// StoreDatabaseHelper db;
		// db = new StoreDatabaseHelper(getApplicationContext());
		for (AverageListItem item : averages) {
			upcList.add(item.getUPC());
		}
		// db.closeDB();
		//
		// adapter = new ArrayAdapter<String>(this,
		// R.layout.history_category, R.id.historyCategory, names);
		//
		// setListAdapter(adapter);
		// adapter.notifyDataSetChanged();
		//
		new GetItems().execute(upcList);
		return averages;
	}

	private void optionsDate() {
		datesList.clear();
		CheckoutListDatabaseHelper db;
		db = new CheckoutListDatabaseHelper(getApplicationContext());
		datesList = db.getAllDates();
		db.closeDB();

		adapter = new ArrayAdapter<String>(this, R.layout.history_category,
				R.id.historyCategory, datesList);

		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	private List<ExpensiveListItem> optionsItemExpenses() {
		datesList.clear();
		ExpensiveListItem eItem = new ExpensiveListItem();

		CheckoutListDatabaseHelper cdb;
		cdb = new CheckoutListDatabaseHelper(getApplicationContext());
		expenses = cdb.getItemExpensive();
		cdb.closeDB();
		
		TreeMap<Float, ExpensiveListItem> map = new TreeMap<Float, ExpensiveListItem>();
		
		for(ExpensiveListItem item : expenses){
			map.put(item.getTotalPrice(), item);
		}
		
		NavigableMap sortedExpenses = map.descendingMap();

		
		Set<Float> keys = sortedExpenses.keySet();
		Iterator<Float> it = keys.iterator();
		expenses.clear();
		while(it.hasNext()){
			Float next = it.next();
			eItem = (ExpensiveListItem) sortedExpenses.get(next);
			expenses.add(eItem);
			datesList.add(eItem.getDate());
		}

		adapter = new ArrayAdapter<String>(this, R.layout.history_category,
				R.id.historyCategory, datesList);
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();

		return expenses;
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

						// JSONObject data = item.getJSONObject(0);

						tempItem = new Item();
						tempItem.setUpc(item.getString("upc"));
						tempItem.setName(item.getString("name"));
						tempItem.setDescription(item.getString("description"));
						tempItem.setPrice(item.getInt("price"));
						tempItem.setCategory(item.getString("category"));
						tempItem.setImage(item.getString("image"));
						Log.d("Response: ",
								"Adding to list: " + tempItem.getName());
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
				Log.d("TEST", i.getName());
				names.add(i.getName());
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					adapter = new ArrayAdapter<String>(getApplicationContext(),
							R.layout.history_category, R.id.historyCategory,
							names);
					adapter.notifyDataSetChanged();
					setListAdapter(adapter);
				}
			});
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
					
					Log.i("Displayhistory", "Successfully loaded image");

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
			if( flagview == 0 ){
			iv.setImageBitmap(result);
			}
			if ( flagview == 1){
			aiv.setImageBitmap(result);
			}
		}
	}
	
	
	
}