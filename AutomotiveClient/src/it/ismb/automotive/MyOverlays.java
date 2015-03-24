
/* MyOverlays.java */
/* 02/08/12 */

package it.ismb.automotive;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyOverlays extends ItemizedOverlay<OverlayItem> {
	
	// All markers
	private ArrayList<MyOverlayItem> mOverlays = new ArrayList<MyOverlayItem>();
	
	private Context mContext;

	public MyOverlays(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	// On tap on one overlay (marker)
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		int itemType = (mOverlays.get(index)).getItemType();
		if (itemType == Constants.TYPE_NORMAL) {
			dialog.setIcon(mContext.getResources().getDrawable(R.drawable.image_info));
		}
		else if (itemType == Constants.TYPE_ENGINE) {
			dialog.setIcon(mContext.getResources().getDrawable(R.drawable.image_engine));
		}
		else if (itemType == Constants.TYPE_FUEL) {
			dialog.setIcon(mContext.getResources().getDrawable(R.drawable.image_fuel));
		}
		
		dialog.show();		
		return true;		
	}
	
	public void addOverlay(OverlayItem item) {
		MyOverlayItem moi = (MyOverlayItem)item;
		if (moi.getItemType() == Constants.TYPE_NORMAL) { // clear other previous markers of the same type
			 clearItemTypeNormal();
		}	
		
		mOverlays.add((MyOverlayItem) item);
		// call createItem(int i)
		populate();
	}
	
	public void clear() {
		mOverlays.clear();		
	}
	
	private void clearItemTypeNormal() {
		for (Iterator<MyOverlayItem> it = mOverlays.iterator(); it.hasNext();) {
			MyOverlayItem moi = (MyOverlayItem) it.next();
			if (moi.getItemType() == Constants.TYPE_NORMAL) { // remove from list
				 it.remove();
			}
		}
	}
}


