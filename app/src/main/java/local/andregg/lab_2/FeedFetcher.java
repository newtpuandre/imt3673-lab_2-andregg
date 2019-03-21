package local.andregg.lab_2;

import android.database.sqlite.SQLiteDatabase;

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

    FeedFetcher(){} //Empty constructor

    //Fetch XML from URL
    protected SyndFeed RunFetch(String... url) {

        URL feedUrl = null;

        //Try to get URL
        try {
            feedUrl = new URL(url[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //Create required Rome Feed variables
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;

        //Try to parse the XML.
        try {
            feed = input.build(new XmlReader(feedUrl));
        } catch (
                FeedException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        //Return the parsed feed
        return feed;
    }

    public ArrayList<NewsItem> Fetch(String url) {
        if (url == "") { //No reason to fetch anything if url is empty
            return null;
        }

        //Temporary return array
        ArrayList<NewsItem> temp = new ArrayList<>();

        Thread t = new Thread(() -> { //Start fetching in a new thread
            final SyndFeed feeds = RunFetch(url);
            SyndEntry entry;

            //Loop over all items in the feed list
            for (int i = 0; i < feeds.getEntries().size(); i++) {

                //Get entry with index i
                entry = feeds.getEntries().get(i);
                String description;

                if(entry.getDescription() != null) {    //Check if there are no description
                    if (entry.getDescription().getValue().length() > 100) { //Make sure the description isn't to long
                        description = entry.getDescription().getValue().substring(0, 100) + "...";
                    }else{
                        description = entry.getDescription().getValue();
                    }
                } else { // Add a description if none were provided
                    description = "No description provided";
                }

                //Create new object of type NewsItem with information parsed
                NewsItem newNewsItem = new NewsItem((int) NewsStorage.lastAddedID + 1 ,entry.getLink(), entry.getTitle(), description);
                //Add the item to the return list
                temp.add(newNewsItem);
            }
        });

        //Start the thread
        t.start();

        //Try to wait for the thread to finish.
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Return the temp return array
        return temp;
    }

    public void handleData(NewsStorage dbHelper){

        //Initialize SQLite DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Create different Arrays
        ArrayList<NewsItem> fifoTemp = new ArrayList<>();
        ArrayList<NewsItem> temp = new ArrayList<>();
        ArrayList<NewsItem> data = Fetch(FeedPreferences.URL);

        //Loop through the data and insert it into the db AND add it to the temp array
        for(int i = 0; i < data.size(); i++) {
            if (dbHelper.insertItem(db, data.get(i))){
                temp.add(data.get(i));
            }
        }

        int fifoIndex = 0; //Variable to keep track indexes of inserted arrayLists.

        while ( temp.size() != 0) { //As long as there are something to loop over
            int index = 0; //Keep track of what index we are on

            while (index != FeedPreferences.Limit && temp.size() > 0) { //As long as we havent looped
                                                                        //Over the Limit and there
                                                                        //are content left to loop on
                fifoTemp.add(temp.get(0)); //Add it to the fifoTemp array
                temp.remove(0);     //Remove it from the temp array
                index++;
            }

        MainActivity.fifoList.add(fifoIndex++, fifoTemp); //Add it to the MAIN fifoList.
        }

    }

}
