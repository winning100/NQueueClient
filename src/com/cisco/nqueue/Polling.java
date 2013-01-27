package com.cisco.nqueue;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class Polling extends Service{
	String client_id;
	String restaurant_id;
	String web_server;
	TalkToServer talkToServer;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate(){
		Log.i("+++polling+++", "in the onCreate");
		//talkToServer = new TalkToServer();
		//Log.i("+++polling+++", "in the onStartCommand");
	    //handleCommand(getIntent());
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		talkToServer = new TalkToServer();
		Log.i("+++polling+++", "in the onStartCommand");
	    handleCommand(intent);
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
	
	void handleCommand(Intent intent){
		Bundle extras = intent.getExtras();
		
		client_id = intent.getStringExtra("client_id");
		restaurant_id = intent.getStringExtra("restaurant_id");
		web_server = intent.getStringExtra("web_server");
		String tag = "++in the service+++";
		Log.i(tag, "client: "+client_id);
		Log.i(tag,restaurant_id);
		Log.i(tag, web_server);
		//Handler mHandler = new Handler();
		Thread pollingRequest = new PollingRequest();
		//mHandler.postDelayed(pollingRequest, 3000);
		pollingRequest.start();
		
	}
		//new PollingRequest().execute(restaurant_id, client_id);
		/*Runnable m_statusChecker = new Runnable()
		{
		     @Override 
		     public void run() {
		          updateStatus(); //this function can change value of m_interval.
		          m_handler.postDelayed(m_statusChecker, m_interval);
		     }
		};

		void startRepeatingTask()
		{
		    m_statusChecker.run(); 
		}

		void stopRepeatingTask()
		{
		    m_handler.removeCallbacks(m_statusChecker);
		}
	}
	*/
	class PollingRequest extends Thread{
		@Override
			public void run(){
				String requestAddr = web_server+"/get_rank";
				JSONObject jObject = null;
				String action = "";
				talkToServer.setClientId(client_id);
				talkToServer.setRestaurantId(restaurant_id);
				talkToServer.setWebServer(web_server);
				while (true){
				String results = talkToServer.queryRank();
				try {
					jObject = new JSONObject(results);
					action = jObject.getString("action");
					} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
				// check in fails
				if (action.equals("") || action.contains("error")){
					Log.i("+++Polling+++", "check in error");
					Log.i("polling", "error");
					return ;
				}
				
				
				String is_notified = "";
				try{
				//String client_id = jObject.getString("client_id"); 
				//String restaurant_id = jObject.getString("restaurant_id");
				String ETA = jObject.getString("eta");
				is_notified = jObject.getString("is_ready");
				Log.i("json param client_id", client_id);
				Log.i("json param rest_id", restaurant_id);
				Log.i("json param eta", ETA);
				Log.i("json param is_notified", is_notified);
				}catch (Exception e){e.printStackTrace();
				}
				
				if (Boolean.getBoolean(is_notified)){
					Log.i("+++polling+++", "should go to restaurant");
					
				}
				else{
					Log.i("+++polling+++", "should not go to restaurant");
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}

			
	}
}
