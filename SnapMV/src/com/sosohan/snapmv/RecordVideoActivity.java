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
	private int count;
	
	private Button btnVideoRecord;
	private View.OnClickListener btnClickListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		
		array.add("/mnt/sdcard/mp4_sample_first.mp4");
        array.add("/mnt/sdcard/mp4_sample_second.mp4");
        count = 0;
        final VideoView videoView = (VideoView)findViewById(R.id.videoView1);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        
        Uri video = Uri.parse(MOVE_URL);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);
        videoView.requestFocus();
        
        MediaPlayer.OnCompletionListener mComplete = new MediaPlayer.OnCompletionListener() {			
			@Override
			public void onCompletion(MediaPlayer mp) {
				count++;
				if(array.size() > count) {
					Uri video1 = Uri.parse(array.get(count).toString());					
					videoView.setVideoURI(video1);
					videoView.start();
				} else {
					Uri video2 = Uri.parse(array.get(0).toString());
					videoView.setVideoURI(video2);
					count = 0;
				}
			}
		};
		videoView.setOnCompletionListener(mComplete);
		videoView.start();
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
	            Log.e("JWJWJW", "�ㅼ젣寃쎈줈 : " + path + "\n�뚯씪紐�: " + name + "\nuri : " + uri.toString() + "\nuri id : " + uriId);
	        }
	    }	   
	}
	
	// �ㅼ젣 寃쎈줈 李얘린
	private String getPath(Uri uri)
	{
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	 
	// �뚯씪紐�李얘린
	private String getName(Uri uri)
	{
	    String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	 
	// uri �꾩씠��李얘린
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
