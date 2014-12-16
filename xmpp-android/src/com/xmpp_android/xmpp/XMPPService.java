package com.xmpp_android.xmpp;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException.NotConnectedException;

import com.xmpp_android.R;
import com.xmpp_android.activities.MainActivity;

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

	private static SmackAndroid asmk = null;

	static XMPP openFireConnection;

	SharedPreferences sharedPref;

	int notificationID = 111;

	String username;
	String password;
	String serveraddress;
	String domain;
	int serverport = 5222;
	// Schedule job parameters
	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);
	static ScheduledFuture senderHandle;

	static NotificationManager mNotificationManager;

	// Service Methods
	// ************************************************************
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		// super.onCreate();

		// Required for using aSmack - also add the DNSjava jar fire in library
		asmk = SmackAndroid.init(XMPPService.this);

		// run service on seperate thread
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		getSharedPreference();

		openFireConnection = new XMPP(serveraddress, username, password, domain);
		if (openFireConnection.connect()) {

			Toast.makeText(getApplicationContext(),
					"you are connected to server", Toast.LENGTH_LONG).show();
			// Notification
			createNotificationIcon();
			// Schedule function call
			// sendForAnHour();
		} else
			Toast.makeText(getApplicationContext(),
					"you are NOT connected to server", Toast.LENGTH_LONG)
					.show();

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
		// Disconnecting from Server
		try {
			disconnectFromOpenFireServer();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("Disconnection Error", e.getMessage());

		}
		// Deleting all notifications
		try {
			cancellAllNotifications();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("Notification Error", e.getMessage());
		}
		// Cancel periodic job
		// sendForAnHourCancel();

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
		Log.d("Settings in Service", username + "," + password + ","
				+ serveraddress + ":" + serverport + "," + domain);
	}

	private void createNotificationIcon() {
		// TODO Auto-generated method stub
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("XMPP Service")
				.setContentText(
						"this service is for keep connecting to openfire server!");
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notificationID, mBuilder.build());

	}

	// function to send message to addressed user
	public static void sendMessage(String addressedUser2, String sendmsg)
			throws NotConnectedException {
		// TODO Auto-generated method stub
		openFireConnection.chat(addressedUser2, sendmsg);
	}

	// function to Disconnect from openFire
	public static void disconnectFromOpenFireServer()
			throws NotConnectedException {
		// TODO Auto-generated method stub
		openFireConnection.disconnect();
		if (asmk != null) {
			asmk.onDestroy();
			asmk = null;
		}
	}

	// function to remove notifications icon from bars
	public static void cancellAllNotifications() throws NotConnectedException {
		// TODO Auto-generated method stub
		mNotificationManager.cancelAll();
	}

	// function to add user in roster
	public static void addRosterEntry(String rosterNameToAdd , String rosterNickNameToAdd) throws Exception {
		// TODO Auto-generated method stub
		openFireConnection.createEntry(rosterNameToAdd, rosterNickNameToAdd);

	}

	// Function to do something periodically
	public void sendForAnHour() {

		final Runnable sender = new Runnable() {
			public void run() {
				System.out.println("sent");
				Log.d("F", "sendF");
			}
		};

		senderHandle = scheduler.scheduleAtFixedRate(sender, 10, 10, SECONDS);

		scheduler.schedule(new Runnable() {
			public void run() {
				senderHandle.cancel(true);
			}
		}, 60 * 60, SECONDS);
	}

	// Function for cancel periodic job
	public static void sendForAnHourCancel() {

		senderHandle.cancel(true);
		System.out.println("schedule job cancelled");

	}
}
