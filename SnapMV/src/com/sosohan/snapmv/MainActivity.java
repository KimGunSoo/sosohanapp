package com.sosohan.snapmv;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btnVideoRecordActivity;
	private Button btnPlayBgmActivity;
	private View.OnClickListener btnClickListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnVideoRecordActivity	= (Button) findViewById(R.id.video_record_A_btn);
		btnPlayBgmActivity		= (Button) findViewById(R.id.play_bgm_A_btn);
		
		btnClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v == btnVideoRecordActivity) {
					Intent intent = new Intent(MainActivity.this,
							RecordVideoActivity.class);
					startActivity(intent);
				}else if (v == btnPlayBgmActivity) {
					Intent intent = new Intent(MainActivity.this,
							PlayBgmActivity.class);
					startActivity(intent);
				}
				
			}
		};
		
        btnVideoRecordActivity.setOnClickListener(btnClickListener);       
        btnPlayBgmActivity.setOnClickListener(btnClickListener); 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
