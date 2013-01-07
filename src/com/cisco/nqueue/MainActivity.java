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
	
	public static int CHECK_IN_REQUEST = 1;
	public static int CHECK_OUT_REQUEST = 2;
	public static int QUIT_REQUEST = 3;
	public static int UPDATE_REQUEST = 4;
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
		
		
		checkIn_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Log.i("wocao", "in the button");
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
		/*NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null){
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}*/
		
	}
	
	//public void onResume() {
	  //  super.onResume();
	  /*  NdefMessage []msgs = null;
	    Intent intent = getIntent();
	    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
	        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	        if (rawMsgs != null) {
	            msgs = new NdefMessage[rawMsgs.length];
	            for (int i = 0; i < rawMsgs.length; i++) {
	                msgs[i] = (NdefMessage) rawMsgs[i];
	            }
	        }
	        else  // no raw messages
	        	return;
	    }
	    
	    
	    processMsg(msgs[0]);*/
	   //}
	
	
	private class NetworkRequest extends AsyncTask<Integer, Void, ArrayList<String>>{

		@Override
		protected ArrayList<String> doInBackground(Integer... args) {
			
			String address = addrText.getText().toString();
			String clientId = clientIdText.getText().toString();
			String restaurantId = restaurantIdText.getText().toString();
			String phone = phoneText.getText().toString();
				
			toServer.set(address, clientId, restaurantId, phone);
			
		    ArrayList<String> str_list = null;
		    
			if (args[0] == CHECK_IN_REQUEST){
				Log.i("wocao", "in the network request");
				return toServer.check_in();
				}
			
			else if (args[0] == CHECK_OUT_REQUEST){
				Log.i("wocao", "network request2");
				return toServer.check_out();
			}
			else if (args[0] == UPDATE_REQUEST){
				return toServer.queryRank();
			}
			else if (args[0] == QUIT_REQUEST){
				return toServer.quit();
			}
			//Log.i("result ", String.valueOf(result));
			
			return null;
			//return result;
		}
		
		
		
		
		//update UI below
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			//resultText.setText(String.valueOf(result));
	         //showDialog("Downloaded " + result + " bytes");
			StringBuilder sb = new StringBuilder();
			if (result == null || result.size() == 0)
				sb.append("error\n");
			else{
			for (int i = 0; i < result.size(); i++){
				sb.append(result.get(i)).append("\n");
			}
			}
			resultText.setText(sb.toString());
	     }
		
		
		
	}
	
	
	/**
	 * start a new activity to display the queue status 
	 * @param msg
	 */
	public void processMsg(NdefMessage msg){
		
        //extract ip and port from the message
        String ip = new String(msg.getRecords()[0].getPayload());
        int port = Integer.parseInt(new String(msg.getRecords()[1].getPayload()));
        
        //start a Nqactivity 
        Intent myIntent = new Intent(this, NqActivity.class);
        Bundle paramets = new Bundle();

        paramets.putString("ip",ip);
        paramets.putInt("port", port);

        myIntent.putExtras(paramets);
        this.startActivity(myIntent);
        
		
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}*/

}
