package com.develop.sns.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.develop.sns.networkhandler.AppUrlManager;
import com.develop.sns.networkhandler.VolleyMultipartRequest;
import com.develop.sns.utils.AppConstant;
import com.develop.sns.utils.ApplicationManager;
import com.develop.sns.utils.CommonClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ApiRepository {

    private final String TAG = getClass().getSimpleName();

    public static String DEVICE_AGENT = "Device-Agent";
    public static String DEVICE_AGENT_VALUE = "$__EZEONE_|_2015_|_ANDROID_|_APP__$";

    private static final int initialTimeoutMs = 10 * 1000;
    private static final int maxNumRetries = 3;
    private static final float backoffMultiplier = 1.0f;

    public MutableLiveData<JSONObject> serviceCall(String url, int restType, JSONObject requestObject, String token) {
        final MutableLiveData<JSONObject> mutableLiveData = new MutableLiveData<>();
        try {
            switch (restType) {
                case AppConstant.REST_CALL_GET:
                    JsonObjectRequest getObjRequest = new JsonObjectRequest(Request.Method.GET, url, requestObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    mutableLiveData.setValue(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    NetworkResponse response = error.networkResponse;
                                    if (response != null && response.data != null) {
                                        Log.e("status", response.statusCode + "");
                                        switch (response.statusCode) {
                                            default:
                                                String jsonDefault = new String(response.data);
                                                try {
                                                    JSONObject obj = new JSONObject(jsonDefault);
                                                    mutableLiveData.setValue(obj);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                        }
                                    }
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put(DEVICE_AGENT, DEVICE_AGENT_VALUE);
                            params.put("Content-Type", "application/json; charset=utf-8");
                            if (!token.isEmpty()) {
                                params.put("Authorization", "Bearer " + token);
                            }
                            return params;
                        }
                    };
                    getObjRequest.setShouldCache(false);
                    getObjRequest.setRetryPolicy(new DefaultRetryPolicy(initialTimeoutMs, maxNumRetries, backoffMultiplier));
                    ApplicationManager.instance.addToRequestQueue(getObjRequest, TAG);
                    break;
                case AppConstant.REST_CALL_POST:
                    JsonObjectRequest postObjRequest = new JsonObjectRequest(Request.Method.POST, url, requestObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    mutableLiveData.setValue(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    NetworkResponse response = error.networkResponse;
                                    if (response != null && response.data != null) {
                                        Log.e("status", response.statusCode + "");
                                        switch (response.statusCode) {
                                            default:
                                                String jsonDefault = new String(response.data);
                                                try {
                                                    JSONObject obj = new JSONObject(jsonDefault);
                                                    mutableLiveData.setValue(obj);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                        }
                                    }
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put(DEVICE_AGENT, DEVICE_AGENT_VALUE);
                            params.put("Content-Type", "application/json; charset=utf-8");
                            if (!token.isEmpty()) {
                                params.put("Authorization", "Bearer " + token);
                            }
                            return params;
                        }
                    };
                    postObjRequest.setShouldCache(false);
                    postObjRequest.setRetryPolicy(new DefaultRetryPolicy(initialTimeoutMs, maxNumRetries, backoffMultiplier));
                    ApplicationManager.instance.addToRequestQueue(postObjRequest, TAG);
                    break;
                case AppConstant.REST_CALL_PUT:
                    JsonObjectRequest putObjRequest = new JsonObjectRequest(Request.Method.PUT, url, requestObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    mutableLiveData.setValue(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            headers.put(DEVICE_AGENT, DEVICE_AGENT_VALUE);
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            if (!token.isEmpty()) {
                                headers.put("Authorization", "Bearer " + token);
                            }
                            return headers;
                        }
                    };

                    putObjRequest.setShouldCache(false);
                    putObjRequest.setRetryPolicy(new DefaultRetryPolicy(initialTimeoutMs, maxNumRetries, backoffMultiplier));
                    ApplicationManager.instance.addToRequestQueue(putObjRequest, TAG);
                    break;
                case AppConstant.REST_CALL_DELETE:
                    JsonObjectRequest deleteObjRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    mutableLiveData.setValue(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put(DEVICE_AGENT, DEVICE_AGENT_VALUE);
                            params.put("Content-Type", "application/json; charset=utf-8");
                            if (!token.isEmpty()) {
                                params.put("Authorization", "Bearer " + token);
                            }
                            return params;
                        }
                    };

                    deleteObjRequest.setShouldCache(false);
                    deleteObjRequest.setRetryPolicy(new DefaultRetryPolicy(initialTimeoutMs, maxNumRetries, backoffMultiplier));
                    ApplicationManager.instance.addToRequestQueue(deleteObjRequest, TAG);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mutableLiveData;
    }

    public MutableLiveData<NetworkResponse> uploadAttachment(File file) {

        final MutableLiveData<NetworkResponse> mutableLiveData = new MutableLiveData<>();
        String url = AppUrlManager.Companion.getAPIUrl() + "icr/service_attachment";
        VolleyMultipartRequest attachmentObjRequest = new VolleyMultipartRequest(
                Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                System.out.println(response);
                mutableLiveData.setValue(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //params.put("__jwToken", token);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                params.put("attachment", new DataPart(file.getName(), CommonClass.Companion.getFileDataFromFilePath(file.getAbsolutePath())));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(DEVICE_AGENT, DEVICE_AGENT_VALUE);
                return params;
            }
        };
        attachmentObjRequest.setShouldCache(false);
        attachmentObjRequest.setRetryPolicy(new DefaultRetryPolicy(initialTimeoutMs, maxNumRetries, backoffMultiplier));
        ApplicationManager.instance.addToRequestQueue(attachmentObjRequest, TAG);
        return mutableLiveData;
    }

}
