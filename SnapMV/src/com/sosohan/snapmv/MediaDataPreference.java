package com.sosohan.snapmv;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class MediaDataPreference {
	private static final String TAG = "MediaDataPreference";

	private static MediaDataPreference instance = null;
	private SharedPreferences mPref = null;
	private static Context mContext = null;

	private static final String PREF_KEY_FACEBOOK_ID = "facebook_id";
	private static final String PREF_FAIL_STRING = "sosohanfailsosohan";

	private MediaDataPreference() {
		loadPreference();
	}

	public static MediaDataPreference getInstance(Context ctx) {
		mContext = ctx;

		if (instance == null) {
			instance = new MediaDataPreference();
		}
		return instance;
	}

	private void loadPreference(){
		mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
	}
	
	public void setFaceBookId(String id) {
		SharedPreferences.Editor ed = mPref.edit();
		
		ed.putString(PREF_KEY_FACEBOOK_ID, id);
		
		ed.commit();
	}
	
	public String getFaceBookId() {
		String id = null;

		id = mPref.getString(PREF_KEY_FACEBOOK_ID, PREF_FAIL_STRING);
		if( id.compareTo(PREF_FAIL_STRING) == 0) {
			Log.d(TAG, "getFaceBookId() fail : PREF_KEY_FACEBOOK_ID preference value not exist");
			id = null;
		}

		return id;
	}
}
