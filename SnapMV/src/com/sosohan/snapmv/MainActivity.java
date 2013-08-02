package com.sosohan.snapmv;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	private Button btnRecordVideoActivity;
	private Button btnSettingActivity;
	private View.OnClickListener btnClickListener;
	private Context mContext = null;
	private MediaDataPreference mMediaPref = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		btnRecordVideoActivity	= (Button) findViewById(R.id.record_video_A_btn);
		btnSettingActivity		= (Button) findViewById(R.id.setting_A_btn);
		
		btnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v == btnRecordVideoActivity) {
					Intent intent = new Intent(MainActivity.this,
							RecordVideoActivity.class);
					startActivity(intent);
				}else if (v == btnSettingActivity) {
					Intent intent = new Intent(MainActivity.this,
							SettingsActivity.class);
					startActivity(intent);
				}				
			}
		};

		btnRecordVideoActivity.setOnClickListener(btnClickListener);
		btnSettingActivity.setOnClickListener(btnClickListener);

    // BGM loading
		if (mContext == null)
			mContext = getApplicationContext();
		SelectBgmActivity.bgmLoadingThStart(mContext);

		// For preference test.
		String facebook_id;
		mMediaPref = MediaDataPreference.getInstance(mContext);
		facebook_id = mMediaPref.getFaceBookId();
		if(facebook_id == null) {
			mMediaPref.setFaceBookId("wish4679");
			Log.d(TAG,"facebook id is not set");
		} else {
			Log.d(TAG,"facebook id is " + facebook_id);
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
