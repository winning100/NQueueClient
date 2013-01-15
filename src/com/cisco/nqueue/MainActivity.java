package com.cisco.nqueue;

import java.util.ArrayList;

import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static final int CHECK_IN_REQUEST = 1;
	public static final int CHECK_OUT_REQUEST = 2;
	public static final int QUIT_REQUEST = 3;
	public static final int UPDATE_REQUEST = 4;
	public static final int LIST_REQUEST = 5;
	
	
	
	private Database database;
	
	String webServerAddr_;
	int client_id_;
	int restaurant_id_;
	String restaurant_name_;
	
	TextView addrText;
	TextView phoneText;
	TextView clientIdText;
	TextView restaurantIdText;
	TextView resultText;
	
	TalkToServer toServer;
	
	Button checkIn_button;
	Button checkOut_button;
	Button update_button;
	Button quit_button;
	Button list_button;   //retrieve all the checked_in info
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		database = new Database(this);
		toServer = new TalkToServer();
		
		addrText = (TextView)findViewById(R.id.address);
		clientIdText = (TextView)findViewById(R.id.client_id);
		restaurantIdText = (TextView)findViewById(R.id.restaurant_id);
		phoneText = (TextView)findViewById(R.id.phone);
		resultText = (TextView)findViewById(R.id.result);
		
		checkIn_button = (Button)findViewById(R.id.check_in);
		checkOut_button = (Button)findViewById(R.id.check_out);
		update_button = (Button)findViewById(R.id.check_rank);
		quit_button = (Button)findViewById(R.id.quit);
		list_button = (Button)findViewById(R.id.all_the_info);
		
		checkIn_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				new NetworkRequest().execute(CHECK_IN_REQUEST);
			}
		});
		
		checkOut_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Log.i("wocao", "in the button2");
				new NetworkRequest().execute(CHECK_OUT_REQUEST);
			}});
		
		quit_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new NetworkRequest().execute(QUIT_REQUEST);
			}
		});
		
		update_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new NetworkRequest().execute(UPDATE_REQUEST);
			}});
		
		list_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				//new NetworkRequest()
			}
			
		});
			
		NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (mNfcAdapter == null){
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
	}
	
	public void onResume() {
		Log.i("+++onResume+++", "in the resume");
	    super.onResume();
	    Log.i("+++onResume+++", "after super resume");
	    NdefMessage []msgs = null;
	    Intent intent = this.getIntent();
	    if (intent == null){
	    	Log.i("+++onResume+++", "intent empty");
	    	return;}
	    String action = intent.getAction();
	    Log.i("+++onResume+++", "action is "+action);
	    
	    //application is awakened by NFC tag
	    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) 
	    {
	    	Log.i("+++onResume+++", "intent not empty");
	        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
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
	
	@Override
	protected void onNewIntent (Intent intent){
		 setIntent(intent); //guarantee before onResume is called, the intent is 
		 					// the newest one.
		 					// it would be call only when the activity is "singleTop"
		 					// or the intent is some similar flag
	}
	
	
	private class NetworkRequest extends AsyncTask<Integer, Void, ArrayList<String>>{
		/**
		 * The return result will be
		 * used as parameter in the onPostExecute method automatically
		 * */
		@Override
		protected ArrayList<String> doInBackground(Integer... args) {
			
			//String address = addrText.getText().toString();
			//String clientId = clientIdText.getText().toString();
			//String restaurantId = restaurantIdText.getText().toString();
			String phone = "123456";
			ArrayList<String> results = null;	
		    
			if (args[0] == CHECK_IN_REQUEST){
				Log.i("+++NetworkRequest+++", "check in");
				toServer.set(webServerAddr_, restaurant_id_, phone);
				results = toServer.check_in();
				results.add(0, String.valueOf(CHECK_IN_REQUEST));
				return results;
				}
			
			else if (args[0] == CHECK_OUT_REQUEST){
				Log.i("+++NetworkRequest+++", "check out");
				toServer.setWebServer(webServerAddr_);
				toServer.setClientId(client_id_);
				toServer.setRestaurantId(restaurant_id_);
				results = toServer.check_out();
				results.add(0, String.valueOf(CHECK_OUT_REQUEST));
				return results;
				}
			
			else if (args[0] == UPDATE_REQUEST){
				Log.i("+++NetworkRequest+++", "update request");
				toServer.setClientId(client_id_);
				toServer.setRestaurantId(restaurant_id_);
				results = toServer.queryRank();
				results.add(0,String.valueOf(UPDATE_REQUEST));
				return results;
			}
			else if (args[0] == QUIT_REQUEST){
				Log.i("+++NetworkRequest+++", "quit request");
				toServer.setClientId(client_id_);
				toServer.setRestaurantId(restaurant_id_);
				results = toServer.quit();
				results.add(0, String.valueOf(QUIT_REQUEST));
				return results;
			}
			//Log.i("result ", String.valueOf(result));
			
			return null;
		}
		
		//update database and UI
		@Override
		protected void onPostExecute(ArrayList<String> results) {
			if (results == null){
				resultText.append("network request error, null");
				return;
				}
			int request = Integer.parseInt(results.get(0));
			
			switch (request){
			case CHECK_IN_REQUEST:
				checkInPost(results);
				break;
			case CHECK_OUT_REQUEST:
				checkOutPost(results);
				break;
			case UPDATE_REQUEST:
				updatePost(results);
				break;
			case QUIT_REQUEST:
				quitPost(results);
				break;
			case LIST_REQUEST:
				listPost(results);
				break;
			default:
				break;
			}
			}
		
		/***
		 * following methods handle the results from the web server.
		 * update UI and database
		 * 
		 ***/
		
		void checkInPost(ArrayList<String> results){
			Log.i("+++check_in_post+++","");
			int returnCode = Integer.parseInt(results.get(1));
			Log.i("+++check_in_post+++","return code: "+returnCode);
			
			if (returnCode >= 0){ 	 //check in successfully, returnCode is the ClientId
				int rank = Integer.parseInt(results.get(3));
				Log.i("+++check_in_post+++","1");
				insertRecord(restaurant_id_, returnCode, restaurant_name_,rank);
				resultText.append("new record in database: \n");
				resultText.append(restaurant_id_+" "+returnCode+
						          " "+ restaurant_name_);
			}
			
			else{
				resultText.append("can't check in "+returnCode+"\n");
				}
			
			
		}
		
		void checkOutPost(ArrayList<String> results){
			int returnCode = Integer.parseInt(results.get(1));
			switch(returnCode){
			case 1:
				deleteRecord(restaurant_id_, client_id_);
				resultText.append("delete records: \n");
				resultText.append(restaurant_id_+" "+client_id_);
				break;
			default:
				resultText.append("can't check out "+returnCode+"\n");
			}
		}
		
		void updatePost(ArrayList<String> results){
			
		}
		
		void quitPost(ArrayList<String> results){
			
		}
		
		void listPost(ArrayList<String> results){
			
		}
	}
	
	
	/**
	 * process msgs from the nfc tag  
	 * @param msg
	 */
	public void processMsg(NdefMessage msg){
		
        //extract web server address and port from the message
        webServerAddr_ = new String(msg.getRecords()[0].getPayload());
        String restaurant_id_string = new String(msg.getRecords()[1].getPayload());
        restaurant_name_ = new String(msg.getRecords()[2].getPayload());
        
        //restaurant_id_ and client_id_ are prepared for future use in NetworkRequest
        restaurant_id_ = Integer.parseInt(restaurant_id_string);
        
        /*after get data from the NFC tag.
         * according to local database, decide if it has checked in before.
         * if yes, then it would be a check out operation.
         * if not, it would be a check in operation
        */
        int checked_value = isChecked(restaurant_id_);
        if (checked_value >= 0){
        	client_id_ = checked_value;
        	Log.i("+++ProcessMsg+++", "check_out_operation");
        	resultText.append("check_out_operation");
        	new NetworkRequest().execute(CHECK_OUT_REQUEST);
        	
        }
        else{
        	
        	Log.i("+++ProcessMsg+++", "check_in_operation");
        	resultText.append("check_in_operation");
        	new NetworkRequest().execute(CHECK_IN_REQUEST);
        }
        
      
        resultText.append("tag infomation: \n"+
        				  "server: "+webServerAddr_+"\n"+
                          "restaurant id: "+restaurant_id_+"\n"+
        				  "restaurant name: "+restaurant_name_+"\n");
		
	}
	/**
	 * check if the client has checked in the restaurant
	 * @param restaurant_id
	 * @return
	 */
	int isChecked(int restaurant_id){
		database.open();
		int result = database.isChecked(restaurant_id);
		Log.i("+++top_database+++", "checked result: "+result);
		database.close();
		return result;
	}
	
	void insertRecord(int restaurant_id, int client_id, 
					  String restaurant_name, int rank){
		database.open();
		database.insert(restaurant_id, client_id, restaurant_name, rank);
		database.close();
	}
	
	void deleteRecord(int restaurant_id, int client_id){
		database.open();
		database.delete(restaurant_id, client_id);
		database.close();
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}*/

}
