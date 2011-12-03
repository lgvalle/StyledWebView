package com.lgvalle.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class TestWebView_ASActivity extends Activity {
    private static final String   BASE_URL       = "http://www.cadenaser.com/modulo/index.html?modulo=DEPCarruselOU&params=";
    protected static final String TAG            = "AsSamsung";
    private StyledWebView         webview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        webview = (StyledWebView) findViewById(R.id.webview);
    }

    public void onClickButtons(View v) {
        String newUrl = null;
        switch (v.getId()) {
            case R.id.button1:
                newUrl = BASE_URL + "deporte=ftb&com=411&contenedor=res";
                break;
            case R.id.button2:
                newUrl = BASE_URL + "deporte=bal&com=4&contenedor=res";
                break;

            case R.id.button3:
                newUrl = BASE_URL + "deporte=mas&com=1&contenedor=res";
                break;
            default:
                break;
        }
        Log.d(TAG, "onclickbuttons: new url:" + newUrl);
        //webview.clean();
        //webview.loadUrl(newUrl);
    }
}