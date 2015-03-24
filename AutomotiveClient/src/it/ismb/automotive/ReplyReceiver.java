
/* ReplyReceiver.java */
/* 21/05/12 */

package it.ismb.automotive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Class BroadcastReceiver for manage Intent received from REPLY APP. 
 */
public class ReplyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// retrieve Intent from REPLY app
		Bundle extras = intent.getExtras();
		if (extras != null) {
			// the value of an item that previously added with putExtra() or the default value if none was found. 
			int value = intent.getIntExtra(Constants.REPLY_ITEM_KEY, -1);
			Log.i("ReplyReceiver", "Value in Intent="+value);
		
			// start activity (if not already started) AutomotiveClient
			Intent localIntent = new Intent(context,AutomotiveClient.class);
			localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			localIntent.putExtra(Constants.REPLY_ITEM_KEY, value);
			// passing Intent with PROBLEMA value
			context.startActivity(localIntent);
		}
	}
}
