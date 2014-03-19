package com.juan.shopping;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.juan.shopping.sqlitehelper.ShoppingListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.Item;
import com.juan.shopping.sqlitemodel.Shopping_list_item;

public class DisplayCheckoutList extends Activity{
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

	        setContentView(R.layout.checkout_list);
	 
	        ListView list=(ListView)findViewById(R.id.LIST_OF_ITEMS);
	        TextView tv=(TextView)findViewById(R.id.TOTAL_PRICE); 

	        tv.setText("TOTAL");

			List<String> names = new ArrayList<String>();
			
			ShoppingListDatabaseHelper db;
			db = new ShoppingListDatabaseHelper(getApplicationContext());
			List<Shopping_list_item> shoppingList = db.getAllItems();
			db.closeDB();
			
			StoreDatabaseHelper storeDb = new StoreDatabaseHelper(getApplicationContext());
			for(Shopping_list_item item:shoppingList){
				Item storeItem = storeDb.getItem(item.getUPC());
				names.add(storeItem.getName());
			}
			storeDb.closeDB();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.list_items, R.id.itemName, names);

			list.setAdapter(adapter); 

			
			list.setOnItemClickListener(new OnItemClickListener() {
			@Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		                    view.setBackgroundColor(Color.GREEN);
		            }
	        });
	}
}