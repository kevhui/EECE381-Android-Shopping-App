package com.juan.shopping;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
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
import com.juan.shopping.sqlitemodel.PopularItem;
import com.juan.shopping.sqlitemodel.HistoryItem;

public class DisplayHistory extends ListActivity {
	
	private PopularItem pclickedItem;
	private AverageListItem aclickedItem;
	private ArrayAdapter<String> adapter;
	private int flagview;
	private List<ExpensiveListItem> expenses;
	private List<AverageListItem> averages;
	private List<PopularItem> popular;
	List<String> pNames;
	List<String> aNames;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		flagview = 2;
		expenses = new ArrayList<ExpensiveListItem>();
		averages = new ArrayList<AverageListItem>();
		popular = new ArrayList<PopularItem>();

		CheckoutListDatabaseHelper db;
		db = new CheckoutListDatabaseHelper(getApplicationContext());
		List<String> datesList = db.getAllDates();
		db.closeDB();
		
		Log.d("DisplayHistory", "size: " + datesList.size());

		adapter = new ArrayAdapter<String>(this,
				R.layout.history_category, R.id.historyCategory, datesList);

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	    //TODO: change form making new activity to re-populating listView
		Intent intent;
		String message;
		LayoutInflater layoutInflater;
		View popupView;
		final PopupWindow popupWindow;
		switch(flagview){
			case 0:
				pclickedItem = popular.get(position);

				layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);

				// Setup the popupView
				popupView = layoutInflater.inflate(R.layout.popup_popular,
						null);
				popupWindow = new PopupWindow(popupView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
				TextView tvN = (TextView) popupView.findViewById(R.id.tvItemNamePopular);
				TextView tvQ = (TextView) popupView.findViewById(R.id.tvItemQuantityPopular);
				ImageView iv = (ImageView) popupView.findViewById(R.id.ivItemImagePopular);
				
				tvN.setText("Name: " + pNames.get(position));
				tvQ.setText("Quantity: " + Integer.toString(popular.get(position).getQuantity()));
				
				// Display the picture
				int imageId = getResources().getIdentifier("com.juan.shopping:drawable/upc" + pclickedItem.getUPC(), null,null);
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
				popupView = layoutInflater.inflate(R.layout.popup_average,
						null);
				popupWindow = new PopupWindow(popupView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				
				TextView atvN = (TextView) popupView.findViewById(R.id.tvItemNameAverage);
				TextView atvQ = (TextView) popupView.findViewById(R.id.tvItemQuantityAverage);
				TextView atvP = (TextView) popupView.findViewById(R.id.tvItemPriceAverage);
				ImageView aiv = (ImageView) popupView.findViewById(R.id.ivItemImageAverage);
				
				atvN.setText("Name: " + aNames.get(position));
				atvQ.setText("Average Quantity: " + String.format("%.2f", averages.get(position).getQuantity()));
				atvP.setText("Average Price: " + "$" + String.format("%.2f", averages.get(position).getAveragePrice()));
				
				// Display the picture
				int aimageId = getResources().getIdentifier("com.juan.shopping:drawable/upc" + aclickedItem.getUPC(), null,null);
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
	
	private List<PopularItem> optionsPopular(){
		pNames = new ArrayList<String>();
		
		CheckoutListDatabaseHelper cdb;
		cdb = new CheckoutListDatabaseHelper(getApplicationContext());
		List<PopularItem> popular = cdb.getItemByPopularity();
		cdb.closeDB();
		
		StoreDatabaseHelper db;
		db = new StoreDatabaseHelper(getApplicationContext());
		for (PopularItem item : popular) {
			pNames.add(db.getItem(item.getUPC()).getName());
		}
		db.closeDB();

		adapter = new ArrayAdapter<String>(this,
				R.layout.history_category, R.id.historyCategory, pNames);

		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		return popular;
	}
	
	private List<AverageListItem> optionsAverage(){
		aNames = new ArrayList<String>();
		
		CheckoutListDatabaseHelper cdb;
		cdb = new CheckoutListDatabaseHelper(getApplicationContext());
		List<AverageListItem> average = cdb.getItemByAverage();
		cdb.closeDB();
		
		StoreDatabaseHelper db;
		db = new StoreDatabaseHelper(getApplicationContext());
		for (AverageListItem item : average	) {
			aNames.add(db.getItem(item.getUPC()).getName());
		}
		db.closeDB();

		adapter = new ArrayAdapter<String>(this,
				R.layout.history_category, R.id.historyCategory, aNames);

		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		return average;
	}
	
	private void optionsDate(){
		CheckoutListDatabaseHelper db;
		db = new CheckoutListDatabaseHelper(getApplicationContext());
		List<String> datesList = db.getAllDates();
		db.closeDB();

		adapter = new ArrayAdapter<String>(this,
				R.layout.history_category, R.id.historyCategory, datesList);

		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	
	private List<ExpensiveListItem> optionsItemExpenses(){
		List<String> eDatesList = new ArrayList<String>();
		
		CheckoutListDatabaseHelper cdb;
		cdb = new CheckoutListDatabaseHelper(getApplicationContext());
		List<ExpensiveListItem> expense = cdb.getItemExpensive();
		cdb.closeDB();
		

		for (ExpensiveListItem item : expense) {
			eDatesList.add(item.getDate());
		}

		adapter = new ArrayAdapter<String>(this,
				R.layout.history_category, R.id.historyCategory, eDatesList);
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		return expense;
	}

}