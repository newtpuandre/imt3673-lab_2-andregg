package local.andregg.lab_2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

        //Gather information already saved.
        String restoredURL = prefs.getString("URL", null);
        int restoredLimit = prefs.getInt("Limit", -1);
        int restoredUpdateFreq = prefs.getInt("UpdateFreq", -1);

        //Set URL = saved url if there is one set in the preferences
        if (restoredURL != null) {
            Urltxt.setText(restoredURL);
        }

        //Set up spinners
        String[] newsLimitItems = {"10", "20", "50", "100"};
        ArrayAdapter<String> newsLimitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, newsLimitItems);
        newsLimitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newsLimitSpinner.setAdapter(newsLimitAdapter);
        //Set Limit value to stored value
        if(restoredLimit != -1) {
            newsLimitSpinner.setSelection(restoredLimit);
        }

        String[] updateItems = {"10 Minutes", "60 Minutes", "Once a day"};
        ArrayAdapter<String> updateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, updateItems);
        updateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateSpinner.setAdapter(updateAdapter);
        //Set Update frequency value to stored value
        if(restoredUpdateFreq != -1) {
            updateSpinner.setSelection(restoredUpdateFreq);
        }

        //Button handler
        updateBtn.setOnClickListener(v -> {
            //Save all settings in the sharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("URL", Urltxt.getText().toString());
            editor.putInt("Limit", newsLimitSpinner.getSelectedItemPosition());
            editor.putInt("UpdateFreq", updateSpinner.getSelectedItemPosition());
            editor.apply();

            //Update the variables that the rest of the app uses.
            FeedPreferences.getPreferences(this);
            MainActivity.fifoList = MainActivity.consolidateData(MainActivity.fifoList);

            String message = "Settings have been updated.";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.show();
        });
    }
}
