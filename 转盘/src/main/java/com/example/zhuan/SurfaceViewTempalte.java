package com.example.zhuan;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by huanhuan on 2016/5/14.
 */
public class SurfaceViewTempalte extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private SurfaceHolder mHolder;
    private Canvas mCanvas;

    /**
     * 用于绘制的线程
     * */
    private Thread t;
    private boolean isRuning;


    public SurfaceViewTempalte(Context context) {
        this(context, null);
    }

    public SurfaceViewTempalte(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder=getHolder();
        mHolder.addCallback(this);
        setFocusable(true);//可获得焦点
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);//设置常量
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRuning=true;
        t=new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRuning=false;
    }

    @Override
    public void run() {
    //不断绘制
        while(isRuning){
            draw();
        }
    }
    private void draw(){
        try {
            mCanvas = mHolder.lockCanvas();
            if(mCanvas!=null){
                //draw something
            }
        } catch(Exception e){

        }finally {
            if(mCanvas!=null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }

    }
}
