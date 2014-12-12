package com.xmpp_android.xmpp;

import org.jivesoftware.smack.SmackException.NotConnectedException;

import com.xmpp_android.R;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class XMPPService extends Service {

	static XMPP openFireConnection;

	SharedPreferences sharedPref;

	int notificationID = 111;

	String username;
	String password;
	String serveraddress;
	String domain;
	int serverport = 5222;

	// Service Methods
	// ************************************************************
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		// super.onCreate();

		// run service on seperate thread
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		getSharedPreference();

		openFireConnection = new XMPP(serveraddress, username, password, domain);
		if (openFireConnection.connect())

			Toast.makeText(getApplicationContext(),
					"you are connected to server", Toast.LENGTH_LONG).show();
		else
			Toast.makeText(getApplicationContext(),
					"you are NOT connected to server", Toast.LENGTH_LONG)
					.show();

		// Notification
		createNotificationIcon();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "OpenFire Service Started", Toast.LENGTH_LONG)
				.show();
		return START_STICKY;
		// return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Toast.makeText(this, "OpenFire Service Destroyed", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	// *************************************************************************
	// *************************************************************************
	private void getSharedPreference() {
		// TODO Auto-generated method stub
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		username = sharedPref.getString("textUsername", "");
		password = sharedPref.getString("textPassword", "");
		serveraddress = sharedPref.getString("textServerAddress", "");
		domain = getString(R.string.domain_name);
		Log.d("Settings", username + password + serveraddress + serverport
				+ domain);
	}

	private void createNotificationIcon() {
		// TODO Auto-generated method stub
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("XMPP Service")
				.setContentText(
						"this service is for keep connecting to openfire server!");
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notificationID, mBuilder.build());
	}

	// function to send message to addressed user
	public static void sendMessage(String addressedUser2, String sendmsg)
			throws NotConnectedException {
		// TODO Auto-generated method stub
		openFireConnection.chat(addressedUser2, sendmsg);
	}

	// function to Disconnect from openFire
	public static void disconnectFromOpenFireServer() throws NotConnectedException {
		// TODO Auto-generated method stub
		openFireConnection.disconnect();
	}

	// function to add user in roster
	public static void addRosterEntry() throws Exception {
		// TODO Auto-generated method stub
		openFireConnection.createEntry("android2@farmin.virtus.it", "mac");
	}
}
