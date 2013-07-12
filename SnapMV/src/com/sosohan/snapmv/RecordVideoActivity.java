package com.sosohan.snapmv;

import java.util.ArrayList;


import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


public class RecordVideoActivity extends Activity {
	
	public static final int REC_MOV = 1;
	private ArrayList<String> array = new ArrayList<String>();
		
	private Button btnVideoRecord;
	private Button btnPreview;
	private ImageButton btnDebug;
	private View.OnClickListener btnClickListener;
	private Uri captureMediaUri;
	private ArrayList<ImageView> thumbnailArray = new ArrayList<ImageView>();
	private int videoIndex = 0;
	private String videoFilename;
	//ImageView thumbnailArray;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_record_video);       
		
		btnVideoRecord = (Button) findViewById(R.id.record_btn);
		btnPreview = (Button) findViewById(R.id.preview_btn);
		btnDebug = (ImageButton) findViewById(R.id.imageButton1);
		//thumbnailArray = (ImageView) findViewById(R.id.imageView1);
		thumbnailArray.add((ImageView) findViewById(R.id.imageView0));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView1));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView2));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView3));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView4));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView5));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView6));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView7));
		
		btnClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v == btnVideoRecord) {
					if(videoIndex < 8)
					{
						final ContentValues values = new ContentValues();
						videoFilename = "/sdcard/DCIM/sosohan"+videoIndex+".mp4";					
						values.put(MediaStore.Video.Media.DATA, videoFilename);
						captureMediaUri = getContentResolver().insert(
								MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
						final Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, captureMediaUri);
						intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 3);
						startActivityForResult(intent, REC_MOV);
					}
				}else if (v == btnPreview) {					
					Log.e("JWJWJW", "btnPreview pushed" );
					Intent intent = new Intent(RecordVideoActivity.this, PreviewActivity.class);
					intent.putStringArrayListExtra("videolist", array);	
					startActivity(intent);
				}else if (v == btnDebug )
				{
					array.add("/sdcard/DCIM/sosohan0.mp4");
					array.add("/sdcard/DCIM/sosohan1.mp4");					
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
	        	Log.e("JWJWJW", "add = " + videoFilename + ",index = "+videoIndex );
	        	array.add(videoFilename); 
	            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoFilename, Thumbnails.MICRO_KIND);
	            thumbnailArray.get(videoIndex).setImageBitmap(thumbnail);
	            videoIndex++;
	        }
	    }	   
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_capture, menu);
		return true;
	}

}
