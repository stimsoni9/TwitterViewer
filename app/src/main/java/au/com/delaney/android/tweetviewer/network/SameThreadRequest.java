package au.com.delaney.android.tweetviewer.network;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import au.com.delaney.android.tweetviewer.ApiUtils;

/**
 * Created by mdelaney on 7/10/2015.
 */
public abstract class SameThreadRequest <T> {

    private static final int REQUEST_TIMEOUT_MS = 60000;
    private static final int THREAD_TIMEOUT_MS = 61000;
    private static final int MAX_RETRIES = 1;

    private int myMethod;
    private String myUrl;
    private Response.ErrorListener myErrorListener;

    public SameThreadRequest(int method, String url,
            Response.ErrorListener errorListener) {
        myMethod = method;
        myUrl = url;
        myErrorListener = errorListener;
    }

    public T execute(RequestQueue requestQueue) {
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(myMethod, myUrl,
                future, myErrorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = SameThreadRequest.this.getHeaders();
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return SameThreadRequest.this.getParams();
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

        String jsonString = null;
        try {
            jsonString = future.get(THREAD_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            String msg = "Failed to complete this request while " +
                    "getting a request from " + myUrl + " [" + e + "]";
            Log.e(ApiUtils.class.getSimpleName(), msg);
            myErrorListener.onErrorResponse(new VolleyError(e));
        } catch (ExecutionException e) {
            //this exception can be ignored as volley has already passed this onto the error listener
        } catch (TimeoutException e) {
            String msg = "Failed to complete the request due to the thread wait timing out " +
                    "before the Request timed out. Url was " + myUrl + " [" + e + "]";
            Log.e(ApiUtils.class.getSimpleName(), msg);
            myErrorListener.onErrorResponse(new VolleyError(e));
        }

        if(!TextUtils.isEmpty(jsonString)) {
            T parsedObject = parseObject(jsonString);

            return parsedObject;
        }

        return null;
    }

    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }

    public abstract T parseObject(String jsonString);
}
