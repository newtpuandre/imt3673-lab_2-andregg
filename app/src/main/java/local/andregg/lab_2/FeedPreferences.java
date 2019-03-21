package local.andregg.lab_2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class FeedPreferences {

    public static final String PREFS_NAME = "MyNewsReader";
    public static String URL;
    public static int Limit;
    public static int updateFreq;


    public static void getPreferences(Context ctx) {
        int tempLimit;
        int tempUpdateFreq;

        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        URL = prefs.getString("URL", "");
        tempUpdateFreq = prefs.getInt("UpdateFreq", -1);
        tempLimit = prefs.getInt("Limit", -1);

        calculateLimit(tempLimit);
        calculateTime(tempUpdateFreq);
        Log.d("app1", URL + " " + Limit + " " + updateFreq);

    }

    private static void calculateLimit(int m_limit) {
        switch(m_limit){
            case 0: FeedPreferences.Limit = 10; break;
            case 1: FeedPreferences.Limit = 20; break;
            case 2: FeedPreferences.Limit = 50; break;
            case 3: FeedPreferences.Limit = 100; break;
            default: FeedPreferences.Limit = -1; break;
        }

    }

    private static void calculateTime(int updateFreq){
        switch(updateFreq){
            case 0: FeedPreferences.updateFreq = 600000; break; //10 min
            case 1: FeedPreferences.updateFreq = 3600000; break; // 60 min
            case 2: FeedPreferences.updateFreq = 86400000; break; // 24 hours
            default: FeedPreferences.updateFreq = -1; break;
        }

    }

}
