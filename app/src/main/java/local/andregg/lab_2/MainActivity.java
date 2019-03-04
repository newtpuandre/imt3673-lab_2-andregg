package local.andregg.lab_2;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://www.google.com";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            // Display the first 500 characters of the response string.

            Toast.makeText(getApplicationContext(),"Response :" + response, Toast.LENGTH_LONG).show();//display the response on screen
        }, error -> {
            Toast.makeText(getApplicationContext(),"Response :" + error.toString(), Toast.LENGTH_LONG).show();//display the response on screen
            Log.d("app1",error.toString());
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void onItemClick(View view, int position) { //Position corresponds to the item number in class XXX
        Toast.makeText(this, "POG", Toast.LENGTH_SHORT).show();
    }

}
