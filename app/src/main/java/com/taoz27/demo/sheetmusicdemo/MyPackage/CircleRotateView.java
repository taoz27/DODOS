package com.taoz27.demo.sheetmusicdemo.MyPackage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.taoz27.demo.sheetmusicdemo.R;

/**
 * Created by taoz27 on 2017/4/11.
 */
public class CircleRotateView extends View {
    /**专辑图片旋转线程睡眠时间*/
    public static final int ALBUM_ROTATE_THREAD_SLEEP_TIME=25;
    /**专辑图片每分钟转的圈数*/
    public static final float ALBUM_TURNS_PER_MINUTE=1f;
    /**专辑图片每次旋转的角度*/
    public static final float ALBUM_ROTATE_PRE_ANGLE=(ALBUM_TURNS_PER_MINUTE*360f/60)/(1000f/ALBUM_ROTATE_THREAD_SLEEP_TIME);

    private AlbumPlayer albumPlayer;
    private int src;
//    private int mode;
    private float angle;
    private Bitmap bitmap,defaultBmp;
    private Thread thread;
//    private Boolean needRotate;
    private int dstWidth,dstHeight;
    String curPlay="";
    private boolean isRunning;

    public CircleRotateView(Context context) {
        super(context);

//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleRotateView);
//        src=a.getDimensionPixelSize(R.styleable.CircleRotateView_src,android.R.mipmap.sym_def_app_icon);
//        a.recycle();
//        mode=0;
        defaultBmp= BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        bitmap=defaultBmp;
//        needRotate=false;
        angle=0;
    }

    public CircleRotateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleRotateView);
        src=a.getDimensionPixelSize(R.styleable.CircleRotateView_src,android.R.mipmap.sym_def_app_icon);
        a.recycle();
//        mode=0;
        defaultBmp= BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        bitmap=defaultBmp;
//        needRotate=false;
        angle=0;
    }

    private int getSize(int defaultSize,int measureSpec){
        int mySize=defaultSize;
        int mode= MeasureSpec.getMode(measureSpec);
        int size= MeasureSpec.getSize(measureSpec);
        switch (mode){
            case MeasureSpec.UNSPECIFIED:
                mySize=defaultSize;
                break;
            case MeasureSpec.EXACTLY:
                mySize=size;
                break;
            case MeasureSpec.AT_MOST:
                mySize=size;
                break;
        }
        return mySize;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=getSize(100, widthMeasureSpec);
        int height=getSize(100, heightMeasureSpec);

        if (width<height)height=width;
        else width=height;
//        Log.e(this.toString(), width + " " + height);

        int pl=getPaddingLeft(),pt=getPaddingTop(),pr=getPaddingRight(),pb=getPaddingBottom();
        dstWidth=width-pl-pr;
        dstHeight=height-pb-pt;

        setMeasuredDimension(width, height);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawARGB(255, 255, 255, 0);
        int width=getMeasuredWidth();
        int height=getMeasuredHeight();
//        Log.e("Myview",width+" "+height);
        int pl=getPaddingLeft(),pt=getPaddingTop(),pr=getPaddingRight(),pb=getPaddingBottom();
        Rect srcR=new Rect(0,0,width-pl-pr,height-pb-pt);
        Rect dstR=new Rect(pl,pt,width-pr,height-pb);
        canvas.rotate(angle,width/2,height/2);
        int layoutId=canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        Paint paint= new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawCircle(width / 2, height / 2, width / 2 - pr, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        Log.e("Myview",((Boolean)(src==android.R.mipmap.sym_def_app_icon)).toString());
        canvas.drawBitmap(bitmap, srcR, dstR, paint);
        canvas.restoreToCount(layoutId);
//        if (!thread.isAlive()) {
//            thread.start();
//        }
    }

//    public void rotate(boolean needRotate,float angle){
//        this.needRotate=needRotate;
//        if (needRotate)
//            this.angle=angle;
//    }
//    public void rotate(boolean needRotate){
//        this.needRotate=needRotate;
//    }
    private boolean setBitmap(Bitmap bitmap){
        Bitmap tempBmp;
        if (bitmap!=null) {
            tempBmp = bitmap;
        }else {
            tempBmp=defaultBmp;
        }
        if (tempBmp.getWidth()<=0||tempBmp.getHeight()<=0||
                dstWidth<=0||dstHeight<=0)return false;
        this.bitmap=Bitmap.createScaledBitmap(tempBmp, dstWidth, dstHeight, false);
        angle=0;
        return true;
    }
    public void setAlbumPlayer(AlbumPlayer player){
        this.albumPlayer = player;

        thread =new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(ALBUM_ROTATE_THREAD_SLEEP_TIME);
                        if (albumPlayer!=null&&albumPlayer.isPlaying()) {
                            if (albumPlayer.getCurrentAlbum()!=null&&!curPlay.endsWith(albumPlayer.getMusicName())){
                                if (setBitmap(albumPlayer.getCurrentAlbum())){
                                    curPlay=albumPlayer.getMusicName();
                                }
                            }
                            handler.post(rotate);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    Handler handler=new Handler();

    Runnable rotate=new Runnable() {
        @Override
        public void run() {
            angle+= ALBUM_ROTATE_PRE_ANGLE;
//                mode++;
//                if (mode>15)mode=0;
            if (angle>360)angle=0;
            invalidate();
        }
    };

    public interface AlbumPlayer{
        boolean isPlaying();
        String getMusicName();
        Bitmap getCurrentAlbum();
    }

    @Override
    protected void onDetachedFromWindow() {
        isRunning=false;
        super.onDetachedFromWindow();
    }
}