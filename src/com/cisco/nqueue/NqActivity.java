package com.cisco.nqueue;

import android.app.Activity;
import android.os.Bundle;

/**
 * NqActivity should have a good UI
 * users interact with this class to check the queue status.
 * @author Xuan
 *
 */

public class NqActivity extends Activity
{
	String ip;
	int port;
	int current_status;
	String id;
	
	
	public static int INITIAL = 0;
	public static int IN_THE_QUEUE = 1;
	public static int OUT_OF_THE_QUEUE = 2;
	public static String EMPTY_ID = "";
	
	@Override
	protected void onCreate(Bundle saveInstanceState){
		ip = getIntent().getExtras().getString("ip");
		port = getIntent().getExtras().getInt("port");
		current_status = INITIAL;
		id = EMPTY_ID;
	}
	
	
	/**
	 * check in the queue
	 * @return true if added in the queue
	 */
	private boolean checkIn(){
		
		return true;
	}
	
	/**
	 * user actively sends update request to server
	 */
	private void updateStatus(){
		
	}
	
	/**
	 *leave the queue 
	 */
	private void checkOut(){
		
	}
}