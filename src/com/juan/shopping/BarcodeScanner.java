package com.juan.shopping;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.Item;



public class BarcodeScanner extends Activity{
	
	private TextView tv_upc, tv_name, tv_description, tv_price, tv_category;
	private StoreDatabaseHelper shopdb;
	private Item foundItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barcode_scanner);

		tv_upc = (TextView)findViewById(R.id.tv_upc);
		tv_name = (TextView)findViewById(R.id.tv_name);
		tv_description = (TextView)findViewById(R.id.tv_description);
		tv_price = (TextView)findViewById(R.id.tv_price);
		tv_category = (TextView)findViewById(R.id.tv_category);

		}

	
	public void onClickScanButton(View view) {		
		Log.i(this.getClass().toString(), "Launching an intent to zxing to scan barcode");
		IntentIntegrator scanIntegrator = new IntentIntegrator(this);
		scanIntegrator.initiateScan();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		
		if (scanningResult != null) {
			
			//TODO: add a try catch
			shopdb = new StoreDatabaseHelper(getApplicationContext());
			foundItem = shopdb.getItem(scanningResult.getContents());
			shopdb.closeDB();
			
			tv_upc.setText(scanningResult.getFormatName() + ": " +scanningResult.getContents());
			tv_name.setText("NAME: " + foundItem.getName());
			tv_description.setText("DESCRIPTION: " + foundItem.getDescription());
			tv_price.setText("PRICE: " + foundItem.getPrice());
			tv_category.setText("CATEGORY: " + foundItem.getCategory());
			}
		else{
		    Toast toast = Toast.makeText(getApplicationContext(), 
		        "No scan data received!", Toast.LENGTH_SHORT);
		    toast.show();
		}
		}
}
