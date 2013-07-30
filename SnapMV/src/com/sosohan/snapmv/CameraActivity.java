package com.sosohan.snapmv;

import java.io.IOException;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class CameraActivity extends Activity implements SurfaceHolder.Callback {
	String cam_tag = "CameraActivity";
	private Button btnCamera;
	private View.OnClickListener btnCameraClickListener;
	//Camera cam;
	SurfaceView camSurfaceView;
	Camera camera;
	MediaRecorder recorder;
	SurfaceHolder holder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		camSurfaceView = (SurfaceView) findViewById(R.id.camSurfaceView);
		holder = camSurfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//this is a deprecated method, is not required after 3.0 
        		
		btnCamera = (Button) findViewById(R.id.camera_test_btn);
		btnCameraClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				start();			
			}			
		};
		btnCamera.setOnClickListener(btnCameraClickListener);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.v(cam_tag, "onResume");		
		
	}
	private void start() {
		Log.v(cam_tag, "start");		
		try {
			recorder = new MediaRecorder();
			camera.unlock();
			recorder.setCamera(camera);
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			//recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

			recorder.setVideoSize(320,240);
//			mMediaRecorder.setVideoFrameRate(15);
			recorder.setMaxDuration(3000);			
			recorder.setOutputFile("/sdcard/DCIM/jw.mp4"); 
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
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.v(cam_tag, "surfaceCreated");
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		Log.v(cam_tag, "cameraCount="+cameraCount);
		for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
			Camera.getCameraInfo( camIdx, cameraInfo );
			if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
				try {
					Log.v(cam_tag, "camIdx="+camIdx);
					camera = Camera.open( camIdx );
					Log.v(cam_tag, "camera="+camera);
				} catch (RuntimeException e) {
					Log.e(cam_tag, "Camera failed to open: " + e.getLocalizedMessage());
				}
			}
		}		
	}
	
	private boolean previewRunning;
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v(cam_tag, "surfaceChanged");		
		if (previewRunning){
			camera.stopPreview();
		}
		Camera.Parameters p = camera.getParameters();
//		p.setPreviewSize(width, height);
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
		Log.v(cam_tag, "surfaceDestroyed");	
		// TODO Auto-generated method stub
		camera.stopPreview();
		previewRunning = false;
		camera.release();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}
}
