package com.sosohan.snapmv;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SelectBgmActivity extends Activity {
	final static String BGM_TAG = "SelectBgmActivity";
	final static boolean DEBUG = true;
	private ArrayList<String> passVideoList;

	private static ArrayList<String> bgmList = new ArrayList<String>();
	private static String bgmPath = null;
	private static Semaphore loadCompleted = new Semaphore(1);
	private static Thread loadingTh = null;

	private ListView bgmListView;
	private ArrayAdapter<String> adapter;
	private MediaPlayer audioPlayer = null;
	
	private String selectedBGMPath = null;
	private Button btnMakeMVActivity;
	private View.OnClickListener btnBgmClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_bgm);
		Intent intent = getIntent();
		btnMakeMVActivity	= (Button) findViewById(R.id.make_mv_A_btn);		
		passVideoList = (ArrayList<String>) intent.getSerializableExtra("videolist");
		
		btnBgmClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				if (v == btnMakeMVActivity) {
					Intent intent = new Intent(SelectBgmActivity.this,
							MakeMVActivity.class);
					intent.putStringArrayListExtra("videolist", passVideoList);
					intent.putExtra("audioPath",selectedBGMPath);
					startActivity(intent);
				}				
			}
		};
		btnMakeMVActivity.setOnClickListener(btnBgmClickListener);

		audioPlayer = new MediaPlayer();
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			loadCompleted.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		initListView();
		loadCompleted.release();
	}

	private void initListView() {
		if(loadingTh == null) {
			SelectBgmActivity.bgmLoadingThStart(getApplicationContext());
		}

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bgmList);

		bgmListView = (ListView)findViewById(R.id.bgmList);
		bgmListView.setAdapter(adapter);
		bgmListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		bgmListView.setOnItemClickListener(clickListener);
	}

	public static void bgmLoadingThStart(final Context ctx) {
		if(loadingTh == null) {
			loadingTh = new Thread(){
				public void run(){
					if(DEBUG) Log.d(BGM_TAG,"loadingTh start");

					try {
						loadCompleted.acquire();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					String[] itemList = null;
					InputStream in = null;
					OutputStream out = null;
					AssetManager am = ctx.getAssets();

					bgmPath = ctx.getFilesDir().getPath().toString();

					try {
						itemList = am.list("bgm");

						for(int i=0; i<itemList.length; i++) {
							if(DEBUG) Log.d(BGM_TAG,"item  = " + itemList[i] + ", bgmPath="+bgmPath );

							File f = new File(bgmPath +"/"+ itemList[i]);

							if(f.createNewFile()) {
								out = new FileOutputStream(bgmPath +"/"+ itemList[i]);
								in = am.open("bgm/"+itemList[i]);

								copyFile(in, out);
								
								in.close();
								in = null;
								out.flush();
								out.close();
								out = null;
							}

							bgmList.add(itemList[i]);
						}
					
					} catch (IOException e) {
						Log.d(BGM_TAG,"fileLoading() fail");
						e.printStackTrace();
					} finally {
						loadCompleted.release();
					}
				}
			};
			loadingTh.start();
		}
	}

	private static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int ret = 0;

		while( (ret =in.read(buffer)) != -1 ) {
			out.write(buffer, 0, ret);
		}

		in.close();
		in = null;
		out.flush();
		out.close();
		out = null;
	}

	private OnItemClickListener clickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			String str = (String)adapter.getItem(position);
			Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();

			if(audioPlayer.isPlaying()) {
				if(DEBUG) Log.d(BGM_TAG,"isPlaying false case");
				audioPlayer.stop();
			}

			audioPlayer.reset();

			// Play the BGM.
			playBGM(str);
		}

		private void playBGM(String bgmName) {
			selectedBGMPath = bgmPath +"/"+ bgmName;
			if(DEBUG) Log.d(BGM_TAG,"BGM path= " + selectedBGMPath);

			try {
				FileInputStream fis = new FileInputStream(selectedBGMPath);
				FileDescriptor fd = fis.getFD();
				audioPlayer.setDataSource(fd);
				audioPlayer.prepare();
				audioPlayer.start();
				fis.close();
			} catch (FileNotFoundException e) {
				Log.d(BGM_TAG,"playBGM() fail : FileNotFoundException");
				e.printStackTrace();
			} catch (IOException e) {
				Log.d(BGM_TAG,"playBGM() fail : IOException");
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onPause() {
		if(audioPlayer.isPlaying()) {
			if(DEBUG) Log.d(BGM_TAG,"isPlaying false case");
			audioPlayer.stop();
			audioPlayer.reset();
		}

		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_bgm, menu);
		return true;
	}

}
