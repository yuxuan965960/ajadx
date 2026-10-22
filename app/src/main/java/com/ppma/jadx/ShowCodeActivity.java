package com.ppma.jadx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.ppma.filemanager.R;
import java.util.ArrayList;
import thereisnospon.codeview.CodeView;
import thereisnospon.codeview.CodeViewTheme;

public class ShowCodeActivity extends AppCompatActivity {

    CodeView codeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_code);
        ArrayList<String> str = getIntent().getStringArrayListExtra("fileName");
        if (str == null || str.size() < 2) {
            Toast.makeText(this, "没有可显示的代码", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setTitle(str.get(0));
        codeView = findViewById(R.id.codeview);
        codeView.setTheme(CodeViewTheme.IDEA);

        WebSettings webSettings = codeView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        // 不要按屏幕宽度缩放整页，否则长代码行会被压扁/裁掉
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        codeView.setHorizontalScrollBarEnabled(true);
        codeView.setVerticalScrollBarEnabled(true);
        codeView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        // highlight.js 的主题里 pre 没有横向滚动，超过屏宽的行会被直接裁掉（显示不全）。
        // 页面加载完后补一段 CSS：保持原始缩进、不折行、允许 pre 内部横向滚动。
        codeView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                try {
                    view.evaluateJavascript(
                            "(function(){try{"
                                    + "var s=document.createElement('style');"
                                    + "s.innerHTML='pre{white-space:pre!important;word-wrap:normal!important;"
                                    + "overflow-x:auto!important;-webkit-overflow-scrolling:touch!important;}"
                                    + "code{white-space:pre!important;word-wrap:normal!important;}"
                                    + "body{margin:0!important;overflow-x:auto!important;}';"
                                    + "document.head.appendChild(s);}catch(e){}})();",
                            null);
                } catch (Throwable ignore) {
                }
            }
        });

        try {
            String code = str.get(1);
            codeView.showCode(code == null ? "" : code);
        } catch (Throwable t) {
            Toast.makeText(this, "代码加载失败：" + t, Toast.LENGTH_LONG).show();
        }
    }
}