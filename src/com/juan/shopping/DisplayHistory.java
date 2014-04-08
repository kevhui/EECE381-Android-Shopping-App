package com.juan.shopping;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
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
import android.widget.Toast;

import com.juan.shopping.sqlitehelper.CheckoutListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.AverageListItem;
import com.juan.shopping.sqlitemodel.ExpensiveListItem;
import com.juan.shopping.sqlitemodel.Item;
import com.juan.shopping.sqlitemodel.PopularItem;
import com.juan.shopping.sqlitemodel.HistoryItem;

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

		Log.d("DisplayHistory", "size: " + datesList.size());

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
			ImageView iv = (ImageView) popupView
					.findViewById(R.id.ivItemImagePopular);

			tvN.setText("Name: " + names.get(position));
			tvQ.setText("Quantity: "
					+ Integer.toString(popular.get(position).getQuantity()));

			// Display the picture
			int imageId = getResources().getIdentifier(
					"com.juan.shopping:drawable/upc" + pclickedItem.getUPC(),
					null, null);
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
			ImageView aiv = (ImageView) popupView
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
			int aimageId = getResources().getIdentifier(
					"com.juan.shopping:drawable/upc" + aclickedItem.getUPC(),
					null, null);
			aiv.setImageResource(aimageId);

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
			popular = optionsPopular();
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
	private List<PopularItem> optionsPopular() {
		names.clear();
		upcList.clear();
		CheckoutListDatabaseHelper cdb;
		cdb = new CheckoutListDatabaseHelper(getApplicationContext());
		List<PopularItem> popular = cdb.getItemByPopularity();
		cdb.closeDB();

		// StoreDatabaseHelper db;
		// db = new StoreDatabaseHelper(getApplicationContext());
		for (PopularItem item : popular) {
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
		return popular;
	}

	@SuppressWarnings("unchecked")
	private List<AverageListItem> optionsAverage() {
		names.clear();
		upcList.clear();
		CheckoutListDatabaseHelper cdb;
		cdb = new CheckoutListDatabaseHelper(getApplicationContext());
		List<AverageListItem> average = cdb.getItemByAverage();
		cdb.closeDB();

		// StoreDatabaseHelper db;
		// db = new StoreDatabaseHelper(getApplicationContext());
		for (AverageListItem item : average) {
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
		return average;
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

		CheckoutListDatabaseHelper cdb;
		cdb = new CheckoutListDatabaseHelper(getApplicationContext());
		List<ExpensiveListItem> expense = cdb.getItemExpensive();
		cdb.closeDB();

		for (ExpensiveListItem item : expense) {
			datesList.add(item.getDate());
		}

		adapter = new ArrayAdapter<String>(this, R.layout.history_category,
				R.id.historyCategory, datesList);
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();

		return expense;
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
}