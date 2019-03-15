package ca.btraas.comp7031assignment1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
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
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import ca.btraas.comp7031assignment1.lib.ImageLibrary;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    public static final int CAMERA_REQUEST = 1888;
    public static Location location = null;

    TextToSpeech tts;

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


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.CANADA);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "English TTS is not supported", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization Failed!", Toast.LENGTH_LONG).show();
                }
            }
        });





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

        // will load the first file into library.currentPath
        library = new ImageLibrary(this, null, null);
        setImageFromPath(library.currentPath); // draw on imageView

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
                library.setCaption(library.currentPath, newCaption);

            }
        });


        getSupportActionBar().setTitle("COMP 7082 Photo Gallery");
    }


    public void snap(View v) {


        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            try {
                File photoFile = library.createImageFile(MainActivity.location);
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

    }

    @Override
    public void onClick(View v) {
        autoRunning = false;
        Bitmap bmp = library.getNext(false);
        setImageFromPath(library.currentPath);
//        Log.i("File last modified @ : "+ lastModDate.toString());

    }

    void setImageFromPath(String path) {
        Log.d("MainActivity", "setImageFromPath: " + path);

        ImageView imageView = ((ImageView) MainActivity.this.findViewById(R.id.imageView));
        EditText captionView = ((EditText) findViewById(R.id.caption));
        TextView timestampView = ((TextView) findViewById(R.id.timestamp));
        TextView locationView = ((TextView) findViewById(R.id.location));


        if(path == null || path.equals("")) {
            Log.d("MainActivity", "setImageFromPath: path is empty. clearing...");
            imageView.setImageBitmap(null);
            imageView.setVisibility(View.INVISIBLE);
            captionView.setText("");
            timestampView.setText("no image");
            locationView.setText("");
            return;
        }
        imageView.setVisibility(View.VISIBLE);

        Log.d("MainActivity", "setImageFromPath: " + path);
        library.currentPath = path;

        Bitmap bmp = library.getBitmapFromPath(path);
        imageView.setImageBitmap(bmp);

        File file = library.getFileFromPath(library.currentPath);
        if (file == null) {
            Log.w("MainActivity", "file not found!");
        } else {
            Date lastModDate = new Date(file.lastModified());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                timestampView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(lastModDate));
            }
        }
        captionView.setText(library.getCaption( library.currentPath ));

        String locationString = library.getLocationString( library.currentPath );

        // set location now if not set already.
        if(locationString.equals("") || locationString.toLowerCase().contains("no location") || locationString.toLowerCase().contains("nolocation")) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            @SuppressLint("MissingPermission") Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try {
                library.setLocation(library.currentPath, loc);

            } catch(RuntimeException e) {
                e.printStackTrace();
            }
        }

        locationView.setText(library.getLocationString( library.currentPath ));
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

            } else {
                setImageFromPath(null);
            }

        } else { // camera capture ??
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            setImageFromPath(library.currentPath);


        }
    }//onActivityResult

    public void delete(View view) {
        if(library.deleteFile(library.currentPath)) {
            Toast.makeText(MainActivity.this, "Photo deleted!", Toast.LENGTH_SHORT).show();
            library.getNext(true);
            setImageFromPath(library.currentPath);
        } else {
            Toast.makeText(MainActivity.this, "Failed to delete photo!", Toast.LENGTH_LONG).show();
        }
    }

    public void auto(View view) {
//        String[] bottleLabels = new String[]{
//                "Plastic Bottle",
//                "Bottle",
//                "Water",
//                "Water Bottle",
//                "Bottled Water",
//                "Drinking Water",
//                "Mineral Water",
//                "Distilled Water",
//                "Two-liter Bottle",
//                "Enhanced Water",
//                "Drinkware",
//                "Liquid"
//        };
//        String[] mbp = new String[]{
//                "Space Bar",
//                "Computer Keyboard",
//                "Office Equipment",
//                "Text",
//                "Electronic Device",
//                "Technology",
//                "Laptop",
//                "Computer",
//                "Font",
//                "Input Device",
//                "Touchpad"
//        };
//        String[] mbpDoc = new String[]{
//                "command",
//                "MacBook Pro",
//                "String"
//        };
//        library.setVisionAnnotationLabels(library.currentPath, mbp);
//        library.setVisionDocumentLabels(library.currentPath, mbpDoc);


//        Toast.makeText(this, library.getVisionAnnotationLabels(library.currentPath)[1], Toast.LENGTH_LONG).show();
//        Toast.makeText(this, library.getVisionDocumentLabels(library.currentPath)[1], Toast.LENGTH_LONG).show();

        String[] annLabels = library.getVisionAnnotationLabels(library.currentPath);
        if(annLabels.length > 0) {
            String[] prefix = new String[]{
                    "This photo may contain:",
                    "This photo has:",
                    "I found:",
                    "Here's what I see:"
            };
            tts.speak(prefix[new Random().nextInt(prefix.length)], TextToSpeech.QUEUE_ADD, null);
        }
        int count = 0;
        for(String annotation: annLabels) {
            count++;
            if(count > 3) break;
            tts.speak( ((count == 3 || count == annLabels.length) ? "and " : "") +
                    (annotation.startsWith("a")
                || annotation.startsWith("e")
                || annotation.startsWith("i")
                || annotation.startsWith("o")
                || annotation.startsWith("u") ? "an " : "a ") + annotation, TextToSpeech.QUEUE_ADD, null);

        }

        String[] annDoc = library.getVisionDocumentLabels(library.currentPath);
        if(annDoc.length > 0) {
            tts.speak("Also, here's some text I found in the image: ", TextToSpeech.QUEUE_ADD, null);
        }
        count = 0;
        for(String annotation: annDoc) {
            count++;
            if(count > 3) break;
            tts.speak( ((count == 3 || count == annDoc.length) ? "and " : ", ") +
                    annotation, TextToSpeech.QUEUE_ADD, null);

        }

        if(annLabels.length == 0 && annDoc.length == 0) {
            tts.speak("I found no data for this image.", TextToSpeech.QUEUE_ADD, null);
        }



    }
}
