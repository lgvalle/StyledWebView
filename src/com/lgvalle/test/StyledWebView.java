package com.lgvalle.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;

public class StyledWebView extends WebView {

    private static final String TAG              = "AsSamsung";
    public static String mDefaultEncoding = "UTF-8";
    private Context             mContext;
    private String mCss, mUrl;

    public StyledWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setHorizontalScrollBarEnabled(true);
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setBuiltInZoomControls(true);

        // Read custom attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StyledWebView);

        mCss = a.getString(R.styleable.StyledWebView_css);
        mUrl = a.getString(R.styleable.StyledWebView_url);

        a.recycle();
        
        loadUrl(mUrl);
    }
    

    /**
     * Complete delete everything in the webview.
     * Is needed to repaint 
     */
    public void clean() {
        super.loadUrl("javascript:document.getElementsByTagName('body')[0].innerHTML='';");

    }

    /**
     * Download url content, store it as a file and concat styles
     */
    @Override
    public void loadUrl(String url) {
    	String page = WGet(url);
    	String css = buildCss();
    	page = injectCss(page, css);
		super.loadDataWithBaseURL(url, page, null, mDefaultEncoding, null);
    }

    /**
     * Concatenates CSS rules to given page content
     * @return Concatenation result
     */
    private String injectCss(String page, String css) {
		int headEnd = page.indexOf("</head>");
		String res = "";
		if (headEnd > 0) {
			res = page.substring(0, headEnd) + css + page.substring(headEnd, page.length());	
		} else {
			res = "<head>" + css + "</head>" + page;
		}
		return res;
	}

    /**
     * Fetches url content and returns it as string
     */
    private String WGet(String webUrl) {
        StringBuilder total = new StringBuilder();
        try {
            URL url = new URL(webUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            if (encoding == null) {
                encoding = mDefaultEncoding;
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(is, encoding));
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return total.toString();
    }

    /**
     * Read CSS file from assets
     * @return String with the format '<style> xxxx </style>'
     */
    private String buildCss() {
        StringBuilder contents = new StringBuilder();

        InputStreamReader reader;
        try {
            reader = new InputStreamReader(mContext.getAssets().open(mCss), mDefaultEncoding);
            BufferedReader br = new BufferedReader(reader);

            String line;
            while ((line = br.readLine()) != null) {
                contents.append(line);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "<style>" + contents.toString().trim().replace("\n", "") + "</style>";

    }

    
    public void setDefaultEncoding(String encoding) {
    	mDefaultEncoding = encoding;
    }


}
