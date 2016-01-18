package com.sosohan.snapmv;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;


import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class MakeMVActivity extends Activity {
	private String tag = "MakeMVActivity";
	private String outputMV = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath() ;// "/sdcard/DCIM/snap.mp4";
		
	private ArrayList<String> videoPaths;
	private String audioPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_mv);
		
		outputMV = outputMV + "/snapmv" 
		+ Calendar.getInstance().get(Calendar.YEAR)
		+ (Calendar.getInstance().get(Calendar.MONTH)+1)
		+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
		+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
		+ Calendar.getInstance().get(Calendar.MINUTE)
		+ Calendar.getInstance().get(Calendar.SECOND)
		+".mp4";
		
		Intent intent = getIntent();
		videoPaths = (ArrayList<String>) intent.getSerializableExtra("videolist");
		audioPath = (String) intent.getExtras().getString("audioPath");
		Log.i(tag, videoPaths + "," + audioPath);
			
		new Thread(new Runnable(){
			public void run(){					
				for (int i = 0; i < videoPaths.size() ; i ++)
				{
					appendMV(videoPaths.get(i));
				}
				makeMV();
				videoPaths.clear();
				videoPaths.add(outputMV);
				Intent intent = new Intent(MakeMVActivity.this,
						PreviewActivity.class);
				intent.putStringArrayListExtra("videolist", videoPaths);	
				startActivity(intent);
				MediaScannerConnection.scanFile(getApplicationContext(), new String[]{outputMV}, new String[]{"video/mpeg4"}, null);
				//sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
				//		Uri.parse("file://"+ Environment.getExternalStorageDirectory())));	//refresh SD
				finish();
			}						
		}).start();	
				
	}
	
	List<Track> videoTracks = new LinkedList<Track>();
	void appendMV(String path)
	{
		tag = "MakeMVA_appendMV()";
		try {
			 Movie m = MovieCreator.build(new FileInputStream(path).getChannel());
			 Track t = m.getTracks().get(0);
			 if (t.getHandler().equals("vide")) {
				 Log.v(tag,"add "+path);
                 videoTracks.add(t);
             }else
             {
            	 Log.w(tag, path + "do NOT have a video in track 0.");
             }			 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void makeMV()
	{
		tag = "MakeMVA_makeMV()";
		Movie result = new Movie();
		if (videoTracks.size() > 0) {
			Log.v(tag,"make mv "+outputMV);
            try {
            	new MovieCreator();
				Movie audiofile = MovieCreator.build(new FileInputStream(audioPath).getChannel());
            	Track audio = audiofile.getTracks().get(0);
            	Log.v(tag,"videoTracks"+ videoTracks);
				result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));	
				result.addTrack(audio);
				IsoFile out = new DefaultMp4Builder().build(result);
				FileOutputStream fos;
				fos = new FileOutputStream(new File(outputMV));
				out.getBox(fos.getChannel());
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
}

