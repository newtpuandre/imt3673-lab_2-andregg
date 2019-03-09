package local.andregg.lab_2;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class FetchNewsIntentService extends Service {

    public static boolean isServiceRunning = false;
    private static int updateFreq = -1;
    private static String url = "";
    Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new Handler();
        FetchNews();
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

    private void FetchNews(){
        try {
            Log.d("app1", "testFetch");
        } catch (Exception e) {
            Log.e("Error", "In onStartCommand");
            e.printStackTrace();
        }
        scheduleNext();
    }

    private void scheduleNext() {
        mHandler.postDelayed(() -> FetchNews(), 5000);
    }

    void stopMyService() {
        stopSelf();
        isServiceRunning = false;
    }
}