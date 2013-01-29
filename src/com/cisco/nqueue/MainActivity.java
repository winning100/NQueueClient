package com.cisco.nqueue;

import org.json.JSONException;
import org.json.JSONObject;

import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final int CHECK_IN_REQUEST = 1;
	public static final int CHECK_OUT_REQUEST = 2;
	public static final int UPDATE_REQUEST = 3;
	public static final int CHECK_IN_NFC_REQUEST = 4;

	private Database database;

	String webServerAddr_ = "http://98.235.161.80:6666/";
	String client_id_ = "none";
	String restaurant_id_ = "50f1d13fcf7f130d7f0077d2";
	String restaurant_name_;

	TextView restaurantIdText;
	TextView resultText;

	TalkToServer toServer;

	Button checkIn_button;
	Button checkInNFC_button;
	Button checkOut_button;
	Button update_button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		database = new Database(this);
		toServer = new TalkToServer();

		//addrText = (TextView) findViewById(R.id.address);
		//clientIdText = (TextView) findViewById(R.id.client_id);
		restaurantIdText = (TextView) findViewById(R.id.restaurant_name);
		resultText = (TextView) findViewById(R.id.result);

		//list_button = (Button) findViewById(R.id.all_the_info);
		//quit_button = (Button) findViewById(R.id.quit);
		update_button = (Button) findViewById(R.id.check_rank);
		checkOut_button = (Button) findViewById(R.id.check_out);
					
		checkOut_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.i("button click", "check out");
				new NetworkRequest().execute(CHECK_OUT_REQUEST);
			}
		});

		

		update_button.setOnClickListener(new OnClickListener() {  //query the new rank

			@Override
			public void onClick(View v) {
				new NetworkRequest().execute(UPDATE_REQUEST);
			}
		});

		

		NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null) {
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}

	}

	public void onResume() {
		Log.i("+++onResume+++", "in the resume");
		super.onResume();
		Log.i("+++onResume+++", "after super resume");
		NdefMessage[] msgs = null;
		Intent intent = this.getIntent();
		if (intent == null) {
			Log.i("+++onResume+++", "intent empty");
			return;
		}
		String action = intent.getAction();
		Log.i("+++onResume+++", "action is " + action);

		// application is awakened by NFC tag
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Log.i("+++onResume+++", "intent not empty");
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				Log.i("+++onResume+++", "rawMsg not null");
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
				processMsg(msgs[0]);
			}

		}
	}
    void startPolling(){
    	
    	//Intent intent = new Intent(MainActivity.class, Polling.class);
    	Log.i("++startPolling+++", "mainActvity");
    	Intent intent = new Intent(this, Polling.class);
    	Log.i("++startPolling+++", restaurant_id_);
    	Log.i("++startPolling+++", client_id_);
    	Log.i("++++", webServerAddr_);
    	intent.putExtra("restaurant_id", restaurant_id_);
    	
    	intent.putExtra("client_id", client_id_);
    	intent.putExtra("web_server", webServerAddr_);
		startService(intent);
		
    }
    
    
    void stopPolling(){
    	String tag = "+++stopPolling+++";
    	Log.i(tag, "before stop");
    	Intent intent = new Intent(this, Polling.class);
    	stopService(intent);
    	
    	
    }
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent); // guarantee before onResume is called, the intent is
							// the newest one.
							// it would be call only when the activity is
							// "singleTop"
							// or the intent is some similar flag
	}

	private class NetworkRequest extends
			AsyncTask<Integer, Void, String> {
		/**
		 * The return result will be used as parameter in the onPostExecute
		 * method automatically
		 * 
		 * the value returned in doInBackground is a jason formatted string
		 * */
		int request ;
		@Override
		protected String doInBackground(Integer... args) {			

			String phone = "12345";
			String results = null;
			request = args[0];    // args[0] is the current request
			if (request == CHECK_IN_REQUEST) {
				Log.i("+++NetworkRequest+++", "check in");
				toServer.set(webServerAddr_, restaurant_id_, phone);
				results = toServer.check_in();
				return results;
				
			} else if (request == CHECK_IN_NFC_REQUEST) {
				Log.i("+++NetworkRequest+++", "physically go to restaurant");
				toServer.set(webServerAddr_, restaurant_id_, phone);
				toServer.setClientId(client_id_); // client_id_ is set when we scan the tag 
				Record record = getRecord();
				if (record == null)
				{
					Log.i("+++check_in_nfc+++", "can't check_in_nfc, don't check in before");
					results = "{action:error}";
					return results;
				}
				toServer.setClientId(record.client_id);  //for debug
				toServer.setRestaurantId(restaurant_id_);
				Log.i("client id", record.client_id);
				results = toServer.check_in_nfc();
				return results;
				
			} else if (request == CHECK_OUT_REQUEST) {
				Log.i("+++NetworkRequest+++", "check out");
				Record record = getRecord();
				if (record == null){
					Log.i("can't check out", "not check in");
					results = "{action:error}";
					return results;
				}
				restaurant_id_ = record.restaurant_id;
				client_id_ = record.client_id;
				toServer.set(webServerAddr_, restaurant_id_, phone); //webServerAddr  is hardcoded
				toServer.setClientId(client_id_);
				results = toServer.check_out();
				return results;
			} else if (request == UPDATE_REQUEST) {   //query the new rank
				Log.i("+++NetworkRequest+++", "update request");
				toServer.set(webServerAddr_, restaurant_id_, phone);//webServerAddr is hardcoded
				Record record = getRecord();
				if (record == null){
					Log.i("+++update request+++", "no check in before");
					results = "{action:error}";
					return results;
				}
				client_id_ = record.client_id;
				restaurant_id_ = record.restaurant_id;
				toServer.setClientId(client_id_);
				toServer.setRestaurantId(restaurant_id_);
				results = toServer.queryRank();
				
				return results;
			} 
			// Log.i("result ", String.valueOf(result));
			return null;
		}

		// update database and UI
		@Override
		protected void onPostExecute(String results) {
			
			if (results == null) {
				resultText.append("network request error, null");
				Log.i("+++network error+++", "json string is null");
				return;
			}
			try{
			switch (request) {
			case CHECK_IN_REQUEST:   //done
				checkInPost(results);
				break;
			case CHECK_OUT_REQUEST:  //quit the queue
				checkOutPost(results);
				break;
			case UPDATE_REQUEST:   //done, query if it can pop up
				updatePost(results);
				break;
			case CHECK_IN_NFC_REQUEST:  //done
				checkInNFCPost(results);
				break;
			default:
				break;
			}
			}catch (Exception e){e.printStackTrace();}
		}

		/***
		 * following methods handle the results from the web server. update UI
		 * and database
		 * 
		 ***/

		void checkInPost(String results) throws JSONException {
			JSONObject jObject = null;
			String action = "";
			try {
				jObject = new JSONObject(results);
				action = jObject.getString("action");
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			// check in fails
			if (action.equals("") || action.contains("error")){
				Log.i("+++checkInPost+++", "check in error");
				resultText.setText("Reservation failed");
								
			/*	// Possible future error checking //
			if (action.equals(""))	{
				Log.i("+++checkInPost+++", "No Action");
				resultText.setText("Reservation failed \nNo Action");
				}
			else if (action.contains("error")){
				Log.i("+++checkInPost+++", "action contains \"error\"");
				resultText.setText("Reservation failed \nAction contains \"error\"");
			}
				else{
				Log.i("+++checkInPost+++", "unknown check in error");
				resultText.setText("Reservation failed \nUnkown Error");
			}*/				
				return ;
			}
			
			// handle check in success
			
			String client_id = jObject.getString("client_id");
			client_id_ = client_id;
			String restaurant_id = jObject.getString("restaurant_id");
			String ETA = jObject.getString("eta");
			String is_notified = jObject.getString("is_ready");
			Log.i("json param client_id", client_id);
			Log.i("json param rest_id", restaurant_id);
			Log.i("json param eta", ETA);
			Log.i("json param is_notified", is_notified);
			
			restaurantIdText.setText(restaurant_id);
			resultText.setText("Reservation successful!");
			
			startPolling();
			Log.i("+++service+++", "after start");
			insertRecord(restaurant_id, client_id, is_notified);
			
		}

		void checkOutPost(String results) throws JSONException {
			JSONObject jObject = null;
			String action = "";
			try {
				jObject = new JSONObject(results);
				action = jObject.getString("action");
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			// check out fails
			if (action.equals("") || action.contains("error")){
				Log.i("+++checkOutPost+++", "check out error");
				deleteRecord();
				resultText.setText("Could not cancel reservation. \nUnknown error");
				return ;
			}
			
			// handle check out success
			resultText.setText("Successfully canceled reservation");
			stopPolling();
			Log.i("+++check_out+++", action);
			deleteRecord();
		}

		void updatePost(String results) throws JSONException {
			Log.i("+++update_Post+++", "");
			JSONObject jObject = null;
			String action = "";
			try {
				jObject = new JSONObject(results);
				action = jObject.getString("action");
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			
			if (action.equals("") || action.contains("error")){
				Log.i("+++updatePost+++", "update error");
				resultText.setText("Could not retrieve update");
				return ;
			}
			
			// handle update success
			String client_id = jObject.getString("client_id"); 
			String restaurant_id = jObject.getString("restaurant_id");
			String ETA = jObject.getString("eta");
			String is_notified = jObject.getString("is_ready");
			Log.i("json param client_id", client_id);
			Log.i("json param rest_id", restaurant_id);
			Log.i("json param eta", ETA);
			Log.i("json param is_notified", is_notified);
			
			
			// ~~~~~~~~ Unsure why there's and if/else here ~~~~~~ ///
			if (Boolean.getBoolean(is_notified)){
				Log.i("+++on top +++", is_notified);
				resultText.setText("Update successful");
			}
			else{
				Log.i("+++on top +++", is_notified);
				resultText.setText("Update unsuccessful");
			}
			
		}

	/*	void quitPost(ArrayList<String> results) {
			if (parserJSON(results.get(1)) == "success") { // check in successfully,
												// returnCode is
				resultText
						.append("Successfully check out, removed from the waiting queue for today\n");
			} else {
				resultText.append("Fail to check out, please try again!"
						+ client_id_ + "\n");
			}
		}
*/
		void checkInNFCPost(String results) throws JSONException {
			JSONObject jObject = null;
			String action = "";
			try {
				jObject = new JSONObject(results);
				action = jObject.getString("action");
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			// check in nfc fails
			if (action.equals("") || action.equals("error")){
				Log.i("+++checkInNFCPost+++", "check in nfc error");
				resultText.setText("Check in Failed");
				return ;
			}
			// check in nfc fails, not notified
			else if (action.equals("illegal")){
				Log.i("+++checkInNFCPost+++", "not notified");
				resultText.setText("Check in Failed \nServer not notified");
				return;
			}
			//successfully check in !
			else if (action.equals("success")){
				Log.i("+++checkInNFCPost+++", "out of the queue on the server side");
				resultText.setText("Check in Successful!\nEnjoy your meal");
				
			}
			
			//insertRecord(restaurant_id, client_id, is_notified);
			deleteRecord(restaurant_id_, client_id_);
			
		}
	}

	/**
	 * process msgs from the nfc tag
	 * 
	 * @param msg
	 */
	public void processMsg(NdefMessage msg) {

		// extract web and restaurant information from the nfc message
		webServerAddr_ = new String(msg.getRecords()[0].getPayload());
		restaurant_id_ = new String(msg.getRecords()[1].getPayload());
		restaurant_name_ = new String(msg.getRecords()[2].getPayload());

		// restaurant_id_ and client_id_ are prepared for future use in
		// NetworkRequest
		// restaurant_id_ = 123 ;//Integer.parseInt(restaurant_id_string);

		/*
		 * after get data from the NFC tag. according to local database, decide
		 * if it has checked in before. if yes, then it would be a check out
		 * operation. if not, it would be a check in operation
		 */
		String checked_value = isChecked(restaurant_id_);
		if (checked_value != "none") { // the restaurant information is already in the database
			client_id_ = checked_value;
			Log.i("+++ProcessMsg+++", "check_in_nfc_operation");
			resultText.append("check_in_nfc_operation");
			new NetworkRequest().execute(CHECK_IN_NFC_REQUEST);
		} else {                     //the restaurant information is not in the database
			Log.i("+++ProcessMsg+++", "check_in_operation");
			resultText.append("check_in_operation");
			new NetworkRequest().execute(CHECK_IN_REQUEST);
		}
		resultText.append("tag infomation: \n" + "server: " + webServerAddr_
				+ "\n" + "restaurant id: " + restaurant_id_ + "\n"
				+ "restaurant name: " + restaurant_name_ + "\n");

	}

	/**
	 * check if the client has checked in the restaurant
	 * 
	 * @param restaurant_id
	 * @return
	 */
	String isChecked(String restaurant_id) {
		database.open();
		String result = database.isChecked(restaurant_id);
		Log.i("+++top_database+++", "checked result: " + result);
		database.close();
		return result;
	}
	void insertRecord(String restaurant_id, String client_id, String is_notified){
		database.open();
		database.insert(restaurant_id, client_id, is_notified);
		database.close();
	}
	
	Record getRecord(){
		database.open();
		Record record = database.getRecord();
		database.close();
		return record;
	}
	void insertRecord(String restaurant_id, String client_id,
			String restaurant_name, String rank, String eta) {
		database.open();
		database.insert(restaurant_id, client_id, restaurant_name, rank, eta);
		database.close();
	}

	void deleteRecord(String restaurant_id, String client_id) {
		database.open();
		database.delete(restaurant_id, client_id);
		database.close();
	}
//delete all the records in the database
	void deleteRecord(){
		database.open();
		database.delete();
		database.close();
	}
	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 */

}
