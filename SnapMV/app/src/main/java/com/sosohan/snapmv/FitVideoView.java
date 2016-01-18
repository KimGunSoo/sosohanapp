package com.sosohan.snapmv;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.VideoView;

/**
 * Created by gg on 16. 1. 18.
 */
public class FitVideoView extends VideoView {
    private int width;
    private int height;

    private double ratio;

    public FitVideoView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
    @Override
    protected  void onMeasure(int width, int height) {
        Display dis = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width=dis.getWidth();
        height= (int)(dis.getWidth() * (3 / 4.0));
        Log.v("FitVideoView","w:"+width+" h:"+height);
        setMeasuredDimension(width, height);
    }
}
