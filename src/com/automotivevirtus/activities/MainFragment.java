package com.automotivevirtus.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.automotivevirtus.R;
import com.automotivevirtus.settings.About;
import com.automotivevirtus.settings.Connection_Setting;
import com.automotivevirtus.xmpp.XMPPService;

@SuppressWarnings("deprecation")
public class MainFragment extends FragmentActivity implements
		ActionBar.TabListener {

	ViewPager Tab;
	FragmentPageAdapter TabAdapter;

	SharedPreferences sharedPref;
	String username;
	String password;
	String serveraddress;
	int serverport;
	String domain;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Check for Network State
		final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {

		} else {
			Toast.makeText(this, "You're not Connected" , Toast.LENGTH_LONG).show();
		}

		domain = getString(R.string.domain_name);

		// Getting User and Server Configuration from Setting shared Preferences
		getSharedPreference();

		// Tab Bar Creation-----------------------------------------
		// -----------------------------------------------------
		TabAdapter = new FragmentPageAdapter(getSupportFragmentManager());
		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		// Specify that we will be displaying tabs in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		Tab = (ViewPager) findViewById(R.id.pager);
		Tab.setAdapter(TabAdapter);
		Tab.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When swiping between different app sections, select the
				// corresponding tab.
				// We can also use ActionBar.Tab#select() to do this if we have
				// a reference to the
				// Tab.
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
		// ------------------------------------------------------
		// Start the Service
		Intent serviceIntent = new Intent(getBaseContext(), XMPPService.class);
		startService(serviceIntent);

	}

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

	// Setting Menu -------------------------------------
	// ----------------------------------------------

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