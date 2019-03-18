package local.andregg.lab_2;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener{

    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;

    public static final String PREFS_NAME = "MyNewsReader";
    public static int Limit;
    public static String url;
    public static int UpdateFreq;
    private static NewsStorage dbHelper;
    ArrayList<NewsItem> data;
    ArrayList<NewsItem> tempData;
    private int lastLoadedID = 0;

    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup dbHelper
        dbHelper = new NewsStorage(getApplicationContext());

        //Shared preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        url = prefs.getString("URL", "");
        UpdateFreq = prefs.getInt("UpdateFreq", -1);

        //Variables
        final Button btnSettings = findViewById(R.id.settings_button);
        final EditText filterTxt = findViewById(R.id.filter_txt);

        data = new ArrayList<>();
        adapter = new RecyclerViewAdapter(this, data);
        adapter.setClickListener(this);

        //Initialize SQLite DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Start service
        Intent FetchIntent = new Intent(MainActivity.this, FetchNewsIntentService.class);
        startService(FetchIntent);

        //Calculate limits
        calculateLimit(prefs.getInt("Limit", -1));

        //Get newsItems from sqlite db
        tempData = dbHelper.getItems(db);
        if(tempData.size() != 0) {
            if(tempData.size() > Limit) {

                for(int i = lastLoadedID; i < lastLoadedID + Limit; i++) {
                    data.add(tempData.get(lastLoadedID++));
                }

            } else {
                data.addAll(tempData);
            }

        }



        updateRecyclerView();

        //Set up the RecyclerView
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == data.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });

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
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { //Do nothing
                 }

            @Override
            public void afterTextChanged(Editable s) {//Do nothing
                 }
        });

    }

    private void loadMore() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            int scrollPosition = data.size();
            int currentSize = scrollPosition;
            int nextLimit = currentSize + Limit;

            data.add(new NewsItem("lol", Integer.toString(currentSize), "lol"));
            currentSize++;


            adapter.notifyDataSetChanged();
            isLoading = false;
        }, 500);


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

    public NewsStorage returnDbHelper(){
        return dbHelper;
    }

    private void calculateLimit(int m_limit) {
        switch(m_limit){
            case 0: this.Limit = 10; break;
            case 1: this.Limit = 20; break;
            case 2: this.Limit = 50; break;
            case 3: this.Limit = 100; break;
            default: this.Limit = -1; break;
        }

        Log.d("app1", String.valueOf(this.Limit));
    }

    public static class FetchNewsIntentService extends Service {

        Handler mHandler;
        private int updatefreq;

        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            mHandler = new Handler();
            calculateTime();
            FetchNews();
            return START_STICKY;
        }

        // In case the service is deleted or crashes some how
        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public IBinder onBind(Intent intent) {
            // Used only in case of bound services.
            return null;
        }

        private void calculateTime(){
            int m_updatefreq = 0;
            switch(m_updatefreq){
                case 0: this.updatefreq = 600000; break; //10 min
                case 1: this.updatefreq = 3600000; break; // 60 min
                case 2: this.updatefreq = 86400000; break; // 24 hours
                default: this.updatefreq = -1; break;
            }

            Log.d("app1", String.valueOf(this.updatefreq));
        }

        private void FetchNews(){
            try {
                FeedFetcher fetcher = new FeedFetcher();
                fetcher.Fetch(MainActivity.url, dbHelper);
                Log.d("NewsFetch", "fetching news");
            } catch (Exception e) {
                Log.e("Error", "In onStartCommand");
                e.printStackTrace();
            }
            scheduleNext();
        }

        private void scheduleNext() {
            mHandler.postDelayed(() -> FetchNews(), this.updatefreq);
        }

    }

}
