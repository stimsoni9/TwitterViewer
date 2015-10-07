package au.com.delaney.android.tweetviewer.apimappings;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mdelaney on 7/10/2015.
 */
public class Authentication {
    @SerializedName("token_type")
    private String myTokenType;
    @SerializedName("access_token")
    private String myAccessToken;

    public String getAccessToken() {
        return myAccessToken;
    }

    public String getTokenType() {
        return myTokenType;
    }
}
