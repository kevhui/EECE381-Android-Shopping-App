package com.juan.shopping;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juan.shopping.sqlitehelper.CheckoutListDatabaseHelper;
import com.juan.shopping.sqlitehelper.ShoppingListDatabaseHelper;
import com.juan.shopping.sqlitemodel.HistoryItem;
import com.juan.shopping.sqlitemodel.Item;
import com.juan.shopping.sqlitemodel.Shopping_list_item;


public class DisplayCheckoutList extends Activity {

	public List<HistoryItem> checkoutList;
	private List<String> names;
	ArrayAdapter<String> adapter;
	private double totalPrice;
	ListView list;
	TextView tv;
	JSONObject json_item = null;
	Item item;
    String s = "1";
    String ss;
    String r;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// This call will result in better error messages if you
		// try to do things in the wrong thread.

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkout_list);

		// Set up a timer task. We will use the timer to check the
		// input queue every 500 ms
		TCPReadTimerTask tcp_task = new TCPReadTimerTask();
		Timer tcp_timer = new Timer();
		tcp_timer.schedule(tcp_task, 3000, 500);

		checkoutList = new ArrayList<HistoryItem>();
		names = new ArrayList<String>();
		list = (ListView) findViewById(R.id.LIST_OF_ITEMS);
		tv = (TextView) findViewById(R.id.TOTAL_PRICE);
		tv.setText("TOTAL");

		adapter = new ArrayAdapter<String>(this, R.layout.list_items,
				R.id.itemName, names);
		list.setAdapter(adapter);

		openSocket();
		// sendMessage();
		
	}
	
    public void sendMessageToDE2(View view) throws UnsupportedEncodingException{
        
        MyApplication app = (MyApplication) getApplication();
       
        byte msg[] = new byte[1];
        msg[0] = (byte) 'I';
        String value = new String(msg, "UTF-8");
       
        OutputStream out;
        try {
                out = app.sock.getOutputStream();
                Log.i("PAST","OUTPUT");
                try {
                        out.write(msg, 0, 1);
                } catch (IOException e) {
                        e.printStackTrace();
                }
        } catch (IOException e) {
                e.printStackTrace();
        }

}

	// Route called when the user presses "connect"

	public void openSocket() {
		MyApplication app = (MyApplication) getApplication();

		// Make sure the socket is not already opened

		if (app.sock != null && app.sock.isConnected() && !app.sock.isClosed()) {
			return;
		}

		// open the socket. SocketConnect is a new subclass
		// (defined below). This creates an instance of the subclass
		// and executes the code in it.

		new SocketConnect().execute((Void) null);
	}

	// Called when the user wants to send a message

	public void sendMessage(View view) {
		ShoppingListDatabaseHelper sdb = new ShoppingListDatabaseHelper(getApplicationContext());
		List<Shopping_list_item> missingItems = sdb.getAllItems();
		sdb.closeDB();
		
		for(HistoryItem hItem : checkoutList){
			for(Shopping_list_item item : missingItems){
				if(hItem.getUPC().equals(item.getUPC())){
					if(hItem.getQuantity() >= item.getQuantity()){
						missingItems.remove(item);	
					}
				}
			}
		}
		if(!missingItems.isEmpty()){
			LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);

			// Setup the popupView
			View popupView = layoutInflater.inflate(R.layout.popup_checkout,
					null);
			final PopupWindow popupWindow = new PopupWindow(popupView,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			Button btnConfirm = (Button) popupView.findViewById(R.id.bConfirmCheckout);
			Button btnCancel = (Button) popupView.findViewById(R.id.bCancelCheckout);

			TextView tv1 = (TextView) popupView.findViewById(R.id.tvCheckoutWarning);
			tv1.setText("Warning: You are missing some items from your shopping list. Do you wish to continue?");
			
			// Show the popUp
			popupWindow.update();
			popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
			
			btnConfirm.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					String currentDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
					
					CheckoutListDatabaseHelper cdb = new CheckoutListDatabaseHelper(getApplicationContext());
					for (HistoryItem item : checkoutList) {
						item.setDate(currentDate);
						item.setRid(cdb.getMaxRid() + 1);
					}
					for (HistoryItem item : checkoutList) {
						cdb.addItem(item);
						Log.d("Debug","upc = " + item.getUPC());
					}
					cdb.closeDB();
					
					totalPrice = 0;
					tv.setText("$" + String.format("%.2f", totalPrice));
					names.clear();
					checkoutList.clear();
					adapter.notifyDataSetChanged();
					popupWindow.dismiss();
				}
			});
			
			btnCancel.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {

					popupWindow.dismiss();
				}
			});
		}
		else{
			String currentDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
			
			CheckoutListDatabaseHelper cdb = new CheckoutListDatabaseHelper(getApplicationContext());
			for (HistoryItem item : checkoutList) {
				item.setDate(currentDate);
				item.setRid(cdb.getMaxRid() + 1);
			}
			for (HistoryItem item : checkoutList) {
				cdb.addItem(item);
				Log.d("Debug","upc = " + item.getUPC());
			}
			cdb.closeDB();
			
			totalPrice = 0;
			tv.setText("$" + String.format("%.2f", totalPrice));
			names.clear();
			checkoutList.clear();
			adapter.notifyDataSetChanged();
		}
	}
	

	public void sendUpc(View view) {
		MyApplication app = (MyApplication) getApplication();
		// Get made up upc

//		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
//		RadioButton selectRadio = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
//		String selected = selectRadio.getText().toString();
		
		
		int i = (int)((Math.random()*100)%5);
		String upc = "058779203061";
		

//		if ( selected.equals("Magnum")){
//			upc = "U058779203061";
//		}
//		else if ( selected.equals("Peach")){
//			upc = "U058779278090";
//		}
//		else if ( selected.equals("Banana")){
//			upc = "U055000892957";
//		}
//		else if ( selected.equals("Vanilla")){
//			upc = "U681131913461";
//		}
//		else if ( selected.equals("Dark")){
//			upc ="U681131913454";
//		}

		if ( i == 0){
			upc = "058779203061";
		}
		else if (  i == 1){
			upc = "058779278090";
		}
		else if (  i == 2){
			upc = "055000892957";
		}
		else if (  i == 3){
			upc = "681131913461";
		}
		else if (  i == 4){
			upc ="681131913454";

		}

		
		Log.d("Debug","send upc " + upc);
		// Create an array of bytes. First byte will be the
		// message length, and the next ones will be the message

		byte buf[] = new byte[upc.length() + 1];
		buf[0] = (byte) upc.length();
		System.arraycopy(upc.getBytes(), 0, buf, 1, upc.length());
		// Now send through the output stream of the socket

		OutputStream out;
		try {
			out = app.sock.getOutputStream();

			try {
				out.write(buf, 0, upc.length() + 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Called when the user closes a socket
	public void closeSocket(View view) {
		MyApplication app = (MyApplication) getApplication();
		Socket s = app.sock;
		try {
			s.getOutputStream().close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Construct an IP address from the four boxes
	

	public String getConnectToIP() {
		String ip = "192.168.0.100";
		return ip;
	}

	// Gets the Port from the appropriate field.

	public Integer getConnectToPort() {
		Integer port = 50002;
		return port;
	}

	// This is the Socket Connect asynchronous thread. Opening a socket
	// has to be done in an Asynchronous thread in Android. Be sure you
	// have done the Asynchronous Tread tutorial before trying to understand
	// this code.

	public class SocketConnect extends AsyncTask<Void, Void, Socket> {

		// The main parcel of work for this thread. Opens a socket
		// to connect to the specified IP.

		@Override
		protected Socket doInBackground(Void... voids) {
			Socket s = null;
			String ip = getConnectToIP();
			Integer port = getConnectToPort();

			try {
				s = new Socket(ip, port);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return s;
		}

		// After executing the doInBackground method, this is
		// automatically called, in the UI (main) thread to store
		// the socket in this app's persistent storage

		@Override
		protected void onPostExecute(Socket s) {
			MyApplication myApp = (MyApplication) DisplayCheckoutList.this
					.getApplication();
			myApp.sock = s;
		}
	}

	// This is a timer Task. Be sure to work through the tutorials
	// on Timer Tasks before trying to understand this code.

	public class TCPReadTimerTask extends TimerTask {
		@Override
		public void run() {
			MyApplication app = (MyApplication) getApplication();
			if (app.sock != null && app.sock.isConnected()
					&& !app.sock.isClosed()) {

				try {
					InputStream in = app.sock.getInputStream();

					// See if any bytes are available from the Middleman
					//Log.d("TCP", "none available");		
					int bytes_avail = in.available();
					if (bytes_avail > 0) {

						// If so, read them in and create a st ring
 						Log.d("TCP", "0");		
						byte Typebuf[] = new byte[1];
                        in.read(Typebuf);
 						Log.d("TCP", "1");						
                        byte PCbuf[] = new byte[1];
                        in.read(PCbuf);
 						Log.d("TCP", "Typebuf "+Typebuf[0]);     
                         if (Typebuf[0] == 'I'){
      						Log.d("TCP", "I");		
                                 byte idbuf[] = new byte[1];
                                 in.read(idbuf);
                                 //s  = new String(idbuf, "UTF-8");
                                 r = String.valueOf(idbuf[0]);//    format("%4d", idbuf);
                                
                                 r = "USER ID: " + r;
                                
                                 byte CSbuf[] = new byte[1];
                                 in.read(CSbuf);
                                 ss = new String(CSbuf, "UTF-8");
                         }
                        
                         else if (Typebuf[0] == 'U'){
      						Log.d("TCP", "U");		
                                 byte UPCbuf[] = new byte[12];
                                 in.read(UPCbuf);
                                 s = new String(UPCbuf, 0, 12, "US-ASCII");
                                Log.d("READ STRING",s);
                                 
                                 byte CSbuf[] = new byte[1];
                                 in.read(CSbuf);

//***********************************************************//
//							if (true){
//								byte UPCbuf[] = new byte[13];
//							    in.read(UPCbuf);
//                                s = new String(UPCbuf, 1, 12, "US-ASCII");
//***********************************************************//
                                 
                                ServiceHandler sh = new ServiceHandler();
                                
                                //Get item from server
         						String upc = s;
         						
         						// Making a request to url and getting response
         						String jsonStr = sh
         								.makeServiceCall("http://162.243.133.20/items/" + upc);

         						Log.d("Response: ", "Query item upc: " + upc);
         						Log.d("Response: ", "> " + jsonStr);

         						item = new Item();

         						if (jsonStr != null) {
         							try {
         								JSONObject jsonObj = new JSONObject(jsonStr);

         								// Getting JSON Array node
         								json_item = jsonObj.getJSONObject("item");
         								
         								//JSONObject data = item.getJSONObject(0);

         								item = new Item();
         								item.setUpc(json_item.getString("upc"));
         								item.setName(json_item.getString("name"));
         								item.setDescription(json_item.getString("description"));
         								item.setPrice(json_item.getInt("price"));
         								item.setCategory(json_item.getString("category"));
         								item.setImage(json_item.getString("image"));
         								Log.d("Response: ","Adding to list: " + item.getName());
         							} catch (JSONException e) {
         								e.printStackTrace();
         							}
         						} else {
         							Log.e("ServiceHandler",
         									"Couldn't get any data from the url");
         						}
                         }
						
						

						// As explained in the tutorials, the GUI can not be
						// updated in an asyncrhonous task. So, update the GUI
						// using the UI thread.

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Log.d("Debug", s);
								
								String upc = s;
								
                                final TextView tEv = (TextView) findViewById(R.id.textView2);
                                tEv.setText(r);
                               
								if (upc.length() == 12) {
									String currentDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
									
									if (!itemExists(upc)){
										HistoryItem hi = new HistoryItem(upc, item.getPrice(), currentDate, 1);
										checkoutList.add(hi);
										names.add(item.getName());
									}
									totalPrice += item.getPrice();
									tv.setText("$" + String.format("%.2f", totalPrice));
									adapter.notifyDataSetChanged();
								}
							}
						});

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
//	public void addItem(View view){
//		MyApplication app = (MyApplication) getApplication();
//
//		// Get the message from the box
//
//		Log.d("Debug","Send message");
//		int i = (int)((Math.random()*100)%4);
//		String upc;
//		
//		if ( i == 0){
//			upc = "073141551342";
//		}
//		else if ( i == 1){
//			upc = "678523080016";
//		}
//		else if ( i == 2){
//			upc = "058807415817";
//		}
//		else{
//			upc = "058807414025";
//		}
//		
//		String currentDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//
//		StoreDatabaseHelper db = new StoreDatabaseHelper(
//				getApplicationContext());
//		Item i1 = db.getItem(upc);
//		db.closeDB();
//		
//		if (itemExists(upc)){
//		}
//		else {
//			HistoryItem hi = new HistoryItem(upc, i1.getPrice(), currentDate, 1);
//			checkoutList.add(hi);
//			names.add(i1.getName());
//		}
//		totalPrice += i1.getPrice();
//		tv.setText("$" + String.format("%.2f", totalPrice));
//		adapter.notifyDataSetChanged();
//	}
	
	private boolean itemExists(String upc){
		for (HistoryItem item : checkoutList) {
			if(item.getUPC().equals(upc)){
				item.addQuantity(1);
				return true;
			}
		}
		return false;
	}
}