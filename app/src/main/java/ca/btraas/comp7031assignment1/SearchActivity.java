package ca.btraas.comp7031assignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchActivity extends AppCompatActivity {

    ImageLibrary library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        library = new ImageLibrary(this, null);

        getSupportActionBar().setTitle("COMP 7082 Photo Search");

        // perform set on query text listener event

    }

    public void cancel(View view) {
    }

    public void search(View view) {

        String tag = ((EditText)findViewById(R.id.search_tag)).getText().toString();
        String startDate = ((EditText)findViewById(R.id.search_fromDate)).getText().toString();
        String endDate = ((EditText)findViewById(R.id.search_toDate)).getText().toString();

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

        String pathMatch = "";

        for(File file: library.files) {
            String path = file.getAbsolutePath();
            String caption = library.getCaption(SearchActivity.this, path);
            Date lastModified = new Date(file.lastModified());

            boolean matchText = true;
            if(tag.length() > 0) matchText = false;

            if(tag.trim().equals("")) {
                matchText = true;
            } else if(caption.toLowerCase().contains(tag.toLowerCase())) {
                // match found!
                matchText = true;
            }

            boolean matchDate = true;
            if(start != null && end != null && startDate.length() > 0 || endDate.length() > 0) {
                matchDate = false;

                boolean afterStart = lastModified.after(start);
                boolean beforeEnd = lastModified.before(end);

                if(afterStart && beforeEnd) matchDate = true;


            }

            if(matchText && matchDate) {
                pathMatch = path;
            }

        }



        if(pathMatch.equals(""))
            Toast.makeText(SearchActivity.this, "Photo not found!", Toast.LENGTH_LONG).show();
        else {
            Toast.makeText(SearchActivity.this, "Found a matching photo!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.putExtra("photo_path", pathMatch);
            setResult(AppCompatActivity.RESULT_OK, intent);
        }
        finish();
    }
}
