package local.andregg.lab_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
        final EditText filterTxt = findViewById(R.id.filter_txt);
        FeedFetcher fetcher = new FeedFetcher(Limit);

        final ArrayList<NewsItem> data = new ArrayList<>();
        adapter = new RecyclerViewAdapter(this, data);
        adapter.setClickListener(this);

        //Initialize SQLite DB
        NewsStorage dbHelper = new NewsStorage(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        NewsItem tempnews = new NewsItem("link", "title", "desc");
        dbHelper.insertItem(db, tempnews);

        //Get newsItems from sqlite db
        ArrayList<NewsItem> tempdata = dbHelper.getItems(db);
        for (int i = 0; i < tempdata.size(); i++) {
            data.add(tempdata.get(i));
        }
         dbHelper.getItems(db);
        updateRecyclerView();

        //fetcher.Fetch(url, data, this);

        //Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Start service
        Intent FetchIntent = new Intent(this, FetchNewsIntentService.class);
        FetchIntent.setAction("local.andregg.lab_2 test"); //TODO edit this string.
        startService(FetchIntent);

        //Settings button logic
        btnSettings.setOnClickListener(v -> {
            Intent I = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(I);
        });

        filterTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString() != "") { //TODO Move to a function.
                    //Use the filtered version
                    ArrayList<NewsItem> temp = new ArrayList<>();
                    for(int i = 0; i < data.size(); i++) {
                        if(data.get(i).returnHeader().toLowerCase().contains(s.toString().toLowerCase()) ||
                                data.get(i).returnDescription().toLowerCase().contains(s.toString().toLowerCase())) {
                            temp.add(data.get(i));
                        }
                    }
                    adapter.setData(MainActivity.this, temp);
                } else {
                    //Revert data back to original
                    adapter.setData(MainActivity.this, data);
                }

                updateRecyclerView();
                //adapter.
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { //Do nothing
                 }

            @Override
            public void afterTextChanged(Editable s) {//Do nothing
                 }
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
