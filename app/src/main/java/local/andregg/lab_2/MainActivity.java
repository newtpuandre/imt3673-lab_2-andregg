package local.andregg.lab_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener{

    RecyclerViewAdapter adapter;
    public static final String PREFS_NAME = "MyNewsReader";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Shared preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int Limit = prefs.getInt("Limit", -1);
        String url = prefs.getString("URL", "");
        int UpdateFreq = prefs.getInt("UpdateFreq", -1);

        //Variables
        final Button btnSettings = findViewById(R.id.settings_button);
        FeedFetcher fetcher = new FeedFetcher(Limit);

        final ArrayList<NewsItem> data = new ArrayList<>();
        adapter = new RecyclerViewAdapter(this, data);
        adapter.setClickListener(this);

        fetcher.Fetch(url, data, this);

        //Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Start service
        Intent FetchIntent = new Intent(this, FetchNewsIntentService.class);
        FetchIntent.setAction("local.andregg.lab_2 test");
        startService(FetchIntent);

        //Settings button logic
        btnSettings.setOnClickListener(v -> {
            Intent I = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(I);
        });


    }

    @Override
    public void onItemClick(View view, int position) { //Position corresponds to the item number in class XXX
        Intent I = new Intent(MainActivity.this, ViewContentActivity.class);
        I.putExtra("URL", adapter.getItem(position).returnLink());
        startActivity(I);
    }

    public void updateRecyclerView(){
       runOnUiThread(() -> adapter.notifyDataSetChanged());

    }

}
