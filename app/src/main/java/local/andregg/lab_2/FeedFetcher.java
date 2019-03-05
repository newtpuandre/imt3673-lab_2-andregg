package local.andregg.lab_2;

import android.os.AsyncTask;
import android.util.Log;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FeedFetcher {

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
        new Thread(() -> {
            final SyndFeed feeds = RunFetch(url);

            for (SyndEntry entry : feeds.getEntries()) {
                String description;
                if (entry.getDescription().getValue().length() > 100) {
                    description = entry.getDescription().getValue().substring(0, 100) + "...";
                }else{
                    description = entry.getDescription().getValue();
                }
                NewsItem newNewsItem = new NewsItem(entry.getLink(), entry.getTitle(), description);
                data.add(newNewsItem);
            }

            ref.updateRecyclerView();

        }).start();
    }


}
