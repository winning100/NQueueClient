package com.cisco.nqueue;

import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null){
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
	}
	
	public void onResume() {
	    super.onResume();
	    NdefMessage []msgs = null;
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
	    
	    
	    processMsg(msgs[0]);
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
