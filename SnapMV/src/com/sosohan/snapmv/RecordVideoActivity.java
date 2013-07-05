package com.sosohan.snapmv;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class RecordVideoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_video);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_capture, menu);
		return true;
	}

}
