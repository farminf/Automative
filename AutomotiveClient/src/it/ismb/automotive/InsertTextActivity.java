
/* InsertTextActivity.java */
/* 21/05/12 */

package it.ismb.automotive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class InsertTextActivity extends Activity{
	private EditText etTextInserted;

	@Override
	public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			setContentView(R.layout.insert_text);
			
			etTextInserted =(EditText) findViewById (R.id.etTextInserted);
			Button btnOk = (Button) findViewById (R.id.send_ok);
			Button btnCancel = (Button) findViewById (R.id.send_cancel);
			
			btnOk.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					String stringInserted = etTextInserted.getText().toString();
					// Intent to return
					Intent i = new Intent();
					i.putExtra("valueInserted", stringInserted);
					setResult(Activity.RESULT_OK,i);
					finish();
				}
			});
			
			btnCancel.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					// Intent to return
					Intent i = new Intent();
					setResult(Activity.RESULT_CANCELED,i);
					finish();
				}
			});
	}
}
