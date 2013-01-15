package com.cisco.nqueue;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {
	 private SQLiteDatabase database;
	 private DatabaseHelper dbHelper;
	 String TAG = "+++Database+++";
	 
	 Database(Context context){
		 dbHelper = new DatabaseHelper(context);
	 }
	 
	 public void open(){
		 database = dbHelper.getWritableDatabase();
	 }
	 
	 public void close(){
		if (database == null)
			return;
		
		database.close();
	}
	
	 public void delete(int restaurant_id, int client_id){
		 String query = "DELETE FROM "+ DatabaseHelper.CHECK_IN_TABLE+" "+
				 		"WHERE client_id="+client_id+" and restaurant_id="+restaurant_id;
		 database.execSQL(query);
		 
	 }
	 public void insert(int restaurant_id, int client_id, String restaurant_name, int rank){
		 String query = "INSERT INTO "+DatabaseHelper.CHECK_IN_TABLE+" "+
			 		"(restaurant_id, client_id, restaurant_name, rank) Values "+
			 		"( "+restaurant_id+" , "+client_id+" , "+"'"+restaurant_name+"'"+" , "+
			 		 rank+" );";
		 database.execSQL(query);
	 }
	 
	 public ArrayList<Record> allRecords(){
		 ArrayList<Record> records = new ArrayList<Record>();
		 String []columns={"restaurant_id","client_id"};
		 Cursor cursor = database.query(DatabaseHelper.CHECK_IN_TABLE, columns,
				 						null, null, null, null, null);
		 cursor.moveToFirst();
		 while (!cursor.isAfterLast()) {
		    	int restaurant_id = cursor.getInt(0);
		    	int client_id = cursor.getInt(1);
		    	String restaurant_name = cursor.getString(2);
		    	int rank = cursor.getInt(3);
		    	Record record = new Record(restaurant_id, client_id, restaurant_name, rank);
		    	records.add(record);
		    	cursor.moveToNext();
		    }
		 
		 return records;
	 }
	
	 /**
	 *return:
	 *-1 : no record containing restaurant_id
	 *n : the client id on restaurant 
	 * */
	 public int isChecked(int restaurant_id){
		 String where = "restaurant_id="+restaurant_id;
		 Cursor cursor = database.query(DatabaseHelper.CHECK_IN_TABLE, 
				 						null, where, null, null, null, null);
		 if (cursor.getCount() == 0)
			 return -1;
		 cursor.moveToFirst();
		 int client_id = cursor.getInt(1);
		 Log.i("+++database+++", "database query, client_id is "+client_id);
		 return client_id;
	 }
}

class Record{
	int restaurant_id;
	int client_id;
	String restaurant_name;
	int rank;
	
	Record(int restaurant_id, int client_id, String restaurant_name, int rank){
		this.restaurant_id = restaurant_id;
		this.client_id = client_id;
		this.restaurant_name = restaurant_name;
		this.rank = rank;
	}
	
}