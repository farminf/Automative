package com.automotivevirtus.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.automotivevirtus.R;
import com.automotivevirtus.settings.About;
import com.automotivevirtus.settings.Connection_Setting;

@SuppressWarnings("deprecation")
public class MainFragment extends FragmentActivity implements
		ActionBar.TabListener {

	ViewPager Tab;
	FragmentPageAdapter TabAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TabAdapter = new FragmentPageAdapter(getSupportFragmentManager());

		final ActionBar actionBar = getActionBar();
		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
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