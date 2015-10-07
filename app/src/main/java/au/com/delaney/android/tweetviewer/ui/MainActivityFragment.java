package au.com.delaney.android.tweetviewer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import au.com.delaney.android.tweetviewer.ApiUtils;
import au.com.delaney.android.tweetviewer.R;
import au.com.delaney.android.tweetviewer.apimappings.Tweet;
import au.com.delaney.android.tweetviewer.network.SearchParam;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Tweet>> {

    @InjectView(R.id.tweetList_lsvTweets)
    ListView myListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<Tweet>> onCreateLoader(int id, Bundle args) {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("TEST", "" + volleyError);
                //TODO show error msg to user that they were unable to authenticate with twitter.
            }
        };
        return new TweetLoader(getActivity(), errorListener);
    }

    @Override
    public void onLoadFinished(Loader<List<Tweet>> loader, List<Tweet> data) {
        myListView.setAdapter(new Adapter(data));
    }

    @Override
    public void onLoaderReset(Loader<List<Tweet>> loader) {}

    private class Adapter extends BaseAdapter {

        private final List<Tweet> myTweets;

        public Adapter(List<Tweet> tweets) {
            myTweets = tweets;
        }

        @Override
        public int getCount() {
            return myTweets.size();
        }

        @Override
        public Object getItem(int position) {
            return myTweets.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = getViewHolder(parent.getContext(), convertView);
            Tweet tweet = (Tweet)getItem(position);
            vh.lblText.setText(tweet.getText());
            Picasso.with(parent.getContext()).load(
                    tweet.getUser().getProfileImageUrl()).into(vh.imgProfilePic);
            return vh.mainView;
        }

        private ViewHolder getViewHolder(Context context, View convertView) {
            if(convertView == null) {
                ViewHolder vh = new ViewHolder(context);
                return vh;
            }

            return (ViewHolder)convertView.getTag();
        }
    }

    static class ViewHolder {
        View mainView;
        @InjectView(R.id.lsvItemTweets_imgProfile)
        ImageView imgProfilePic;
        @InjectView(R.id.lsvItemTweets_lblText)
        TextView lblText;

        public ViewHolder(Context context) {
            mainView = View.inflate(context, R.layout.lsv_item_tweets, null);
            ButterKnife.inject(this, mainView);
            mainView.setTag(this);
        }
    }

    private static class TweetLoader extends AsyncTaskLoader<List<Tweet>> {

        private WeakReference<Response.ErrorListener> myErrorListener;
        private static List<Tweet> myTweets;

        public TweetLoader(Context context, Response.ErrorListener errorListener) {
            super(context);
            myErrorListener = new WeakReference<Response.ErrorListener>(errorListener);
        }

        @Override
        public List<Tweet> loadInBackground() {
            List<SearchParam> params = new ArrayList<SearchParam>();
            params.add(new SearchParam(SearchParam.SearchType.FREE_TEXT, "hipages"));
            params.add(new SearchParam(SearchParam.SearchType.HASH, "hipages"));
            params.add(new SearchParam(SearchParam.SearchType.PERSON, "hipages"));

            return ApiUtils.findTweets(myErrorListener.get(), params);
        }

        @Override
        protected void onStartLoading() {
            if (myTweets != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(myTweets);
            }

            if (myTweets == null) {
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
        public void deliverResult(List<Tweet> tweets) {
            myTweets = tweets;
            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(tweets);
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
