package au.com.delaney.android.tweetviewer;

import android.app.Application;
import android.content.Context;

/**
 * Created by mdelaney on 7/10/2015.
 */
public class TweetViewerApplication extends Application {

    private static Context theAppContext;

    @Override
    public void onCreate() {
        super.onCreate();

        theAppContext = this;
    }

    public static Context getAppContext() {
        return theAppContext;
    }
}
