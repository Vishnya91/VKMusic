package com.yasya.vkmusic;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FragWebView extends Fragment {
    private static String ACCESS_TOKEN = "";
    private static String USER_ID = "";
    WebView mWebView;
    private onFinishWork listener;
    private static FragWebView instance;
    public JSONObject musicList;

    public FragWebView() {}

    public static FragWebView getInstance(onFinishWork listener) {
        if (instance == null) {
            instance = new FragWebView();
            instance.listener = listener;
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragwebview, null);

        mWebView = (WebView) view.findViewById(R.id.webview);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.isPrivateBrowsingEnabled();
        class MyWebViewClient extends WebViewClient {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.e("shouldOverrideUrlLoading", url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url,
                                      Bitmap favicon) {
                if (url.contains("access_token=")) {
                    ACCESS_TOKEN = url.substring(45, 130);
                    USER_ID = url.substring(156);

                    new getJSON().execute(makeUrl(ACCESS_TOKEN, USER_ID));
                } else if (url.contains("error=")) {
                    Log.e("onPageStarted: ", "Url contains error!");
                }
                super.onPageStarted(view, url, favicon);
            }
        }

        mWebView.setWebViewClient(new MyWebViewClient());

        Uri url = Uri.parse("https://oauth.vk.com/authorize?" +
                "client_id=3636270&" +
                "scope=audio&" +
                "redirect_uri=https://oauth.vk.com/blank.html&" +
                "display=mobile&" +
                "response_type=token");
        mWebView.loadUrl(url.toString());

        return view;
    }

    private String makeUrl(String token, String id) {
        return ("https://api.vk.com/method/audio.get?uid=" + id + "&access_token=" + token);
    }

    class getJSON extends AsyncTask<String, JSONObject, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... audiourl) {
            Uri url = Uri.parse(audiourl[0]);

            Log.e("url", url.toString());

            HttpGet httpGet = new HttpGet(url.toString());
            HttpClient client = new DefaultHttpClient();
            try {
                HttpResponse res = client.execute(httpGet);
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(res.getEntity()
                                .getContent()));
                StringBuilder sb = new StringBuilder();
                String s;
                while ((s = r.readLine()) != null) {
                    sb.append(s);
                }
                //FragListView.res = new JSONObject(sb.toString());
                musicList = new JSONObject(sb.toString());

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return musicList;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            FragListView.res = jsonObject;
            listener.createdJSON(jsonObject);
            super.onPostExecute(jsonObject);
        }
    }

    public interface onFinishWork {
        public void createdJSON(JSONObject res);
    }
}

