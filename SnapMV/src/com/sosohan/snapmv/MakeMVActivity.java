package com.sosohan.snapmv;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
		Intent intent = getIntent();
		decodingArray = (ArrayList<String>) intent.getSerializableExtra("videolist");
		
		extractor = new MediaExtractor();
		
		btnTestListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v== btnTest)
				{
					debugDumpOpen();
					appendMV();
					finish();
				}
			}
		};
		
		btnTest.setOnClickListener(btnTestListener);		
		 // Find a code that supports the mime type	   
	}
	
	private MediaCodec videoEncoder;
	private void initVideoEncoder(String mimeType)
	{	
		videoEncoder = MediaCodec.createEncoderByType(mimeType);
		Log.i("JWJWJW initVideoEncoder", "MediaCodec.createEncoderByType "+mimeType);
		MediaFormat mediaFormat = MediaFormat.createVideoFormat(mimeType, 480,368);
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 125000);
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
	    mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,findColorFormat(mimeType));
	    mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
	    videoEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
	    videoEncoder.start();
	}
	private int findColorFormat(String mimeType)
	{
		int colorFormat = -1;
		int numCodecs = MediaCodecList.getCodecCount();
	    MediaCodecInfo codecInfo = null;
	    for (int i = 0; i < numCodecs && codecInfo == null; i++) {
	        MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
	        if (!info.isEncoder()) {
	            continue;
	        }
	        String[] types = info.getSupportedTypes();
	        boolean found = false;
	        for (int j = 0; j < types.length && !found; j++) {
	            if (types[j].equals(mimeType))
	                found = true;
	        }
	        if (!found)
	            continue;
	        codecInfo = info;
	    }
	    if(codecInfo == null)
	    {
	    	Log.i("JWJWJW findColorFormat", "not found codec supporting" + mimeType);
	    	return colorFormat;
	    }
	    
	    Log.i("JWJWJW findColorFormat", "Found " + codecInfo.getName() + " supporting " + mimeType);
	    
	    MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
	    for (int i = 0; i < capabilities.colorFormats.length && colorFormat == -1; i++) {
	        int format = capabilities.colorFormats[i];
	        switch (format) {
	        case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
	        case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
	        case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
	        case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
	        case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
	            colorFormat = format;
	            break;
	        default:
	        	 Log.i("JWJWJW", "Skipping unsupported color format " + format);
	            break;
	        }
	    }
	    Log.i("JWJWJW", "Using color format " + colorFormat);
	    return colorFormat;
	}
	
	FileOutputStream fos;
	FileChannel fcOut;
	private void debugDumpOpen()
	{		
		try {
			fos = new FileOutputStream("/sdcard/jw.mp4");
			fcOut = fos.getChannel();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	private void debugDumpWrite(ByteBuffer buf)
	{
		try {
			fcOut.write(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void appendMV()
	{
		Log.e("JWJWJW", "appendMV : " + decodingArray.get(1));
		extractor.setDataSource(decodingArray.get(1));
			
		int numTracks = extractor.getTrackCount();		
		int i = 0;
		mediaFormat = extractor.getTrackFormat(i);
		int width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
		int height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
				
		extractor.selectTrack(0);
		
		mime = mediaFormat.getString(MediaFormat.KEY_MIME);
		Log.e("JWJWJW", "numTracks" + i + ": " + mime);
		Log.e("JWJWJW", "WIDTH" + i + ": " + width);
		Log.e("JWJWJW", "HEIGHT" + i + ": " + height);
		
		decoder = MediaCodec.createDecoderByType(mime);
		decoder.configure(mediaFormat, null, null, 0);
		decoder.start();
		
		ByteBuffer[] inputBuffers = decoder.getInputBuffers();
		ByteBuffer[] outputBuffers = decoder.getOutputBuffers();

		boolean inEOS = false;
		boolean outEOS = false;
		final long kTimeOutUs = 10000;
		MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
		int numBytesDecoded = 0;

		//initVideoEncoder(mime);
		Log.d("JWJWJW", "init ok");
		//while (i < 90)
		while (!outEOS)
		{
			if(!inEOS) {
				int inBufIdx = decoder.dequeueInputBuffer(kTimeOutUs);
				Log.d("JWJWJW", "inBufIdx:" + inBufIdx);
				if(inBufIdx >= 0) {					
					ByteBuffer dstBuf = inputBuffers[inBufIdx];
					int size = extractor.readSampleData(dstBuf, 0);
					Log.d("JWJWJW", "size:" + size);
					long ptTimeUs = 0;
					if(size < 0) {
						Log.d("JWJWJW", "inEOS");
						size = 0;						
						inEOS = true;
						//break;
					}else {
						ptTimeUs = extractor.getSampleTime();
					}
					decoder.queueInputBuffer(inBufIdx, 0, size, ptTimeUs, inEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
					Log.d("JWJWJW", "ptTimeUs =" +ptTimeUs);
					if(!inEOS) {
						extractor.advance();					
					}
				}
			}
			int res = decoder.dequeueOutputBuffer(info, kTimeOutUs);
			Log.d("JWJWJW", "outBufIdx (?) :" + res);
			if (res >= 0) {
				int outBufIdx = res;
				ByteBuffer buf = outputBuffers[outBufIdx];
				buf.position(info.offset);
				buf.limit(info.offset + info.size);
				
				{
					//int limit = buf.limit();
					//int pos = buf.position();
					//Log.d("JWJWJW decoder", "buf limit = " + limit + ", position = " + pos);
					//	Log.d("JWJWJW decoder", "info offset = " + info.offset + ", size = " + info.size);
				}
				debugDumpWrite(buf);
				decoder.releaseOutputBuffer(outBufIdx, false /*render*/);
				if((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
				{
					Log.e("JWJWJW","BUFFER_FLAG_END_OF_STREAM");
					outEOS = true;
				}				
			} else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED){
				outputBuffers = decoder.getOutputBuffers();
				Log.d("JWJWJW","INFO_OUTPUT_BUFFERS_CHANGED");
			} else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
				MediaFormat oformat = decoder.getOutputFormat();
				Log.d("JWJWJW","INFO_OUTPUT_FORMAT_CHANGED" + oformat);
			}else if (res == MediaCodec.INFO_TRY_AGAIN_LATER){
				Log.d("JWJWJW","INFO_TRY_AGAIN_LATER" );
				try {
					Thread.sleep(500);
					i ++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}


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
	
	void writeVideoData(byte[] input)
	{
		
	    try {
	        ByteBuffer[] inputBuffers = videoEncoder.getInputBuffers();
	        ByteBuffer[] outputBuffers = videoEncoder.getOutputBuffers();
	        int inputBufferIndex = videoEncoder.dequeueInputBuffer(-1);
	        if (inputBufferIndex >= 0) {
	            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
	            inputBuffer.clear();
	            inputBuffer.put(input);
	            videoEncoder.queueInputBuffer(inputBufferIndex, 0, input.length, 0, 0);
	        }

	        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
	        int outputBufferIndex = videoEncoder.dequeueOutputBuffer(bufferInfo,0);
	        while (outputBufferIndex >= 0) {
	            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
	            byte[] outData = new byte[bufferInfo.size];
	            outputBuffer.get(outData);
	           // outputStream.write(outData, 0, outData.length);
	            Log.i("JWJWJW AvcEncoder", outData.length + " bytes written");

	            videoEncoder.releaseOutputBuffer(outputBufferIndex, false);
	            outputBufferIndex = videoEncoder.dequeueOutputBuffer(bufferInfo, 0);
	        }
	    } catch (Throwable t) {
	        t.printStackTrace();
	    }
		
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.make_mv, menu);
		return true;
	}
	
}
//	int res = decoder.dequeueOutputBuffer(info, kTimeOutUs);
//	if (res >= 0) {
//		int outBufIdx = res;
//		ByteBuffer buf = outputBuffers[outBufIdx];
//
//		{
//			int limit = buf.limit();
//			int pos = buf.position();
//			Log.d("JWJWJW decoder", "buf limit = " + limit + ", position = " + pos);
//			Log.d("JWJWJW decoder", "info offset = " + info.offset + ", size = " + info.size);
//			
////			for (int i = 0; i < info.size; i += 2) {
////				short sample = buf.getShort(i);
////				Log.i("JWJWJW AvcEncoder", sample + " short bytes");							
////			}
//			//byte[] myData = new byte[info.size];
//			//buf.get(myData);
//			//writeVideoData(myData);
//			
//
//			//Log.i("JWJWJW AvcEncoder", outData.length + " bytes written");
//		}
//		debugDumpWrite(buf);
//		decoder.releaseOutputBuffer(outBufIdx, false /*render*/);
//		if((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
//		{
//			Log.e("JWJWJW","BUFFER_FLAG_END_OF_STREAM");
//			outEOS = true;
//		}				
//	} else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED){
//		outputBuffers = decoder.getOutputBuffers();
//		Log.d("JWJWJW","INFO_OUTPUT_BUFFERS_CHANGED");
//	} else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
//		MediaFormat oformat = decoder.getOutputFormat();
//		Log.d("JWJWJW","INFO_OUTPUT_FORMAT_CHANGED" + oformat);
//	}
//	//Log.d("JWJWJW","working");
//}
//
