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
import java.util.Iterator;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;

public class StyledWebView extends WebView {

    private static final String TAG              = "AsSamsung";
    private static final String CSS_FILE         = "resultados.css";
    private static final int    INITIAL_ZOOM     = 120;             // En tanto por ciento
    private static final String DEFAULT_ENCODING = "ISO-8859-1";
    private Context             mContext;
    
    private String mCss;
    private String mUrl;

    public StyledWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setHorizontalScrollBarEnabled(false);
        this.getSettings().setJavaScriptEnabled(true);
        this.setInitialScale(INITIAL_ZOOM);
        this.getSettings().setBuiltInZoomControls(false);

        // Read custom attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StyledWebView);

        mCss = a.getString(R.styleable.StyledWebView_css);
        mUrl = a.getString(R.styleable.StyledWebView_url);

        Log.d(TAG, "URL: "+mUrl);
        Log.d(TAG, "CSS: "+mCss);
        a.recycle();
        
        loadUrl(mUrl);

    }

    /**
     * Descarga la web pasada como par‡metro y concatena estilos antes de mostrarla
     */
    @Override
    public void loadUrl(String url) {
    	setInitialScale(INITIAL_ZOOM);
    	
    	String page = WGet(url);
    	String css = buildCss();
    	page = injectCss(page, css);
    	page = page.replaceAll("\"", "\\\"");
    	
    	File cacheDir = mContext.getCacheDir();
        File styledUrl = new File(cacheDir, "url.html");

        FileOutputStream fos;
		try {
			fos = new FileOutputStream(styledUrl);
	        fos.write(page.toString().getBytes());
	        fos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        
    	
        //String composedUrl = "javascript:document.write(\"" +page + "\");";
        //Log.d(TAG, "loadUrl: "+composedUrl);
		
		String styledUrlPath = styledUrl.getAbsolutePath();
		Log.d(TAG, "loadUrl: "+styledUrlPath);
        super.loadUrl("file:///"+styledUrlPath);
    }

    private String injectCss(String page, String css) {
		int headEnd = page.indexOf("</head>");
		String prev = page.substring(0, headEnd);
		String post = page.substring(headEnd, page.length());
		String res = prev + css + post;
		return res;
	}

	/**
     * Captura todos los clicks sobre la webView y no hace nada con ellos. Evita zoom, navegaci—n,
     * etc.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
         return super.onTouchEvent(ev);
        //return true;
    }

    /**
     * Obtiene el contenido de una URL y lo devuelve como string
     * 
     * @param webUrl
     *            Url a descargar
     */
    private String WGet(String webUrl) {
        StringBuilder total = new StringBuilder();
        try {
            URL url = new URL(webUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            if (encoding == null) {
                encoding = DEFAULT_ENCODING;
            }

            BufferedReader r = new BufferedReader(new InputStreamReader(is, encoding));

            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Wget result: " + total.toString());
        return total.toString();
    }

    private String buildCss() {
        StringBuilder contents = new StringBuilder();

        InputStreamReader reader;
        try {
            reader = new InputStreamReader(mContext.getAssets().open(mCss), "UTF-8");
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

    public void clean() {
        super.loadUrl("javascript:document.getElementsByTagName('body')[0].innerHTML='';");

    }


}
