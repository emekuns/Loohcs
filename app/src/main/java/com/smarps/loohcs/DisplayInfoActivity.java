package com.smarps.loohcs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class DisplayInfoActivity extends ActionBarActivity {
	LocalDatabase localDatabase;

	private TextView passwordTxt, usernameTxt, emailTxt;
	private Button logOutButton;
	
	
	public static final String TAG = DisplayInfoActivity.class.getSimpleName();
	
	public static final int TAKE_VIDEO_REQUEST = 0;
	public static final int PICK_VIDEO_REQUEST = 1;
	
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	protected Uri mMediaUri;
	
	protected DialogInterface.OnClickListener mDialogListener = 
			new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch(which) {
						case 0: // Take video
							Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
							mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
//							if (mMediaUri == null) {
//								// display an error
//								Toast.makeText(DisplayInfoActivity.this, R.string.error_external_storage,
//									Toast.LENGTH_LONG).show();
//							}
//							else {
								videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
								videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
								videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // 0 = lowest res
								startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
//							}
							break;
						case 1: // Choose video
							break;
					}
				}
				
				private Uri getOutputMediaFileUri(int mediaType) {
					// To be safe, you should check that the SDCard is mounted
				    // using Environment.getExternalStorageState() before doing this.
					if (isExternalStorageAvailable()) {
						// get the URI
						
						// 1. Get the external storage directory
						String appName = DisplayInfoActivity.this.getString(R.string.app_name);
						File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
								appName);
						
						// 2. Create our subdirectory
						if (! mediaStorageDir.exists()) {
							if (! mediaStorageDir.mkdirs()) {
								Log.e(TAG, "Failed to create directory.");
								return null;
							}
						}
						
						// 3. Create a file name
						// 4. Create the file
						File mediaFile;
						Date now = new Date();
						String timestamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(now);
						
						String path = mediaStorageDir.getPath() + File.separator;
						
						if (mediaType == MEDIA_TYPE_VIDEO) {
							mediaFile = new File(path + "VID_" + timestamp + ".mp4");
						}
						else {
							return null;
						}
						
						Log.d(TAG, "File: " + Uri.fromFile(mediaFile));
						
						// 5. Return the file's URI				
						return Uri.fromFile(mediaFile);
					}
					else {
						return null;
					}
				}
				
				private boolean isExternalStorageAvailable() {
					String state = Environment.getExternalStorageState();
					
					if (state.equals(Environment.MEDIA_MOUNTED)) {
						return true;
					}
					else {
						return false;
					}
				}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_info);
		
		passwordTxt = (TextView)findViewById(R.id.TFpassword);
		usernameTxt = (TextView)findViewById(R.id.TFusername);
		emailTxt = (TextView)findViewById(R.id.TFEmail);
		
		localDatabase = new LocalDatabase(this);
		
		logOutButton = (Button) findViewById(R.id.logOutButton);
		logOutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	localDatabase.clearData();
            	localDatabase.setUserLoggedIn(false);
            	
            	Intent intent = new Intent(DisplayInfoActivity.this, LogInActivity.class);
            	startActivity(intent);
            }
        });
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (authenticate() == true) {
			displayUserDetails();
		} else {
			Intent intent = new Intent(DisplayInfoActivity.this, LogInActivity.class);
        	startActivity(intent);
		}
	}
	
	private boolean authenticate()
	{
		return localDatabase.getUserLoggedIn();
	}
	
	private void displayUserDetails() {
		User user = localDatabase.getLoggedInUser();
		usernameTxt.setText(user.username);
		emailTxt.setText(user.email);
		passwordTxt.setText(user.password);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_info, menu);
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
		} else if (id == R.id.action_camera) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setItems(R.array.camera_choices, mDialogListener);
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		return super.onOptionsItemSelected(item);
	}
}
