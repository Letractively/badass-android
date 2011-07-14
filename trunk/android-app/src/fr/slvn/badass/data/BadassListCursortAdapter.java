package fr.slvn.badass.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import fr.slvn.badass.R;

public class BadassListCursortAdapter extends SimpleCursorAdapter {


	LayoutInflater mInflater; 
	
	public BadassListCursortAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		mInflater = LayoutInflater.from(context); 
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.list_cell, parent, false);
		View newView		= v.findViewById(R.id.cell_new);
		View favoriteView	= v.findViewById(R.id.cell_star);
		
		if (c.getInt(BadassHandler.READ_COLUMN) > 0)
			newView.setVisibility(View.INVISIBLE);
		if (c.getInt(BadassHandler.FAVORITE_COLUMN) < 1)
			favoriteView.setVisibility(View.INVISIBLE);
		
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		TextView dateCell = (TextView) view.findViewById(R.id.cell_date);
		dateCell.setText(cursor.getString(BadassHandler.DATE_COLUMN));
		
		TextView nameCell = (TextView) view.findViewById(R.id.cell_name);
		nameCell.setText(cursor.getString(BadassHandler.NAME_COLUMN));
		
		View newView		= view.findViewById(R.id.cell_new);
		View favoriteView	= view.findViewById(R.id.cell_star);
		
		if (cursor.getInt(BadassHandler.READ_COLUMN) > 0)
			newView.setVisibility(View.INVISIBLE);
		else
			newView.setVisibility(View.VISIBLE);
		
		if (cursor.getInt(BadassHandler.FAVORITE_COLUMN) < 1)
			favoriteView.setVisibility(View.INVISIBLE);
		else 
			favoriteView.setVisibility(View.VISIBLE);
	} 
}
