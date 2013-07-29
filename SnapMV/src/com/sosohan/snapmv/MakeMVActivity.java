package com.sosohan.snapmv;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MakeMVActivity extends Activity {
	
	private SnapFileWriter debugFile = null;
	private final String intermediateVideo = "/sdcard/snap.h264";
	
	private String selectedAudio = "/sdcard/exam.m4a";
	private final String outputMV = "/sdcard/DCIM/snap.mp4";
	private void debugDumpOpen()
	{	
		debugFile = new SnapFileWriter();		
		debugFile.open("/sdcard/jw.yuv");
	}
	private void debugDumpClose()
	{
		debugFile.close();
		debugFile = null;
	}
	private void debugDumpWrite(ByteBuffer buf)
	{
		if (debugFile == null)
		{
			Log.e("JWJWJW", "debug file is not opened.");
		}
		debugFile.write(buf);
	}
	
	private SnapFileWriter outputFile = null;
	private void outputOpen()
	{	
		outputFile = new SnapFileWriter();		
		outputFile.open(intermediateVideo);
	}
	private void outputClose()
	{
		outputFile.close();
		outputFile = null;
	}
	private void outputWrite(ByteBuffer buf)
	{
		if (outputFile == null)
		{
			Log.e("JWJWJW", "output file is not opened.");
		}
		outputFile.write(buf);
	}

	private ArrayList<String> decodingArray;
	private Button btnTest;
	private View.OnClickListener btnTestListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_mv);
		debugFile = new SnapFileWriter();
		
		btnTest = (Button) findViewById(R.id.test_btn);		
		Intent intent = getIntent();
		decodingArray = (ArrayList<String>) intent.getSerializableExtra("videolist");
				
		btnTestListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(v== btnTest)
				{
					btnTest.setClickable(false);
					
					new Thread(new Runnable(){
						public void run(){
							//debugDumpOpen();
							outputOpen();
							for (int i = 0; i < decodingArray.size() ; i ++)
							{
								appendMV(decodingArray.get(i));
							}
							makeMV();
							finish();
						}

						
					}).start();						
				}
			}
		};		
		btnTest.setOnClickListener(btnTestListener);	
	}
	
	List<Track> videoTracks = new LinkedList<Track>();
	void appendMV(String path)
	{
		try {
			 Movie m = MovieCreator.build(new FileInputStream(path).getChannel());
			 Track t = m.getTracks().get(0);
			 if (t.getHandler().equals("vide")) {
				 Log.v("JWJWJW","add "+path);
                 videoTracks.add(t);
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
		Movie result = new Movie();
		if (videoTracks.size() > 0) {
			Log.v("JWJWJW","make mv "+outputMV);
            try {
            	//for (Track videoTrack : videoTracks)
            	{
            	Log.v("JWJWJW","make mv "+ videoTracks);
				result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
				}
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

