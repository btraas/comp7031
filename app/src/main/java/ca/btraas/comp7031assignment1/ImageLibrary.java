package ca.btraas.comp7031assignment1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

public class ImageLibrary {

    Activity activity;

    File[] files = new File[]{};
    //String currentName = "";
    String currentPath;



    public ImageLibrary(Activity activity, String initialPath) {
        this.activity = activity;
        this.currentPath = initialPath;
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        this.files = storageDir.listFiles();
    }

    public Bitmap getBitmapFromPath(String path) {
        for(int i = 0; i < files.length; i++) {
            if(files[i].getAbsolutePath().equals(path)) {
                return getBitmapFromFileIndex(i);
            }
        }
        return null;
    }

    public File getFileFromPath(String path) {
        for(int i = 0; i < files.length; i++) {
            if(files[i].getAbsolutePath().equals(path)) {
                return files[i];
            }
        }
        return null;
    }

    public String[] getParts(String forPath) {
        File from = getFileFromPath(forPath);

        if(from == null) return new String[]{};

        String name = from.getName();
        String[] split = name.split(";");
        return split;
    }

    public String getCaption(Context ctx, String forPath) {
//        String[] split = getParts(forPath);
//        return split.length > 2 ? split[0] : "-";

        SharedPreferences pref = ctx.getSharedPreferences(forPath.substring(forPath.lastIndexOf("/")+1) + "_data", MODE_PRIVATE);
        return pref.getString("caption", "");
    }

    public String getLocationString(Context ctx, String forPath) {
//        String[] split = getParts(forPath);
//        return split.length > 2 ? split[1] : "No Location";




        SharedPreferences pref = ctx.getSharedPreferences(forPath.substring(forPath.lastIndexOf("/")+1) + "_data", MODE_PRIVATE);
        String lat =  pref.getString("latitude", "");
        String lon = pref.getString("longitude", "");

        if(lat.equals("") || lon.equals("")) return "No Location";
        return lat + " N x " + lon + " W";

    }

    public void setCaption(Context ctx, String caption) {

        SharedPreferences.Editor editor = ctx.getSharedPreferences(currentPath.substring(currentPath.lastIndexOf("/")+1) + "_data", MODE_PRIVATE).edit();
        editor.putString("caption", caption);
        editor.apply();
//        File from = getFileFromPath(currentPath);
//        String[] split = getParts(currentPath);
//        String ext = "jpg";
//
//        String oldLocation = split.length > 2 ? split[1] : "";
//
//        if(caption.length() == 0) caption = "-";
//
//        File directory = from.getParentFile();
//        File to        = new File(directory,  caption + ";" + oldLocation+ "." + ext);
//        from.renameTo(to);
//
//        // find old file in array and replace
//        for(int i = 0; i < files.length; i++) {
//            if(files[i].getAbsolutePath().equals(currentPath)) {
//                files[i] = to;
//                this.currentPath = to.getAbsolutePath();
//            }
//        }
    }


    public void setLocation(Context ctx, Location loc) {


        SharedPreferences.Editor editor = ctx.getSharedPreferences(currentPath.substring(currentPath.lastIndexOf("/")+1) + "_data", MODE_PRIVATE).edit();
        editor.putString("latitude", ""+loc.getLatitude());
        editor.putString("longitude", ""+loc.getLongitude());
        editor.apply();

//        File from = getFileFromPath(currentPath);
//        String[] split = getParts(currentPath);
//        String ext = "jpg";
//
//        String oldCaption = split.length > 2 ? split[0] : "-";
//
//        File directory = from.getParentFile();
//        File to        = new File(directory,  oldCaption + ";" + loc.getLatitude() + "-" + loc.getLongitude() + "." + ext);
//        from.renameTo(to);
//
//        // find old file in array and replace
//        for(int i = 0; i < files.length; i++) {
//            if(files[i].getAbsolutePath().equals(currentPath)) {
//                files[i] = to;
//                this.currentPath = to.getAbsolutePath();
//            }
//        }
    }

    public void setLocationFromExif(Context context, Uri uri, String imagePath) {
        if(uri == null) return;
        try {
            final InputStream inputStream = context.getContentResolver().openInputStream(uri);
            final android.support.media.ExifInterface exif = new android.support.media.ExifInterface(inputStream);
            final double[] latLong = exif.getLatLong();
            Log.v("ImageLibrary", "Image selected at position: " + latLong[0] + " : " + latLong[1]);

        } catch (IOException e) {
            Log.w("ImageLibrary", "Error when getting location from image", e);
        }

//        try {
//            final ExifInterface exifInterface = new ExifInterface(imagePath);
//            float[] latLong = new float[2];
//            if (exifInterface.getLatLong(latLong)) {
//                // Do stuff with lat / long...
//
//            }
//        } catch (IOException e) {
//            Log.e("ImageLibrary","Couldn't read exif info: " + e.getLocalizedMessage());
//        }
    }

    public Bitmap getNext(boolean forward) {
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        this.files = storageDir.listFiles();

        // loop until we find the file
        for(int _i = 0; _i < files.length; _i++ ) {

            int nextIndex = _i + (forward ? 1 : -1);
            if(nextIndex >= files.length) {
                nextIndex = 0;
            } else if(nextIndex < 0) {
                nextIndex = files.length - 1;
            }




            String thisPath = this.files[_i].getAbsolutePath();

            if(currentPath == null) {
                Log.d("ImageLibrary", "currentPath == null -> return getBitmapFromFileIndex("+nextIndex+")");
                return getBitmapFromFileIndex(nextIndex);

            } else if((thisPath.equals(currentPath))) {
                Log.d("ImageLibrary", "thisPath.equals(currentPath) -> return getBitmapFromFileIndex("+nextIndex+")");
                return getBitmapFromFileIndex(nextIndex);

            }

        }

        if(files.length == 0) {
            Log.d("ImageLibrary", "files.length == 0. return null");
            return null;
        }

//        currentName = files[0].getName();
        Log.d("ImageLibrary", "return getBitmapFromFileIndex(0) (currentPath="+currentPath+")");
        return getBitmapFromFileIndex(0);
    }

    public Bitmap getBitmapFromFileIndex(int index) {

        currentPath = files[index].getAbsolutePath();

        Bitmap bmp = BitmapFactory.decodeFile(currentPath);
        if(bmp == null) {
            (new File(currentPath)).delete();
            index++;
            if(index >= files.length) {
                index = 0;
            }
            return getBitmapFromFileIndex(index);
        }

        return bmp;

    }
}
