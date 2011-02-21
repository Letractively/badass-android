package fr.slvn.badass.tools;

import android.content.Context;
import android.database.Cursor;
import android.provider.Contacts.People;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import fr.slvn.badass.R;

public class BadassListCursortAdapter extends SimpleCursorAdapter {

	public BadassListCursortAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
	}
	
	public void bindView(View view, Context context, Cursor cursor) {
		
		int read = cursor.getInt(BadassHandler.READ_COLUMN);
		
		
	}
}
