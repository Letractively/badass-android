package com.ensemblevide.badass;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class BadassActivity extends Activity {

	static public final String BADASS_NAME = "badass_name";
	static public final String BADASS_LINK = "badass_link";

	static public final String TITLE_PREFIX = "Badass : ";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		
		setContentView(R.layout.badass);

		String badassName =	getIntent().getExtras().getString(BADASS_NAME);
		String badassLink =	getIntent().getExtras().getString(BADASS_LINK);
		
		if (badassName != null)
			setTitle(TITLE_PREFIX + badassName);
		
		WebView webview = (WebView) findViewById(R.id.badass_webview);
		
		// Should i activate Google ads ? Later...
		//webview.getSettings().setJavaScriptEnabled(true);

		final Activity activity = this;
		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				activity.setProgress(progress * 100);
			}
		});
		webview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
			}
		});



		webview.loadUrl(checkUrl(badassLink));

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
