package or_dvir.hotmail.com.githubbrowser.pojos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import or_dvir.hotmail.com.githubbrowser.R;
import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.UserList;

/**
 * a custom adapter used to display a list of search results
 */
public class AdapterUserSearch extends BaseAdapter
{
	private static class viewHolder
	{
		TextView textView;

		ImageView imageView;
	}

	private UserList mUserList;

	private Context mContext;

	/**
	 * creates a new instance of this adapter
	 * @param context the context to use
	 * @param userList the list of users to display
	 */
	public AdapterUserSearch(Context context, UserList userList)
	{
		mContext = context;

		mUserList = userList;
	}

	/**
	 *
	 */
	@Override
	public int getCount()
	{
		return mUserList.size();
	}

	/**
	 *
	 */
	@Override
	public User getItem(int position)
	{
		return mUserList.get(position);
	}

	/**
	 *
	 */
	@Override
	public long getItemId(int position)
	{
		return mUserList.get(position).getId();
	}

	/**
	 *
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		viewHolder holder;

		if (convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(mContext);

			convertView = inflater.inflate(R.layout.list_item_user_search, null);

			holder = new viewHolder();

			holder.textView = (TextView) convertView.findViewById(R.id.textView_activitySearch_userName);

			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView_activitySearch_userImage);

			convertView.setTag(holder);
		}

		else
		{
			holder = (viewHolder) convertView.getTag();
		}

		initializeAllFields(holder);

		holder.textView.setText(mUserList.get(position).getLogin());

		Picasso.with(mContext)
			   .load(mUserList.get(position).getAvatar_url())
			   .error(R.drawable.background_login)
			   .resizeDimen(R.dimen.userSearch_widthAndHeight, R.dimen.userSearch_widthAndHeight)
			   .centerInside()
			   .into(holder.imageView);

		return convertView;
	}

	/**
	 *
	 */
	private void initializeAllFields(viewHolder holder)
	{
		holder.textView.setText("");

		holder.imageView.setImageDrawable(null);
	}
}
