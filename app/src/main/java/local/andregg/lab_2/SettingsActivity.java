package local.andregg.lab_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Variables
        final Spinner newsLimitSpinner = findViewById(R.id.newslimit_spinner);
        final Spinner updateSpinner = findViewById(R.id.update_spinner);

        //Set up spinners
        String[] newsLimitItems = {"10", "20", "50", "100"};
        ArrayAdapter<String> newsLimitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, newsLimitItems);
        newsLimitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newsLimitSpinner.setAdapter(newsLimitAdapter);

        String[] updateItems = {"10 Minutes", "60 Minutes", "Once a day"};
        ArrayAdapter<String> updateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, updateItems);
        updateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateSpinner.setAdapter(updateAdapter);
    }
}
