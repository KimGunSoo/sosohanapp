package com.sosohan.snapmv;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.media.MediaCodec;
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

//import android.media.videoeditor.VideoEditor;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MakeMVActivity extends Activity {
	
	private ArrayList<String> decodingArray;
	private MediaExtractor extractor;
	private MediaCodec decoder;
	private MediaFormat mediaFormat = null;
	String mime = null;
	
	private Button btnTest;
	private View.OnClickListener btnTestListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_mv);
		
		btnTest = (Button) findViewById(R.id.test_btn);
		
		extractor = new MediaExtractor();
		Intent intent = getIntent();
		decodingArray = (ArrayList<String>) intent.getSerializableExtra("videolist");
		btnTestListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v== btnTest)
				{
					appendMV();
				}
			}
		};
		
		btnTest.setOnClickListener(btnTestListener);
	}
	private void appendMV()
	{
		extractor.setDataSource(decodingArray.get(1));
		
		int numTracks = extractor.getTrackCount();
		//for(int i = 0 ; i < numTracks ; i ++)		
		{
			int i = 0;
			mediaFormat = extractor.getTrackFormat(i);
			mime = mediaFormat.getString(mediaFormat.KEY_MIME);
			Log.e("JWJWJW", "numTracks" + i + ": " + mime);
			Log.e("JWJWJW", "WIDTH" + i + ": " + mediaFormat.getInteger(mediaFormat.KEY_WIDTH));
			Log.e("JWJWJW", "HEIGHT" + i + ": " + mediaFormat.getInteger(mediaFormat.KEY_HEIGHT));
		}
		decoder = MediaCodec.createDecoderByType(mime);
		decoder.configure(mediaFormat, null, null, 0);
		decoder.start();
		ByteBuffer[] inputBuffers = decoder.getInputBuffers();
		ByteBuffer[] outputBuffers = decoder.getOutputBuffers();
		extractor.selectTrack(0);
/*
		for (;;) {
			int inputBufferIndex = codec.dequeueInputBuffer(timeoutUs);
			if (inputBufferIndex >= 0) {
				// fill inputBuffers[inputBufferIndex] with valid data
				...
				codec.queueInputBuffer(inputBufferIndex, ...);
			}

			int outputBufferIndex = codec.dequeueOutputBuffer(timeoutUs);
			if (outputBufferIndex >= 0) {
				// outputBuffer is ready to be processed or rendered.
				...
				codec.releaseOutputBuffer(outputBufferIndex, ...);
			} else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
				outputBuffers = codec.getOutputBuffers();
			} else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
				// Subsequent data will conform to new format.
				MediaFormat format = codec.getOutputFormat();
				...
			}
		}
*/		
		boolean inEOS = false;
		boolean outEOS = false;
		final long kTimeOutUs = 5000;
		MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
		int numBytesDecoded = 0;
		
		while (!outEOS)
		{
			if(!inEOS) {
				int inBufIdx = decoder.dequeueInputBuffer(kTimeOutUs);
				if(inBufIdx >= 0) {
					ByteBuffer dstBuf = inputBuffers[inBufIdx];
					
					int size = extractor.readSampleData(dstBuf, 0);
					long ptTimeUs = 0;
					
					if(size < 0) {
						size = 0;
						Log.d("JWJWJW", "inEOS");
						inEOS = true;
						//break;
					}
					ptTimeUs = extractor.getSampleTime();
					
					decoder.queueInputBuffer(inBufIdx, 0, size, ptTimeUs, inEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
					Log.e("JWJWJW", "size =" +size);
					Log.e("JWJWJW", "ptTimeUs =" +ptTimeUs);
					if(!inEOS) {
						extractor.advance();					
					}
				}
			}
			
			int res = decoder.dequeueOutputBuffer(info, kTimeOutUs);
			if (res > 0) {
				int outBufIdx = res;
				ByteBuffer buf = outputBuffers[outBufIdx];
				
				{
					int limit = buf.limit();
					int pos = buf.position();
					Log.d("JWJWJW", "buf limit = " + limit + ", position = " + pos);
					Log.d("JWJWJW", "info offset = " + info.offset + ", size = " + info.size);
				}
				
				decoder.releaseOutputBuffer(outBufIdx, false /*render*/);
				if((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
				{
					Log.e("JWJWJW","BUFFER_FLAG_END_OF_STREAM");
					outEOS = true;
				}				
			} else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED){
				Log.d("JWJWJW","INFO_OUTPUT_BUFFERS_CHANGED");
			} else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
				Log.d("JWJWJW","INFO_OUTPUT_FORMAT_CHANGED");
			}
			//Log.d("JWJWJW","working");
		}
				
		decoder.stop();
		decoder.release();
		decoder = null;		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.make_mv, menu);
		return true;
	}
	
}
