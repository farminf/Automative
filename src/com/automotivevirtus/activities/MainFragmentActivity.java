package com.automotivevirtus.activities;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.automotivevirtus.R;
import com.automotivevirtus.location.LocationService;
import com.automotivevirtus.location.UpdateLocationService;
import com.automotivevirtus.settings.About;
import com.automotivevirtus.settings.Connection_Setting;
import com.automotivevirtus.xmpp.XMPPHelper;
import com.automotivevirtus.xmpp.XMPPService;

@SuppressWarnings("deprecation")
public class MainFragmentActivity extends FragmentActivity implements
		ActionBar.TabListener {

	ViewPager Tab;
	FragmentPageAdapter TabAdapter;

	SharedPreferences sharedPref;
	SharedPreferences.Editor SharedPrefEditor;

	String username;
	String password;
	String serveraddress;
	int serverport;
	int currentFrag;

	AlertDialog.Builder noNetDialog;
	AlertDialog.Builder noXMPPDialog;

	String[] incomingMessage;
	String incomingMessageSender;
	String incomingMessageBody;

	ProgressDialog progressDialog;

	LocalBroadcastManager mLocalBroadcastManager;

	Boolean isXMPPConnected = false;

	LocationService currentLocation;
	double currentLatitude;
	double currentLongitude;
	String curLat;
	String curLong;

	// Schedule job parameters
	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);
	static ScheduledFuture<?> senderHandle;
	// Maybe this is correct
	// static ScheduledFuture senderHandle;

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// do something based on the intent's action
			String action = intent.getAction();
			if (action.equals("ShowProgressBar")) {
				Log.d("info", "ShowProgressBar received");

				showProgressBar();

			} else if (action.equals("DismissProgressBar")) {
				Log.d("info", "DismissProgressBar received");

				dismissProgressBar();

			} else if (action.equals("XMPPConnected")) {
				Log.d("info", "XMPPConnected received");

				isXMPPConnected = true;

				SharedPrefEditor = sharedPref.edit();
				SharedPrefEditor.putBoolean("isConnectedXMPPServer", true);
				SharedPrefEditor.commit();

				// startUpdateLocationService();
				sendForAnHour();

			} else if (action.equals("NoXMPPDialog")) {
				Log.d("info", "NoXMPPDialog open");
				noXMPPDialog.show();

			} else if (action.equals("receivedMessage")) {
				Log.d("info", "receivedMessage Broadcast Received");
				incomingMessage = XMPPHelper.getReceivedMessage();
				incomingMessageSender = incomingMessage[0];
				incomingMessageBody = incomingMessage[1];
				Log.d("incoming message got", incomingMessageSender + ":"
						+ incomingMessageBody);

			} else {
				Log.d("info", "something else received");

			}
			Log.d("info", "one Broadcast received");
		}
	};

	// ---------------------------------------------------------------
	// ----------------------On Create ----------------------------------
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// create Alert Dialog for not having any network connectivity
		noConnectionDialog();

		// Creating Alert for unavailability of XMPP Server
		noXMPPDialog();

		// Check for Network State
		final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();

		// -------------------------------------------------------------

		// Getting User and Server Configuration from Setting shared Preferences

		getSharedPreference();

		// Tab Bar Creation-----------------------------------------
		// -----------------------------------------------------
		TabAdapter = new FragmentPageAdapter(getSupportFragmentManager());
		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		// actionBar.setDisplayHomeAsUpEnabled(true);

		// Specify that we will be displaying tabs in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		Tab = (ViewPager) findViewById(R.id.pager);
		Tab.setAdapter(TabAdapter);
		Tab.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

				actionBar.setSelectedNavigationItem(position);

			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText(R.string.first_tab)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.second_tab)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.third_tab)
				.setTabListener(this));

		if (activeNetwork != null && activeNetwork.isConnected()) {
			// We have Network Connectivity

			// after refreshing if we already have connection or we're fresh
			// starting?

			if (isXMPPConnected) {
				Log.d("!", "We Are Already connected");
				// Do nothing
			} else {
				// Starting XMPP Service to connect to Server
				startXMPPService();
			}

		} else {

			noNetDialog.show();
		}

	}

	// ----------- On Pause ------------------------
	// ------------------------------------------------------

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LocalBroadcastManager.getInstance(getApplicationContext())
				.unregisterReceiver(broadcastReceiver);
		Log.d("info", "Broadcast Unregistered in onPause");
	}

	// ----------- On Resume ------------------------
	// ------------------------------------------------------

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// Register the Broadcast Receiver
		IntentFilter filter = new IntentFilter();

		filter.addAction("ShowProgressBar");
		filter.addAction("DismissProgressBar");
		filter.addAction("NoXMPPDialog");
		filter.addAction("receivedMessage");
		filter.addAction("XMPPConnected");

		registerReceiver(broadcastReceiver, filter);
		Log.d("info", "Broadcast registered");

		// get isConnected from sharedPreferences, for checking if we are
		// already connected or not
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		isXMPPConnected = sharedPref.getBoolean("isConnectedXMPPServer", false);
		String isc = isXMPPConnected.toString();
		Log.d("isConnected SharedPref", isc);

	}

	// -----------------------------------------------
	// ------------------on Stop-----------------------------------
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
//		LocalBroadcastManager.getInstance(getApplicationContext())
//				.unregisterReceiver(broadcastReceiver);
//		Log.d("info", "Broadcast Unregistered in onStop");

	}

	// -----------------------------------------------
	// ------------------on Destroy-----------------------------------
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LocalBroadcastManager.getInstance(getApplicationContext())
				.unregisterReceiver(broadcastReceiver);
		Log.d("info", "Broadcast Destroyed");

	}

	// ------------------------------------------------------

	@Override
	public void onTabSelected(android.app.ActionBar.Tab tab,
			FragmentTransaction ft) {
		// TODO Auto-generated method stub
		Tab.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(android.app.ActionBar.Tab tab,
			FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(android.app.ActionBar.Tab tab,
			FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	// -----------------------------------------------------------------
	// ------------Functions--------------------------------------------
	private void startXMPPService() {
		// TODO Auto-generated method stub
		Intent serviceIntent = new Intent(getApplicationContext(),
				XMPPService.class);
		startService(serviceIntent);
		// Intent serviceIntent = new Intent(getApplicationContext(),
		// ServiceXMPP.class);
		// startService(serviceIntent);
		// showProgressBar();

	}

	private void startUpdateLocationService() {
		// TODO Auto-generated method stub
		Intent serviceIntent = new Intent(getApplicationContext(),
				UpdateLocationService.class);
		startService(serviceIntent);

	}

	private void getSharedPreference() {
		// TODO Auto-generated method stub
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		username = sharedPref.getString("textUsername", "");
		password = sharedPref.getString("textPassword", "");
		serveraddress = sharedPref.getString("textServerAddress", "");
		serverport = 5222;

		isXMPPConnected = sharedPref.getBoolean("isConnectedXMPPServer", false);

		Log.d("Settings", username + "," + password + "," + serveraddress + ":"
				+ serverport);

	}

	public void showProgressBar() {

		progressDialog = new ProgressDialog(Tab.getContext());
		progressDialog.setMessage("Trying to connect...");
		progressDialog.setTitle("Connecting to XMPPServer");
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(true);
		progressDialog.show();

	}

	public void dismissProgressBar() {
		progressDialog.dismiss();
	}

	public void noConnectionDialog() {
		// Alert Dialog for not having any network connectivity
		// -------------------------------
		noNetDialog = new AlertDialog.Builder(this);
		noNetDialog
				.setMessage(R.string.no_network_message)
				.setPositiveButton(R.string.mobile_setting,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User clicked setting button
								// Mobile setting will show up
								startActivityForResult(
										new Intent(
												android.provider.Settings.ACTION_SETTINGS),
										0);
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog

							}
						});
		// Create the AlertDialog
		noNetDialog.create();
	}

	public void noXMPPDialog() {
		// Alert Dialog for not finding XMPP server to connect
		// -------------------------------
		noXMPPDialog = new AlertDialog.Builder(this);
		noXMPPDialog
				.setMessage(R.string.no_xmpp_message)
				.setPositiveButton(R.string.server_setting,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								startActivityForResult(new Intent(
										getApplicationContext(),
										Connection_Setting.class), 0);
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog

							}
						});
		// Create the AlertDialog
		noXMPPDialog.create();
	}

	public void sendForAnHour() {

		final Runnable sender = new Runnable() {
			public void run() {
				Log.d("F", "sendF");
				startUpdateLocationService();
				// System.out.println("sent");
				// Log.d("F", "sendF");
			}
		};

		senderHandle = scheduler.scheduleAtFixedRate(sender, 30, 60, SECONDS);

		scheduler.schedule(new Runnable() {
			public void run() {
				senderHandle.cancel(true);
			}
		}, 60 * 60, SECONDS);
	}

	// Function for cancel periodic job
	public void sendForAnHourCancel() {

		senderHandle.cancel(true);
		System.out.println("schedule job cancelled");

	}

	private void getCurrentLocation() {
		// TODO Auto-generated method stub
		currentLocation = new LocationService(getApplicationContext());
		if (currentLocation.canGetLocation()) {
			currentLatitude = currentLocation.getLatitude();
			currentLongitude = currentLocation.getLongitude();

			curLat = String.valueOf(currentLatitude);
			curLong = String.valueOf(currentLongitude);

			Log.v("Location", "lat:" + curLat + " long: " + curLong);

		} else {
			// GPS or Network no available and ask user to turn on in setting
			currentLocation.showSettingsAlert();
		}

	}

	// Setting Menu -------------------------------------
	// --------------------------------------------------

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
			return true;

		case R.id.action_settings:
			// Go to Preference setting
			Intent ConnectionSettingIntent = new Intent(this,
					Connection_Setting.class);
			startActivity(ConnectionSettingIntent);
			return true;

		case R.id.refresh:
			// Restart the main activity
			finish();
			Intent refresh = new Intent(this, MainFragmentActivity.class);
			startActivity(refresh);
			return true;

		case R.id.wifi_setting:
			Intent WifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
			startActivity(WifiIntent);
			return true;

		case R.id.about:
			Intent aboutIntent = new Intent(this, About.class);
			startActivity(aboutIntent);
			return true;

		case R.id.exit:
			SharedPrefEditor = sharedPref.edit();
			SharedPrefEditor.putBoolean("isConnectedXMPPServer", false);
			SharedPrefEditor.commit();

			if (isXMPPConnected) {
				XMPPHelper.stopXMPPService();
				sendForAnHourCancel();
			}

			finish();
			System.exit(0);
			return true;

		default:
			break;
		}
		return false;

	}
}