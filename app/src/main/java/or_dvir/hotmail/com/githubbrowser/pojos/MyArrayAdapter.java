package or_dvir.hotmail.com.githubbrowser.pojos;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * a simple wrapper adapter, in which i can get all of its' items
 */
public class MyArrayAdapter<String> extends ArrayAdapter<String>
{
	private ArrayList<String> mItems;

	public MyArrayAdapter(Context context, int resource, ArrayList<String> items)
	{
		super(context, resource, items);

		mItems = items;
	}

	/**
	 *
	 */
	public ArrayList<String> getItems()
	{
		return mItems;
	}
}
