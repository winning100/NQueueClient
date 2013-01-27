package com.cisco.nqueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TalkToServer {

	private String server_address; // http server address
	private String client_id;
	private String restaurant_id;
	private String phone;

	HttpClient httpClient = new DefaultHttpClient();

	public TalkToServer(String server_address, String client_id,
			String restaurant_id, String phone) {
		this.server_address = server_address;
		this.client_id = client_id;
		this.restaurant_id = restaurant_id;
		this.phone = phone;
	}

	public TalkToServer(String server_address, String restaurant_id,
			String phone) {
		this.server_address = server_address;
		this.restaurant_id = restaurant_id;
		this.phone = phone;
		this.client_id = "none";
	}

	public TalkToServer() {

		this.client_id = "none";
	}

	public void setClientId(String clientId) {
		this.client_id = clientId;
	}

	public void setRestaurantId(String restaurant_id) {
		this.restaurant_id = restaurant_id;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setWebServer(String serverAddr) {
		this.server_address = serverAddr;
	}

	public void set(String server_address, String restaurant_id, String phone) {
		this.server_address = server_address;
		this.restaurant_id = restaurant_id;
		if (phone != "none") {
			this.phone = phone;
		}
	}

	/**
	 * check in to the specified restaurant
	 * 
	 * @return
	 */
	String check_in() {
	
		//Log.i("real address", server_address + "check_in");
		List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
		postData.add(new BasicNameValuePair("phone_number", phone));
		postData.add(new BasicNameValuePair("restaurant_id", restaurant_id));
		Log.i("+++post data+++", "phone_number: "+phone);
		Log.i("+++post data+++", "restaurant_id: "+restaurant_id);
		return getServerInfo("check_in", postData);
		// Log.i("+++check_in+++", "get(0) is "+result.get(0).trim());

		}

	String check_in_nfc() {
		// HttpClient httpClient = new DefaultHttpClient();
		// HttpPost httppost = new HttpPost(server_address+"/check_in.php");
		Log.i("real address", server_address + "check_in_nfc");
		List<NameValuePair> postData = new ArrayList<NameValuePair>(3);
		// postData.add(new BasicNameValuePair("client_id", client_id));
		postData.add(new BasicNameValuePair("client_id", client_id));
		postData.add(new BasicNameValuePair("restaurant_id", restaurant_id));
		postData.add(new BasicNameValuePair("phone_number", phone));
		return getServerInfo("check_in_nfc", postData);
		// Log.i("+++check_in+++", "get(0) is "+result.get(0).trim());
		}

	String check_out() {
		Log.i("real address", server_address + "check_out");
		List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
		postData.add(new BasicNameValuePair("client_id", client_id));
		postData.add(new BasicNameValuePair("restaurant_id", restaurant_id));

		return getServerInfo("check_out", postData);
	}

	/*String quit() {
		Log.i("real address", server_address + "/check_out");
		List<NameValuePair> postData = new ArrayList<NameValuePair>(3);
		postData.add(new BasicNameValuePair("client_id", String
				.valueOf(client_id)));
		postData.add(new BasicNameValuePair("restaurant_id", String
				.valueOf(restaurant_id)));
		postData.add(new BasicNameValuePair("force", "true"));
		return getServerInfo("check_out", postData);
	}*/

	String queryRank() {
		// HttpPost httppost = new HttpPost(server_address+"/update_rank.php");
		Log.i("real address", server_address + "/get_rank");
		List<NameValuePair> postData = new ArrayList<NameValuePair>(3);
		postData.add(new BasicNameValuePair("client_id", String
				.valueOf(client_id)));
		postData.add(new BasicNameValuePair("restaurant_id", String
				.valueOf(restaurant_id)));
		postData.add(new BasicNameValuePair("phone_number", phone));
		return getServerInfo("get_rank", postData);
	}

	// the server return a json formatted string
	String getServerInfo(String method, List<NameValuePair> postData) {
		Log.i("+++requested address+++", server_address+method);
		HttpPost httppost = new HttpPost(server_address + method);
		HttpResponse response = null;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(postData));
			response = httpClient.execute(httppost);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder builder = new StringBuilder();
		try {
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					content));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String jsonString = builder.toString();
		Log.i("json", "json string: "+jsonString);
		return jsonString;
			}

/*	public void outputDebug(String method, JSONArray resultArray) {
		try {
			if (method == "check_in" || method == "get_rank") {
				String revclientID = resultArray.getJSONObject(0)
						.getString("client_id").toString();
				Log.v("+++check_in+++", "client_id: " + revclientID);
				client_id = revclientID;
				setClientId(revclientID);
				Log.v("+++check_in+++",
						"restaurant_id: "
								+ resultArray.getJSONObject(1)
										.getString("restaurant_id").toString());
				Log.v("+++check_in+++", "estimated time to wait: "
						+ resultArray.getJSONObject(2).getString("eta")
								.toString());
				Log.v("+++check_in+++", "ready to serve: "
						+ resultArray.getJSONObject(3).getString("is_ready")
								.toString());
				Log.v("+++check_in+++",
						"action: "
								+ resultArray.getJSONObject(4)
										.getString("action").toString());
			} else if (method == "check_out" || method == "check_in_nfc") {
				if (resultArray.getJSONObject(0).getString("action").toString() == "success") {
					Log.v("+++" + method + "+++", method
							+ "done successfully!!");
				} else if (resultArray.getJSONObject(0).getString("action")
						.toString() == "error") {
					Log.v("+++" + method + "+++", method + "error!!");
				} else if (resultArray.getJSONObject(0).getString("action")
						.toString() == "illegal") {
					Log.v("+++" + method + "+++",
							"Sorry you get the table now, it is not ready yet!!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/*
	 * ArrayList<String> result = new ArrayList<String>(); if (entity != null) {
	 * InputStream instream = null; try { instream = entity.getContent(); result
	 * = convertStreamToString(instream); } catch (IOException e) {
	 * e.printStackTrace(); } for (int i = 0; i < result.size(); i++) {
	 * Log.i("result from server", result.get(i)); } //
	 * Log.i("Read from server", result); }
	 * 
	 * return result; }
	 */
	public ArrayList<String> convertStreamToString(InputStream inputStream)
			throws IOException {
		ArrayList<String> str_list = new ArrayList<String>();
		if (inputStream != null) {
			// StringBuilder sb = new StringBuilder();
			String line;
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					str_list.add(line);
					// sb.append(line).append("\n");
				}
			} finally {
				inputStream.close();
			}
		}
		return str_list;
	}

}