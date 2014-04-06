package com.juan.shopping;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;

public class DisplayCategories extends ListActivity {

	List<String> categoryList;
	ArrayAdapter<String> adapter;

	JSONArray categories = null;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);


		categoryList = new ArrayList<String>();
		// StoreDatabaseHelper db;
		// db = new StoreDatabaseHelper(getApplicationContext());
		// List<String> categoryList = db.getAllCategories();
		// db.closeDB();

		// Display the categories on the list View
		adapter = new ArrayAdapter<String>(this,
				R.layout.list_categories, R.id.categoryName, categoryList);
		
		setListAdapter(adapter);
		
		new getCategories().execute("http://162.243.133.20/category");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Creates a new listView to displayed items
		Intent intent = new Intent(this, DisplayItems.class);
		String message = l.getItemAtPosition(position).toString();
		intent.putExtra("com.juan.shopping.CATEGORY", message);
		startActivity(intent);
	}

	class getCategories extends AsyncTask<String, Void, List<String>> {

		// This is the "guts" of the asynchronus task. The code
		// in doInBackground will be executed in a separate thread

		@Override
		protected List<String> doInBackground(String... url) {
			Log.i("MainActivity", "Inside the asynchronous task");

			List<String> list_category = new ArrayList<String>();
			
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url[0]);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					// Getting JSON Array node
					categories = jsonObj.getJSONArray("categories");
					
					// looping through All Contacts
					for (int i = 0; i < categories.length(); i++) {
						JSONObject data = categories.getJSONObject(i);

						String category = data.getString("category");

						Log.d("Response: ", "Adding to list: " + category);
						list_category.add(category);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			return list_category;

		}

		// This routine is called at the end of the task. This
		// routine is run as part of the main thread, so it can
		// update the GUI. The input parameter is automatically
		// set by the output parameter of doInBackground()

		@Override
		protected void onPostExecute(List<String> result) {
			categoryList.clear();
			categoryList.addAll(result);
			adapter.notifyDataSetChanged();
		}
	}

}
