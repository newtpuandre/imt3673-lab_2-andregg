package local.andregg.lab_2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FetchNewsIntentService extends Service {

    public static boolean isServiceRunning = false;
    private static int updateFreq = -1;
    private static String url = "";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction().equals("local.andregg.lab_2 test")) {
            FetchNews(2);
        }
        else stopMyService();
        return START_STICKY;
    }

    // In case the service is deleted or crashes some how
    @Override
    public void onDestroy() {
        isServiceRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    private void FetchNews(int updatefreq){

    }


    void stopMyService() {
        stopSelf();
        isServiceRunning = false;
    }
}