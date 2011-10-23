package fr.slvn.badass.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import fr.slvn.badass.R;
import fr.slvn.badass.activity.EntryActivity;
import fr.slvn.badass.data.BadassHandler;
import fr.slvn.badass.data.BadassListCursortAdapter;
import fr.slvn.badass.service.DataService;

public class EntryListFragment extends ListFragment {
	
	// Static values
	private static final String CURRENT_CHOICE	= "current_choice";
	
	// Attributes
	private BadassHandler				mDb;
	private Cursor						mCursor;
	private BadassListCursortAdapter	mAdapter;
	private boolean						mDualPane;
	private int							mCurCheckPosition = 0;


    @Override
    public void onActivityCreated(Bundle savedState) {
         super.onActivityCreated(savedState);
       
        Context mContext = getContext();
        
        if (mContext != null)
        {
	        // Populate list with our static array of titles.
			mDb = new BadassHandler(mContext).open();
	
		    mCursor = mDb.getAllEntries();
		    
		    if (mCursor.getCount() < 1) {
		    	launchListRefresh();
		    }
	
		    // the desired columns to be bound
		    String[] columns = new String[] { BadassHandler.KEY_DATE ,BadassHandler.KEY_NAME};
		    // the XML defined views which the data will be bound to
		    int[] to = new int[] { R.id.cell_date, R.id.cell_name};	
		    mAdapter = new BadassListCursortAdapter(mContext, R.layout.list_cell, mCursor, columns, to);
		    setListAdapter(mAdapter);
        }
	    
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.entry_details);
        mDualPane = detailsFrame != null
                && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedState != null) {
            mCurCheckPosition = savedState.getInt(CURRENT_CHOICE, 0);
        }

        if (mDualPane) {
            // In dual-pane mode, list view highlights selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            if (mCurCheckPosition < 0) {
            	// XXX SplashScreen ?
            } else {
            	showDetails(mCurCheckPosition);
            }
        }
    }
    
    private Context getContext()
    {
    	Context context = null;
    	Activity activity = getActivity();
    	if (activity != null)
    		context = activity.getApplicationContext();
    	return context;
    }
    
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_CHOICE, mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        showDetails(pos);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        mCurCheckPosition = index;
    	Cursor badassCursor = (Cursor) mAdapter.getItem(index);
    	String link = badassCursor.getString(BadassHandler.LINK_COLUMN);
    	String name = badassCursor.getString(BadassHandler.NAME_COLUMN);
    	
        if (mDualPane) {
            // We can display everything in-place with fragments.
            // Have the list highlight this item and show the data.
            getListView().setItemChecked(index, true);

            // Check what fragment is shown, replace if needed.
            EntryFragment details = (EntryFragment)  getFragmentManager().findFragmentById(R.id.entry_details);
            if (details == null || details.getShownIndex() != index) {

                // Make new fragment to show this selection.
                details = EntryFragment.newInstance(index, link, name);

                // Execute a transaction, replacing any existing
                // fragment with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.entry_details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), EntryActivity.class);
            intent.putExtra(EntryFragment.EXTRA_INDEX, index);
            intent.putExtra(EntryFragment.EXTRA_FILE, link);
            intent.putExtra(EntryFragment.EXTRA_NAME, name);
            startActivity(intent);
        }
    }
    
    private void launchListRefresh()
    {
    	Context context = getContext();
    	if (context != null) {
    		Intent intent = new Intent(context, DataService.class);
    		intent.setAction(DataService.ACTION_REFRESH_LIST);
    		sendBroadcast(intent);
    	}
    }
    
    private boolean sendBroadcast(Intent intent)
    {
    	Activity activity = getActivity();
    	if (activity != null) {
    		activity.sendBroadcast(intent);
    		return true;
    	}
    	return false;
    }

}
