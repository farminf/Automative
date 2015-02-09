package com.automotivevirtus.settings;

import com.automotivevirtus.R;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

public class Connection_Setting extends Activity {

	ActionBar actionBar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Making Back button on action bar
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new ConnectionFragment())
				.commit();
	}

	public static class ConnectionFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.connection_setting);

		}
	}

	// *****************************************************************
	// *****************************************************************
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
			return true;

		default:
			break;
		}
		return false;

	}

}