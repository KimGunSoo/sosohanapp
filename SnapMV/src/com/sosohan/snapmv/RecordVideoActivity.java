package com.sosohan.snapmv;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class RecordVideoActivity extends Activity {
	
	public static final int REC_MOV = 1;
	
	private Button btnVideoRecord;
	private View.OnClickListener btnClickListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_video);

		btnVideoRecord = (Button) findViewById(R.id.record_btn);
		
		btnClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v == btnVideoRecord) {
					Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					startActivityForResult(intent, REC_MOV);
				}
			}
		};
		
		btnVideoRecord.setOnClickListener(btnClickListener);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
	    super.onActivityResult(requestCode, resultCode, intent);
	    if (resultCode == RESULT_OK)
	    {
	        if (requestCode == REC_MOV)
	        {
	            Uri uri = intent.getData();
	            String path = getPath(uri);
	            String name = getName(uri);
	            String uriId = getUriId(uri);
	            Log.e("JWJWJW", "실제경로 : " + path + "\n파일명 : " + name + "\nuri : " + uri.toString() + "\nuri id : " + uriId);
	        }
	    }	   
	}
	
	// 실제 경로 찾기
	private String getPath(Uri uri)
	{
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	 
	// 파일명 찾기
	private String getName(Uri uri)
	{
	    String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	 
	// uri 아이디 찾기
	private String getUriId(Uri uri)
	{
	    String[] projection = { MediaStore.Images.ImageColumns._ID };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_capture, menu);
		return true;
	}

}
