package au.com.delaney.android.tweetviewer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.delaney.android.tweetviewer.apimappings.Authentication;
import au.com.delaney.android.tweetviewer.apimappings.Tweet;
import au.com.delaney.android.tweetviewer.network.ApiParser;
import au.com.delaney.android.tweetviewer.network.SameThreadRequest;
import au.com.delaney.android.tweetviewer.network.SearchParam;

/**
 * This class contains static methods for doing Api calls. You can't make an instance of this class
 * Created by mdelaney on 7/10/2015.
 */
public class ApiUtils {

    public static final String UTF_8 = "UTF-8";

    //Endpoints
    private static final String API_AUTHENTICATION = "https://api.twitter.com/oauth2/token";
    private static final String API_SEARCH_TWEETS = "https://api.twitter.com/1.1/search/tweets.json?q=%s";

    private static final String HEADER_AUTHORIZATION = "Authorization";

    private final static RequestQueue myMainRequestQueue = Volley.newRequestQueue(TweetViewerApplication
            .getAppContext());
    private static Authentication myTwitterAuthentication;

    /**
     * Private so that an instance of this class can't be made.
     */
    private ApiUtils(){}

    public static Authentication getBearerToken(Response.ErrorListener errorListener) {
        SameThreadRequest<Authentication> request = new SameThreadRequest<Authentication>(
                Request.Method.POST, API_AUTHENTICATION, errorListener) {

            @Override
            public Map<String, String> getHeaders() {
                return getAuthHeader();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grant_type", "client_credentials");
                return params;
            }

            @Override
            public Authentication parseObject(String jsonString) {
                myTwitterAuthentication = ApiParser.parseAuthentication(jsonString);

                return myTwitterAuthentication;
            }
        };

        return request.execute(myMainRequestQueue);
    }

    public static List<Tweet> findTweets(Response.ErrorListener errorListener,
            final List<SearchParam> searchParams) {

        StringBuilder sb = new StringBuilder();
        for(SearchParam param : searchParams) {
            if(sb.length() > 0) {
                sb.append(" OR ");
            }
            sb.append(param.getSearchType().getSearchString())
                    .append(param.getSearchText());
        }

        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder
                    .encode(sb.toString(), UTF_8);
        } catch (UnsupportedEncodingException e) {
            errorListener.onErrorResponse(new VolleyError(e));
        }

        String url = String.format(API_SEARCH_TWEETS, encodedUrl);
        SameThreadRequest<List<Tweet>> request = new SameThreadRequest<List<Tweet>>(
                Request.Method.GET, url, errorListener) {
            @Override
            public List<Tweet> parseObject(String jsonString) {
                try {
                    return ApiParser.parseTweets(jsonString);
                } catch (JSONException e) {
                    Log.d(ApiUtils.class.getSimpleName(), "Failed to parse tweets [" + e + "]");
                }

                return null;
            }

            @Override
            public Map<String, String> getHeaders() {
                return getAuthenticatedHeader();
            }
        };

        return request.execute(myMainRequestQueue);
    }

    @NonNull
    private static Map<String, String> getAuthHeader() {
        Context context = TweetViewerApplication.getAppContext();
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String secret = context.getString(R.string.twitter_consumer_secret);

        Map<String, String> headers = new HashMap<String, String>();
        String auth = "Basic "
                + Base64.encodeToString((consumerKey
                        + ":" + secret).getBytes(),
                Base64.NO_WRAP);
        headers.put(HEADER_AUTHORIZATION, auth);
        return headers;
    }

    @NonNull
    private static Map<String, String> getAuthenticatedHeader() {

        Map<String, String> headers = new HashMap<String, String>();
        String encodedAuth = myTwitterAuthentication.getTokenType() + " " +
                myTwitterAuthentication.getAccessToken();
        headers.put(HEADER_AUTHORIZATION, encodedAuth);
        return headers;
    }

}
