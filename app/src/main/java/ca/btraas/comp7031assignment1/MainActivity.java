package ca.btraas.comp7031assignment1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    public static final int CAMERA_REQUEST = 1888;



    String mCurrentPhotoPath;

    ImageLibrary library;

    boolean autoRunning = false;

    static int REQUEST_CODE_CURRENT_PATH = 1293;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        library = new ImageLibrary(this, null);

        ((Button)findViewById(R.id.left)).setOnClickListener(this);
        ((Button)findViewById(R.id.right)).setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                autoRunning = false;
                ((ImageView)MainActivity.this.findViewById(R.id.imageView)).setImageBitmap(library.getNext(true));
            }
        });

        ((Button)findViewById(R.id.upload)).setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                UploadTask task = new UploadTask(MainActivity.this);
                task.execute(library.getFileFromPath(library.currentPath));

            }
        });
    }


    public File createImageFile() throws IOException {
// Create an image file name
        String imageFileName = "7031_" + System.currentTimeMillis() + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



    public void snap(View v) {


        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


        if(cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            try {
                File photoFile = this.createImageFile();

                Uri uri = FileProvider.getUriForFile(this,  "ca.btraas.comp7031assignment1.fileprovider", photoFile);
                //this@ImageCell.imageUri = uri
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                ActivityCompat.startActivityForResult(this, cameraIntent, CAMERA_REQUEST, null);






            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Failed to create image file", Toast.LENGTH_LONG).show();
            }

        }


//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(this, Intent.createChooser(intent, "Select Picture"));
//
//
    }

    @Override
    public void onClick(View v) {
        autoRunning = false;
        ((ImageView)MainActivity.this.findViewById(R.id.imageView)).setImageBitmap(library.getNext(false));
    }



    public void auto(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                autoRunning = true;
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((Button)MainActivity.this.findViewById(R.id.auto)).setEnabled(false);
                    }
                });
                while(autoRunning) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView)MainActivity.this.findViewById(R.id.imageView)).setImageBitmap(library.getNext(true));
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((Button)MainActivity.this.findViewById(R.id.auto)).setEnabled(true);
                    }
                });

            }
        }).start();
    }

    public void swtch(View view) {
        Intent myIntent = new Intent(MainActivity.this, CanvasActivity.class);
        myIntent.putExtra("photo_path", library.currentPath == null ? "" : library.currentPath); //Optional parameters
        MainActivity.this.startActivityForResult(myIntent, REQUEST_CODE_CURRENT_PATH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_CURRENT_PATH) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("photo_path");

                Bitmap current = library.getBitmapFromPath(result);

                autoRunning = false;
                ((ImageView)MainActivity.this.findViewById(R.id.imageView)).setImageBitmap(current);


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
}
