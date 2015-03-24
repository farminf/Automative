
/* PreferencesGpsActivity.java */
/* 21/05/12 */

package it.ismb.automotive;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferencesGpsActivity extends PreferenceActivity {
	   	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	  	addPreferencesFromResource(R.xml.preferences_gps);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// User pressed BACK key		
		setResult(RESULT_CANCELED, null);
	}
}
