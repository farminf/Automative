package com.xmpp_android.activities;

import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException.NotConnectedException;

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
import android.widget.Toast;

import com.xmpp_android.R;
import com.xmpp_android.settings.Connection_Setting;
import com.xmpp_android.xmpp.ServiceXMPP;

public class MainActivity extends Activity {

	SharedPreferences sharedPref;
	String username;
	String password;
	String serveraddress;
	int serverport;
	ServiceXMPP openFireConnection;
	Button btnconnectToServer;
	Button btnSendMessage;
	Button btnDisconnectFromServer;
	String AddressedUser;
	String domain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		domain = getString(R.string.domain_name);

		// Required for using aSmack - also add the DNSjava jar fire in library
		SmackAndroid.init(MainActivity.this);
		// Getting User and Server Configuration from Setting shared Preferences
		getSharedPreference();

		// button connect and it's listener
		btnconnectToServer = (Button) findViewById(R.id.btnconnectToServer);
		btnconnectToServer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// AGAIN Getting User and Server Configuration from Setting
				// shared Preferences if users has changed it
				getSharedPreference();
				// Connecting to Openfire Server
				connectToOpenFireServer();

			}
		});

		// button Send and it's listener
		btnSendMessage = (Button) findViewById(R.id.btnsendmsg);
		btnSendMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AddressedUser = "android2";
				try {
					sendMessage(AddressedUser);
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// button Disconnect and it's listener
		btnSendMessage = (Button) findViewById(R.id.btnDisconnectFromServer);
		btnSendMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					disconnectFromOpenFireServer();
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	// End Of OnCreate
	// ********************************************************************

	// function which reads parameters that has set in settings
	private void getSharedPreference() {
		// TODO Auto-generated method stub
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		username = sharedPref.getString("textUsername", "");
		password = sharedPref.getString("textPassword", "");
		serveraddress = sharedPref.getString("textServerAddress", "");
		serverport = 5222;
		Log.d("Settings", username + password + serveraddress + serverport);
	}

	// function of connecting to XMPP Server
	private void connectToOpenFireServer() {

		// TODO Auto-generated method stub
		openFireConnection = new ServiceXMPP(serveraddress, username, password,
				domain);
		// openFireConnection.connect();
		if (openFireConnection.connect())

			Toast.makeText(getApplicationContext(),
					"you are connected to server", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getApplicationContext(),
					"you are NOT connected to server", Toast.LENGTH_SHORT)
					.show();

	}
	//function to Disconnect from openFire
	protected void disconnectFromOpenFireServer() throws NotConnectedException {
		// TODO Auto-generated method stub
		openFireConnection.disconnect();
	}

	// function of sending message to addressed user
	private void sendMessage(String addressedUser2)
			throws NotConnectedException {
		// TODO Auto-generated method stub
		openFireConnection.chat(addressedUser2);
	}

	// Menu and Setting functions
	// -----------------------------------------------
	// ---------------------------------------------------------------------------
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
		case R.id.exit:
			finish();
			System.exit(0);
			return true;
		default:
			break;
		}
		return false;

	}
}
