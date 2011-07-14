package fr.slvn.badass.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;

public enum FileManager {

	INSTANCE;

	private boolean mExternalStorageAvailable = false;
	private boolean mExternalStorageWriteable = false;
	
	private String mExternalDir		= "";
	private String mDirectory		= "/Android/data/fr.slvn.badass/files/";
	private String mCacheDirectory	= "cache/";
	private String mNoMediaFile		= ".nomedia";


	public void init() {

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		mExternalDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		initDir();
	}
	
	public boolean canRead() {
		return mExternalStorageAvailable;
	}
	
	public boolean canWrite() {
		return mExternalStorageWriteable;
	}
	
	public boolean isFileCached(String fileName)
	{
		File f = new File(getFilePath(fileName));
		return f.exists();
	}
	
	public String getFilePath(String fileName)
	{
		return mExternalDir + mDirectory + mCacheDirectory + fileName;
	}
	
	public boolean downloadFileFromInternet(String fileName, String urlPath)
	{
		File f = new File(getFilePath(fileName));
		if (f.exists())
		{
			return true;
		} else {
			try
			{
				URL url = new URL(urlPath);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(true); 
				urlConnection.connect();
				f.createNewFile();
				FileOutputStream fileOutput = new FileOutputStream(f);
				InputStream is	= urlConnection.getInputStream();

				byte[] buffer = new byte[1024];
				int bufferLength = 0;
				while ( (bufferLength = is.read(buffer)) > 0 )
				{
					fileOutput.write(buffer, 0, bufferLength);
				}
				fileOutput.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean writeFile(String fileName, String content)
	{
		try 
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(getFilePath(fileName)));
			out.write(content);
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String getCachedFile(String fileName)
	{
		if (isFileCached(fileName))
		{
		    StringBuilder contents = new StringBuilder();
		    BufferedReader input = null;
		    try {
		      input =  new BufferedReader(new FileReader(getFilePath(fileName)));
		        String line = null;
		        while (( line = input.readLine()) != null){
		          contents.append(line);
		        }
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }
		    if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    return contents.toString();
		}
		return null;
	}
	
	public Writer getFileWriterBuffered(String fileName)
	{
		FileWriter fstream;
		try {
			fstream = new FileWriter(getFilePath(fileName));
			BufferedWriter out = new BufferedWriter(fstream);
			return out;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void initDir() {
		if (mExternalStorageWriteable) {
			File f =  new File(mExternalDir + mDirectory + mCacheDirectory + mNoMediaFile);
			if (!f.exists()) {
				try {
					f.getParentFile().mkdirs();
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}