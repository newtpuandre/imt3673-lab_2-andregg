package local.andregg.lab_2;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
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
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener{

    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    SQLiteDatabase db;


    public static final String PREFS_NAME = "MyNewsReader";
    public static int Limit;
    public static String url;
    public static int UpdateFreq;
    private static NewsStorage dbHelper;
    public static ArrayList<ArrayList<NewsItem>> fifoList;
    boolean searching = false;
    ArrayList<NewsItem> data;
    ArrayList<NewsItem> tempData;

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
        db = dbHelper.getWritableDatabase();

        //Start service
        Intent FetchIntent = new Intent(MainActivity.this, FetchNewsIntentService.class);
        startService(FetchIntent);

        //Calculate limits
        calculateLimit(prefs.getInt("Limit", -1));

        //Get newsItems from sqlite db
        fifoList = splitData(db, Limit);

        if(fifoList.size() != 0) { //Check if there is data in the fifo list
            tempData = fifoList.get(0);
            fifoList.remove(0);
            data.addAll(tempData);
        } else {
            String message = "Nothing to show. Go to settings and add a RSS/ATOM 2.0 feed.";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();
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
                filterData(s.toString(),count);
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

        if(searching) {
            return;
        }

        Handler handler = new Handler();
        handler.post(() -> {
            int scrollPosition = data.size();

            if ( fifoList.size() != 0) {
                ArrayList<NewsItem> newData = fifoList.get(0);
                fifoList.remove(0);

                for (int i = 0; i < newData.size(); i++) {
                    data.add(newData.get(i));
                }
            }

            if (scrollPosition == dbHelper.countData(db)){
                String message = "There are currently no more items to fetch. Check back later";
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.show();
            }

            adapter.notifyDataSetChanged();
            isLoading = false;
        });




    }

    public void filterData(String s, int count){
        if (count != 0) {
            //Use the filtered version
            ArrayList<NewsItem> temp = new ArrayList<>();
            for(int i = 0; i < data.size(); i++) {
                if(data.get(i).returnHeader().toLowerCase().contains(s.toLowerCase()) ||
                        data.get(i).returnDescription().toLowerCase().contains(s.toLowerCase())) {
                    temp.add(data.get(i));
                }
            }
            //adapter.clear();
            adapter.setData(MainActivity.this, temp);
            searching = true;
        } else {
            //Revert data back to original
            adapter.setData(MainActivity.this, data);
            searching = false;

        }
        adapter.notifyDataSetChanged();
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



    public ArrayList<ArrayList<NewsItem>> splitData(SQLiteDatabase db, int limit){
        ArrayList<ArrayList<NewsItem>> retList = new ArrayList<>();

        ArrayList<NewsItem> dbData = dbHelper.getItems(db);
        ArrayList<NewsItem> temp;

        while(dbData.size() > 0){
            int index = 0;
            temp = new ArrayList<>();

            while (index != limit && dbData.size() > 0) {
                temp.add(dbData.get(0));
                dbData.remove(0);
                index++;
            }

            retList.add(temp);
        }


        return retList;
    }

    private void calculateLimit(int m_limit) {
        switch(m_limit){
            case 0: this.Limit = 10; break;
            case 1: this.Limit = 20; break;
            case 2: this.Limit = 50; break;
            case 3: this.Limit = 100; break;
            default: this.Limit = -1; break;
        }

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
