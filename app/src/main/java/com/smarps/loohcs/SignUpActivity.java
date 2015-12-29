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
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class SignUpActivity extends ActionBarActivity {
	
	private Button signUpBtn;
	private EditText passwordTxt, usernameTxt, emailTxt, confirmPasswordTxt;
	LocalDatabase localDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
        
        localDatabase = new LocalDatabase(this);
		
		passwordTxt = (EditText)findViewById(R.id.signUpPassword);
		confirmPasswordTxt = (EditText)findViewById(R.id.signUpConfirmPassword);
		usernameTxt = (EditText)findViewById(R.id.signUpUsername);
		emailTxt = (EditText)findViewById(R.id.signUpEmail);
		
	}
	
	public void onRegisterClick(View view) {
        String email = emailTxt.getText().toString();
        String username = usernameTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        String confirmPassword = confirmPasswordTxt.getText().toString();

        if(password.equals(confirmPassword)) {
            final User user = new User(username, email, password);
            ServerRequests serverRequests = new ServerRequests(this);
            serverRequests.storeDataInBackground(user, new GetUserCallback() {
                @Override
                public void done(User returnedUser) {
                	authenticate(user);
                }
            });
        } else {
            Toast.makeText(this, "Passwords don't match! Enter again!", Toast.LENGTH_LONG).show();
        }
    }
	
	
	private void authenticate(User user) {
    	ServerRequests serverRequests = new ServerRequests(this);
    	serverRequests.fetchDataInBackground(user, new GetUserCallback() {
    		@Override
    		public void done(User returnedUser) {
    			if (returnedUser == null) {
    				AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
    				builder.setMessage("Username & Password don't match!");
    				builder.setPositiveButton("OK", null);
    				builder.show();
    			}
    			else {
    				localDatabase.storeData(returnedUser);
    				localDatabase.setUserLoggedIn(true);
    				
    				Intent intent = new Intent(SignUpActivity.this, DisplayInfoActivity.class);
                	startActivity(intent);
    			}
    		}
    	});
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
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
