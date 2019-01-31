package ca.btraas.comp7031assignment1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;

public class SearchActivity extends AppCompatActivity {

    ImageLibrary library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        library = new ImageLibrary(this, "");

        getSupportActionBar().setTitle("COMP 7082 Photo Search");

        // perform set on query text listener event

    }

    public void cancel(View view) {
    }

    public void search(View view) {
        finish();
    }
}
