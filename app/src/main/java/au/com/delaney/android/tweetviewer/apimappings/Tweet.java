package au.com.delaney.android.tweetviewer.apimappings;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mdelaney on 7/10/2015.
 */
public class Tweet {

    @SerializedName("text")
    private String myText;
    @SerializedName("user")
    private User myUser;

    public String getText() {
        return myText;
    }

    public User getUser() {
        return myUser;
    }

    public static class User {
        @SerializedName("name")
        private String myName;
        @SerializedName("profile_image_url")
        private String myProfileImageUrl;

        public String getName() {
            return myName;
        }

        public String getProfileImageUrl() {
            return myProfileImageUrl;
        }
    }
}
