package com.smarps.loohcs;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ServerRequests {
	ProgressDialog progressDialog;
	public static final int CONNECTION_TIMEOUT = 15000;
	public static final String SERVER_ADDRESS = "http://smarps.net78.net/";
	
	public ServerRequests(Context context) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Processing");
		progressDialog.setMessage("Please wait..");
	}
	
	public void storeDataInBackground(User user, GetUserCallback callback) {
		progressDialog.show();
		new StoreDataAsyncTask(user, callback).execute();
	}
	
	public void fetchDataInBackground(User user, GetUserCallback callback) {
		progressDialog.show();
		new FetchDataAsyncTask(user, callback).execute();
	}
	
	public class StoreDataAsyncTask extends AsyncTask<Void, Void, Void>{
		User user;
		GetUserCallback callback;
		
		public StoreDataAsyncTask(User user, GetUserCallback callback) {
			this.user = user;
			this.callback = callback;
		}

		@Override
		protected Void doInBackground(Void... params) {
			ArrayList<NameValuePair> data_to_send = new ArrayList<NameValuePair>();
			data_to_send.add(new BasicNameValuePair("Username", user.username));
			data_to_send.add(new BasicNameValuePair("Email", user.email));
			data_to_send.add(new BasicNameValuePair("Password", user.password));
			
			HttpParams httpRequestParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);
			
			HttpClient client = new DefaultHttpClient(httpRequestParams);
			HttpPost post = new HttpPost(SERVER_ADDRESS + "Register.php");
			
			try {
				post.setEntity(new UrlEncodedFormEntity(data_to_send));
				client.execute(post);
			} catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			callback.done(null);
			super.onPostExecute(result);
		}
	}
	
	public class FetchDataAsyncTask extends AsyncTask<Void, Void, User> {
		User user;
		GetUserCallback callback;
		
		public FetchDataAsyncTask(User user, GetUserCallback callback) {
			this.user = user;
			this.callback = callback;
		}
		
		@Override
		protected User doInBackground(Void... params) {
			ArrayList<NameValuePair> data_to_send = new ArrayList<NameValuePair>();
			data_to_send.add(new BasicNameValuePair("Username", user.username));
			data_to_send.add(new BasicNameValuePair("Password", user.password));
			
			HttpParams httpRequestParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);
			
			HttpClient client = new DefaultHttpClient(httpRequestParams);
			HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchUserData.php");
			
			User returnedUser = null;
			try {
				post.setEntity(new UrlEncodedFormEntity(data_to_send));
				HttpResponse httpResponse = client.execute(post);
				
				HttpEntity entity = httpResponse.getEntity();
				String result = EntityUtils.toString(entity);
				
				JSONObject jsonObject = new JSONObject(result);
				
				returnedUser = null;
				if (jsonObject.length() == 0) {
					returnedUser = null;
				} else {
					String username = null;
					String email = null;
					
					if (jsonObject.has("username")) {
						username = jsonObject.getString("username");
					}
					if (jsonObject.has("email")) {
						email = jsonObject.getString("email");
					}
					
					returnedUser = new User(username, email, user.password);
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			return returnedUser;
		}

		@Override
		protected void onPostExecute(User returnedUser) {
			progressDialog.dismiss();
			callback.done(returnedUser);
			
			super.onPostExecute(returnedUser);
		}
	}
}
