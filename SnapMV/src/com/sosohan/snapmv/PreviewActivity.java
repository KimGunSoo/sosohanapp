package com.sosohan.snapmv;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

public class PreviewActivity extends Activity {
	private int videoCount = 0;
	private VideoView previewView;
	private ArrayList<String> videoArray;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_preview);
		previewView = (VideoView)findViewById(R.id.previewView);
		Intent intent = getIntent();
		
		videoArray = (ArrayList<String>) intent.getSerializableExtra("videolist");
		
		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(previewView);

		previewView.setMediaController(mediaController);

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
		Uri video1 = Uri.parse(videoArray.get(videoCount).toString());
		previewView.setVideoURI(video1);
		previewView.start();
		videoCount++;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preview, menu);
		return true;
	}

}
