package com.sosohan.snapmv;

import java.util.ArrayList;


import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;


public class RecordVideoActivity extends Activity {
	
	public static final int REC_MOV = 1;
	private ArrayList<String> array = new ArrayList<String>();
		
	private Button btnVideoRecord;
	private Button btnPreview;
	private ImageButton btnDebug;
	private View.OnClickListener btnClickListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_record_video);       
		
		btnVideoRecord = (Button) findViewById(R.id.record_btn);
		btnPreview = (Button) findViewById(R.id.preview_btn);
		btnDebug = (ImageButton) findViewById(R.id.imageButton1);
		
		btnClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v == btnVideoRecord) {
					Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 3);
					startActivityForResult(intent, REC_MOV);
				}else if (v == btnPreview) {					
					Log.e("JWJWJW", "btnPreview pushed" );
					Intent intent = new Intent(RecordVideoActivity.this, PreviewActivity.class);
					intent.putStringArrayListExtra("videolist", array);	
					startActivity(intent);
				}else if (v == btnDebug )
				{
					array.add("/sdcard/DCIM/Camera/20130710_182252.mp4");
					array.add("/sdcard/DCIM/Camera/20130710_182519.mp4");
					array.add("/sdcard/DCIM/Camera/20130710_182645.mp4");
				}
			}
		};
		
		btnVideoRecord.setOnClickListener(btnClickListener);
		btnPreview.setOnClickListener(btnClickListener);
		btnDebug.setOnClickListener(btnClickListener);
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
	            Log.e("JWJWJW", "path: " + path + "\nname: " + name + "\nuri : " + uri.toString() + "\nuri id : " + uriId);
	            array.add(path);            
	            Log.e("JWJWJW", "add " + path );
	        }
	    }	   
	}
	
	private String getPath(Uri uri)
	{
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	 
	private String getName(Uri uri)
	{
	    String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	 
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
