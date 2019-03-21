package local.andregg.lab_2;

import android.app.Service;
import android.content.Intent;
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
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener{

    //Class variables
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;

    //SQLite variables
    private SQLiteDatabase db;
    private static NewsStorage dbHelper;

    //Used to determine if we are filtering the data or not.
    boolean searching = false;

    //Data used for displaying or store in memory
    public static ArrayList<ArrayList<NewsItem>> fifoList;
    private ArrayList<NewsItem> data;
    private ArrayList<NewsItem> tempData;

    //Used to determine if we are waiting for the recyclerview to update.
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup dbHelper
        dbHelper = new NewsStorage(getApplicationContext());

        //Get sharedPreferences
        FeedPreferences.getPreferences(this);

        //Variables
        final Button btnSettings = findViewById(R.id.settings_button);
        final EditText filterTxt = findViewById(R.id.filter_txt);

        //Initialize data arrayList
        data = new ArrayList<>();

        //Initialize Adapter and add click listener
        adapter = new RecyclerViewAdapter(this, data);
        adapter.setClickListener(this);

        //Initialize SQLite DB
        db = dbHelper.getWritableDatabase();

        //Start service
        Intent FetchIntent = new Intent(MainActivity.this, FetchNewsIntentService.class);
        startService(FetchIntent);

        //Create items in the
        fifoList = splitData(db, FeedPreferences.Limit);

        if(fifoList.size() != 0) { //Check if there is data in the fifo list
            tempData = fifoList.get(0);
            fifoList.remove(0);
            data.addAll(tempData);
        } else {
            String message = "Nothing to show. Go to settings and add a RSS/ATOM 2.0 feed.";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();
        }

        //Update the recyclerview
        updateRecyclerView();

        //Set up the RecyclerView
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(mLayoutManager);

        //Add 'infinte' scroll
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

        //Filter textbox text watcher
        filterTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( count != 0 ){ //Check if there are characters inputed
                    ArrayList<NewsItem> filterData = filterData(data ,s.toString());
                    adapter.setData(MainActivity.this, filterData);
                    searching = true;
                } else { //Revert back to original data
                    adapter.setData(MainActivity.this, data);
                    searching = false;
                }
                adapter.notifyDataSetChanged();
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
        if(searching) { //Dont load anything if we are currently filtering
            return;
        }

        Handler handler = new Handler();

        //Do recyclerview endless loading on the ui thread.
        handler.post(() -> {
            int scrollPosition = data.size();

            //Is there stuff left to add?
            if ( fifoList.size() != 0) {
                //Always get the first element and then remove it.
                ArrayList<NewsItem> newData = fifoList.get(0);
                fifoList.remove(0);

                for (int i = 0; i < newData.size(); i++) { //Add the data to the recyclerview
                    data.add(newData.get(i));
                }
            }

            //Have we reached the end of the data?
            if (scrollPosition == dbHelper.countData(db)){
                String message = "There are currently no more items to fetch. Check back later";
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.show();
            }

            adapter.notifyDataSetChanged();
            isLoading = false;
        });




    }

    public static ArrayList<NewsItem> filterData(ArrayList<NewsItem> m_data,String s){
        ArrayList<NewsItem> temp = new ArrayList<>(); //Temp return array
            for(int i = 0; i < m_data.size(); i++) { //Loop over array
                //Does the title or description contain the search string?
                if(m_data.get(i).returnHeader().toLowerCase().contains(s.toLowerCase()) ||
                        m_data.get(i).returnDescription().toLowerCase().contains(s.toLowerCase())) {
                    temp.add(m_data.get(i)); //Add it to the return array
                }
            }

        return temp;
    }


    @Override
    public void onItemClick(View view, int position) { //Position corresponds to the item number in the recyclerView
        Intent I = new Intent(MainActivity.this, ViewContentActivity.class);
        I.putExtra("URL", adapter.getItem(position).returnLink());
        startActivity(I);
    }


    public void updateRecyclerView(){ //Updates the RecyclerView
       runOnUiThread(() -> adapter.notifyDataSetChanged());
    }


    //Splits data into arrays of size Limit. Returns a Array of Arrays containing NewsItems
    public ArrayList<ArrayList<NewsItem>> splitData(SQLiteDatabase db, int limit){
        ArrayList<ArrayList<NewsItem>> retList = new ArrayList<>(); //Temporary return array

        //Get data from database
        ArrayList<NewsItem> dbData = dbHelper.getItems(db);
        ArrayList<NewsItem> temp;

        //Loop over data as long as there still are data left
        while(dbData.size() > 0){
            int index = 0;
            temp = new ArrayList<>(); //Temporary arraylist

            while (index != limit && dbData.size() > 0) { //As long as index are less than limit
                                                          // and there still are data left to loop over
                temp.add(dbData.get(0)); //Add data to temp arraylist
                dbData.remove(0);  //remove from database array list
                index++;
            }

            retList.add(temp);  //Add temp arraylist to return array
        }


        return retList;
    }

    public static class FetchNewsIntentService extends Service {

        Handler mHandler;
        FeedFetcher fetcher;

        @Override
        public void onCreate() {
            super.onCreate();
            fetcher = new FeedFetcher(); //New fetcher object
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            mHandler = new Handler();
            FetchNews(); //Initial fetch
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


        private void FetchNews(){ //Fetch news
            try {
                fetcher.handleData(dbHelper); //Fetch news
            } catch (Exception e) {
                Log.e("Error", "In onStartCommand");
                e.printStackTrace();
            }
            scheduleNext(); //Schedule next fetch
        }

        private void scheduleNext() { //Wait for updateFreq time. Specified by user
            mHandler.postDelayed(() -> FetchNews(), FeedPreferences.updateFreq);
        }

    }

}
