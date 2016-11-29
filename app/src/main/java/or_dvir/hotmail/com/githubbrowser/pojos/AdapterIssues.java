package or_dvir.hotmail.com.githubbrowser.pojos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import or_dvir.hotmail.com.githubbrowser.R;
import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.IssuesList;

/**
 * a custom adapter used to display a list of repository issues
 */
public class AdapterIssues extends BaseAdapter
{
	private IssuesList mIssuesList;

	private static class viewHolder
	{
		TextView textViewName;

		TextView textViewDate;
	}

	private Context mContext;

	/**
	 * creates a new instance of this adapter
	 * @param context the context to be used
	 * @param issuesList the list of issues to display
	 */
	public AdapterIssues(Context context, IssuesList issuesList)
	{
		mContext = context;

		mIssuesList = issuesList;
	}

	/**
	 *
	 */
	@Override
	public int getCount()
	{
		return mIssuesList.size();
	}

	/**
	 *
	 */
	@Override
	public Issue getItem(int position)
	{
		return mIssuesList.get(position);
	}

	/**
	 *
	 */
	@Override
	public long getItemId(int position)
	{
		return mIssuesList.get(position).getId();
	}

	/**
	 *
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		viewHolder holder;

		if (convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(mContext);

			convertView = inflater.inflate(R.layout.list_item_issue, null);

			holder = new viewHolder();

			holder.textViewName = (TextView) convertView.findViewById(R.id.textView_issueName);

			holder.textViewDate = (TextView) convertView.findViewById(R.id.textView_issueDate);

			convertView.setTag(holder);
		}

		else
		{
			holder = (viewHolder) convertView.getTag();
		}

		initializeAllFields(holder);

		holder.textViewName.setText(mIssuesList.get(position).getTitle());

		holder.textViewDate.setText(mIssuesList.get(position).getCreated_at());

		return convertView;
	}

	/**
	 *
	 */
	private void initializeAllFields(viewHolder holder)
	{
		holder.textViewName.setText("");

		holder.textViewDate.setText("");
	}

	/**
	 *
	 */
	public IssuesList getIssuesList()
	{
		return mIssuesList;
	}
}
