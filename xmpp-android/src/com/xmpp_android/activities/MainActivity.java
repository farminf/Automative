package com.xmpp_android.activities;


import org.jivesoftware.smack.SmackAndroid;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.xmpp_android.R;
import com.xmpp_android.settings.Connection_Setting;
import com.xmpp_android.xmpp.xmppConnect;

public class MainActivity extends Activity {
	
	SharedPreferences sharedPref;
	String username;
	String password;
	String serveraddress;
	int serverport;
	xmppConnect openFireConnection;
	Button btnconnectToServer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Required for using aSmack - also add the DNSjava jar fire in library
		SmackAndroid.init(MainActivity.this);
					
		getSharedPreference();
		
		btnconnectToServer = (Button) findViewById(R.id.btnconnectToServer);
		btnconnectToServer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//Getting User and Server Configuration from Setting shared Preferences
				getSharedPreference();				
				//Connecting to Openfire Server
				connectToOpenFireServer();			
			}
		});
		
	}
	
	//Fuction which reads parameters that has set in settings
	private void getSharedPreference() {
		// TODO Auto-generated method stub
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		username = sharedPref.getString("textUsername", "");
		password = sharedPref.getString("textPassword", "");
		serveraddress = sharedPref.getString("textServerAddress", "");
		serverport = 5222 ;
		Log.d("log", username + password + serveraddress + serverport);	 
	}
	
	//function of connecting to XMPP Server
	private void connectToOpenFireServer() {
		// TODO Auto-generated method stub
		openFireConnection = new xmppConnect (serveraddress, username , password);
		openFireConnection.connect();
		
		
	}
	// Menu and Setting functions -----------------------------------------------
	//---------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, Connection_Setting.class);
			startActivity(intent);
			return true;
		case R.id.about:
			return true;
		default:
			break;
		}
		return false;

	}
}
