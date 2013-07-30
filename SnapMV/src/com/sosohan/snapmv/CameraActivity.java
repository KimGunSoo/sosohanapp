package com.sosohan.snapmv;

import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class CameraActivity extends Activity {
	String cam_tag = "CameraActivity";
	private Button btnCamera;
	private View.OnClickListener btnCameraClickListener;
	//Camera cam;
	SurfaceView camSurfaceView;
	MediaRecorder mMediaRecorder;
	SurfaceHolder mHolder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		camSurfaceView = (SurfaceView) findViewById(R.id.camSurfaceView);
		mHolder = camSurfaceView.getHolder();
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//this is a deprecated method, is not required after 3.0 
        		
		btnCamera = (Button) findViewById(R.id.camera_test_btn);
		btnCameraClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				start();			
			}			
		};
		btnCamera.setOnClickListener(btnCameraClickListener);
	}
	private void start() {
		Log.v(cam_tag, "start");
		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
		
//		mMediaRecorder.setVideoSize(640, 480);
//		mMediaRecorder.setVideoFrameRate(15);
//		mMediaRecorder.setMaxDuration(3000);
		mMediaRecorder.setOutputFile("/sdcard/DCIM/jw.mp4"); 
		mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaRecorder.start();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

}
