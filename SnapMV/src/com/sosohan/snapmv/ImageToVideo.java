package com.sosohan.snapmv;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ImageToVideo {
	// untested function
	Bitmap  b;
	MediaCodec mediaCodec;
	private BufferedOutputStream outputStream;
	
	public ImageToVideo(){		
		b = Bitmap.createBitmap(640, 480, Bitmap.Config.RGB_565);
		File f = new File("/sdcard/DCIM/video_encoded.264");
		Log.e("ImageToVideo", "initialized");
	    try {
	        outputStream = new BufferedOutputStream(new FileOutputStream(f));
	        Log.i("AvcEncoder", "outputStream initialized");
	    } catch (Exception e){ 
	        e.printStackTrace();
	    }
	}

	public void test()
	{	
		Log.e("ImageToVideo", "test");
		Paint whitePaint = new Paint();
		whitePaint.setTextSize(30);
		whitePaint.setColor(Color.WHITE);
		Canvas c = new Canvas( b );
		c.drawText("this is SnapMV test", 100, 300, whitePaint);
		
		byte[] yuvbyte = getNV21(640,480,b);
		mediaCodec = MediaCodec.createEncoderByType("video/mp4v-es");
		MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/mp4v-es", 640,480);
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1250000);
	    mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
	    mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
	    mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
	    mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
	    mediaCodec.start();
	    for(int i = 0; i < 4 ; i++)
	    	offerEncoder(yuvbyte);
	    try {
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	   
	}
	
	public void offerEncoder(byte[] input) {
	    try {
	        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
	        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
	        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
	        if (inputBufferIndex >= 0) {
	            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
	            inputBuffer.clear();
	            Log.i("AvcEncoder", "offerEncoder inputBuffer.limit() = "+inputBuffer.limit());
	            inputBuffer.put(input);
	            
	            Log.i("AvcEncoder", "offerEncoder input.length = "+ input.length);
	            mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, 0, 0);
	        }
	        Log.i("AvcEncoder", "offerEncoder inputBufferIndex = "+inputBufferIndex);
	        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
	        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,0);
	        while (outputBufferIndex >= 0) {
	            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
	            byte[] outData = new byte[bufferInfo.size];
	            outputBuffer.get(outData);
	            outputStream.write(outData, 0, outData.length);
	            Log.i("AvcEncoder", outData.length + " bytes written");

	            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
	            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);

	        }
	        Log.i("AvcEncoder", "offerEncoder outputBufferIndex = "+outputBufferIndex);
	    } catch (Throwable t) {
	        t.printStackTrace();
	    }

	}
	
    byte [] getNV21(int inputWidth, int inputHeight, Bitmap scaled) {

        int [] argb = new int[inputWidth * inputHeight];

        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

        byte [] yuv = new byte[inputWidth*inputHeight*3/2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);

        scaled.recycle();
        
        return yuv;
    }

    void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ( (  66 * R + 129 * G +  25 * B + 128) >> 8) +  16;
                U = ( ( -38 * R -  74 * G + 112 * B + 128) >> 8) + 128;
                V = ( ( 112 * R -  94 * G -  18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) { 
                    yuv420sp[uvIndex++] = (byte)((V<0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte)((U<0) ? 0 : ((U > 255) ? 255 : U));
                }

                index ++;
            }
        }
    }

}
