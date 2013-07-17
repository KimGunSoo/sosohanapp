package com.sosohan.snapmv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PlayBgmActivity extends Activity {
	final static String BGM_TAG = "PlayBgmActivity";

	private ListView bgmListView;
	private ArrayList<String> bgmList = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private String bgm_path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_bgm);

		bmgFileLoading();
		initListView();
	}

	private void initListView() {
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bgmList);

		bgmListView = (ListView)findViewById(R.id.bgmList);
		bgmListView.setAdapter(adapter);
		bgmListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		bgmListView.setOnItemClickListener(clickListener);
	}

	private void bmgFileLoading() {
		AssetManager am = this.getAssets();
		String[] itemList = null;
		InputStream in = null;
		OutputStream out = null;
		File bgm_directory;

		bgm_path = this.getFilesDir().getPath().toString() + "/bgm";
		bgm_directory = new File(bgm_path);
		
		if(!bgm_directory.exists()) {

			try {
				itemList = am.list("bgm");

				for(int i=0; i<itemList.length; i++) {
					Log.d(BGM_TAG,"item  = " + itemList[i] );
					in = am.open("bgm/"+itemList[i]);
					out = new FileOutputStream(bgm_path + itemList[i]);
					copyFile(in, out);
					bgmList.add(itemList[i]);
				}

				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				Log.d(BGM_TAG,"fileLoading() fail");
				e.printStackTrace();
			}

		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
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

			//TODO: play the BGM selected by user.
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_bgm, menu);
		return true;
	}

}
