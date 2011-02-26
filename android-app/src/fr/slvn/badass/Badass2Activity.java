package fr.slvn.badass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.xml.sax.XMLReader;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class Badass2Activity extends Activity {
	
	static private String TAG = Badass2Activity.class.getName();

	static public final String BADASS_NAME = "badass_name";
	static public final String BADASS_LINK = "badass_link";

	static private final String TITLE_PREFIX = "Badass : ";

	static private final int STATUS_OK			= 0x00;
	static private final int STATUS_NO_DATA		= 0x01;
	static private final int STATUS_PARSE_EX	= 0x02;
	static private final int STATUS_IO_EX		= 0x03;

	private TextView	textView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.badass2);

		String badassName =	getIntent().getExtras().getString(BADASS_NAME);
		String badassLink =	getIntent().getExtras().getString(BADASS_LINK);

		if (badassName != null)
			setTitle(TITLE_PREFIX + badassName);

		textView = (TextView)	findViewById(R.id.badass_textview);
		
		LoadTextView loader = new LoadTextView();
		loader.execute(checkUrl(badassLink));

	}


	private String urlStart		= "http://";
	private String urlDefault	= "http://www.badassoftheweek.com/";

	private String checkUrl(String pUrl) {
		if (pUrl.startsWith(urlStart))
			return pUrl;
		else 
			return urlDefault + pUrl;
	}


	private class LoadTextView extends AsyncTask<String, Integer, Integer> {

		private StringBuilder text = new StringBuilder();

		protected Integer doInBackground(String... urls) {


			URL url;
			try {
				url = new URL(urls[0]);

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("GET");

				connection.setDoOutput(true);

				connection.connect();
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				String line;
				boolean flag1 = false;

				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (!flag1 && line.contains("</center>"))
						flag1 = true;
					if (flag1 && line.contains("<br><center>"))
						break;
					if (flag1) {
						text.append(line);
					}
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}

		protected void onPostExecute(Integer result) {
			String message = null;
			switch(result)
			{
			case STATUS_OK:
				textView.setText(Html.fromHtml(text.toString(), new HtmlImageGetter(), null));
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
	
	private class HtmlImageGetter implements Html.ImageGetter {

		@Override
		public Drawable getDrawable(String source) {
			try
			{
				InputStream is = (InputStream) new URL(checkUrl(source)).getContent();
				Drawable d = Drawable.createFromStream(is, "src name");
				return d;
			}catch (Exception e) {
				System.out.println("Exc="+e);
				return null;
			}
		}
		
	}
}
