package com.automotivevirtus.xmpp;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.automotivevirtus.R;

public class XMPPService extends IntentService {

	public XMPPService() {
		super("XMPPService");
		// TODO Auto-generated constructor stub

	}

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
	static ScheduledFuture<?> senderHandle;
	// Maybe this is correct
	// static ScheduledFuture senderHandle;

	static NotificationManager mNotificationManager;

	public ProgressDialog progressDialog;

	LocalBroadcastManager mLocalBroadcastManager;

	Boolean isConnected;

	// Service Methods
	// ************************************************************
	// ------------------------------------------------------------------
	// -------------------------------On Start-------------------------

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);

		// BroadCast Manager to send broadcast
		createBroadcastMessage("ShowProgressBar");
		Log.d("sender", "Broadcasting message in start service");
	}

	// ------------------------------------------------------------------
	// -------------------------------On Handle-------------------------
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

		asmk = SmackAndroid.init(XMPPService.this);

		getSharedPreference();
		Log.d("service", "before connect in servive");

		openFireConnection = new XMPP(serveraddress, username, password, domain);
		isConnected = openFireConnection.connect();
		if (isConnected) {
			createBroadcastMessage("DismissProgressBar");
			Log.d("sender",
					"Broadcasting DismissProgressBar message because we are connected");

			Toast.makeText(getApplicationContext(),
					"you are connected to server", Toast.LENGTH_LONG).show();
			// Notification
			createNotificationIcon();
			// Schedule function call
			// sendForAnHour();
		} else {

			Toast.makeText(getApplicationContext(),
					"you are NOT connected to server", Toast.LENGTH_LONG)
					.show();
			createBroadcastMessage("NoXMPPDialog");
		}
	}

	// @Override
	// public void onCreate() {
	// // TODO Auto-generated method stub
	// // super.onCreate();
	//
	// // progressDialog = new ProgressDialog(XMPPService.this);
	// // progressDialog.setMessage("Loading..");
	// // progressDialog.setTitle("Checking Network");
	// // progressDialog.setIndeterminate(false);
	// // progressDialog.setCancelable(true);
	// // progressDialog.show();
	//
	// // Required for using aSmack - also add the DNSjava jar fire in library
	// //asmk = SmackAndroid.init(XMPPService.this);
	//
	// // run service on seperate thread
	// // HandlerThread thread = new HandlerThread("ServiceStartArguments",
	// // Process.THREAD_PRIORITY_BACKGROUND);
	// // thread.start();
	// //
	// // getSharedPreference();
	// // Log.d("service", "before connect");
	//
	// }
	//
	// // @Override
	// // public int onStartCommand(Intent intent, int flags, int startId) {
	// // // TODO Auto-generated method stub
	// // Toast.makeText(this, "OpenFire Service Started", Toast.LENGTH_LONG)
	// // .show();
	// //
	// // return START_STICKY;
	// // // return super.onStartCommand(intent, flags, startId);
	// //
	// // }
	// //
	// ------------------------------------------------------------------
	// -------------------------------On Destroy-------------------------

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		createBroadcastMessage("DismissProgressBar");
		Log.d("sender",
				"Broadcasting DismissProgressBar message in destroy service");

		if (isConnected) {

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

			Toast.makeText(this, "OpenFire Service Destroyed",
					Toast.LENGTH_LONG).show();

		} else {
			Toast.makeText(this, "Check the Server Settings", Toast.LENGTH_LONG)
					.show();
		}
	}

	// ------------------------------------------------------------------
	// -------------------------------Binder-------------------------
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
		username = sharedPref.getString("textUsername", "android");
		password = sharedPref.getString("textPassword", "android");
		serveraddress = sharedPref
				.getString("textServerAddress", "192.168.1.1");
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
	public static void addRosterEntry(String rosterNameToAdd,
			String rosterNickNameToAdd) throws Exception {
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

	// function to create a pub/sub node
	public static void createPubSubNode(String nodeName)
			throws NoResponseException, XMPPErrorException,
			NotConnectedException {

		openFireConnection.createPubSubNode(nodeName);
	}

	// function to subscribe to a node
	public static void subscribeToNode(String nodeName)
			throws NoResponseException, XMPPErrorException,
			NotConnectedException {

		openFireConnection.subscribePubSubNode(nodeName);
	}

	// function to publish to the node
	public static void publishToNode(String nodeName)
			throws NoResponseException, XMPPErrorException,
			NotConnectedException {

		openFireConnection.publishToPubSubNode(nodeName);
	}

	// Function to send AdHoc Command
	public static void sendAdhocCommand(String username, String command)
			throws XMPPException, SmackException {

		openFireConnection.sendAdHocCommands(username, command);
	}

	private void createBroadcastMessage(String action) {
		mLocalBroadcastManager = LocalBroadcastManager
				.getInstance(getApplicationContext());
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(action);
		// broadcastIntent.putExtra(whateverExtraData you need to pass back);
		sendBroadcast(broadcastIntent);
	}
}
