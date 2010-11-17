package com.ensemblevide.badass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class BadassParser {
	
	private static int DATE_GROUP		= 1;
	private static int LINK_GROUP		= 2;
	private static int NAME_GROUP		= 3;

	String mDate;
	String mLink;
	String mName;
	
	String mUrl;

	List<BadassEntry> mBadassEntries;

	public BadassParser(String pUrl) {
		this.mUrl = pUrl;
	}

	public List<BadassEntry> parse() {

		mBadassEntries = new ArrayList<BadassEntry>();

		try {
			URL url = new URL(mUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line;
			boolean flag1 = false;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!flag1 && line.contains("<font size=+1>"))
					flag1 = true;
				if (flag1 && line.contains("<br><center>"))
					break;
				if (flag1) {
					if (line.contains("href")) {
						parseBadass(line);	
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mBadassEntries;
	}

	//private String fullRegExp  = "<li class=\"ligneProg\".*>.*<div class=\"heureProg .*>(.*).*(<img.*)?<\\/div>.*<div class=\"contenuProg.*<h3 class=\"couleur(.*)\">(.*)<\\/h3>.*<div (TITLE=\"header=\\[(.*)\\].*body=\\[(.*)\\].*\".*)?>(.*)<br.*\\/>(.*)<br.*\\/>(.*)<br.*\\/>";
	private String fullRegExp  = "([0-9\\/]*):&nbsp;\\s<a\\shref=\"([^\"]*)\">([^<]*)<\\/a>";
	private Pattern mPattern;
	
	private void parseBadass(String string) {
		Log.i("BADASS",string);
		mPattern = Pattern.compile(fullRegExp);
		BadassEntry entry = parser(mPattern, mPattern.matcher(string));
		if (entry != null) {
			//Log.i("BADASS", entry.toString());
			mBadassEntries.add(entry);
		}
	}

	private BadassEntry parser(Pattern pPattern, Matcher pMatcher) {
		Integer code;
		while (pMatcher.find()) {
			code = Integer.parseInt(pMatcher.group(DATE_GROUP).replaceAll("/", ""));
			BadassEntry.Builder builder = new BadassEntry.Builder(0, pMatcher.group(NAME_GROUP), pMatcher.group(DATE_GROUP), pMatcher.group(LINK_GROUP), code);
			return builder.build();
		}
		return null;
	}
}

