package com.automotivevirtus.settings;

import com.automotivevirtus.R;
import com.automotivevirtus.activities.MainFragmentActivity;
import com.automotivevirtus.xmpp.XMPPHelper;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

public class About extends Activity {

	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		//Making Back button on action bar
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

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
