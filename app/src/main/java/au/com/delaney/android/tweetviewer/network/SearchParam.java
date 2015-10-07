package au.com.delaney.android.tweetviewer.network;

/**
 * Created by mdelaney on 7/10/2015.
 */
public class SearchParam {

    private SearchType mySearchType;
    private String mySearchText;

    public SearchParam (SearchType searchType, String searchText) {
        mySearchType = searchType;
        mySearchText = searchText;
    }

    public String getSearchText() {
        return mySearchText;
    }

    public void setSearchText(String searchText) {
        mySearchText = searchText;
    }

    public SearchType getSearchType() {
        return mySearchType;
    }

    public void setSearchType(SearchType searchType) {
        mySearchType = searchType;
    }

    public enum SearchType {
        PERSON ("@"),
        HASH ("#"),
        FREE_TEXT("");
        private String mySearchString;

        SearchType(String searchString) {
            mySearchString = searchString;
        }

        public String getSearchString() {
            return mySearchString;
        }
    }
}
