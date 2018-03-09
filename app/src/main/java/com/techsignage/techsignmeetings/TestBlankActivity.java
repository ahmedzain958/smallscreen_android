package com.techsignage.techsignmeetings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class TestBlankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_blank);


        final WebView webview = (WebView)findViewById(R.id.webview_main);
        webview.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

                super.onReceivedError(view, request, error);
            }
        });
        webview.getSettings().setJavaScriptEnabled(true);

        Button btn1 = (Button)findViewById(R.id.btn1);
        final TextView txt1 = (TextView)findViewById(R.id.txt1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webview.loadUrl(String.format("http://192.168.1.8:782/main.html?poi=%s", txt1.getText().toString()));
            }
        });
    }
}
