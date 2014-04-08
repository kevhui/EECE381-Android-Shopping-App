package com.juan.shopping;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juan.shopping.sqlitehelper.CheckoutListDatabaseHelper;
import com.juan.shopping.sqlitehelper.ShoppingListDatabaseHelper;
import com.juan.shopping.sqlitemodel.HistoryItem;
import com.juan.shopping.sqlitemodel.Item;
import com.juan.shopping.sqlitemodel.Shopping_list_item;

public class DisplayRecommendedList extends ListActivity {
	private List<Shopping_list_item> shoppingList;
	private List<Item> recommendedList;
	private List<String> names;
	private List<String> dateList;
	private Shopping_list_item clickedItem;
	private NumberPicker np;
	private int clickedItemPosition;
	private ArrayAdapter<String> adapter;
	private ImageView iv;
	JSONObject item = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		names = new ArrayList<String>();
		shoppingList = new ArrayList<Shopping_list_item>();
		recommendedList = new ArrayList<Item>();
		dateList = new ArrayList<String>();

		ShoppingListDatabaseHelper db;
		db = new ShoppingListDatabaseHelper(getApplicationContext());
		shoppingList = db.getAllItems();
		db.closeDB();

		CheckoutListDatabaseHelper cldb;
		cldb = new CheckoutListDatabaseHelper(getApplicationContext());
		dateList = cldb.getAllDates();
		cldb.closeDB();

		adapter = new ArrayAdapter<String>(this, R.layout.shopping_list,
				R.id.itemName, names);

		setListAdapter(adapter);

		List<String> upcList = new ArrayList<String>();
		for (Shopping_list_item i : shoppingList) {
			upcList.add(i.getUPC());
		}

		List<HistoryItem> recieptItems = new ArrayList<HistoryItem>();
		List<String> receiptUPC = new ArrayList<String>();
		double similarity = 0.0;
		HashMap<String, Double> similarityList = new HashMap<String, Double>();

		// Go through each reciept
		for (String d : dateList) {
			receiptUPC.clear();
			similarity = 0.0;

			cldb = new CheckoutListDatabaseHelper(getApplicationContext());
			recieptItems = cldb.getItemsByDate(d);
			// Go through each item in reciept
			for (HistoryItem i : recieptItems) {
				receiptUPC.add(i.getUPC());
			}
			cldb.closeDB();

			similarity = calculateCosineSimilarity(upcList, receiptUPC);

			// Relate the similarity to the date
			similarityList.put(d, similarity);
			Log.d("Recommended Item","Date: " + d + "has similiarity " + similarity);
		}

		//Find the Max similarity
		Comparator<Map.Entry<String, Double>> comparator = new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> e1,
					Map.Entry<String, Double> e2) {
				return e1.getValue().compareTo(e2.getValue());
			}
		};
		
		String similiarRecieptDate = Collections.max(similarityList.entrySet(), comparator).getKey();
		
		receiptUPC.clear();
		cldb = new CheckoutListDatabaseHelper(getApplicationContext());
		recieptItems = cldb.getItemsByDate(similiarRecieptDate);
		for (HistoryItem i : recieptItems) {
			Log.d("Recommended Item","Adding to recommended: " + i.getUPC());
			receiptUPC.add(i.getUPC());
		}
		cldb.closeDB();
		
		for (String i : receiptUPC){
			Log.i("DEBUG", "IN recieptUPC " + i);
		}
		
		if (upcList.size() != 0) {
			new GetItems().execute(receiptUPC);
		}
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
		np = (NumberPicker) popupView
				.findViewById(R.id.npNumberItemsShoppingList);

		Button btnDismiss = (Button) popupView.findViewById(R.id.bSaveCartItem);
		TextView tv = (TextView) popupView
				.findViewById(R.id.tvItemNameShoppingList);
		iv = (ImageView) popupView.findViewById(R.id.ivItemImageShoppingList);

		tv.setText(names.get(position));

		// Display the picture
		new GetImage().execute(recommendedList.get(clickedItemPosition)
				.getImage());

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

	class GetItems extends AsyncTask<List<String>, Void, List<Item>> {

		// This is the "guts" of the asynchronus task. The code
		// in doInBackground will be executed in a separate thread

		@Override
		protected List<Item> doInBackground(List<String>... upcList) {
			Log.i("GetItems", "Inside the asynchronous task");

			List<Item> list_items = new ArrayList<Item>();

			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();
			Log.i("GetItems", "Need to query " + upcList.length + " item(s)");
			
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
			recommendedList.clear();

			for (Item i : result) {
				names.add(i.getName());
				recommendedList.add(i);
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

	public static Double calculateCosineSimilarity(List<String> shoppingList,
			List<String> reciept) {
		Double similarity = 0.0; // The similarity range from -1 to 1
		Double sum = 0.0; // the sum of the dot product for each item
		Double SLnorm = 0.0; // the normal of the shoppingList
		Double Rnorm = 0.0; // the normal of the receipt
		for (String upc : shoppingList) {
			if (reciept.contains(upc)) {
				sum++;
			}
		}
		SLnorm = calculateNorm(shoppingList);
		Rnorm = calculateNorm(reciept);
		similarity = sum / (SLnorm * Rnorm);
		return similarity;
	}

	// calculate the norm of one vector
	// Simplified normal because our vector is either 0 or 1 (bought or not
	// bought)
	public static Double calculateNorm(List<String> itemList) {
		return Math.sqrt(itemList.size());
	}

}
