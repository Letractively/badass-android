package fr.slvn.badass;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class AboutActivity extends Activity implements OnClickListener {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.about);
		setTitle(getString(R.string.main_menu_about));
		
		findViewById(R.id.about_book1).setOnClickListener(this);
		findViewById(R.id.about_book2).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()) {
		case R.id.about_book1:
			launchBrowsing(getString(R.string.url_badass_book));
			break;
		case R.id.about_book2:
			launchBrowsing(getString(R.string.url_badass_legend));
			break;
		}
				
	}
	
	private void launchBrowsing(String url) {
		Uri uriUrl = Uri.parse(url);
		startActivity(new Intent(Intent.ACTION_VIEW, uriUrl)); 
	}
}
