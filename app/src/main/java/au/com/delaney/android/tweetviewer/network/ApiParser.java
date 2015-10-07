package au.com.delaney.android.tweetviewer.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import au.com.delaney.android.tweetviewer.apimappings.Authentication;
import au.com.delaney.android.tweetviewer.apimappings.Tweet;

/**
 * Created by mdelaney on 7/10/2015.
 */
public class ApiParser {

    public static Authentication parseAuthentication(String jsonString) {

        Authentication auth = new Gson().fromJson(jsonString, Authentication.class);
        return auth;
    }

    public static List<Tweet> parseTweets(String jsonString) throws JSONException {
        JSONArray jsonObject = new JSONObject(jsonString).getJSONArray("statuses");
        Type listType = new TypeToken<List<Tweet>>() {}.getType();
        List<Tweet> tweets = new Gson().fromJson(jsonObject.toString(), listType);
        return tweets;
    }
}
