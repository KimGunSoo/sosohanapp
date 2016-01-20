package com.sosohan.MVlog;

import java.io.File;
import java.util.ArrayList;


import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore.Images.Thumbnails;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class RecordVideoActivity extends Activity {
	static final String TAG = "JWJWRVA";
    public static final int mLogoYellow = 0xaaffd700;
    public static final int REC_MOV = 1;

	private ArrayList<String> array = new ArrayList<String>();
	private TextView btnNew;
	private TextView btnCamera;
	private TextView btnDone;	
	private View.OnClickListener btnClickListener;
	private View.OnTouchListener btnTouchListener;
	private TextView mTextGuide;

	//private ImageView bigPrev;
	private ArrayList<ImageView> thumbnailArray = new ArrayList<ImageView>();	
	
	static final int cnt = 8;
	public static final String promisedPath = Environment.getExternalStorageDirectory().getPath() +"/";// "/sdcard/DCIM/";
	//private Button btnDebug_make_text_image;
	MediaDataPreference mediaPref = null;
	
	private static Typeface mTypeface = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_record_video);       
		
		mediaPref = MediaDataPreference.getInstance(getApplicationContext());
		
		//mTypeface = Typeface.createFromAsset(getAssets(),"font/space1.ttf");
		mTypeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
		//bigPrev = (ImageView) findViewById(R.id.big_preview);
        mTextGuide = (TextView) findViewById(R.id.guild_text);
		btnNew = (TextView) findViewById(R.id.new_mv_btn);
		btnCamera = (TextView) findViewById(R.id.camera_A_btn);
		//btnCamera.setTypeface(tf,Typeface.BOLD);
		btnDone = (TextView) findViewById(R.id.done_btn);	
		//btnDone.setTypeface(tf,Typeface.BOLD);
		
		thumbnailArray.add((ImageView) findViewById(R.id.imageView0));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView1));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView2));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView3));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView4));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView5));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView6));
		thumbnailArray.add((ImageView) findViewById(R.id.imageView7));
		
		btnTouchListener = new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ImageView view = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					for(int i=0 ; i < thumbnailArray.size() ; i++)	{					
						thumbnailArray.get(i).setColorFilter(0x00000000, Mode.SRC_OVER);
					}
					view.setColorFilter(mLogoYellow, Mode.SRC_OVER);
					int thumbIdx = thumbnailArray.indexOf(view);
					Log.d(TAG, "onTouch index = "+ thumbIdx);
					mediaPref.setCurrentIdx(thumbIdx);
					//String tmp = ""+thumbIdx;
					setSceneNo(thumbIdx);
					Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(promisedPath+thumbIdx+".mp4", Thumbnails.MINI_KIND);
					//bigPrev.setImageBitmap(thumbnail);
				}					
				return true;
			}			
		};
		
		
		
		for(int i=0 ; i < thumbnailArray.size() ; i++)	{					
			thumbnailArray.get(i).setOnTouchListener(btnTouchListener);
		}
		
		btnClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v == btnCamera)
				{
					Intent intent = new Intent(RecordVideoActivity.this,
							CameraActivity.class);
					startActivity(intent);
				}/*else if (v == bigPrev && !array.isEmpty()) {
					ArrayList<String> prev_array = new ArrayList<String>();
					
					for(int i = mediaPref.getCurrentIdx(); i < cnt; i++){
						Log.e(TAG, promisedPath+i+".mp4");
						if(new File(promisedPath+i+".mp4").exists())
							prev_array.add(promisedPath+i+".mp4");
					}
					Log.e("bigPrev", "prev_array: " + prev_array ); 
					if (!prev_array.isEmpty()){
						Intent intent = new Intent(RecordVideoActivity.this, PreviewActivity.class);
						intent.putStringArrayListExtra("videolist", prev_array);	
						startActivity(intent);	
					}					
				}*/else if (v == btnDone && isAllClipAvailable()){
					Intent intent = new Intent(RecordVideoActivity.this,
					SelectBgmActivity.class);
					intent.putStringArrayListExtra("videolist", array);	
					startActivity(intent);					
				}/*else if (v == btnNew){
					array.clear();
					mediaPref.setCurrentIdx(0);	
					for(int i=0 ; i < thumbnailArray.size() ; i++)	{
						thumbnailArray.get(i).setImageBitmap(null);
						thumbnailArray.get(i).setColorFilter(0x00000000, Mode.SRC_OVER);
					}
				}
				/*
				else if (v == btnDebug_make_text_image)
				{
					String path = "/sdcard";//Environment.getExternalStorageDirectory().getAbsolutePath();
					Log.e("hjhjhj", "path : " + path);
					Bitmap  b = Bitmap.createBitmap(640, 480, Bitmap.Config.RGB_565);
					View view = (View)findViewById(R.id.imageView0);
					Paint whitePaint = new Paint();
					whitePaint.setTextSize(30);
					whitePaint.setColor(Color.WHITE);
					try {
						File f = new File(path+"/notes");

						f.mkdir();
						File f2 = new File(path + "/notes/"+"test"+".png");

						Canvas c = new Canvas( b );
						c.drawText("this is SnapMV test", 100, 300, whitePaint);
						view.draw( c );
						FileOutputStream fos = new FileOutputStream(f2);
						if ( fos != null )
						{
							b.compress(Bitmap.CompressFormat.PNG, 100, fos ); 
							fos.close();
						}
					} catch ( Exception e ) {
						Log.e("testSaveView", "Exception: " + e.toString() ); 
					}

					Process chperm;
					try {
						
						chperm=Runtime.getRuntime().exec("ffmpeg -f image2 -i " + path + "/notes/"+"test"+".png"+" /sdcard/DCIM/7.mp4\n");
//						DataOutputStream os = 
//								new DataOutputStream(chperm.getOutputStream());
//						Log.e("testSaveView", "ffmpeg -f image2 -i "+path + "/notes/"+"test"+".png"+" /sdcard/DCIM/7.mp4\n"); 
//						os.writeBytes("ffmpeg -f image2 -i " + path + "/notes/"+"test"+".png"+" /sdcard/DCIM/7.mp4\n");
//						os.flush();

						chperm.waitFor();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
				*/
			}
		};
		//bigPrev.setOnClickListener(btnClickListener);
		//btnNew.setOnClickListener(btnClickListener);
		btnCamera.setOnClickListener(btnClickListener);
		btnDone.setOnClickListener(btnClickListener);

		setGlobalFont(getWindow().getDecorView());
		//btnDebug_make_text_image.setOnClickListener(btnClickListener);


        mAdView = (AdView) findViewById(R.id.adView_rec_vd);
        mAdRequest = new AdRequest.Builder().build();
        mAdView.loadAd(mAdRequest);

    }

	private boolean isAllClipAvailable() {
		for(int i=0; i < cnt; i++){
			if(!(new File(promisedPath+i+".mp4").exists()))
				return false;
		}
		return true;
	}

    AdView mAdView;
    AdRequest mAdRequest;

	@Override
	protected void onResume() {
		super.onResume();
		
		//File [] filelist = new File[cnt];
		Log.d(TAG, "onResume");
		array.clear();
		
		for(int i=0; i < cnt; i++){
			//filelist[i] = new File(promisedPath+i+".mp4");
			Log.d(TAG, promisedPath+i+".mp4");
			if(new File(promisedPath+i+".mp4").exists()) {
				array.add(promisedPath+i+".mp4");
				Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(promisedPath+i+".mp4", Thumbnails.MICRO_KIND);
				thumbnailArray.get(i).setImageBitmap(thumbnail);
				thumbnailArray.get(i).setColorFilter(0x00000000, Mode.SRC_OVER);
			}			
			
		}
			
		int idx = mediaPref.getCurrentIdx();
		thumbnailArray.get(idx).setColorFilter(mLogoYellow, Mode.SRC_OVER);
		setSceneNo(idx);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_capture, menu);
		return true;
	}
	
	private void setGlobalFont(View view) {
		if (view != null) {
			if(view instanceof ViewGroup){
				ViewGroup vg = (ViewGroup)view;
				int vgCnt = vg.getChildCount();
				for(int i=0; i < vgCnt; i++){
					View v = vg.getChildAt(i);
					if(v instanceof TextView){
						((TextView) v).setTypeface(mTypeface,Typeface.NORMAL);
					}
					setGlobalFont(v);
				}
			}
		}
	}
	private void setSceneNo(int no)
	{
		btnNew.setText("Scene #"+(no+1));
	}
}
