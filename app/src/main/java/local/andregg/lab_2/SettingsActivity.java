package local.andregg.lab_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyNewsReader";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Variables
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final Spinner newsLimitSpinner = findViewById(R.id.newslimit_spinner);
        final Spinner updateSpinner = findViewById(R.id.update_spinner);
        final Button updateBtn = findViewById(R.id.update_btn);
        final EditText Urltxt = findViewById(R.id.url_text);


        String restoredURL = prefs.getString("URL", null);
        int restoredLimit = prefs.getInt("Limit", -1);
        int restoredUpdateFreq = prefs.getInt("UpdateFreq", -1);

        if (restoredURL != null) {
            Urltxt.setText(restoredURL);
        }

        //Set up spinners
        String[] newsLimitItems = {"10", "20", "50", "100"};
        ArrayAdapter<String> newsLimitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, newsLimitItems);
        newsLimitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newsLimitSpinner.setAdapter(newsLimitAdapter);
        if(restoredLimit != -1) {
            newsLimitSpinner.setSelection(restoredLimit);
        }

        String[] updateItems = {"10 Minutes", "60 Minutes", "Once a day"};
        ArrayAdapter<String> updateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, updateItems);
        updateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateSpinner.setAdapter(updateAdapter);
        if(restoredUpdateFreq != -1) {
            updateSpinner.setSelection(restoredUpdateFreq);
        }

        updateBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("URL", Urltxt.getText().toString());
            editor.putInt("Limit", newsLimitSpinner.getSelectedItemPosition());
            editor.putInt("UpdateFreq", updateSpinner.getSelectedItemPosition());
            editor.apply();

            FeedPreferences.getPreferences(this); //Update the Preference class
        });
    }
}
