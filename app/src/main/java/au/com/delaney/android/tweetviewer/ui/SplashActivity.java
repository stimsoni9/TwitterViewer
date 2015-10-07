package au.com.delaney.android.tweetviewer.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.lang.ref.WeakReference;

import au.com.delaney.android.tweetviewer.ApiUtils;
import au.com.delaney.android.tweetviewer.R;
import au.com.delaney.android.tweetviewer.apimappings.Authentication;

/**
 * Created by mdelaney on 7/10/2015.
 */
public class SplashActivity extends Activity implements LoaderManager.LoaderCallbacks<Authentication> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Authentication> onCreateLoader(int id, Bundle args) {

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //TODO show error msg to user that they were unable to authenticate with twitter.
                Log.d("TEST", "" + volleyError);
            }
        };
        return new AuthLoader(this, errorListener);
    }

    @Override
    public void onLoadFinished(Loader<Authentication> loader, Authentication data) {
        if (data != null && data.getAccessToken() != null) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in,
                    android.R.anim.fade_out);
        } else {
            //TODO show error msg to user that they were unable to authenticate with twitter.
        }
    }

    @Override
    public void onLoaderReset(Loader<Authentication> loader) {

    }

    private static class AuthLoader extends AsyncTaskLoader<Authentication> {

        private WeakReference<Response.ErrorListener> myErrorListener;
        private static Authentication myTwitterAuthentication;

        public AuthLoader(Context context, Response.ErrorListener errorListener) {
            super(context);
            myErrorListener = new WeakReference<Response.ErrorListener>(errorListener);
        }

        @Override
        public Authentication loadInBackground() {
            return ApiUtils.getBearerToken(myErrorListener.get());
        }

        @Override
        protected void onStartLoading() {
            if (myTwitterAuthentication != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(myTwitterAuthentication);
            }

            if (myTwitterAuthentication == null) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override
        public void deliverResult(Authentication auth) {
            myTwitterAuthentication = auth;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(auth);
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override
        protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }
    }
}
