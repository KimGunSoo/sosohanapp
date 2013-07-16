package com.sosohan.snapmv;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
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
	final static String DATA_PATH = "/data/data/com.sosoan.snapmv/";

	private ListView bgmListView;
	private ArrayList<String> bgmList = new ArrayList<String>();
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_bgm);

		/*
		fileScan(DATA_PATH, bgmList);
		*/
		bgmList.add("AA");
		bgmList.add("BB");
		bgmList.add("CC");
		//*/
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bgmList);
		
		bgmListView = (ListView)findViewById(R.id.bgmList);
		bgmListView.setAdapter(adapter);
		bgmListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		bgmListView.setOnItemClickListener(clickListener);

	}
	
	private void fileScan(String dataPath, ArrayList<String> list) {
		File f = new File(dataPath);
		File[] fileList = null;
		fileList = f.listFiles();
		
		for(int i=0; i< fileList.length; i++) {
			Log.d(BGM_TAG,"item  = " + fileList[i].getName() );
		}
	}

	private OnItemClickListener clickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			String str = (String)adapter.getItem(position);
			Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_bgm, menu);
		return true;
	}

}
