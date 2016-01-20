package com.sosohan.MVlog;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class PreviewActivity extends Activity {
	private int videoCount = 0;
	private FitVideoView previewView;
	private ArrayList<String> videoArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);
		previewView = (FitVideoView)findViewById(R.id.previewView);
		Intent intent = getIntent();
		
		videoArray = (ArrayList<String>) intent.getSerializableExtra("videolist");
				
		previewView.requestFocus();
		
		 MediaPlayer.OnCompletionListener mComplete = new MediaPlayer.OnCompletionListener() {					
	        	@Override
				public void onCompletion(MediaPlayer mp) {
	        		Log.e("JWJWJW", "onCompletion enter array.size()="+videoArray.size()+",count="+videoCount);
		       		if(videoArray.size() > videoCount) {
						Uri video1 = Uri.parse(videoArray.get(videoCount).toString());					
						previewView.setVideoURI(video1);
						previewView.start();
						videoCount++;
					} else {
						Log.e("JWJWJW", "play end");
						finish();
					}			
				}
			};
		previewView.setOnCompletionListener(mComplete);

	}	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Uri video1 = Uri.parse(videoArray.get(videoCount).toString());
		previewView.setVideoURI(video1);
		previewView.start();
		Log.e("JWJWJW", "play start");
		videoCount++;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preview, menu);
		return true;
	}

}
