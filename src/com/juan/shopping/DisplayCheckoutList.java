package com.juan.shopping;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.juan.shopping.sqlitehelper.StoreDatabaseHelper;
import com.juan.shopping.sqlitemodel.Item;

public class DisplayCheckoutList extends Activity{
	
	private List<Item> checkoutList; //make into historyItem later
	private List<String> names;
	ArrayAdapter<String> adapter;
	private int totalPrice;
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
		
		
		list=(ListView)findViewById(R.id.LIST_OF_ITEMS);
        tv=(TextView)findViewById(R.id.TOTAL_PRICE); 
        tv.setText("TOTAL");

		adapter = new ArrayAdapter<String>(this, R.layout.list_items, R.id.itemName, names);
		list.setAdapter(adapter);
	}

	// Route called when the user presses "connect"

	public void openSocket(View view) {
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
		MyApplication app = (MyApplication) getApplication();

		// Get the message from the box

		EditText et = (EditText) findViewById(R.id.MessageText);
		String msg = et.getText().toString();

		// Create an array of bytes. First byte will be the
		// message length, and the next ones will be the message

		byte buf[] = new byte[msg.length() + 1];
		buf[0] = (byte) msg.length();
		System.arraycopy(msg.getBytes(), 0, buf, 1, msg.length());

		// Now send through the output stream of the socket

		OutputStream out;
		try {
			out = app.sock.getOutputStream();
			try {
				out.write(buf, 0, msg.length() + 1);
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
		String ip = "192.168.1.100";
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

						// If so, read them in and create a st	ring

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
								StoreDatabaseHelper db = new StoreDatabaseHelper(getApplicationContext());
								checkoutList.add(db.getItem(s));
								names.add(db.getItem(s).getName());
								totalPrice += db.getItem(s).getPrice();								
								db.closeDB();
								
								list.setAdapter(adapter);
								tv.setText(totalPrice);
							}
						});

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}	
}