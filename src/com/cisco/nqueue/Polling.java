package com.cisco.nqueue;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class Polling extends Service{
	String client_id;
	String restaurant_id;
	String web_server;
	TalkToServer talkToServer;
	public static final int INTERVAL = 3000;  //3 seconds 
	//public static int A = 3;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate(){
		Log.i("+++polling+++", "in the onCreate");
		
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
		//Bundle extras = intent.getExtras();
		
		
		client_id = intent.getStringExtra("client_id");
		restaurant_id = intent.getStringExtra("restaurant_id");
		web_server = intent.getStringExtra("web_server");
		String tag = "++in the service+++";
		Log.i(tag, "client: "+client_id);
		Log.i(tag,restaurant_id);
		Log.i(tag, web_server);
		
		Thread pollingRequest = new PollingRequest();
		//Thread testRequest = new TestRequest();
		//testRequest.start();
		//mHandler.postDelayed(pollingRequest, 3000);
		pollingRequest.start();
		
	}
	
	
		
		class PollingRequest extends Thread{
		@Override
			public void run(){
				//String requestAddr = web_server+"/get_rank";
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
				String is_notified = "";
				// check in fails
				
				if (action.equals("") || action.contains("error")){
					Log.i("+++Polling+++", "check in error");
					Log.i("polling", "error");
					return ;
				}
				
				
				
				try{
				String ETA = jObject.getString("eta");
				is_notified = jObject.getString("is_ready");
				//Log.i("json param client_id", client_id);
				//Log.i("json param rest_id", restaurant_id);
				Log.i("json param eta", ETA);
				Log.i("json param is_notified", is_notified);
				}catch (Exception e){e.printStackTrace();
				}
					if (is_notified.equals("true")){
					Log.i("+++polling+++", "should go to restaurant");
					
					
					//notification test below.
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Polling.this);
					mBuilder.setSmallIcon(R.drawable.notification_icon)
							.setContentTitle("NQ notification")
							.setContentText("Your food is ready!")
							.setAutoCancel(true)
							.setSound(RingtoneManager
						             .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
									);
					Intent resultIntent = new Intent(Polling.this, MainActivity.class);
					resultIntent.setAction(MainActivity.POLLING_ACTION);
					resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
					TaskStackBuilder stackBuilder = TaskStackBuilder.create(Polling.this);
					stackBuilder.addParentStack(MainActivity.class);
					stackBuilder.addNextIntent(resultIntent);
					
					PendingIntent resultPendingIntent =
					        stackBuilder.getPendingIntent(
					            0,
					            PendingIntent.FLAG_UPDATE_CURRENT
					        );
					mBuilder.setContentIntent(resultPendingIntent);
					NotificationManager mNotificationManager =
					    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					// mId allows you to update the notification later on.
					mNotificationManager.notify(0, mBuilder.build());
					
					return; // the return statement is neccessary here
					//stopSelf ();
				}
				else{
					Log.i("+++polling+++", "should not go to restaurant");
					//no return statement here;
				}
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}

			
	}
}
