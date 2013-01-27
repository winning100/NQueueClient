package com.cisco.nqueue;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
/**
 * This activity will display all the check-in events
 * for the user 
 * 
 * */
public class QueueList extends ListActivity{
	private Database database;
	private TalkToServer toServer;
	
	@Override
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);
		database = new Database(this);
		toServer = new TalkToServer();
		String []FRUITS = null;
		int []columnIds =  new int[] {R.id.row_restaurant_name, R.id.row_rank};
		String[] columnTags = new String[] {"col1", "col2"};

		ArrayList<HashMap<String, String>> mylistData =
                new ArrayList<HashMap<String, String>>();
		
		/*
		for(int i=0; i<3; i++)
		{
		 HashMap<String,String> map = new HashMap<String, String>();
		 //initialize row data
		 for(int j=0; j<3; j++)
		 {
		    map.put(columnTags[j], "rowÓ+i+Ócol"+j);
		 }
		 mylistData.add(map);
		}
		*/
		SimpleAdapter arrayAdapter =
		               new SimpleAdapter(this, mylistData, R.layout.item_info,
		                             columnTags , columnIds);
	
		//setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2,FRUITS));
		
		setListAdapter(arrayAdapter);
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
 
	}

/*private class UpdateRequest extends AsyncTask<Integer, Void, ArrayList<String>>{
	
	*//**
	 * args[0]:restaurant_id
	 * args[1]:client_id
	 * 
	 * *//*
	@Override
	protected ArrayList<String> doInBackground(Integer... args) {
		String restaurant_id = args[0];
		String client_id = args[1];
		toServer.setClientId(client_id);
		toServer.setRestaurantId(restaurant_id);
		ArrayList<String> results= toServer.queryRank();
		
		
		return null;
	}
	
}
*/


/*
  update one record in the database
*/
void updateOne(int restaurant_id, int client_id){
	 ContentValues args = new ContentValues();
	 int rank = 0;
     args.put("rank", rank);
     //args.put("client", name);
    /* database.update(DatabaseHelper.CHECK_IN_TABLE,
    		 		 args, "restaurant_id=" + restaurant_id+" and "+"client_id="+client_id,
    		 		 null);
    */
}	
	
	
/*
  update all the ranks in the database
*/
void updateAll(){
	
}	
/*
  fetch all the check_in records in local database	
 */
ArrayList<Record> allRecords(){
	ArrayList<Record> records = null;
	database.open();
	records = database.allRecords();
	database.close();
	
	return records;
}	
}