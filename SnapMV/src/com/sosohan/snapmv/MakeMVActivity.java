package com.sosohan.snapmv;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import java.util.ArrayList;

import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.H264TrackImpl;





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
							destroyVideoEncoder();
							outputClose();
							makeMP4();
							//debugDumpClose();
							finish();
						}

						
					}).start();						
				}
			}
		};		
		btnTest.setOnClickListener(btnTestListener);	
	}
	
	private MediaCodec videoEncoder;
	private void initVideoEncoder(String mimeType, int width, int height)
	{			
		videoEncoder = MediaCodec.createEncoderByType(mimeType);
		Log.i("JWJWJW initVideoEncoder", "MediaCodec.createEncoderByType "+mimeType);
		MediaFormat mediaFormat = MediaFormat.createVideoFormat(mimeType, width, height);
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 3000000);
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
	    mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, findColorFormat(mimeType));
	    mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
	    videoEncoder.configure(mediaFormat,null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
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
	
	private void destroyVideoEncoder() 
	{
		videoEncoder.stop();
		videoEncoder.release();
		videoEncoder = null;
	}
	
	private MediaCodec videoDecoder;
	boolean isInitVideoEncoder = false;
	private void appendMV(String path)
	{
		String mime = null;
		MediaFormat mediaFormat = null;
		MediaExtractor extractor = null;
		
		extractor = new MediaExtractor();
		Log.v("JWJWJW", "appendMV : " + path);
		extractor.setDataSource(path);
		
		int tracks = extractor.getTrackCount();		
		for (int i = 0; i < tracks ; i++)
		{
			mediaFormat = extractor.getTrackFormat(i);
			mime = mediaFormat.getString(MediaFormat.KEY_MIME);
			if (mime.startsWith("video/")){
				extractor.selectTrack(i);
				Log.v("JWJWJW", "numTracks" + i + ": " + mime + 
						":KEY_WIDTH:" +mediaFormat.getInteger(MediaFormat.KEY_WIDTH) + 
						":KEY_HEIGHT:" +mediaFormat.getInteger(MediaFormat.KEY_HEIGHT));
				
			}			
			break;
		}
							
		videoDecoder = MediaCodec.createDecoderByType(mime);
		videoDecoder.configure(mediaFormat, null, null, 0);
		videoDecoder.start();
		
		ByteBuffer[] inputBuffers = videoDecoder.getInputBuffers();
		ByteBuffer[] outputBuffers = videoDecoder.getOutputBuffers();

		boolean inEOS = false;
		boolean outEOS = false;
		final long kTimeOutUs = 10000;
		MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
		
		if(!isInitVideoEncoder ) {
			initVideoEncoder(mime, mediaFormat.getInteger(MediaFormat.KEY_WIDTH), mediaFormat.getInteger(MediaFormat.KEY_HEIGHT));
			Log.d("JWJWJW", "init ok");
			isInitVideoEncoder = true;
		}
		while (!outEOS)	{
			if(!inEOS) {
				int inBufIdx = videoDecoder.dequeueInputBuffer(kTimeOutUs);
				//Log.d("JWJWJW", "inBufIdx:" + inBufIdx);
				if(inBufIdx >= 0) {					
					ByteBuffer dstBuf = inputBuffers[inBufIdx];
					int size = extractor.readSampleData(dstBuf, 0);
					//Log.d("JWJWJW", "size:" + size);
					long ptTimeUs = 0;
					if(size < 0) {
						Log.d("JWJWJW", "inEOS");
						size = 0;						
						inEOS = true;						
					}else {
						ptTimeUs = extractor.getSampleTime();
					}
					videoDecoder.queueInputBuffer(inBufIdx, 0, size, ptTimeUs, inEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
					//Log.d("JWJWJW", "ptTimeUs =" +ptTimeUs);
					if(!inEOS) {
						extractor.advance();					
					}
				}
			}
			int res = videoDecoder.dequeueOutputBuffer(info, kTimeOutUs);
			Log.d("JWJWJW", "outBufIdx (?) :" + res);
			if (res >= 0) {
				int outBufIdx = res;
				ByteBuffer buf = outputBuffers[outBufIdx];
				Log.d("JWJWJW", "info.offset :" + info.offset + "info.size :" + info.size);
				buf.position(info.offset);
				buf.limit(info.size);
				
				//debugDumpWrite(buf);
				writeVideoData(buf);
				videoDecoder.releaseOutputBuffer(outBufIdx, false /*render*/);
				if((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
				{
					Log.i("JWJWJW","BUFFER_FLAG_END_OF_STREAM");
					outEOS = true;
				}				
			} else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED){
				outputBuffers = videoDecoder.getOutputBuffers();
				Log.d("JWJWJW","INFO_OUTPUT_BUFFERS_CHANGED");
			} else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
				MediaFormat oformat = videoDecoder.getOutputFormat();
				Log.d("JWJWJW","INFO_OUTPUT_FORMAT_CHANGED" + oformat);
			}
//			else if (res == MediaCodec.INFO_TRY_AGAIN_LATER){
//				Log.d("JWJWJW","INFO_TRY_AGAIN_LATER" );
//				try {
//					Thread.sleep(500);
//					i ++;
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}				
//			}
		}
		videoDecoder.stop();
		videoDecoder.release();
		videoDecoder = null;	
	}
	void writeVideoData(ByteBuffer input)
	{
		ByteBuffer[] inputBuffers = videoEncoder.getInputBuffers();
		ByteBuffer[] outputBuffers = videoEncoder.getOutputBuffers();
		int inputBufferIndex = videoEncoder.dequeueInputBuffer(-1);
		if (inputBufferIndex >= 0) {
			ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
			inputBuffer.clear();
			inputBuffer.put(input);
			videoEncoder.queueInputBuffer(inputBufferIndex, 0, input.limit(), 0, 0);
		}

		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		int outputBufferIndex = videoEncoder.dequeueOutputBuffer(bufferInfo,0);
		Log.i("JWJWJW AvcEncoder", "outputBufferIndex:" + outputBufferIndex);
		while (outputBufferIndex >= 0) {
			ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
			outputBuffer.position(bufferInfo.offset);
			outputBuffer.limit(bufferInfo.size);
			outputWrite(outputBuffer);
			videoEncoder.releaseOutputBuffer(outputBufferIndex, false);
			outputBufferIndex = videoEncoder.dequeueOutputBuffer(bufferInfo, 0);
			Log.i("JWJWJW AvcEncoder", bufferInfo.size + " bytes written offset :"
					+ bufferInfo.offset );
		}
	}
	void makeMP4()
	{
		H264TrackImpl video;
		try {
			video = new H264TrackImpl(new BufferedInputStream(new FileInputStream(intermediateVideo)));
			
			Movie m = new Movie();
			m.addTrack(video);
			//m.addTrack(audio);
			IsoFile out = new DefaultMp4Builder().build(m);
			FileOutputStream fos = new FileOutputStream(new File(outputMV));
			out.getBox(fos.getChannel());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		btnTest.setClickable(true);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.make_mv, menu);
		return true;
	}
	
}

