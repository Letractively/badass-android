package fr.slvn.badass.data;

import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BadassHandler {

	private static final String DATABASE_NAME	= "badass.db";
	private static final String DATABASE_TABLE	= "badass";
	private static final int DATABASE_VERSION	= 5;

	// The index (key) column name for use in where clauses.
	public static final String KEY_ID		= "_id";
	public static final int	ID_COLUMN		= 0;

	// The name and column index of each column in your database.
	public static final String KEY_NAME		= "_name"; 
	public static final int NAME_COLUMN		= 1;
	public static final String KEY_DATE		= "_date";
	public static final int	DATE_COLUMN		= 2;
	public static final String KEY_LINK		= "_link";
	public static final int LINK_COLUMN		= 3;
	public static final String KEY_FAVORITE	= "_favorite";
	public static final int FAVORITE_COLUMN	= 4;
	public static final String KEY_READ		= "_read";
	public static final int READ_COLUMN		= 5;
	public static final String KEY_CODE		= "_code";
	public static final int CODE_COLUMN		= 6;

	public static final String[] ALL_COLUMNS	= new String[] {KEY_ID, KEY_NAME, KEY_DATE, KEY_LINK, KEY_FAVORITE, KEY_READ, KEY_CODE};

	// SQL Statement to create a new database.
	private static final String DATABASE_CREATE = "CREATE TABLE " + 
	DATABASE_TABLE + " (" +
	KEY_ID		+ " integer primary key autoincrement, " +
	KEY_NAME	+ " text not null unique, " +
	KEY_DATE	+ " text not null, " +
	KEY_LINK	+ " text not null, " +
	KEY_FAVORITE+ " integer default 0, " +
	KEY_READ	+ " integer default 0," +
	KEY_CODE	+ " integer" +
	");";

	// Variable to hold the database instance
	private SQLiteDatabase db;
	// Context of the application using the database.
	private final Context context;
	// Database open/upgrade helper
	private myDbHelper dbHelper;

	public BadassHandler(Context _context) {
		context = _context;
		dbHelper = new myDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public BadassHandler open() throws SQLException {
		
		if (db == null || !db.isOpen())
			db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	public long insertEntry(BadassEntry mObject) {

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NAME,		mObject.mName);
		contentValues.put(KEY_DATE,		mObject.mDate);
		contentValues.put(KEY_LINK,		mObject.mLink);
		contentValues.put(KEY_CODE,		mObject.mCode);

		return db.insert(DATABASE_TABLE, null, contentValues);

	}

	public boolean fillDatabaseWith(List<BadassEntry> pEntries)
	{
		boolean value = Boolean.valueOf(false);

		for (int i = pEntries.size() -1 ; i >= 0 ; i--)
		{
			if (insertEntry(pEntries.get(i)) == -1)
			{
				break;
			}
		}

		return value;
	}

	public boolean removeEntry(long _rowIndex) {
		return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
	}

	public boolean removeAllEntries() {
		return db.delete(DATABASE_TABLE, null, null) > 0;
	}

	public Cursor getAllEntries () {
		return db.query(DATABASE_TABLE, ALL_COLUMNS, 
				null, null, null, null, KEY_ID + " DESC");
	}

	public boolean updateEntry(long _rowIndex, BadassEntry _myObject) {
		// TODO: Create a new ContentValues based on the new object
		// and use it to update a row in the database.
		return true;
	}

	public boolean setFavorite(int _id, int newState)
	{
		ContentValues values = new ContentValues(1);
		values.put(KEY_FAVORITE, newState);
		db.update(DATABASE_TABLE, values, KEY_ID + "=" + _id, null);
		return true;
	}

	public boolean setRead(int _id, int newState)
	{
		ContentValues values = new ContentValues(1);
		values.put(KEY_READ, newState);
		db.update(DATABASE_TABLE, values, KEY_ID + "=" + _id, null);
		return true;
	}

	private static class myDbHelper extends SQLiteOpenHelper {

		public myDbHelper(Context context, String name, 
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// Called when no database exists in disk and the helper class needs
		// to create a new one. 
		@Override
		public void onCreate(SQLiteDatabase _db) {
			Log.i("DB.TV","Creation ?");
			_db.execSQL(DATABASE_CREATE);
			Log.i("DB.TV","Creation OK");
		}

		// Called when there is a database version mismatch meaning that the version
		// of the database on disk needs to be upgraded to the current version.
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			// Log the version upgrade.
			Log.w("TaskDBAdapter", "Upgrading from version " + 
					_oldVersion + " to " +
					_newVersion + ", which will destroy all old data");

			// Upgrade the existing database to conform to the new version. Multiple 
			// previous versions can be handled by comparing _oldVersion and _newVersion
			// values.

			// The simplest case is to drop the old table and create a new one.
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			// Create a new one.
			onCreate(_db);
		}
	}		
}
