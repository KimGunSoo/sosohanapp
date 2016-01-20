package com.sosohan.MVlog;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import android.util.Log;

public class SnapFileWriter {
	private final String tag = "SnapFileWriter";
	FileOutputStream fos = null;
	FileChannel fc = null;
			
	public void open(String path)
	{		
		try {
			fos = new FileOutputStream(path);
			fc = fos.getChannel();
			Log.v(tag, "file is opened");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public int write(ByteBuffer buf)
	{
		int res = -1;
		if (fc == null)
		{
			Log.e(tag, "file is not opened");
			return res;
		}		
		try {
			res = fc.write(buf);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(tag, "write fail");
			e.printStackTrace();			
		}
		return res;
	}
	
	public void close(){
		try {
			fos.flush();
			fc.close();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
