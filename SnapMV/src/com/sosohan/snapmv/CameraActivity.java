package com.sosohan.snapmv;

import java.io.IOException;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, OnInfoListener{
	String cam_tag = "CameraActivity";
	String OutputPath = "/sdcard/DCIM/";
	SurfaceView camSurfaceView;
	Camera camera;
	MediaRecorder recorder;
	SurfaceHolder holder;
	
	ToggleButton recordToggle;
	ToggleButton backFrontCamToggle;
	CamcorderProfile camProfile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		
		camSurfaceView = (SurfaceView) findViewById(R.id.camSurfaceView);
		holder = camSurfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//this is a deprecated method, is not required after 3.0 
        		
		camProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_TIME_LAPSE_480P);
		Log.v(cam_tag,"w" + camProfile.videoFrameWidth + "/h"+ camProfile.videoFrameHeight); 
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.v(cam_tag, "onResume");		
		
	}	
	int idx = 1;
	private void start() {
		Log.v(cam_tag, "start");		
		try {
			recorder = new MediaRecorder();
			camera.unlock();
			recorder.setCamera(camera);
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			//recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
			
//			CamcorderProfile camcorderProfile_HQ = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//			recorder.setProfile(camcorderProfile_HQ);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
			
			recorder.setVideoSize(camProfile.videoFrameWidth,camProfile.videoFrameHeight);
			//if g3
			//recorder.setVideoSize(1280,720);
			recorder.setVideoFrameRate(30);
			recorder.setVideoEncodingBitRate(6000000);
			recorder.setMaxDuration(3000);	
			recorder.setOnInfoListener(this);
			recorder.setOutputFile(OutputPath+idx+".mp4"); 
			recorder.setPreviewDisplay(holder.getSurface());
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void stop() {
		Log.v(cam_tag, "stop");		
		recorder.stop();
		recorder.release();
		finish();
	}
	private int getCameraInfo(int info)
	{
		int result = -1;
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		Log.v(cam_tag, "cameraCount="+cameraCount);
		for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
			Camera.getCameraInfo( camIdx, cameraInfo );
			if ( cameraInfo.facing == info  ) {
				try {
					Log.v(cam_tag, "camIdx="+camIdx);
					//camera = Camera.open( camIdx );
					result = camIdx;
					Log.v(cam_tag, "camera="+camera);
				} catch (RuntimeException e) {
					Log.e(cam_tag, "Camera failed to open: " + e.getLocalizedMessage());
				}
			}
		}	
		return result;		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(cam_tag, "surfaceCreated");
		DisplayMetrics displayMetrics = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		
		int height = displayMetrics.heightPixels;
		holder.setFixedSize((camProfile.videoFrameWidth*displayMetrics.heightPixels)/camProfile.videoFrameHeight, height);
		//Log.d(cam_tag+"vd", "size="+(camProfile.videoFrameWidth*height)/camProfile.videoFrameHeight+", "+height);
		camera = Camera.open( getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT) );	
	}	
	private boolean previewRunning;
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i(cam_tag, format+"surfaceChanged"+width+"*"+height);		
		if (previewRunning){
			camera.stopPreview();
		}
		Camera.Parameters p = camera.getParameters();
		if(p != null)
		{
			List<Camera.Size> sizeList = p.getSupportedPreviewSizes();
			for(Size size : sizeList)
				Log.d(cam_tag+"prv", "size="+size.width+", "+size.height);
			sizeList = p.getSupportedVideoSizes();
			for(Size size : sizeList)
				Log.d(cam_tag+"vd", "size="+size.width+", "+size.height);
		}
		p.setPreviewSize(camProfile.videoFrameWidth, camProfile.videoFrameHeight);
//		p.setPreviewFormat(PixelFormat.JPEG);
		camera.setParameters(p);

		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			previewRunning = true;
		}
		catch (IOException e) {
			Log.e(cam_tag,e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(cam_tag, "surfaceDestroyed");	
		camera.stopPreview();
		previewRunning = false;
		camera.release();
	}
	
	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
			Log.v(cam_tag,"Maximum Duration Reached"); 
			mr.stop();
			recorder.release();
			recording = false;
			//camSurfaceView.setClickable(true);
		}
	}
	
	boolean recording = false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub		
		if (event.getAction() == MotionEvent.ACTION_DOWN && !recording){
			//camSurfaceView.setClickable(false);
			recording = true;
			start();
			idx++;
		}				
		return super.onTouchEvent(event);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}
}
