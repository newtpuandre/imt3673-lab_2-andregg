package local.andregg.lab_2;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener{

    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Variables
        final Button btnSettings = findViewById(R.id.settings_button);

        //Dummy data
        ArrayList<NewsItem> testData = new ArrayList<>();
        NewsItem testNewsItem = new NewsItem("link", "testHeader", "TestDescription");
        testData.add(testNewsItem);


        //Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, testData);
        adapter.setClickListener(this);
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
}
