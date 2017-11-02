package com.techsignage.techsignmeetings.Network;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohamed on 2/3/2017.
 */
public class VolleyRequest {

    static RequestQueue queue;
    //ProgressDialog dialog;

    public VolleyRequest()
    {

    }

    public void getJson(final VolleyCallback callback, final Activity con, final Context context,
                        final String url, final String token, final JSONObject object) {

//        if (con != null)
//        {
//            con.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    dialog = new ProgressDialog(con, R.style.StyledDialog);
//                    dialog.setMessage(con.getResources().getString(R.string.processing));
//                    dialog.show();
//                }
//            });
//        }

        if(queue == null)
            queue = Volley.newRequestQueue(context);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            callback.onSuccess(response);
                        }
                        catch (Exception ex) {
                            Log.v("", "");
                        }
//                        if (dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        if (dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", String.format("Bearer %s", token));

                return params;
            }
        };

        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);
    }


    public void getString(final VolleyCallbackString callback, final Activity con, final Context context, final String url, final String token, final String object,
                          final String contentType) {

//        if (con != null)
//        {
//            con.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    dialog = new ProgressDialog(con, R.style.StyledDialog);
//                    dialog.setMessage(con.getResources().getString(R.string.processing));
//                    dialog.show();
//                }
//            });
//        }

        if(queue == null)
            queue = Volley.newRequestQueue(context);

        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            callback.onSuccess(response);
                        }
                        catch (Exception ex) {
                            String ss = "";
                        }
//                        if (dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.getMessage());
                error.printStackTrace();
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
            }
        }) {
            @Override
            public String getBodyContentType() {
                return contentType;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return object == null ? null : object.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", String.format("Bearer %s", token));

                return params;
            }
        };

        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);
    }
}
