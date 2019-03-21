package local.andregg.lab_2;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FeedUnitTest {
    @Test
    public void FilterTest(){
        //Variables.
        ArrayList<NewsItem> dataSet = new ArrayList<>();
        NewsItem temp;

        //Create items for testing
        for(int i = 0; i < 10; i++) {
            temp = new NewsItem(i, "SomeLink", "SomeHeader", "SomeDescription");
            dataSet.add(temp);
        }

        //Add 2 items we are filtering for.
        temp = new NewsItem(11, "SomeLink", "Fake news article", "Android tutorial");
        dataSet.add(temp);
        temp = new NewsItem(12, "SomeLink", "Serious news does something", "Android exposed to hackers");
        dataSet.add(temp);

        //Get the Array with the filtered items.
        ArrayList<NewsItem> filteredDataSet = MainActivity.filterData(dataSet, "news");

        //Check that the size of the array is the expected size
        assertEquals(2, filteredDataSet.size());

        //Check that the headers contains the word news
        for (int i = 0; i < filteredDataSet.size(); i++) {
            assertTrue(filteredDataSet.get(i).returnHeader().toLowerCase().contains("news"));
        }

        //Check that the description contains the word android
        for (int i = 0; i < filteredDataSet.size(); i++) {
            assertTrue(filteredDataSet.get(i).returnDescription().toLowerCase().contains("android"));
        }

    }

    @Test
    public void ParsingTest(){
        //Setup the fetcher.
        FeedFetcher feeds = new FeedFetcher();

        //Fetch items from URL. This should possibly be a static page containing preset data.
        ArrayList<NewsItem> returnFeeds =  feeds.Fetch("https://www.vg.no/rss/feed/?categories=1068%2C1069%2C1070%2C1071%2C1072%2C1073%2C1074%2C1075%2C1076%2C1077%2C1079%2C1080%2C1081%2C1082%2C1085%2C1096%2C1097%2C1098%2C1099%2C1100%2C1101%2C1102%2C1103%2C1104%2C1105%2C1106%2C1107%2C1109%2C1507&limit=100&format=atom&private=1&submit=Abonn%C3%A9r+n%C3%A5%21");

        //Check that the size is not 0
        assertNotEquals(0, returnFeeds.size());

        //Loop over the whole data set and confirm that no null items are present.
        //If there are null items something went wrong.
        for (int i = 0; i < returnFeeds.size(); i++) {
            assertNotEquals("null", returnFeeds.get(i).returnDescription());
            assertNotEquals("null", returnFeeds.get(i).returnLink());
            assertNotEquals("null", returnFeeds.get(i).returnHeader());
        }
    }
}
