package com.example.zhuan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by huanhuan on 2016/5/14.
 */
public class LuckyPan extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private SurfaceHolder mHolder;
    private Canvas mCanvas;

    /**
     * 用于绘制的线程
     * */
    private Thread t;
    private boolean isRuning;//线程控制开关

    private String[] mStrs = new String[]{"单反相机","IPAD","恭喜发财","IPHONE","服装一套","恭喜发财"};
    private int[] mImgs = new int[]{R.drawable.danfan,R.drawable.ipad,R.drawable.f040,
            R.drawable.iphone,R.drawable.meizi,R.drawable.f015};
    private Bitmap[] mImgsBitmap;

    private int mItemCount = 6;

    private RectF mRange = new RectF();//盘快的范围

    private int mRadius;//盘快的直径

    private int  mCenter;//转盘的中心位置

    private int mPadding;//以paddingLeft为准

    private Paint mArcPaint;//绘制盘快的画笔

    private Paint mTextPaint;//绘制文本的画笔

    private int[] mColor = new int[]{
            0xFFFFC300,0xFFF17E01,0xFFFFC300,0xFFF17E01,0xFFFFC300,0xFFF17E01
    };
    private double mSpeed;

    private volatile float mStartAngle =0;//保证线程间的可见xing

    private boolean isShouleEnd;//是否点击了按钮

    private Bitmap mBgBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.bg2);

    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            20,getResources().getDisplayMetrics());


    public LuckyPan(Context context) {
        this(context, null);
    }

    public LuckyPan(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder=getHolder();

        mHolder.addCallback(this);
        setFocusable(true);//可获得焦点
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);//设置常量
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(),getMeasuredHeight());
        mPadding = getPaddingLeft();
        mRadius = width-mPadding*2;
        mCenter = width/2;
        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //初始化绘制盘快的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        //初始化绘制文字的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);
        //初始化盘快的绘制范围
        mRange = new RectF(mPadding,mPadding,mPadding+mRadius,mPadding+mRadius);
        //初始化图片
        mImgsBitmap = new Bitmap[mItemCount];
        for (int i=0;i<mItemCount;i++) {
            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(),mImgs[i]);

        }
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
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if(end-start<50){
                try {
                    Thread.sleep(50-(end-start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void draw(){
        try {
            mCanvas = mHolder.lockCanvas();
            if(mCanvas!=null){
                //draw something
                drawBg();//绘制背景
                //绘制盘快
                float tmpAngle = mStartAngle;
                float sweepAngle = 360/mItemCount;
                for(int i=0;i<mItemCount;i++){
                    mArcPaint.setColor(mColor[i]);
                    //绘制盘快
                    mCanvas.drawArc(mRange, tmpAngle,sweepAngle,true,mArcPaint);
                    //绘制文本
                    drawText(tmpAngle,sweepAngle,mStrs[i]);
                    //绘制图片
                    drawIcon(tmpAngle,mImgsBitmap[i]);
                    tmpAngle+=sweepAngle;
                }
                mStartAngle+=mSpeed;
               //点击停止按钮
                if(isShouleEnd){
                    mSpeed-=1;
                }if(mSpeed<=0){
                   mSpeed=0;
                    isShouleEnd=false;
                }
            }
        } catch(Exception e){

        }finally {
            if(mCanvas!=null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }

    }
    /*点击启动旋转***/
    public void luckStart(int index){
        //计算每一项的角度
        float angle = 360/mItemCount;
        //计算每一项的中奖范围
        float from = 270-(index+1)*angle;
        float end = from+angle;

        //设置停下来需要旋转的距离
        float targetFrom = 4*360+from;
        float targetEnd =  4*360+end;

        float v1 = (float)((-1+Math.sqrt(1+8*targetFrom))/2);
        float v2 = (float)((-1+Math.sqrt(1+8*targetEnd))/2);


        mSpeed=v1+Math.random()*(v2-v1);
        isShouleEnd=false;
    }
    public void luckyEnd(){
        mStartAngle=0;
        isShouleEnd=true;
    }
    /***
     *
     * 转盘是否在旋转
     * */
    public boolean isStart(){
        return mSpeed!=0;

    }
    public boolean isShouleEnd(){
        return isShouleEnd;
    }

    private void drawIcon(float tmpAngle, Bitmap bitmap) {
      //设置蹄片的宽度为直径的1/8
        int imgWidth = mRadius/8;
        float angle = (float) ((tmpAngle+360/mItemCount/2)*Math.PI/180);
        int x = (int) (mCenter+mRadius/2/2*Math.cos(angle));
        int y = (int) (mCenter+mRadius/2/2*Math.sin(angle));

        //确定那个图片的位置
        Rect rect = new Rect(x-imgWidth/2,y-imgWidth/2,x+imgWidth/2,y+imgWidth/2);
        mCanvas.drawBitmap(bitmap,null,rect,null);

    }

    //绘制文本
    private void drawText(float tmpAngle, float sweepAngle, String string) {
        Path path = new Path();
        path.addArc(mRange,tmpAngle,sweepAngle);

        //利用水平偏移量让字居中
        float textWidth = mTextPaint.measureText(string);
        int hOffset = (int)(mRadius*Math.PI/mItemCount/2-textWidth/2);
        int vOffset = mRadius/2/6;//垂直方向偏移量

        mCanvas.drawTextOnPath(string,path,hOffset,vOffset,mTextPaint);
        


    }

    //背景
    private void drawBg() {
        mCanvas.drawColor(( 0xffffffff));
        mCanvas.drawBitmap(mBgBitmap,null,new Rect(mPadding/2,
                mPadding/2,getMeasuredWidth()-mPadding/2,
                getMeasuredHeight()-mPadding/2),null);
    }
}
