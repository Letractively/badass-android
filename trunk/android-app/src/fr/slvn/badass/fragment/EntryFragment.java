package fr.slvn.badass.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import fr.slvn.badass.tools.FileManager;

public class EntryFragment extends Fragment {
	
	// Common static attributes
	private static final String TAG = "EntryFragment - ";

	// extras names
	public static final String EXTRA_INDEX	= "extra_index";
	public static final String EXTRA_FILE	= "extra_file";
	public static final String EXTRA_NAME	= "extra_name";
	
    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static EntryFragment newInstance(int index, String fileName, String entryName) {
    	EntryFragment f = new EntryFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt(EXTRA_INDEX, index);
        args.putString(EXTRA_FILE, fileName);
        args.putString(EXTRA_NAME, entryName);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt(EXTRA_INDEX, 0);
    }
    
    public String getFile() {
    	return getArguments().getString(EXTRA_FILE);
    }
    
    public String getName() {
    	return getArguments().getString(EXTRA_NAME);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            // Currently in a layout without a container, so no
            // reason to create our view.
            return null;
        }
        
        String content = FileManager.INSTANCE.getCachedFile(getFile());
        if (content == null) {
        	// XXX The file is not cached !
        	return null;
        }

        TextView textView = new TextView(getActivity().getApplicationContext());
        textView.setText(Html.fromHtml(content,  new HtmlImageGetter(), null));
        
        ScrollView scroller = new ScrollView(getActivity());
        scroller.addView(textView);
        
        return scroller;
    }
    
    
	private class HtmlImageGetter implements Html.ImageGetter
	{
		public int min;
		
		public HtmlImageGetter() {
			super();
			min	= getImageWidth();
		}
		
		
		@Override
		public Drawable getDrawable(String source)
		{
			try
			{
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
	
	private int mWidth;
	private int mHeight;
	
	private int getImageWidth() {
		int result = 150;
		
		View view = getView();
		if (view != null) {
			mWidth	= view.getWidth();
			mHeight	= view.getHeight();
			if (mWidth != 0 && mHeight != 0) {
				return Math.min(mWidth, mHeight);
			}
		}
		return result;
	}
}
