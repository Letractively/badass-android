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

		setContentView(R.layout.badass2);

		String badassName =	getIntent().getExtras().getString(BADASS_NAME);
		String badassLink =	getIntent().getExtras().getString(BADASS_LINK);

		if (badassName != null)
			setTitle(TITLE_PREFIX + badassName);

		textView = (TextView)	findViewById(R.id.badass_textview);
		
		LoadTextView loader = new LoadTextView();
		loader.execute(badassLink);	

	}

	private String urlStart		= "http://";
	private String urlDefault	= "http://www.badassoftheweek.com/";

	private String checkUrl(String pUrl)
	{
		if (pUrl.startsWith(urlStart))
			return pUrl;
		else 
			return urlDefault + pUrl;
	}


	private class LoadTextView extends AsyncTask<String, Integer, Integer>
	{

		private String content;

		protected void onPreExecute ()
		{
			setProgressBarIndeterminateVisibility(true);
		}
		
		protected Integer doInBackground(String... urls)
		{
			ArrayList<String> images = new ArrayList<String>();
			if(!FileManager.INSTANCE.isFileCached(urls[0]))
			{
				URL url;
				try {
					StringBuilder text = new StringBuilder();
					url = new URL(checkUrl(urls[0]));
	
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setDoOutput(true);
					connection.connect();
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	
					String line;
					boolean flag1 = false;
	
					while ((line = reader.readLine()) != null) {
						line = line.trim();
						if (line.contains("</center>"))
						{
							flag1 = true;
							break;
						}
					}
					
					
					if (flag1)
					{
						while ((line = reader.readLine()) != null)
						{
							if (line.contains("<br><center>"))
							{
								break;
							}
							if (line.startsWith("<img vspace="))
							{
								images.add(getImageName(line));
							}
							text.append(line);
							//line = null;
						}
						content = text.toString();
					}

					FileManager.INSTANCE.writeFile(urls[0], content);
					reader.close();
					connection.disconnect();
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else 
			{
				content = FileManager.INSTANCE.getCachedFile(urls[0]);
			}
			
			int totalProgress	= 5000;
			int totalImage	 	= images.size();
			int progress		= totalProgress/totalImage;
			publishProgress(totalProgress);
			
			for(int i = 0; i < totalImage; i++)
			{
				FileManager.INSTANCE.downloadFileFromInternet(images.get(i), checkUrl(images.get(i)));
				totalProgress += progress;
				publishProgress(totalProgress);
			}
			
			publishProgress(10000);
			return 0;
		}
		
	     protected void onProgressUpdate(Integer... progress)
	     {
	        setProgress(progress[0]);
	     }


		protected void onPostExecute(Integer result)
		{
			setProgressBarIndeterminateVisibility(false);
			String message = null;
			switch(result)
			{
			case STATUS_OK:
				textView.setText(Html.fromHtml(content, new HtmlImageGetter(), null));
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
		
		
		/* Get image file name */
		private String fullRegExp	= "<img.*\"(.*)\">";
		private int imgUrlGroup		= 1;
		private Pattern mPattern	= Pattern.compile(fullRegExp);
		
		private String getImageName(String line)
		{
			Matcher pMatcher = mPattern.matcher(line);
			while (pMatcher.find())
			{
				return pMatcher.group(imgUrlGroup);
			}
			return null;
		}
	}
	
	private class HtmlImageGetter implements Html.ImageGetter
	{

		@Override
		public Drawable getDrawable(String source)
		{
			try
			{
				Display display = getWindowManager().getDefaultDisplay();
				int min	= display.getHeight() < display.getWidth() ?
						display.getHeight() : display.getWidth();
				
				Bitmap bitmap = BitmapFactory.decodeFile(FileManager.INSTANCE.getFilePath(source));
				float scale =  (float) min /  (float) bitmap.getWidth() ;
				int x = (int) (scale * bitmap.getWidth());
				int y = (int) (scale * bitmap.getHeight());
				bitmap = Bitmap.createScaledBitmap(bitmap, x, y, true);
				Drawable d		= new BitmapDrawable(bitmap);
				d.setBounds(0, 0, x, y);
		
				return d;
			}catch (Exception e) {
				Log.e(TAG, " getDrawable(String) " + e.getMessage());
				return null;
			}
		}	
	}
}
