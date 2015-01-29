package com.automotivevirtus.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FragmentPageAdapter extends FragmentStatePagerAdapter{

	public FragmentPageAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int i) {
		// TODO Auto-generated method stub
		
		switch (i) {
        case 0:
            //Fragement for Android Tab
            return new firstTab();
        case 1:
           //Fragment for Ios Tab
            return new secondTab();
        case 2:
            //Fragment for Windows Tab
            return new thirdTab();
        }
    
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

}
