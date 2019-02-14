package ca.btraas.comp7031assignment1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    public static final int CAMERA_REQUEST = 1888;

    public static Location location = null;


    String mCapturePhotoPath;
    ImageLibrary library;

    boolean autoRunning = false;

    static int REQUEST_CODE_CURRENT_PATH = 1293;


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 50: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    try {
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                MainActivity.location = location;

                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {
                            }

                            @Override
                            public void onProviderEnabled(String provider) {
                            }

                            @Override
                            public void onProviderDisabled(String provider) {
                            }
                        }, null);
                    } catch (SecurityException | NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 50);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    MainActivity.location = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            }, null);
        } catch (SecurityException | NullPointerException e) {
            e.printStackTrace();
        }

        library = new ImageLibrary(this, null);

        ((Button) findViewById(R.id.left)).setOnClickListener(this);
        ((Button) findViewById(R.id.right)).setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                autoRunning = false;
                library.getNext(true);
                setImageFromPath(library.currentPath);
            }
        });

        ((EditText) findViewById(R.id.caption)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                File from = library.getFileFromPath(library.currentPath);

                String newCaption = s.toString();

                if(library.getFileFromPath(library.currentPath) == null) return;
                library.setCaption(MainActivity.this, newCaption);

            }
        });

//        ((Button)findViewById(R.id.upload)).setOnClickListener(new Button.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                UploadTask task = new UploadTask(MainActivity.this);
//                task.execute(library.getFileFromPath(library.currentPath));
//
//            }
//        });

        getSupportActionBar().setTitle("COMP 7082 Photo Gallery");
    }


    /**
     * Before actually capturing an image.
     *
     * @return
     * @throws IOException
     */
    public File createImageFile() throws IOException {


        String locationName = (MainActivity.location == null ? "UnknownLocation" :
                MainActivity.location.getLatitude() + "N" +
                        MainActivity.location.getLongitude() + "W");


        // Create an image file name
//        String imageFileName = "7031_" + System.currentTimeMillis() + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("." + locationName + ".", ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCapturePhotoPath = image.getAbsolutePath();
        return image;
    }


    public void snap(View v) {


        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            try {
                File photoFile = this.createImageFile();
                library.currentPath = photoFile.getAbsolutePath();

                Uri uri = FileProvider.getUriForFile(this, "ca.btraas.comp7031assignment1.fileprovider", photoFile);
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
        Bitmap bmp = library.getNext(false);
        setImageFromPath(library.currentPath);
//        Log.i("File last modified @ : "+ lastModDate.toString());

    }

    void setImageFromPath(String path) {
        if(path == null || path.equals("")) {
            System.out.println(path);
        }
        Log.d("MainActivity", "setImageFromPath: " + path);
        library.currentPath = path;

        Bitmap bmp = library.getBitmapFromPath(path);
        ((ImageView) MainActivity.this.findViewById(R.id.imageView)).setImageBitmap(bmp);
        File file = library.getFileFromPath(library.currentPath);
        if (file == null) {
            Log.w("MainActivity", "file not found!");
        } else {

            Date lastModDate = new Date(file.lastModified());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ((TextView) findViewById(R.id.timestamp)).setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(lastModDate));
            }
        }
        ((EditText) findViewById(R.id.caption)).setText(library.getCaption(MainActivity.this, library.currentPath));

        String locationString = library.getLocationString(MainActivity.this, library.currentPath);
        if(locationString.equals("") || locationString.toLowerCase().equals("no location") || locationString.toLowerCase().equals("nolocation")) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            @SuppressLint("MissingPermission") Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try {
                library.setLocation(MainActivity.this, loc);

            } catch(RuntimeException e) {
                e.printStackTrace();
            }
        }

        ((TextView) findViewById(R.id.location)).setText(library.getLocationString(MainActivity.this, library.currentPath));
    }


//    public void auto(View view) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                autoRunning = true;
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((Button)MainActivity.this.findViewById(R.id.auto)).setEnabled(false);
//                    }
//                });
//                while(autoRunning) {
//                    MainActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ImageView)MainActivity.this.findViewById(R.id.imageView)).setImageBitmap(library.getNext(true));
//                        }
//                    });
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((Button)MainActivity.this.findViewById(R.id.auto)).setEnabled(true);
//                    }
//                });
//
//            }
//        }).start();
//    }

    public void swtch(View view) {
        Intent myIntent = new Intent(MainActivity.this, CanvasActivity.class);
        myIntent.putExtra("photo_path", library.currentPath == null ? "" : library.currentPath); //Optional parameters
        MainActivity.this.startActivityForResult(myIntent, REQUEST_CODE_CURRENT_PATH);
    }

    public void search(View view) {
        Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
        MainActivity.this.startActivityForResult(myIntent, REQUEST_CODE_CURRENT_PATH);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_CURRENT_PATH) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("photo_path");
                library.currentPath = result;

                library.getBitmapFromPath(result);

                autoRunning = false;
                setImageFromPath(result);

                final Uri originalUri = data.getData();
                library.setLocationFromExif(this, originalUri, result);


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else { // camera capture ??
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            setImageFromPath(library.currentPath);




        }
    }//onActivityResult
}
