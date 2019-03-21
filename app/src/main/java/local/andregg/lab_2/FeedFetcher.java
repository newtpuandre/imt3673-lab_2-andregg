package local.andregg.lab_2;

import android.database.sqlite.SQLiteDatabase;
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

    FeedFetcher(){ }

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

    public ArrayList<NewsItem> Fetch(String url) {
        if (url == "") { //No reason to fetch anything if url is empty
            return null;
        }

        ArrayList<NewsItem> temp = new ArrayList<>();

        Thread t = new Thread(() -> { //Start fetching in a new thread
            Log.d("app1", "handling data : " + url);
            final SyndFeed feeds = RunFetch(url);
            SyndEntry entry;

            for (int i = 0; i < feeds.getEntries().size(); i++) {
                entry = feeds.getEntries().get(i);
                String description;
                if(entry.getDescription() != null) {
                    if (entry.getDescription().getValue().length() > 100) { //Make sure the description isn't to long
                        description = entry.getDescription().getValue().substring(0, 100) + "...";
                    }else{
                        description = entry.getDescription().getValue();
                    }
                } else {
                    description = "No description provided";
                }


                NewsItem newNewsItem = new NewsItem((int) NewsStorage.lastAddedID + 1 ,entry.getLink(), entry.getTitle(), description);
                temp.add(newNewsItem);
            }


        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return temp;
    }

    public void handleData(NewsStorage dbHelper){

        //Initialize SQLite DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<NewsItem> fifoTemp = new ArrayList<>();
        ArrayList<NewsItem> temp = new ArrayList<>();
        ArrayList<NewsItem> data = Fetch(FeedPreferences.URL);

        for(int i = 0; i < data.size(); i++) {
            if (dbHelper.insertItem(db, data.get(i))){
                temp.add(data.get(i));
            }
        }

        int fifoIndex = 0;
        while ( temp.size() != 0) {
            int index = 0;

            while (index != FeedPreferences.Limit && temp.size() > 0) {
                fifoTemp.add(temp.get(0));
                temp.remove(0);
                index++;
            }

        MainActivity.fifoList.add(fifoIndex++, fifoTemp);
        }

    }

}
