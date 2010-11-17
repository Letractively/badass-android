package com.ensemblevide.badass;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class BadassActivity extends Activity {
	
	static public final String BADASS_NAME = "badass_name";
	static public final String BADASS_LINK = "badass_link";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.badass);
		
		//String badassName =	getIntent().getExtras().getString("BADASS_NAME");
		String badassLink =	getIntent().getExtras().getString(BADASS_LINK);
		
		Log.i("BADASS Intent", "=> " + badassLink);
		
		WebView webView = (WebView) findViewById(R.id.badass_webview);
		webView.loadUrl(checkUrl(badassLink));
	
	}
	
	private String urlStart		= "http://";
	private String urlDefault	= "http://www.badassoftheweek.com/";
	
	private String checkUrl(String pUrl) {
		if (pUrl.startsWith(urlStart))
			return pUrl;
		else 
			return urlDefault + pUrl;
	}
}
