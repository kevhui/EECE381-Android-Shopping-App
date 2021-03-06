package com.juan.shopping;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.juan.shopping.sqlitehelper.CheckoutListDatabaseHelper;
import com.juan.shopping.sqlitehelper.ShoppingListDatabaseHelper;

public class MainActivity extends Activity {

	// Database Helper
	//StoreDatabaseHelper storedb;
	ShoppingListDatabaseHelper shoppingListdb;
	CheckoutListDatabaseHelper historydb;
	public SQLiteDatabase database;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.activity_main);

		// Initialize databases if this is first time opening app
		//storedb = new StoreDatabaseHelper(getApplicationContext());
		//storedb.closeDB();
		shoppingListdb = new ShoppingListDatabaseHelper(getApplicationContext());
		shoppingListdb.closeDB();
		historydb = new CheckoutListDatabaseHelper(getApplicationContext());
		historydb.closeDB();
	}


	public void onClickEditShoppingList(View view) {
		Intent intent = new Intent(this, DisplayCategories.class);
		startActivity(intent);
	}

	public void onClickViewShoppingList(View view) {
		Intent intent = new Intent(this, DisplayShoppingList.class);
		startActivity(intent);
	}

	public void onClickCheckoutList(View view) {
		Intent intent = new Intent(this, DisplayCheckoutList.class);
		startActivity(intent);
	}
	
	public void onClickHistory(View view) {
		Intent intent = new Intent(this, DisplayHistory.class);
		startActivity(intent);
	}

	public void onClickBarcodeScanner(View view) {
		Intent intent = new Intent(this, BarcodeScanner.class);
		startActivity(intent);
	}

	public void onClickConnectToDE2(View view) {
		Intent intent = new Intent(this, ConnectTCP.class);
		startActivity(intent);
	}

	public void onClickRecommendedItems(View view) {
		Intent intent = new Intent(this, DisplayRecommendedList.class);
		startActivity(intent);
	}
	
}
