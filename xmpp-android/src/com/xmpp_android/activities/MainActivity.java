package com.xmpp_android.activities;

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

import com.xmpp_android.R;
import com.xmpp_android.settings.About;
import com.xmpp_android.settings.Connection_Setting;
import com.xmpp_android.xmpp.XMPP;
import com.xmpp_android.xmpp.XMPPService;

public class MainActivity extends Activity {

	SharedPreferences sharedPref;
	String username;
	String password;
	String serveraddress;
	int serverport;
	XMPP openFireConnection;
	Button btnconnectToServer;
	Button btnSendMessage;
	Button btnDisconnectFromServer;
	Button btnAddRoster;
	Button btnStartService;
	String AddressedUser;
	String domain;
	String SendMessage;
	String rosterUsernameToAdd;
	String rosterNickNameToAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		domain = getString(R.string.domain_name);

		// Getting User and Server Configuration from Setting shared Preferences
		getSharedPreference();

		btnStartService = (Button) findViewById(R.id.btnStartService);
		btnStartService.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Start the Service
				Intent serviceIntent = new Intent(getBaseContext(),
						XMPPService.class);
				startService(serviceIntent);
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
					SendMessage = "Hey man";
					XMPPService.sendMessage(AddressedUser, SendMessage);
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// button Disconnect and it's listener
		btnDisconnectFromServer = (Button) findViewById(R.id.btnDisconnectFromServer);
		btnDisconnectFromServer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent serviceIntent = new Intent(getBaseContext(),
						XMPPService.class);
				stopService(serviceIntent);
			}
		});

		// button Add Roster and it's listener
		btnAddRoster = (Button) findViewById(R.id.btnAddRoster);
		btnAddRoster.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					rosterUsernameToAdd = "android2";
					rosterNickNameToAdd = "mac";
					XMPPService.addRosterEntry(rosterUsernameToAdd , rosterNickNameToAdd);
				} catch (Exception e) {
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
		Log.d("Settings", username + "," + password + "," + serveraddress + ":"
				+ serverport);
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
			Intent ConnectionSettingIntent = new Intent(this,
					Connection_Setting.class);
			startActivity(ConnectionSettingIntent);
			return true;
		case R.id.about:
			Intent aboutIntent = new Intent(this, About.class);
			startActivity(aboutIntent);
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
