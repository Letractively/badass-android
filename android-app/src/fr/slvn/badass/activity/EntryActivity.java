package fr.slvn.badass.activity;

import fr.slvn.badass.R;
import fr.slvn.badass.fragment.EntryFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class EntryActivity extends FragmentActivity {

	private int mIndex = -1;
	private String mName;
	private String mFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIndex	= savedInstanceState.getInt(EntryFragment.EXTRA_INDEX, mIndex);
		mName	= savedInstanceState.getString(EntryFragment.EXTRA_NAME);
		mFile	= savedInstanceState.getString(EntryFragment.EXTRA_FILE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.entry);
	}
}
