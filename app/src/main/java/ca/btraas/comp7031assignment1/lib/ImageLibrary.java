package ca.btraas.comp7031assignment1.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ImageLibrary {

    private static final int SEARCH_RADIUS_M = 1000;

//    Activity activity;
    private Context ctx;

    // store Bitmap in hashmap for caching
    private File[] files = new File[]{};

    // public only for unit tests...
    public HashMap<String, Bitmap> bitmapCache = new HashMap<>();


    //String currentName = "";
    public String currentPath;

    private File storageDir;

    public ImageLibrary(Context ctx, String initialPath, File storageDir) {

        if(storageDir == null)
            this.storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        else
            this.storageDir = storageDir;

        //this.activity = activity;
        this.currentPath = initialPath;
        this.ctx = ctx;
        reloadFiles();
        if(initialPath == null && this.files.length > 0) {
            this.currentPath = this.files[0].getAbsolutePath();
        }
    }

    private void reloadFiles() {

        File[] files = storageDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jpg") || name.endsWith(".png");
            }
        });
        for(File file : files) {
            String path = file.getAbsolutePath();
            if(path.endsWith(".jpg") || path.endsWith(".png")) {
                if(!bitmapCache.containsKey(path)) {
                    Bitmap bmp = BitmapFactory.decodeFile(path);
                    if(bmp == null) {
                        deleteFile(path);
                        reloadFiles(); // reload after deleting
                        return;
                    } else {
                        bitmapCache.put(path, bmp);
                    }
                }
            }
        }
        this.files = files; // important!


    }

    public File[] getFiles() {
        return files;
    }


    public Bitmap getBitmapFromFileIndex(int index) {
        reloadFiles();

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

    public Bitmap getBitmapFromPath(String path) {
        reloadFiles();
        return bitmapCache.get(path);
    }

    public File getFileFromPath(String path) {
        reloadFiles();

        for(int i = 0; i < files.length; i++) {
            if(files[i].getAbsolutePath().equals(path)) {
                return files[i];
            }
        }
        return null;
    }

    private void stringToFile(String data, File file) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(data.trim());
            fw.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String getPrefKeyForPath(String path) {
        return path.substring(path.lastIndexOf("/")+1).replace(".", "_").replace("/", "_") + "_data";
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


    private String fileToString(File file) {
        FileInputStream is = null;
        try {
            try {
                is = new FileInputStream(file);
                String ret = convertStreamToString(is).trim();
                //Make sure you close all streams.
                is.close();
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                if(is != null) {
                    is.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public String getCaption(String forPath) {


        File file = new File(storageDir.getAbsolutePath() + "/" + getPrefKeyForPath(forPath) + ".caption");
        return fileToString(file);

    }



    public void setCaption(String forPath, String caption) {

        File file = new File(storageDir.getAbsolutePath() + "/" + getPrefKeyForPath(forPath) + ".caption");
        stringToFile(caption, file);

    }


    public String[] getVisionAnnotationLabels(String forPath) {

        File file = new File(storageDir.getAbsolutePath() + "/" + getPrefKeyForPath(forPath) + ".vision-annotation-labels");
        String strVal = fileToString(file);

        if(strVal == null || strVal.isEmpty()) return new String[]{};

        return strVal.split(",");

    }

    public void setVisionAnnotationLabels(String forPath, String[] labels) {
        File file = new File(storageDir.getAbsolutePath() + "/" + getPrefKeyForPath(forPath) + ".vision-annotation-labels");
        String strVal = "";
        for(String label: labels) {
            strVal = strVal.concat(label + ",");
        }
        stringToFile(strVal, file);
    }

    public String[] getVisionDocumentLabels(String forPath) {


        File file = new File(storageDir.getAbsolutePath() + "/" + getPrefKeyForPath(forPath) + ".vision-document-labels");
        String strVal = fileToString(file);

        if(strVal == null || strVal.isEmpty()) return new String[]{};
        return strVal.split(",");

    }

    public void setVisionDocumentLabels(String forPath, String[] labels) {
        File file = new File(storageDir.getAbsolutePath() + "/" + getPrefKeyForPath(forPath) + ".vision-document-labels");
        String strVal = "";
        for(String label: labels) {
            strVal = strVal.concat(label + ",");
        }
        stringToFile(strVal, file);
    }

    public Location getLocation(String forPath) {



        SharedPreferences pref = ctx.getSharedPreferences(getPrefKeyForPath(forPath), MODE_PRIVATE);
        String lat =  pref.getString("latitude", "");
        String lon = pref.getString("longitude", "");

        if(!lat.equals("") && !lon.equals("")) {
            Location loc = new Location("tmp");
            loc.setLatitude(Double.parseDouble(lat));
            loc.setLongitude(Double.parseDouble(lon));

            setLocation(forPath, loc); // save to new loc
        }

        File fileLat = new File(storageDir.getAbsolutePath() + "/" + getPrefKeyForPath(forPath) + ".latitude");
        File fileLon = new File(storageDir.getAbsolutePath() + "/" + getPrefKeyForPath(forPath) + ".longitude");

        String latitude = fileToString(fileLat);
        String longitude = fileToString(fileLon);

        if(latitude.equals("") || longitude.equals("")) return null;

        Location loc2 = new Location("tmp");
        loc2.setLatitude(Double.parseDouble(latitude));
        loc2.setLongitude(Double.parseDouble(longitude));

        return loc2;
    }

    public String getLocationString(String forPath) {

        Location loc = getLocation(forPath);

        if(loc == null || loc.getLatitude() == 0 || loc.getLongitude() == 0) return "No Location";
        return loc.getLatitude() + " N x " + loc.getLongitude() + " W";

    }

    /**
     * set location for the current path
     * @param loc
     */
    public void setLocation(String forPath, Location loc) {
        if(loc == null) return;
        File fileLat = new File(storageDir.getAbsolutePath() + "/" + getPrefKeyForPath(forPath) + ".latitude");
        File fileLon = new File(storageDir.getAbsolutePath() + "/" + getPrefKeyForPath(forPath) + ".longitude");

        stringToFile(""+loc.getLatitude(), fileLat);
        stringToFile(""+loc.getLongitude(), fileLon);

    }



    public Bitmap getNext(boolean forward) {
        reloadFiles();

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


    /**
     *
     * @param tag String
     * @param start Date
     * @param end Date
     * @param searchLoc Location
     * @param auto String
     * @return ArrayList<String> -> Paths of matching files
     */
    public ArrayList<String> search(String tag, Date start, Date end, Location searchLoc, String auto) {

        reloadFiles();

        ArrayList<String> matchedPaths = new ArrayList<>();

        for(File file: files) {
            String path = file.getAbsolutePath();
            String caption = getCaption(path);
            Location fileLocation = getLocation(path);
            Date lastModified = new Date(file.lastModified());
            String[] visionAnnotationLabels = getVisionAnnotationLabels(path);
            String[] visionDocLabels = getVisionDocumentLabels(path);

            boolean matchText = true;
            if(tag != null && tag.length() > 0) matchText = false;

            if(tag == null || tag.trim().equals("")) {
                matchText = true;
            } else if(caption.toLowerCase().contains(tag.toLowerCase())) {
                // match found!
                matchText = true;
            }

            boolean matchDate = true;
            if(start != null && end != null) {
                matchDate = false;

                boolean afterStart = lastModified.after(start);
                boolean beforeEnd = lastModified.before(end);

                if(afterStart && beforeEnd) matchDate = true;

            }

            boolean matchLoc = true;
            if(searchLoc != null) {
                matchLoc = false;
                if(fileLocation != null)
                    if(searchLoc.distanceTo(fileLocation) < SEARCH_RADIUS_M) matchLoc = true;
            }

            boolean matchAuto = true;
            if(auto != null) {
                matchAuto = false;

                for(String annLabel: visionAnnotationLabels) {
                    if(annLabel.toLowerCase().equals(auto.toLowerCase())) {
                        matchAuto = true;
                    }
                    for(String word: annLabel.split(" ")) {
                        if(word.toLowerCase().equals(auto.toLowerCase())) {
                            matchAuto = true;
                        }
                    }
                }
                for(String docLabel: visionDocLabels) {
                    if(docLabel.toLowerCase().equals(auto.toLowerCase())) {
                        matchAuto = true;
                    }
                    for(String docWord: docLabel.split(" ")) {
                        if(docWord.toLowerCase().equals(auto.toLowerCase())) {
                            matchAuto = true;
                        }
                    }
                }

            }

            if(matchText && matchDate && matchLoc && matchAuto) {
                matchedPaths.add(path);
            }

        }
        return matchedPaths; // default return null;
    }

    /**
     * Before actually capturing an image.
     *
     * Doesn't reload our files because it isn't created yet...
     *
     * The file will likely be deleted the next time reloadFiles() is called, if a bitmap was never saved into it.
     *
     * @return File
     * @throws IOException when it failed to save
     */
    public File createImageFile(Location location) throws IOException {


        String locationName = (location == null ? "Unknown Location" :
                location.getLatitude() + "N" +
                        location.getLongitude() + "W");


        File storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("img-",".jpg", storageDir);


        // Save a file: path for use with ACTION_VIEW intents
        currentPath = image.getAbsolutePath();

        setLocation(currentPath, location); // save the location of this image

        return image;
    }

    public boolean deleteFile(String path) {
        File file = new File(path);
        boolean success =  file.delete();
        reloadFiles();

        return success;
    }
}
