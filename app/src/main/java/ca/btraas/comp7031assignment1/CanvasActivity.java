package ca.btraas.comp7031assignment1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.speech.RecognizerIntent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.text.DecimalFormat;
import java.util.Locale;

import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;

public class CanvasActivity extends AppCompatActivity {

    DrawingView dv;

    ImageLibrary library;

    public static final int VR_REQUEST_ID = 129;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_canvas);


        String path = getIntent().getStringExtra("photo_path");

        library = new ImageLibrary(this, path);


//        Canvas canvas = new Canvas();
//        canvas.drawBitmap(BitmapFactory.decodeFile(path), 0f,0f, null);
//
//
//        View v = new View(this);
//        v.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
//        v.draw(canvas);
//        setContentView(v);
//
//        drawView = DrawingView(context)

        Bitmap bmp = BitmapFactory.decodeFile(path);
        Rect source = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());

        int width = getWindowManager().getDefaultDisplay().getWidth();

        int bmpWidth = bmp.getWidth();
        float scaleFactor = (Float.valueOf(width) / Float.valueOf(bmpWidth));

        int height = Math.round(bmp.getHeight() * scaleFactor);

        RelativeLayout rl = new RelativeLayout(this);

        dv = new DrawingView(this, bmp, library, width, height);
        rl.addView(dv);

        Button voiceButton = new Button(this);
        voiceButton.setWidth(250);
        voiceButton.setHeight(50);
        voiceButton.setText("Voice");

        voiceButton.setGravity(Gravity.CENTER_HORIZONTAL);
        rl.setGravity(Gravity.CENTER_HORIZONTAL);


        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(ALIGN_PARENT_BOTTOM);
        voiceButton.setLayoutParams(lp);

        rl.addView(voiceButton);



        setContentView( rl );

        voiceButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vrIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                vrIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                vrIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                vrIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Voice Command");

                CanvasActivity.this.startActivityForResult(vrIntent, VR_REQUEST_ID);
            }
        });





//        Bitmap bmp = BitmapFactory.decodeFile(path);
//        Rect source = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
//        dv.mCanvas.drawBitmap(bmp, null, source, null);
//

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == VR_REQUEST_ID) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
                //data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                dv.receivedWords(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
            }
        }
    }


}
