package ca.btraas.comp7031assignment1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DrawingView extends View implements SensorEventListener {

    public int width;
    public  int height;
    private Bitmap mBitmap;
    public Canvas mCanvas;
    private Paint mBitmapPaint;
    private final Activity activity;


    private boolean paused = false;

    public Paint mPaint;


    private ImageLibrary library;

    private int bmpWidth;
    private int bmpHeight;

    private GestureDetector detector;
    private SensorManager sensorManager;
    private Sensor acceleration;

    public DrawingView(final Activity activity, Bitmap bmp, final ImageLibrary library, int width, int height) {
        super(activity);
        this.activity = activity;
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        this.bmpWidth = width;
        this.bmpHeight = height;


        mBitmap = scaleBmp(bmp);
        mCanvas = new Canvas(mBitmap);

        this.library = library;

        detector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true; // the magic is here
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                System.out.println("!!! onDoubleTap");

                Intent returnIntent = new Intent();
                returnIntent.putExtra("photo_path",library.currentPath);
                activity.setResult(Activity.RESULT_OK,returnIntent);
                activity.finish();


                return super.onDoubleTap(e);
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                System.out.println("!!! onDoubleTapEvent");
                return super.onDoubleTapEvent(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {


                return false;
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return detector.onTouchEvent(motionEvent);
            }
        });

        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, acceleration, SensorManager.SENSOR_DELAY_NORMAL);





    }


    Bitmap scaleBmp(Bitmap bmp) {
        Bitmap workingBitmap = Bitmap.createScaledBitmap(bmp, this.bmpWidth, this.bmpHeight, false);
        return workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);



    }



    float startX = -1;
    float startY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();



        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = x;
                startY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                if(startX == -1) {
                    startX = x;
                }
//
//                if(x - startX > 100) {
//                    mBitmap = scaleBmp(library.getNext(true));
//                    draw(mCanvas);
//                }
//
//                if(x - startX < -100) {
//                    mBitmap = scaleBmp(library.getNext(false));
//                    draw(mCanvas);
//                }

                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                if(startX > -1) {
                    if(x - startX > 100) {
                        mBitmap = scaleBmp(library.getNext(true));

                        // mBitmap = library.getNext(true);

                        draw(mCanvas);
                    }

                    if(startX - x > 100) {
                        mBitmap = scaleBmp(library.getNext(false));
                        //mBitmap = library.getNext(false);

                        draw(mCanvas);
                    }
                }




                startX = -1;
                startY = -1;

                invalidate();
                break;
        }

        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getAccelerometer(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        ((AppCompatActivity)activity).getSupportActionBar().setTitle("x: "+x);

        if(!paused && Math.abs(x) > 4) {
            paused = true;
            mBitmap = scaleBmp(library.getNext(x < 0));
            draw(mCanvas);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    paused = false;
                }
            }).start();
        }




    }

    public void receivedWords(ArrayList<String> words) {

        Boolean forward = null;

        for(String word : words) {
            if(word.equals("left")
                    || word.equals("back")
                    || word.equals("previous")) {
                forward = false;
                Toast.makeText(activity, word + " (moving left)", Toast.LENGTH_LONG).show();
                break;

            } else if(word.equals("right")
                    || word.equals("next")
                    || word.equals("go")
                    || word.equals("switch")) {

                forward = true;
                Toast.makeText(activity, word + " (moving right)", Toast.LENGTH_LONG).show();
                break;
            }
        }




        if(forward != null) {
            mBitmap = scaleBmp(library.getNext(forward));
            draw(mCanvas);
        }

    }

}