package ca.btraas.comp7031assignment1;

import android.graphics.Bitmap;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import ca.btraas.comp7031assignment1.lib.ImageLibrary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Running with AndroidJUnit4 so I can have a Context.
 *
 * Caption and location are specific to this test (not inter-process)
 */
@RunWith(AndroidJUnit4.class)
public class ImageLibraryUnitTests {

    private static final String TEST_PREFIX = "~~JUNIT~~"; // for undoing changes made during tests


    private ImageLibrary testLibrary;
    private File[] files;        // if adding or deleting a file, don't use this!
    private int randomFileIndex; // if adding or deleting a file, don't use this!
    private File randomFile;     // if adding or deleting a file, don't use this!

    private File fileWithNoCaption;
    private File fileWithLocation;

    @Before
    public void initLibrary() {
        File storage = new File("/storage/emulated/0/Android/data/ca.btraas.comp7031assignment1/files/Pictures");
        testLibrary = new ImageLibrary(InstrumentationRegistry.getContext(), null, storage);
        files = testLibrary.getFiles();

        // ensure files exist
        assertNotNull(files);
        assertNotEquals(files.length,0);

        // check if library loaded a file automatically
        assertNotNull(testLibrary.currentPath);

    }

    @Before
    public void initRandomFile() {
        assertNotNull(files);
        assertNotEquals(files.length,0);

        randomFileIndex = new Random().nextInt(files.length);
        randomFile = files[randomFileIndex];
        assertNotNull(randomFile);
    }

    @Before
    public void initExampleFiles() {
        for(File file: files) {
            String caption = testLibrary.getCaption( file.getAbsolutePath() );
            Location loc = testLibrary.getLocation( file.getAbsolutePath() );
            if(fileWithNoCaption == null && (caption == null || caption.trim().isEmpty())) {
                fileWithNoCaption = file;
            }
            if(fileWithLocation == null && loc != null) {
                fileWithLocation = file;
            }
        }
        System.err.println("No files without a caption! Please snap a new photo before running this test!");
        assertNotNull(fileWithNoCaption); // if we haven't found one yet.
    }

    /**
     * Testing the auto-loading of the first file on init
     */
    @Test
    public void testLoadFirstFile() {
        assertNotNull(testLibrary.currentPath);
        assertNotEquals(testLibrary.currentPath, "");
    }

    /**
     * Testing that the files[] array is populated on init.
     *
     * Should already be tested in @Before above
     */
    @Test
    public void testGetFiles() {
        File[] files = testLibrary.getFiles();
        assertNotNull(files);
        assertNotEquals(files.length,0);
    }


    /**
     * Testing the loading of a Bitmap from an index
     *
     *  REQUIRES a file to exist
     */
    @Test
    public void testGetBitmapFromIndex() {
        assertNotNull(testLibrary.getBitmapFromFileIndex(randomFileIndex));
    }

    /**
     * Testing the loading of a Bitmap from a file path (string)
     */
    @Test
    public void testGetBitmapFromPath() {
        assertNotNull(testLibrary.getBitmapFromPath(randomFile.getAbsolutePath()));
    }

    /**
     * Testing getting a File from a path (string)
     *
     * testLibrary.currentPath might be null, but that's checked by another test...
     */
    @Test
    public void testGetFile() {
        assertNotNull(testLibrary.getFileFromPath(testLibrary.currentPath));
    }

    /**
     * Testing that we can get the caption of at least one of the files.
     * Must be set in the filesystem beforehand.
     *
     * REQUIRES:
     *   - a file to exist that has a caption
     *
     */
    @Test
    public void testGetCaption() {

        ArrayList<String> captions = new ArrayList<>();

        for(File file: files) {
            String caption = testLibrary.getCaption( file.getAbsolutePath() );
            if(caption != null && !caption.trim().isEmpty()) {
                captions.add(caption);
            }
        }

        // assert that we read 1+ captions
        assertNotEquals(0, captions.size());

    }

    /**
     * Test setting a caption on a file with no captions.
     *
     * REQUIRES:
     *   - a file to exist that doesn't have a caption
     *   - getCaption() to work as expected (see testGetCaption())
     *
     */
    @Test
    public void testSetCaption() {
        String newCaption = TEST_PREFIX + " caption";
        testLibrary.setCaption(fileWithNoCaption.getAbsolutePath(), newCaption);

        String setCaption = testLibrary.getCaption(fileWithNoCaption.getAbsolutePath());

        testLibrary.setCaption(fileWithNoCaption.getAbsolutePath(), ""); // undo changes

        assertEquals(newCaption, setCaption); // assert that we successfully set and retrieved the caption
        assertEquals("", testLibrary.getCaption(fileWithNoCaption.getAbsolutePath())); // assert that we set the caption back to an empty string
    }

    /**
     * Test getting a location of at least one file.
     *
     * REQUIRES:
     *   - a file to exist that has a location (shared prefs)
     */
    @Test
    public void testGetLocation() {
        ArrayList<Location> locations = new ArrayList<>();

        for(File file: files) {
            Location loc = testLibrary.getLocation( file.getAbsolutePath() );
            if(loc != null && loc.getLatitude() != 0 && loc.getLongitude() != 0) {
                locations.add(loc);
            }
        }

        // assert that we read 1+ locations
        assertNotEquals(0, locations.size());
    }

    /**
     * Test getting a location string of at least one file.
     *
     * REQUIRES:
     *   - a file to exist that has a location (shared prefs)
     */
    @Test
    public void testGetLocationString() {
        ArrayList<String> locations = new ArrayList<>();

        for(File file: files) {
            String locStr = testLibrary.getLocationString( file.getAbsolutePath() );
            if(locStr != null && !locStr.trim().isEmpty()) {
                locations.add(locStr);
            }
        }

        // assert that we read 1+ locations
        assertNotEquals(0, locations.size());
        assertNotNull(locations.get(0));
        assertTrue(locations.get(0).length() > 5); // not a short string, it should have W and N etc.
        assertTrue(locations.get(0).contains("N")); // some other checks...
        assertTrue(locations.get(0).contains("W"));

    }




    /**
     * Test setting a location on a file.
     *  Should set the location back to its original state.
     *
     * REQUIRES:
     *   - a file to exist
     *   - getLocation() to work as expected (see testGetLocation())
     *   - getLocationString() to work as expected (see testGetLocationString())
     *
     */
    @Test
    public void testSetLocation() {

        //  --- Begin modification code (should be no asserts in here, until we set the location back) ---


        Location oldLoc = testLibrary.getLocation( randomFile.getAbsolutePath() );

        Location newLoc = new Location(TEST_PREFIX);
        newLoc.setLatitude(20);
        newLoc.setLongitude(100);
        testLibrary.setLocation(randomFile.getAbsolutePath(), newLoc);

        Location setLoc = testLibrary.getLocation(randomFile.getAbsolutePath());
        String setLocString = testLibrary.getLocationString(randomFile.getAbsolutePath());

        testLibrary.setLocation(randomFile.getAbsolutePath(), oldLoc); // set the location back

        // --- End modification code ---

        // Note that the Location can be different (provider, accuracy etc). We're just checking that the lat & long were set.
        assertEquals(newLoc.getLatitude(), setLoc.getLatitude(), 0); // assert that we successfully set and retrieved the location
        assertEquals(newLoc.getLongitude(), setLoc.getLongitude(), 0); // assert that we successfully set and retrieved the location

        // don't assert that location equals. Need to compare the coordinates. (setLocation() / getLocation()) serializes the location object, creating a new one.
        // Location.equals() appears to not be overridden.
//        assertEquals(oldLoc, testLibrary.getLocation(randomFile.getAbsolutePath())); // assert that we set the location back to its original state

        assertTrue(setLocString.contains(""+newLoc.getLatitude())); // check that the locString contains the latitude of the location we set
        assertTrue(setLocString.contains(""+newLoc.getLongitude())); // check that the locString contains the longitude of the location we set

    }

    /**
     * Expecting to loop back to beginning, so we'll test getNext() until that happens.
     */
    @Test
    public void testGetNextForward() {
        Bitmap prev = null;
        for(int i = 0; i < (files.length + 2); i++) {           // test all of the images, plus 2 for good measure (ensure it wraps around)
            Bitmap _this = testLibrary.getNext(true);
            assertNotNull(_this);                               // ensure the Bitmap is not null
            if(files.length > 1)  assertNotEquals(prev, _this); // ensure not the same as the previous Bitmap.
            prev = _this;
        }

    }

    /**
     * Expecting to loop back to beginning, so we'll test getNext() until that happens.
     */
    @Test
    public void testGetNextReverse() {
        Bitmap prev = null;
        for(int i = 0; i < (files.length + 2); i++) {           // test all of the images, plus 2 for good measure (ensure it wraps around)
            Bitmap _this = testLibrary.getNext(false);
            assertNotNull(_this);                               // ensure the Bitmap is not null
            if(files.length > 1)  assertNotEquals(prev, _this); // ensure not the same as the previous Bitmap.
            prev = _this;
        }

    }

    /**
     * Requires 1+ file with no tag (we'll set one here).
     *
     * Requires that getCaption() and setCaption() are working.
     */
    @Test
    public void testSearchTag() {

        String newCaption = TEST_PREFIX + " caption";
        testLibrary.setCaption(fileWithNoCaption.getAbsolutePath(), newCaption);

        ArrayList<String> search = testLibrary.search(TEST_PREFIX, null, null, null);
        testLibrary.setCaption(fileWithNoCaption.getAbsolutePath(), ""); // undo changes

        assertNotNull(search);
        assertTrue(search.size() > 0);  // assert that we found a match
        assertTrue(search.contains(fileWithNoCaption.getAbsolutePath()));  // assert that we found this match

    }

    /**
     * Requires 1+ file
     */
    @Test
    public void testSearchDate() {

        Date modified = new Date(randomFile.lastModified());

        Calendar cal = Calendar.getInstance();
        cal.setTime(modified);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date plus1 = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, -2 );
        Date minus1 = cal.getTime();

        ArrayList<String> search = testLibrary.search(null, minus1, plus1, null);

        assertNotNull(search);
        assertTrue(search.size() > 0);  // assert that we found a match

        assertTrue(search.contains(randomFile.getAbsolutePath()));  // assert that we found this match

    }

    /**
     * Requires 1+ file with a location
     * Requires getLocation() to work.
     * Assumes 0.001 difference in lat/long is within the valid range.
     */
    @Test
    public void testSearchLoc() {

        Location loc = testLibrary.getLocation( fileWithLocation.getAbsolutePath() );

        Location searchLoc = new Location(TEST_PREFIX);
        searchLoc.setLatitude(loc.getLatitude() + 0.001);
        searchLoc.setLongitude(loc.getLongitude() + 0.001);

        ArrayList<String> search = testLibrary.search(null, null, null, searchLoc);

        assertNotNull(search);
        assertTrue(search.size() > 0);  // assert that we found a match
        assertTrue(search.contains(fileWithLocation.getAbsolutePath()));  // assert that we found this match

    }

    @Test
    public void testCreateImageFile() {
        Location loc = new Location( TEST_PREFIX );
        loc.setLatitude(20);
        loc.setLongitude(100);
        try {
            File newFile = testLibrary.createImageFile(loc);
            assertNotNull(newFile);
            assertNotNull(newFile.getAbsolutePath());
            assertEquals(newFile.getAbsolutePath(), testLibrary.currentPath); // ensure we're set to the new path

            Location newLoc = testLibrary.getLocation(testLibrary.currentPath);
            assertNotNull(newLoc);

            // ensure the location is set
            assertEquals( loc.getLatitude(), newLoc.getLatitude(), 0);
            assertEquals( loc.getLongitude(), newLoc.getLongitude(), 0 );

            assertTrue( !testLibrary.bitmapCache.containsKey(newFile.getAbsolutePath()) ); // assert that it's a new path.

        } catch (Exception e) {
            throw new RuntimeException(e); // fail test
        }
    }




}