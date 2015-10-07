# TwitterViewer
To run application simply checkout this repo and import into android studio. This application can be run in the normal manner of running an application in android studio.

Note: this application uses the latest support libraries, so please ensure your sdk's are up to date and you have API 23 installed (min sdk is 17)

# Code structure

All calls can be seen ApiUtils and parsed in ApiParser. You'll notice the actual call is done in the SameThreadRequest class. This is because the new thread has already been created in a loader. If there was any calls that were not required to load the screen (eg due to user interaction) volley would create it's own thread and SameThreadRequest would not be used


The app contains a splash screen that authenticates with twitter to obtain the bearer token that is used for all subsequent calls.

This call is done in a fragment loader to ensure the call is attached to the life cycle of the activity.

After the call has successfully run it will open the MainActivity.

MainActivity contains a single fragment that displays the tweets required.

MainActivityFragment contains another loader to load the Tweets required.
You can see in loadInBackground of this class that a list of SearchParams is used to easily create a search and abstract the actual search code from the call. This is so the dev doesn't need to know how a call or the twitter search api works. They only need to know that when the correct params are parse in they will recieve the required data.

The ListView in MainActivityFragment uses a ViewHolder to ensure maximum performace. findViewById() is not very quick and can show visible lag in more complex layouts.

This application is missing some error handling (eg notify the user if you can't authenticate with twitter) and the UI is far from pretty. It is probably more complex than required from such a simple application, but it does showcase a number of design patterns as well as good programming practice in the use of android patterns.