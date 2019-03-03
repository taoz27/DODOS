//package com.taoz27.demo.sheetmusicdemo.app;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.view.View;
//
//import com.taoz27.demo.sheetmusicdemo.R;
//
//import org.billthefarmer.mididriver.MidiDriver;
//
///**
// * Created by taoz27 on 2017/11/11.
// */
//
//public class CoolProgress extends View implements Runnable{
//    private static final double SCALE=1-0.618;/**center circle radius / piano key length*/
//
//    private int viewWidth,viewHeight;
//    private int centerX,centerY;
//    private int radius,lengthNormal,lengthPressed;
//
//    private Paint circlePaint,whitePaint,blackPaint,pathPaint;
//
//    private int pressed=0;
//    private MidiDriver midiDriver;
//    private byte[] event;
//
//    void initPaint(){
//        circlePaint=new Paint();
//        circlePaint.setColor(getResources().getColor(R.color.colorPrimary));
//
//        pathPaint=new Paint();
//        pathPaint.setColor(Color.BLACK);
//        pathPaint.setAntiAlias(true);
//        pathPaint.setStyle(Paint.Style.STROKE);
//
//        whitePaint=new Paint();
//        whitePaint.setColor(Color.WHITE);
//        whitePaint.setAntiAlias(true);
//        whitePaint.setStyle(Paint.Style.FILL);
//
//        blackPaint=new Paint();
//        blackPaint.setColor(Color.BLACK);
//        blackPaint.setAntiAlias(true);
//        blackPaint.setStyle(Paint.Style.FILL);
//
//        midiDriver=new MidiDriver();
//        midiDriver.start();
//    }
//
//    public CoolProgress(Context context) {
//        super(context);
//        initPaint();
//    }
//
//    public CoolProgress(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        initPaint();
//    }
//
//    private int getSize(int defaultSize,int measureSpec){
//        int mySize=defaultSize;
//        int mode= MeasureSpec.getMode(measureSpec);
//        int size= MeasureSpec.getSize(measureSpec);
//        switch (mode){
//            case MeasureSpec.UNSPECIFIED:
//                mySize=defaultSize;
//                break;
//            case MeasureSpec.EXACTLY:
//                mySize=size;
//                break;
//            case MeasureSpec.AT_MOST:
//                mySize=size;
//                break;
//        }
//        return mySize;
//    }
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width=getSize(100, widthMeasureSpec);
//        int height=getSize(100, heightMeasureSpec);
//
//        if (width<height)height=width;
//        else width=height;
//
//        int pl=getPaddingLeft(),pt=getPaddingTop(),pr=getPaddingRight(),pb=getPaddingBottom();
//        viewWidth=width-pl-pr;
//        viewHeight=height-pb-pt;
//        centerX=viewWidth/2;
//        centerY=viewHeight/2;
//
//        radius=(int)(SCALE*viewWidth/2);
//        lengthNormal=viewWidth*5/12;
//        lengthPressed=viewWidth*9/24;
//
//        setMeasuredDimension(width, height);
//
//        if (first) {
//            first=false;
//            new Thread(this).start();
//        }
//    }
//    boolean first=true;
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        canvas.drawCircle(centerX,centerY,radius,circlePaint);
//        for (int i=0;i<36;i++){
//            drawKey(canvas,i==pressed,i*10-90,isWhite(i));
//        }
//    }
//
//    boolean isWhite(int k){
//        int t=k%12;
//        if (t<5&&t%2==0)return true;
//        if (t>=5&&t%2==1)return true;
//        return false;
//    }
//
//    void drawKey(Canvas canvas, boolean pressed, int startAngle, boolean white){
//        Path path=getPath(radius,pressed?lengthPressed:lengthNormal,startAngle,10);
//        canvas.drawPath(path,white?whitePaint:blackPaint);
//        canvas.drawPath(path,pathPaint);
//    }
//
//    Point getPoint(int radius,int angle){
//        Point point=new Point();
//        point.x=(int)(centerX+radius*Math.cos(Math.toRadians(angle)));
//        point.y=(int)(centerY+radius*Math.sin(Math.toRadians(angle)));
//        return point;
//    }
//
//    Path getPath(int radiusIn,int radiusOut,int startAngle,int sweepAngle){
//        Path path=new Path();
//        Point startP,midP,endP;
//        startP=getPoint(radiusIn,startAngle);
//        midP=getPoint(radiusOut,startAngle);
//        endP=getPoint(radiusIn,startAngle+sweepAngle);
//        path.moveTo(startP.x,startP.y);
//        path.lineTo(midP.x,midP.y);
//
//        Rect rect=new Rect(centerX-radiusOut,centerY-radiusOut,
//                centerX+radiusOut,centerY+radiusOut);
//        path.arcTo(new RectF(rect),startAngle,sweepAngle);
//
//        path.lineTo(endP.x,endP.y);
//        path.close();
//        return path;
//    }
//
//    Handler handler=new Handler();
//    Runnable repaint=new Runnable() {
//        @Override
//        public void run() {
//            invalidate();
//        }
//    };
//
//    @Override
//    public void run() {
//        while (true){
//            try {
//                Thread.sleep(500);
//                pressed++;
//                pressed%=36;
//                playNote(48+pressed);
//                stopNote(47+pressed,false);
//                handler.post(repaint);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void playNote(int noteNumber) {
//
//        // Construct a note ON message for the note at maximum velocity on channel 1:
//        event = new byte[3];
//        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
//        event[1] = (byte) noteNumber;
//        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)
//
//        // Send the MIDI event to the synthesizer.
//        midiDriver.write(event);
//
//    }
//
//    private void stopNote(int noteNumber, boolean sustainUpEvent) {
//
//        // Stop the note unless the sustain button is currently pressed. Or stop the note if the
//        // sustain button was depressed and the note's button is not pressed.
//        if (sustainUpEvent) {
//            // Construct a note OFF message for the note at minimum velocity on channel 1:
//            event = new byte[3];
//            event[0] = (byte) (0x80 | 0x00);  // 0x80 = note Off, 0x00 = channel 1
//            event[1] = (byte) noteNumber;
//            event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)
//
//            // Send the MIDI event to the synthesizer.
//            midiDriver.write(event);
//        }
//    }
//}
