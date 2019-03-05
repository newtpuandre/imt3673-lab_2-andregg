package local.andregg.lab_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener{

    RecyclerViewAdapter adapter;
    public static final String PREFS_NAME = "MyNewsReader";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Variables
        final Button btnSettings = findViewById(R.id.settings_button);
        FeedFetcher fetcher = new FeedFetcher();

        //Shared preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        final ArrayList<NewsItem> testData = new ArrayList<>();
        adapter = new RecyclerViewAdapter(this, testData);
        adapter.setClickListener(this);

        String url = prefs.getString("URL", "");

        new Thread(() -> {
            final SyndFeed feeds = fetcher.Fetch(url);

                for (SyndEntry entry : feeds.getEntries()) {
                    String description;
                    if (entry.getDescription().getValue().length() > 100) {
                        description = entry.getDescription().getValue().substring(0, 100) + "...";
                    }else{
                        description = entry.getDescription().getValue();
                    }
                    NewsItem newNewsItem = new NewsItem(entry.getLink(), entry.getTitle(), description);
                    testData.add(newNewsItem);
                }
                updateData();
        }).start();

        //Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Settings button logic
        btnSettings.setOnClickListener(v -> {
            Intent I = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(I);
        });


    }

    @Override
    public void onItemClick(View view, int position) { //Position corresponds to the item number in class XXX
        Toast.makeText(this, "POG", Toast.LENGTH_SHORT).show();

    }

    public void updateData(){
        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }
}
