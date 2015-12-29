package com.smarps.loohcs;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


@SuppressWarnings("deprecation")
public class LogInActivity extends ActionBarActivity {
	private TextView registerLink;
	private Button logInButton;
	private EditText usernameTxt, passwordTxt;
	LocalDatabase localDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        
        localDatabase = new LocalDatabase(getApplicationContext());
        
     // Check if user is already logged in or not
        if (localDatabase.getUserLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LogInActivity.this, TabBar.class);
            startActivity(intent);
            finish();
        }
        
        registerLink = (TextView) findViewById(R.id.registerLink);
		registerLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            	startActivity(intent);
            }
        });
		
		
		usernameTxt = (EditText)findViewById(R.id.logInUsername);
		passwordTxt = (EditText)findViewById(R.id.logInPassword);
		logInButton = (Button) findViewById(R.id.loginButton);
		logInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	String username = usernameTxt.getText().toString();
            	String password = passwordTxt.getText().toString();
            	
            	User user = new User(username, password);
            	authenticate(user);
            	
            }
        });
    }
    
    private void authenticate(User user) {
    	ServerRequests serverRequests = new ServerRequests(this);
    	serverRequests.fetchDataInBackground(user, new GetUserCallback() {
    		@Override
    		public void done(User returnedUser) {
    			if (returnedUser == null) {
    				AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
    				builder.setMessage("Username & Password don't match!");
    				builder.setPositiveButton("OK", null);
    				builder.show();
    			}
    			else {
    				localDatabase.storeData(returnedUser);
    				localDatabase.setUserLoggedIn(true);
    				
    				Intent intent = new Intent(LogInActivity.this, TabBar.class);
                	startActivity(intent);
    			}
    		}
    	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
