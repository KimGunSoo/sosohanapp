package com.sosohan.snapmv;

import java.util.ArrayList;

import android.media.MediaPlayer;
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
import android.widget.MediaController;
import android.widget.VideoView;

public class RecordVideoActivity extends Activity {
	
	public static final int REC_MOV = 1;
	private static final String MOVE_URL = "/mnt/sdcard/mp4_sample_first.mp4";
	private ArrayList<String> array = new ArrayList<String>();
	private int videoCount;
	
	private Button btnVideoRecord;
	private Button btnPreview;
	private View.OnClickListener btnClickListener;
	private VideoView videoView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_record_video);
		
        videoCount = 0;
        videoView = (VideoView)findViewById(R.id.videoView1);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        
        //Uri video = Uri.parse(MOVE_URL);
        videoView.setMediaController(mediaController);
       // videoView.setVideoURI(video);
        videoView.requestFocus();
        
        MediaPlayer.OnCompletionListener mComplete = new MediaPlayer.OnCompletionListener() {			
			
        	@Override
			public void onCompletion(MediaPlayer mp) {
        		Log.e("JWJWJW", "onCompletion enter array.size()="+array.size()+",count="+videoCount);
	       		if(array.size() > videoCount) {
					Uri video1 = Uri.parse(array.get(videoCount).toString());					
					videoView.setVideoURI(video1);
					videoView.start();
					videoCount++;
				} else {
					Log.e("JWJWJW", "play end");
				}			
			}
		};
		videoView.setOnCompletionListener(mComplete);
		
		btnVideoRecord = (Button) findViewById(R.id.record_btn);
		btnPreview = (Button) findViewById(R.id.preview_btn);
		
		btnClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v == btnVideoRecord) {
					Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					startActivityForResult(intent, REC_MOV);
				}else if (v == btnPreview) {
					videoCount = 0;
					Log.e("JWJWJW", "btnPreview pushed" );
					Uri video1 = Uri.parse(array.get(videoCount).toString());
					videoView.setVideoURI(video1);
					videoView.start();
					videoCount++;
				}
			}
		};
		
		btnVideoRecord.setOnClickListener(btnClickListener);
		btnPreview.setOnClickListener(btnClickListener);
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
