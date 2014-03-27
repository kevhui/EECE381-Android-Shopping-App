package com.juan.shopping;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.juan.shopping.sqlitehelper.CheckoutListDatabaseHelper;
import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.Item;
import com.juan.shopping.sqlitemodel.Shopping_list_item;
import com.juan.shopping.sqlitemodel.historyItem;


public class DisplayCheckoutList extends Activity {

	public List<historyItem> checkoutList; // make into historyItem later
	private List<String> names;
	ArrayAdapter<String> adapter;
	private double totalPrice;
	ListView list;
	TextView tv;

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

		checkoutList = new ArrayList<historyItem>();
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
		
		CheckoutListDatabaseHelper db = new CheckoutListDatabaseHelper(getApplicationContext());;
		for (historyItem item : checkoutList) {
			db.addItem(item);
		}
		db.closeDB();
		
		totalPrice = 0;
		tv.setText("$" + String.format("%.2f", totalPrice));
		names.clear();
		adapter.notifyDataSetChanged();
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
		String ip = "128.189.221.246";
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

					int bytes_avail = in.available();
					if (bytes_avail > 0) {

						// If so, read them in and create a st ring

						byte buf[] = new byte[bytes_avail];
						in.read(buf);

						final String s = new String(buf, 0, bytes_avail,
								"US-ASCII");

						// As explained in the tutorials, the GUI can not be
						// updated in an asyncrhonous task. So, update the GUI
						// using the UI thread.

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Log.d("Debug", s);

								String upc = s.substring(1);
								if (upc.length() == 12) {
									
									String currentDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

									StoreDatabaseHelper db = new StoreDatabaseHelper(
											getApplicationContext());
									Item i = db.getItem(upc);
									db.closeDB();
									
									historyItem hi = new historyItem();
									for (historyItem item : checkoutList) {
										if(item.getUPC() == s){
											hi.addQuantity(1);
											totalPrice += i.getPrice();
											tv.setText("$"
													+ String.format("%.2f", totalPrice));
											return;
										}
										else
											hi = new historyItem(s, i.getPrice(), currentDate, 1);
											break;
									}
									checkoutList.add(hi);
									
									names.add(i.getName());
									totalPrice += i.getPrice();

									adapter.notifyDataSetChanged();
									tv.setText("$" + String.format("%.2f", totalPrice));
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
	
	public void addItem(View view){
		MyApplication app = (MyApplication) getApplication();

		// Get the message from the box

		Log.d("Debug","Send message");
		int i = (int)((Math.random()*100)%4);
		String upc;
		
		if ( i == 0){
			upc = "073141551342";
		}
		else if ( i == 1){
			upc = "678523080016";
		}
		else if ( i == 2){
			upc = "058807415817";
		}
		else{
			upc = "058807414025";
		}
		
		String currentDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

		StoreDatabaseHelper db = new StoreDatabaseHelper(
				getApplicationContext());
		Item currentItem = db.getItem(upc);
		db.closeDB();
		
		historyItem hi = new historyItem();
		for (historyItem item : checkoutList) {
			if(item.getUPC() == upc){
				hi.addQuantity(1);
				totalPrice += currentItem.getPrice();
				tv.setText("$"
						+ String.format("%.2f", totalPrice));
				return;
			}
			else
				hi = new historyItem(upc, currentItem.getPrice(), currentDate, 1);
				break;
		}
		checkoutList.add(hi);
		
		names.add(currentItem.getName());
		totalPrice += currentItem.getPrice();

		adapter.notifyDataSetChanged();
		tv.setText("$" + String.format("%.2f", totalPrice));
		
		
		adapter.notifyDataSetChanged();
	}
}