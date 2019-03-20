package ca.btraas.comp7031assignment1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ca.btraas.comp7031assignment1.lib.ImageLibrary;
import ca.btraas.comp7031assignment1.lib.NumberWordParser;

public class SearchActivity extends AppCompatActivity {

    static String TAG = "SearchActivity";
    ImageLibrary library;
    private SpeechRecognizer sr;


    class Listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.d(TAG,  "error " +  error);
            Toast.makeText(SearchActivity.this, String.valueOf(error), Toast.LENGTH_LONG).show();
//            mText.setText("error " + error);
        }

        public void onResults(Bundle results)
        {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);



            String sentence0 = null;


            for (int i = 0; i < data.size(); i++)
            {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i) + ", ";

                // trimming



                boolean fromFound = false;
                boolean toFound = false;
                boolean betweenFound = false;
                boolean date1Found = false;
                boolean date2Found = false;
                boolean sinceFound = false;
                boolean beforeFound = false;

                int month1Idx = -1;
                int month2Idx = -1;

                int month1 = 0;
                long day1 = 0;
                int month2 = 0;
                long day2 = 0;

                String sentence = ((String)data.get(i)).toLowerCase().trim();
                //trimming
                if(sentence.startsWith("please")) {
                    sentence = sentence.replace("please", "").trim();
                }
                if(sentence.startsWith("show me photos")) {
                    sentence = sentence.replace("show me photos", "").trim();
                }
                if(sentence.startsWith("show me")) {
                    sentence = sentence.replace("show me", "").trim();
                }
                if(sentence.startsWith("find")) {
                    sentence = sentence.replace("find", "").trim();
                }
                if(sentence.startsWith("show")) {
                    sentence = sentence.replace("show", "").trim();
                }
                if(sentence.startsWith("photos")) {
                    sentence = sentence.replace("photos", "").trim();
                }
                if(sentence.startsWith("images")) {
                    sentence = sentence.replace("images", "").trim();
                }

                if(sentence.startsWith("of")) {
                    sentence = sentence.replace("of", "").trim();
                }
                if(sentence.startsWith("my")) {
                    sentence = sentence.replace("my", "").trim();
                }
                if(sentence.startsWith("a")) {
                    sentence = sentence.replace("a", "").trim();
                }

                if(sentence0 == null) {
                    sentence0 = sentence;
                }

                String[] words = (sentence).split(" ");
                for(int j = 0; j<words.length; j++) {
                    String lower = words[j].toLowerCase();
                    Long number = NumberWordParser.parse(lower);

                    if(lower.equals("before")) {
                        beforeFound = true; // make sure not to break after this
                    }
                    if(lower.equals("since")) {
                        sinceFound = true;
                    }

                    if(lower.equals("from") || lower.equals("in")) {
                        fromFound = true;
                    }
                    else if(lower.equals("between")) {
                        betweenFound = true;
                    }
                    else if(lower.equals("to") || (lower.equals("and") && betweenFound)) { // override "two"
                        toFound = true;
                    }

//                    else if(lower.equals("since")) {
//                        day2 = 31; // in the future
//                        month2 = 12; // in the future
//                        fromFound = true;
//                    }
//
//                    else if(lower.equals("before")) {
//                        day1 = 1; // in the past
//                        month1 = 1; // in the past
//                        fromFound = true;
//                    }

                    else if(number != null) {
                        if(j > 0 && month1Idx == (j-1)) {
                            day1 = number;
                        } else if(j > 0 && month2Idx == (j-1)) {
                            day2 = number;
                        }
                    }
                    else if(lower.equals("january")) {
                        if(toFound) {
                            month2 = 1;
                            month2Idx = j;
                        }
                        else {
                            month1 = 1;
                            month1Idx = j;
                        }
                    }
                    else if(lower.equals("february")) {
                        if(toFound) {
                            month2 = 2;
                            month2Idx = j;
                        }
                        else {
                            month1 = 2;
                            month1Idx = j;
                        }
                    }
                    else if(lower.equals("march")) {
                        if(toFound) {
                            month2 = 3;
                            month2Idx = j;
                        }
                        else {
                            month1 = 3;
                            month1Idx = j;
                        }
                    }
                    else if(lower.equals("april")) {
                        if(toFound) {
                            month2 = 4;
                            month2Idx = j;
                        }
                        else {
                            month1 = 4;
                            month1Idx = j;
                        }
                    }
                    else if(lower.equals("may")) {
                        if(toFound) {
                            month2 = 5;
                            month2Idx = j;
                        }
                        else {
                            month1 = 5;
                            month1Idx = j;
                        }
                    }
                    else if(lower.equals("june")) {
                        if(toFound) {
                            month2 = 6;
                            month2Idx = j;
                        }
                        else {
                            month1 = 6;
                            month1Idx = j;
                        }
                    }
                    else if(lower.equals("july")) {
                        if(toFound) {
                            month2 = 7;
                            month2Idx = j;
                        }
                        else {
                            month1 = 7;
                            month1Idx = j;
                        }
                    }
                    else if(lower.equals("august")) {
                        if(toFound) {
                            month2 = 8;
                            month2Idx = j;
                        }
                        else {
                            month1 = 8;
                            month1Idx = j;
                        }
                    }
                    else if(lower.equals("september")) {
                        if(toFound) {
                            month2 = 9;
                            month2Idx = j;
                        }
                        else {
                            month1 = 9;
                            month1Idx = j;
                        }
                    }
                    else if(lower.equals("october")) {
                        if(toFound) {
                            month2 = 10;
                            month2Idx = j;
                        }
                        else {
                            month1 = 10;
                            month1Idx = j;
                        }
                    }
                    else if(lower.equals("november")) {
                        if(toFound) {
                            month2 = 11;
                            month2Idx = j;
                        }
                        else {
                            month1 = 11;
                            month1Idx = j;
                        }
                    }
                    else if(lower.equals("december")) {
                        if(toFound) {
                            month2 = 12;
                            month2Idx = j;
                        }
                        else {
                            month1 = 12;
                            month1Idx = j;
                        }
                    }


                }

                // hack for "Photos from february"
                if(beforeFound) {
                    month2 = month1;
                    day2 = day1;

                    month1 = 1;
                    day1 = 1;
                } else if(sinceFound) {
                    month2 = 12;
                    day2 = 31;
                } else if(month1 > 0 && fromFound && day1 <= 0 && month2 <= 0 && day2 <= 0) {
                    day1 = 1;
                    month2 = month1+1;
                    day2 = 1;
                }

                if(month1 > 0 && day1 > 0) {
                    if(month2 == 0 || day2 == 0) {
                        month2 = month1;
                        day2 = day1;
                    }

                    String month1Str = "00".substring(String.valueOf(month1).length()) + String.valueOf(month1);
                    String month2Str = "00".substring(String.valueOf(month2).length()) + String.valueOf(month2);
                    String day1Str = "00".substring(String.valueOf(day1).length()) + String.valueOf(day1);
                    String day2Str = "00".substring(String.valueOf(day2).length()) + String.valueOf(day2);



                    String dtStart = month1Str + "/" + day1Str + "/2019";
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                    Date date1 = null;
                    try {
                        date1 = format.parse(dtStart);
                        System.out.println("Date ->" + date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String dtEnd = month2Str + "/" + day2Str + "/2019";
                    Date date2 = null;
                    try {
                        date2 = format.parse(dtEnd);
                        System.out.println("Date ->" + date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(date1 != null && date2 != null) {
                        ((EditText)findViewById(R.id.search_fromDate)).setText("2019-"+month1Str+"-"+day1Str);
                        ((EditText)findViewById(R.id.search_toDate)).setText("2019-"+month2Str+"-"+day2Str);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                    SearchActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            search(null);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        return;
                    }
                }
            }
            ((EditText)findViewById(R.id.search_tag)).setText(sentence0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        SearchActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                search(null);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


//            Toast.makeText(SearchActivity.this, str, Toast.LENGTH_LONG).show();


//            mText.setText("results: "+String.valueOf(data.size()));
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        library = new ImageLibrary(this, null, null);

        getSupportActionBar().setTitle("COMP 7082 Photo Search");

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new Listener());

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        String requiredPermission = Manifest.permission.RECORD_AUDIO;

        // If the user previously denied this permission then show a message explaining why
        // this permission is needed
        if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{requiredPermission}, 101);
        } else {
            // automatically start listening
            voice(null);
        }


        //}


        // perform set on query text listener event

    }

    public void cancel(View view) {
        finish();
    }

    public void voice(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        sr.startListening(intent);
//        Log.i("111111","11111111");
    }

    public void search(View view) {

        String tag = ((EditText)findViewById(R.id.search_tag)).getText().toString();
        String startDate = ((EditText)findViewById(R.id.search_fromDate)).getText().toString();
        String endDate = ((EditText)findViewById(R.id.search_toDate)).getText().toString();

        String searchLat = ((EditText)findViewById(R.id.search_lat)).getText().toString();
        String searchLon = ((EditText)findViewById(R.id.search_lon)).getText().toString();
        Location searchLoc = null;
        if(!searchLat.isEmpty() && !searchLon.isEmpty()) {
            searchLoc = new Location("tmp");
            searchLoc.setLatitude(Double.parseDouble(searchLat));
            searchLoc.setLongitude(Double.parseDouble(searchLon));
        }



        Date start = null;
        Date end = null;
        try {
            if(startDate != null && endDate != null) {
                start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String auto = ((EditText)findViewById(R.id.search_auto)).getText().toString();
        if(auto.equals("")) auto = null;

        ArrayList<String> pathMatch = library.search( tag, start, end, searchLoc, auto);


        if(pathMatch == null || pathMatch.size() == 0 || pathMatch.get(0).equals(""))
            Toast.makeText(SearchActivity.this, "Photo not found!", Toast.LENGTH_LONG).show();
        else {
            Toast.makeText(SearchActivity.this, "Found a matching photo: "+pathMatch.get(0)+"!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.putExtra("photo_path", pathMatch.get(0));
            setResult(AppCompatActivity.RESULT_OK, intent);
        }
        finish();
    }
}
