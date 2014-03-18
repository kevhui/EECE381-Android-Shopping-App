package com.juan.shopping;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.juan.shopping.sqlitehelper.ShoppingListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;

public class MainActivity extends Activity {

	// Database Helper
	StoreDatabaseHelper storedb;
	ShoppingListDatabaseHelper shoppingListdb;
	public SQLiteDatabase database;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Initialize databases if this is first time opening app
		storedb = new StoreDatabaseHelper(getApplicationContext());
		storedb.closeDB();
		shoppingListdb = new ShoppingListDatabaseHelper(getApplicationContext());
		shoppingListdb.closeDB();
	}
	
	public void onClickEditShoppingList(View view) {		
	    Intent intent = new Intent(this, DisplayCategories.class);
	    startActivity(intent);
	}
	
	public void onClickViewShoppingList(View view) {	
	    Intent intent = new Intent(this, DisplayShoppingCart.class);
	    startActivity(intent);
	}
	
	public void onClickCheckoutList(View view) {	
	    Intent intent = new Intent(this, DisplayCheckoutList.class);
	    startActivity(intent);
	}
	
	public void onClickBarcodeScanner(View view) {	
	    Intent intent = new Intent(this, BarcodeScanner.class);
	    startActivity(intent);
	}
	
	public void onClickConnectToDE2 (View view){
		Intent intent = new Intent(this, ConnectTCP.class);
	    startActivity(intent);
	}

	
}
