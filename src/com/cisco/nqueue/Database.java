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
	
	 public void delete(String restaurant_id, String client_id){
		 String query = "DELETE FROM "+ DatabaseHelper.CHECK_IN_TABLE+" "+
				 		"WHERE client_id='"+client_id+"' and restaurant_id='"+restaurant_id+"'";
		 database.execSQL(query);
	 }
	 
	 public void delete(){
		 String query = "DELETE FROM "+DatabaseHelper.CHECK_IN_TABLE+"; ";
		 database.execSQL(query);
		 Log.i("+++database+++", "database cleared");
	 }
	 
	 //insert client_id and restaurant_id and is_notified
	 public void insert(String restaurant_id, String client_id, String is_notified){
		 String query = "INSERT INTO "+DatabaseHelper.CHECK_IN_TABLE+" "+
				 		"(restaurant_id, client_id, is_notified) Values "+
				 		"( '"+restaurant_id+"' , '"+client_id +"', '"+is_notified+"' );";
		 database.execSQL(query);
	 } 
	 
	 //insert a whole record
	 public void insert(String restaurant_id, String client_id, String restaurant_name, String rank, String eta){
		 String query = "INSERT INTO "+DatabaseHelper.CHECK_IN_TABLE+" "+
			 		"(restaurant_id, client_id, restaurant_name, rank, eta) Values "+
			 		"( '"+restaurant_id+"' , '"+client_id+"' , "+"'"+restaurant_name+"'"+" , "+
			 		 rank+"," +
			 		 "'"+eta+"'"+	
			 		 " );";
		 database.execSQL(query);
	 }
	 
	 public ArrayList<Record> allRecords(){
		 ArrayList<Record> records = new ArrayList<Record>();
		 String []columns={"restaurant_id","client_id"};
		 Cursor cursor = database.query(DatabaseHelper.CHECK_IN_TABLE, columns,
				 						null, null, null, null, null);
		 cursor.moveToFirst();
		 while (!cursor.isAfterLast()) {
			 	String restaurant_id = cursor.getString(0);
		    	String client_id = cursor.getString(1);
		    	String restaurant_name = cursor.getString(2);
		    	String rank = cursor.getString(3);
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
	 
	 public Record getRecord( ){
		 Cursor cursor = database.query(DatabaseHelper.CHECK_IN_TABLE, 
				 						null, null, null, null, null, null);
		 if (cursor == null){
			 return null;
		 }
		 if (cursor.getCount() == 0)
			 return null;
		 cursor.moveToFirst();
		 String restaurant_id = cursor.getString(0);
		 String client_id = cursor.getString(1);
		 return new Record(restaurant_id, client_id);
		 
		/* RESTAURANT_ID +" TEXT NOT NULL ,"+
         CLIENT_ID+" TEXT NOT NULL ,"+
         RANK+" int  ,"+
         RESTAURANT_NAME+" TEXT ,"+
         ETA+" int ,"+
         IS_NOTIFIED+" TEXT "+
         */
	 }
	 
	 public String isChecked(String restaurant_id){
		 String where = "restaurant_id='"+restaurant_id+"'";
		 Cursor cursor = database.query(DatabaseHelper.CHECK_IN_TABLE, 
				 						null, where, null, null, null, null);
		 if (cursor.getCount() == 0)
			 return "none";
		 cursor.moveToFirst();
		 String client_id = cursor.getString(1);
		 Log.i("+++database+++", "database query, client_id is "+client_id);
		 return client_id;
	 }
}

class Record{
	String restaurant_id;
	String client_id;
	String restaurant_name;
	String rank;
	
	Record(String restaurant_id, String client_id, String restaurant_name, String rank){
		this.restaurant_id = restaurant_id;
		this.client_id = client_id;
		this.restaurant_name = restaurant_name;
		this.rank = rank;
	}
	Record(String restaurant_id, String client_id){
		this.restaurant_id = restaurant_id;
		this.client_id = client_id;
	}
	
}