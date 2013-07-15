package com.sosohan.snapmv;

import java.util.ArrayList;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MakeMVActivity extends Activity {
	private ArrayList<String> decodingArray;
	private MediaExtractor extractor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_mv);
		
		extractor = new MediaExtractor();
		Intent intent = getIntent();
		decodingArray = (ArrayList<String>) intent.getSerializableExtra("videolist");
		extractor.setDataSource(decodingArray.get(1));
	}

	private MediaFormat mediaFormat = null;
	String mime = null;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int numTracks = extractor.getTrackCount();
		for(int i = 0 ; i < numTracks ; i ++)
		{
			mediaFormat = extractor.getTrackFormat(i);
			mime = mediaFormat.getString(mediaFormat.KEY_MIME);
			Log.e("JWJWJW", "numTracks" + i + ": " + mime);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.make_mv, menu);
		return true;
	}

}
