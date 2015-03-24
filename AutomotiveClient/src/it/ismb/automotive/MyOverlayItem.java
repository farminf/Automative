
/* MyOverlaysItem.java */
/* 02/08/12 */

package it.ismb.automotive;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem {
	
	private float accuracy;
	private int itemType; // NORMAL, FUEL, ENGINE
	
	public MyOverlayItem(GeoPoint point, float accuracy, String title, String message, int type, Context context) {
		super(point, title, message);
		setAccuracy(accuracy);
		setItemType(type);
		// set different marker
		if (type == Constants.TYPE_NORMAL) {
			Drawable drawable = context.getResources().getDrawable(R.drawable.marker_normal); 
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			setMarker(drawable);
		}
		else if (type == Constants.TYPE_ENGINE) {
			Drawable drawable = context.getResources().getDrawable(R.drawable.marker_normal); // FIXME change marker 
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			setMarker(drawable);
		}
		else if (type == Constants.TYPE_FUEL) {
			Drawable drawable = context.getResources().getDrawable(R.drawable.marker_fuel); 
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			setMarker(drawable);
		}
	}

	/**
	 * @return the accuracy
	 */
	public float getAccuracy() {
		return accuracy;
	}
	
	/**
	 * @param accuracy the accuracy to set
	 */
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * @return the itemType
	 */
	public int getItemType() {
		return itemType;
	}

	/**
	 * @param itemType itemType to set
	 */
	public void setItemType(int itemType) {
		this.itemType = itemType;
	}

}
