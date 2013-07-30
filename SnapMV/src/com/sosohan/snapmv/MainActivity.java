package com.sosohan.snapmv;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btnRecordVideoActivity;
	private Button btnSettingActivity;
	private View.OnClickListener btnClickListener;
	private Context mContext = null;
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

		// BGM loading
		if (mContext == null)
			mContext = getApplicationContext();
		SelectBgmActivity.bgmFileLoading(mContext);

		btnRecordVideoActivity.setOnClickListener(btnClickListener);       
        btnSettingActivity.setOnClickListener(btnClickListener);
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
