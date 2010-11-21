package com.ensemblevide.badass;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {
	
	public static final String PARSING_URL	= "http://www.badassoftheweek.com/list.html";
	
	private SimpleCursorAdapter mAdapter;
	private Cursor				mCursor;
	private BadassHandler		mDb;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.main);
		registerForContextMenu(getListView());

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				launchBadassActivity(position);
			}
		});

		mDb = new BadassHandler(this).open();

		UpdateTask updateTask = new UpdateTask();
		updateTask.execute(PARSING_URL);

	    mCursor = mDb.getAllEntries();
	    startManagingCursor(mCursor);
	    
	    // the desired columns to be bound
	    String[] columns = new String[] { BadassHandler.KEY_DATE ,BadassHandler.KEY_NAME, BadassHandler.KEY_LINK };
	    // the XML defined views which the data will be bound to
	    int[] to = new int[] { R.id.cell_date, R.id.cell_name, R.id.cell_link};
   

	    mAdapter = new SimpleCursorAdapter(this,R.layout.list_cell, mCursor, columns, to);
	    setListAdapter(mAdapter);
	}
	
	
	private void launchBadassActivity(int position) {
		Cursor badassCursor = (Cursor) mAdapter.getItem(position);
		
		Intent i = new Intent(MainActivity.this, BadassActivity.class);
		 
		Bundle objetbunble = new Bundle();
		//objetbunble.putString(BadassActivity.BADASS_NAME,badassCursor.getString(BadassHandler.NAME_COLUMN));
		objetbunble.putString(BadassActivity.BADASS_LINK,badassCursor.getString(BadassHandler.LINK_COLUMN));
		i.putExtras(objetbunble);
		 
		startActivity(i);
	}
	

	protected void  onDestroy() {
		if (mCursor != null)
			mCursor.close();
		if (mDb != null)
			mDb.close();
		super.onDestroy();
	}
	
	public static final int MENU_READ_ID		= 1;
	public static final int MENU_FAVORITE_ID	= 2;

	public void  onCreateContextMenu  (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		/*
		AdapterView.AdapterContextMenuInfo info;
		try {
		    info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
		    return;
		}
		long id = getListAdapter().getItemId(info.position);
		*/
		menu.add(0, MENU_READ_ID,		0, R.string.menu_read_label);
		menu.add(0, MENU_FAVORITE_ID,	1, R.string.menu_favorite_label);

	}

	public boolean  onContextItemSelected  (MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
		    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
		    return false;
		}
		launchBadassActivity(info.position);
		
		return true;
	}
	
	
	private class UpdateTask extends AsyncTask<String, Integer, Integer> {
		
		@Override 
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
		}
		
		@Override
		protected Integer doInBackground(String... arg0) {
			
			//mDb.removeAllEntries();
			List<BadassEntry> entries = new BadassParser(arg0[0]).parse();
			mDb.fillDatabaseWith(entries);
			
			return entries.size();
		}
		
		@Override 
	    protected void onProgressUpdate(Integer... progress) {
	    }
		
	    @Override 
	    protected void onPostExecute(Integer result) {
	    	setProgressBarIndeterminateVisibility(false);
	    	mCursor.requery();
	    }
	}
}