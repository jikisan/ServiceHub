package com.example.servicehub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class privacy_and_policy_page extends AppCompatActivity {

    private TextView tv_back;
    private WebView webView;
    private String privacyHtml = "privacy_policy.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_and_policy_page);

        webView = findViewById(R.id.webView);
        tv_back = findViewById(R.id.tv_back);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/" + privacyHtml);

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}