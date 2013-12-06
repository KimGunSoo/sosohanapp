package com.sosohan.snapmv;

import java.io.File;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private static final boolean PREF_TEST = false;
	private Button btnNewVideoBtn;
	private Button btnRecordVideoActivity;
	private Button btnSettingActivity;
	private View.OnClickListener btnClickListener;
	private Context mContext = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		btnNewVideoBtn 			= (Button) findViewById(R.id.new_video_A_btn);
		btnRecordVideoActivity	= (Button) findViewById(R.id.record_video_A_btn);
		btnSettingActivity		= (Button) findViewById(R.id.setting_A_btn);
		
		btnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 if (v == btnNewVideoBtn) {
					 
					 boolean ask = false;
					 for(int i = 0; i < 8; i++)
					 {
						 if(new File(RecordVideoActivity.promisedPath + i + ".mp4").exists())
						 {				
							 ask = true;
							 break;
						 }						 
					 }
					//dialog box	
					 if(ask)
						 ask_to_delete();	
					 else
						 start_record_activity();					 
				}else if (v == btnRecordVideoActivity) {
					start_record_activity();
				}else if (v == btnSettingActivity) {
					Intent intent = new Intent(MainActivity.this,
							SettingsActivity.class);
					startActivity(intent);
				}				
			}
		};
		btnNewVideoBtn.setOnClickListener(btnClickListener);
		btnRecordVideoActivity.setOnClickListener(btnClickListener);
		btnSettingActivity.setOnClickListener(btnClickListener);

		// For preference test.
		if(PREF_TEST) test_preference();
		
	}
	private void start_record_activity()
	{
		Intent intent = new Intent(MainActivity.this,
				RecordVideoActivity.class);
		startActivity(intent);
	}
	private void delete_intermediate_file()
	{
		for(int i = 0; i < 8; i++)
		 {
			 if(new File(RecordVideoActivity.promisedPath + i + ".mp4").delete())
			 {
				Log.d(TAG,"delete file :"+RecordVideoActivity.promisedPath + i + ".mp4"); 				 
			 }
			 else
			 {
				 Log.d(TAG,"not found file :"+RecordVideoActivity.promisedPath + i + ".mp4"); 
			 }			 
		 }
	}
	
	private void ask_to_delete(){
	    AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
	    alt_bld.setMessage("You have some saved clip. delete it all?").setCancelable(
	        false).setPositiveButton("Yes",
	        new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	            // Action for 'Yes' Button
	        	rewindIdx();
	        	delete_intermediate_file();
	        	start_record_activity();
	        }
	        }).setNegativeButton("No",
	        new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	            // Action for 'NO' Button
	            dialog.cancel();
	            rewindIdx();
	            start_record_activity();
	        }
	        });
	    AlertDialog alert = alt_bld.create();
	    // Title for AlertDialog
	    // alert.setTitle("Title");
	    // Icon for AlertDialog
	    // alert.setIcon(R.drawable.icon);
	    alert.show();
	}
	
	private void test_preference() {
		String facebook_id;
		MediaDataPreference mediaPref = null;

		mediaPref = MediaDataPreference.getInstance(mContext);
		facebook_id = mediaPref.getFaceBookId();
		if(facebook_id == null) {
			mediaPref.setFaceBookId("sosohanmv");
			Log.d(TAG,"facebook id is not set");
		} else {
			Log.d(TAG,"facebook id is " + facebook_id);
		}
	}

	private void rewindIdx() {
		MediaDataPreference mediaPref = null;
		mediaPref = MediaDataPreference.getInstance(mContext);
		mediaPref.setCurrentIdx(0);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// BGM loading
		if (mContext == null)
			mContext = getApplicationContext();
		SelectBgmActivity.bgmLoadingThStart(mContext);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
