package local.andregg.lab_2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ViewContentActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_content);

        //Variables
        final WebView webview = findViewById(R.id.web_view);

        //Create a new WebViewClient
        webview.setWebViewClient(new WebViewClient());

        //Enable javascript
        webview.getSettings().setJavaScriptEnabled(true);

        //Get the URL passed to this activity and load that website.
        Intent I = getIntent();
        webview.loadUrl(I.getStringExtra("URL"));
        
    }
}
