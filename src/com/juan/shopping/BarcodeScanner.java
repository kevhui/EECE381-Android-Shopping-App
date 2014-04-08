package com.juan.shopping;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.juan.shopping.sqlitemodel.Item;

public class BarcodeScanner extends Activity {

	private TextView tv_upc, tv_name, tv_description, tv_price, tv_category;
	private Item foundItem;
	JSONObject item = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barcode_scanner);

		tv_upc = (TextView) findViewById(R.id.tv_upc);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_description = (TextView) findViewById(R.id.tv_description);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_category = (TextView) findViewById(R.id.tv_category);

	}

	public void onClickScanButton(View view) {
		Log.i(this.getClass().toString(),
				"Launching an intent to zxing to scan barcode");
		IntentIntegrator scanIntegrator = new IntentIntegrator(this);
		scanIntegrator.initiateScan();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);

		if (scanningResult != null) {

			// TODO: get rid of store database helper
			// shopdb = new StoreDatabaseHelper(getApplicationContext());
			// foundItem = shopdb.getItem(scanningResult.getContents());
			// shopdb.closeDB();

			new GetItems().execute("http://162.243.133.20/items/"
					+ scanningResult.getContents());

		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	class GetItems extends AsyncTask<String, Void, Item> {

		// This is the "guts" of the asynchronus task. The code
		// in doInBackground will be executed in a separate thread

		@Override
		protected Item doInBackground(String... url) {
			Log.i("MainActivity", "Inside the asynchronous task");

			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url[0]);

			Log.d("Response: ", "> " + jsonStr);

			Item tempItem  = new Item();

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					JSONObject data = item.getJSONObject("items");

					tempItem = new Item();
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

			return tempItem;
		}

		// This routine is called at the end of the task. This
		// routine is run as part of the main thread, so it can
		// update the GUI. The input parameter is automatically
		// set by the output parameter of doInBackground()

		@Override
		protected void onPostExecute(Item foundItem) {
			tv_upc.setText("UPC : " + foundItem.getUpc());
			tv_name.setText("NAME: " + foundItem.getName());
			tv_description
					.setText("DESCRIPTION: " + foundItem.getDescription());
			tv_price.setText("PRICE: " + foundItem.getPrice());
			tv_category.setText("CATEGORY: " + foundItem.getCategory());
		}

	}
}
