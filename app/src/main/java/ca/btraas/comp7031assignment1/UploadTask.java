package ca.btraas.comp7031assignment1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class UploadTask extends AsyncTask<File, Integer, String> {

    private Activity activity;
    private String fileName;

    public UploadTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(File... files) {
        String urlString = "https://bcit.btraas.ca/upload.php";

        fileName = files[0].getName();

        try {
            URL url = new URL(urlString);

            BtraasConnection con = new BtraasConnection((HttpsURLConnection)url.openConnection());
            for(File file : files) {

                try {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;

                    try {
                        Bitmap tmpBmp = BitmapFactory.decodeStream(new FileInputStream(file), null, options);

                        File newFile = File.createTempFile("tmp_upload_"+file.getName(), ".jpg", activity.getExternalCacheDir());

                        FileOutputStream os = new FileOutputStream(newFile);

                        if(tmpBmp == null) return null;
                        tmpBmp.compress(Bitmap.CompressFormat.JPEG, 20, os);


                        con.addFile(file.getName(), newFile, new BtraasConnection.OnPercentUploadedHandler() {

                            int lastSentPercent = 0;

                            @Override
                            void onPercentUploaded(int percent) {
                                if(lastSentPercent % 10 != percent % 10) {
                                    UploadTask.this.onProgressUpdate(lastSentPercent);
                                }
                            }
                        });

                    } catch(Exception e) {
                        e.printStackTrace();
                    }





//                } catch(IOException e) {
//                    Log.w("UploadTask", "-- Failed to set image data");
//                    e.printStackTrace();
                } catch(OutOfMemoryError e) {
                    Log.w("UploadTask", "-- OOM Error in ");
                }



            }
//            con.close();

            return con.output();

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }


    }

    @Override
    protected void onProgressUpdate(final Integer... integers) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((AppCompatActivity)activity).getSupportActionBar().setTitle("Upload progress: " + integers[0] + "%");
            }
        });
    }

    @Override
    protected void onPostExecute(String output) {
        if(output != null && output.contains("has been uploaded.")) {
            ((AppCompatActivity)activity).getSupportActionBar().setTitle("Upload complete: "+fileName);
//                Toast.makeText(activity, "Upload complete: "+fileName, Toast.LENGTH_LONG).show();
            Toast.makeText(activity, output, Toast.LENGTH_LONG).show();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bcit.btraas.ca/uploads/"));
            activity.startActivity(browserIntent);
        } else {
            ((AppCompatActivity)activity).getSupportActionBar().setTitle(output);
            Toast.makeText(activity, output, Toast.LENGTH_LONG).show();

        }


    }

}