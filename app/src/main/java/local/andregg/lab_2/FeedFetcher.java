package local.andregg.lab_2;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.rometools.utils.Strings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FeedFetcher {
    private int feedLimit;

    FeedFetcher(int limit){
        switch(limit){
            case 0: this.feedLimit = 10; break;
            case 1: this.feedLimit = 25; break;
            case 2: this.feedLimit = 50; break;
            case 3: this.feedLimit = 100; break;
            default: this.feedLimit = -1; break;
        }
    }

    protected SyndFeed RunFetch(String... url) {
        URL feedUrl = null;
        try {
            feedUrl = new URL(url[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;

        try {
            feed = input.build(new XmlReader(feedUrl));
        } catch (
                FeedException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        return feed;
    }

    public void Fetch(String url, ArrayList<NewsItem> data, MainActivity ref) {
        if (url == "") { //No reason to fetch anything if url is empty
            return;
        }

        new Thread(() -> { //Start fetching in a new thread
                final SyndFeed feeds = RunFetch(url);
                SyndEntry entry;
                Log.d("app1", Integer.toString(feedLimit));

                for (int i = 0; i <= feedLimit; i++) {
                    entry = feeds.getEntries().get(i);
                    Log.d("app1", Integer.toString(i));
                    String description;

                    if (entry.getDescription().getValue().length() > 100) { //Make sure the description isnt to long
                        description = entry.getDescription().getValue().substring(0, 100) + "...";
                    }else{
                        description = entry.getDescription().getValue();
                    }

                    NewsItem newNewsItem = new NewsItem(entry.getLink(), entry.getTitle(), description);
                    data.add(newNewsItem);
                }
                ref.updateRecyclerView(); //Update recyclerView on UI Thread
        }).start();
    }


}
