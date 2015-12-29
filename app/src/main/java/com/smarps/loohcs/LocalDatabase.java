package com.smarps.loohcs;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalDatabase {
	public static final String SP_NAME = "UserDetails";
	SharedPreferences localDatabase;
	
	public LocalDatabase(Context context) {
		localDatabase = context.getSharedPreferences(SP_NAME, 0);
	}
	
	public void storeData(User user) {
		SharedPreferences.Editor spEditor = localDatabase.edit();
		spEditor.putString("Username", user.username);
		spEditor.putString("Email", user.email);
		spEditor.putString("Password", user.password);
		spEditor.commit();
	}
	
	public User getLoggedInUser() {
		String username = localDatabase.getString("Username", "");
		String email = localDatabase.getString("Email", "");
		String password = localDatabase.getString("Password", "");
		
		User storedUser = new User(username, email, password);
		return storedUser;
	}
	
	public void setUserLoggedIn(boolean loggedIn) {
		SharedPreferences.Editor spEditor = localDatabase.edit();
		spEditor.putBoolean("loggedIn", loggedIn);
		spEditor.commit();
	}
	
	public boolean getUserLoggedIn() {
		if (localDatabase.getBoolean("loggedIn", false)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void clearData() {
		SharedPreferences.Editor spEditor = localDatabase.edit();
		spEditor.clear();
		spEditor.commit();
	}
}
