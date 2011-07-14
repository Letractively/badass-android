package fr.slvn.badass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.slvn.badass.service.HtmlImageGetter;
import fr.slvn.badass.tools.FileManager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class BadassActivity extends Activity
{
	
	static private final String TAG = BadassActivity.class.getName();

	static public final String BADASS_NAME = "badass_name";
	static public final String BADASS_LINK = "badass_link";

	static private final String TITLE_PREFIX = "Badass : ";

	static private final int STATUS_OK			= 0x00;
	static private final int STATUS_NO_DATA		= 0x01;
	static private final int STATUS_PARSE_EX	= 0x02;
	static private final int STATUS_IO_EX		= 0x03;

	private TextView	textView;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
	    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.badass);

		String badassName =	getIntent().getExtras().getString(BADASS_NAME);
		String badassLink =	getIntent().getExtras().getString(BADASS_LINK);

		if (badassName != null)
			setTitle(TITLE_PREFIX + badassName);

		textView = (TextView)	findViewById(R.id.badass_textview);
		
		LoadTextView loader = new LoadTextView();
		loader.execute(badassLink);	
	}

	protected void onPostExecute(Integer result)
	{
		String message = null;
		switch(result)
		{
		case STATUS_OK:
			textView.setText(Html.fromHtml(null, new HtmlImageGetter(), null));
			return;
		case STATUS_NO_DATA:
			message = getString(R.string.status_no_data);
			break;
		case STATUS_PARSE_EX:
			message = getString(R.string.status_parse_ex);
			break;
		case STATUS_IO_EX:
			message = getString(R.string.status_io_ex);
			break;
		}
		Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT);
	}
}
