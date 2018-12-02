package ca.btraas.comp7031assignment1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;

public class ImageLibrary {

    Activity activity;

    File[] files = new File[]{};
    //String currentName = "";
    String currentPath;


    public ImageLibrary(Activity activity, String initialPath) {
        this.activity = activity;
        this.currentPath = initialPath;
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

                return getBitmapFromFileIndex(nextIndex);

//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//
//                        //((ImageView)activity.findViewById(R.id.imageView)).setImageBitmap(getBitmapFromFileIndex(finalI));
//                    }
//                });
//                return;
            } else if((thisPath.equals(currentPath))) {
                return getBitmapFromFileIndex(nextIndex);
                //((ImageView)findViewById(R.id.imageView)).setImageBitmap();
//                if(i == 0) i = (this.files.length - 1);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        currentName = files[finalI].getName();
//                        ((ImageView)findViewById(R.id.imageView)).setImageBitmap(getBitmapFromFileIndex(finalI));
//                    }
//                });

            }

        }

        if(files.length == 0) {
            return null;
        }

//        currentName = files[0].getName();
        return getBitmapFromFileIndex(0);
    }

    public Bitmap getBitmapFromFileIndex(int index) {

        currentPath = files[index].getAbsolutePath();

        Bitmap bmp = BitmapFactory.decodeFile(currentPath);

        return bmp;

    }
}
