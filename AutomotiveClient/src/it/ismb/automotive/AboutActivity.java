
/* AboutActivity.java */
/* 01/08/12 */

package it.ismb.automotive;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity {
	   	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
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
	}
}
