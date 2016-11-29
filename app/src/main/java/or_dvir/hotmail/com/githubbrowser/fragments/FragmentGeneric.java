package or_dvir.hotmail.com.githubbrowser.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;
import or_dvir.hotmail.com.githubbrowser.R;
import or_dvir.hotmail.com.githubbrowser.activities.ActivityUserProfile;
import or_dvir.hotmail.com.githubbrowser.activities.MyActivity;
import or_dvir.hotmail.com.githubbrowser.pojos.AdapterIssues;
import or_dvir.hotmail.com.githubbrowser.pojos.MyArrayAdapter;
import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.IssuesList;
import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.RepositoriesList;
import or_dvir.hotmail.com.githubbrowser.pojos.Repository;
import or_dvir.hotmail.com.githubbrowser.pojos.User;
import or_dvir.hotmail.com.githubbrowser.pojos.Utils;
import or_dvir.hotmail.com.githubbrowser.pojos.eItemsType;
import or_dvir.hotmail.com.githubbrowser.activities.ActivityRepository;
import or_dvir.hotmail.com.githubbrowser.pojos.RoboRequest;

/**
 * a fragment to represent all types of lists in any tab of this app
 * (e.g. owned/starred repositories, and open/closed/all issues of a specific repository)
 * @param <T> the type of object this fragment represents (i.e. {@link Repository}, {@link or_dvir.hotmail.com.githubbrowser.pojos.Issue})
 */
@SuppressWarnings({"PointlessBooleanExpression", "FieldCanBeLocal"})
public class FragmentGeneric<T extends ArrayList> extends Fragment
{
	private static final String TAG = "FragmentGeneric";

	/**
	 * the list of items this fragment holds
	 */
	private T mItems;

	/**
	 * whether or not a request is currently running so we can display the progress bar
	 * if the fragment is killed before the request was finished (e.g. orientation change)
	 */
	private boolean mIsRequestRunning;

	/**
	 * the type of items this fragment displays
	 */
	private eItemsType mItemsType;

	private ListView mList;

	private ProgressBar mProgBar;

	private View mView;

	/**
	 * a "load more items" button in case there are
	 * multiple pages in the network requests' results
	 */
	private View mListFooterView;

	/**
	 * the next page to load in case there are
	 * multiple pages in the network requests' results
	 */
	private int mPageToLoad;

	/**
	 * the total number of pages of the network requests' results
	 */
	private int mNumPagesTotal;

	private FancyButton mbtnLoadMore;

	/**
	 * @param type the type of item this fragment represents
	 * @return a new instance of this fragment with the given item type
	 */
	public static FragmentGeneric newInstance(eItemsType type)
	{
		FragmentGeneric frag = new FragmentGeneric();

		Bundle args = new Bundle();

		args.putSerializable(Utils.Keys.ITEMS_TYPE, type);

		frag.setArguments(args);

		return frag;
	}

	/**
	 *
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (getArguments() != null)
		{
			mItemsType = (eItemsType) getArguments().getSerializable(Utils.Keys.ITEMS_TYPE);

			mIsRequestRunning = false;

			mPageToLoad = 1;
		}
	}

	/**
	 *
	 */
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(savedInstanceState != null)
		{
			//noinspection unchecked
			mItems = (T) savedInstanceState.getSerializable(Utils.Keys.LIST_ITEMS);

			mItemsType = (eItemsType) savedInstanceState.getSerializable(Utils.Keys.ITEMS_TYPE);

			mNumPagesTotal = savedInstanceState.getInt(Utils.Keys.NUM_PAGES_TOTAL, 1);

			mPageToLoad = savedInstanceState.getInt(Utils.Keys.PAGE_TO_LOAD);

			mIsRequestRunning = savedInstanceState.getBoolean(Utils.Keys.IS_REQUEST_RUNNING);
		}

		if(mIsRequestRunning == true)
		{
			mList.setEnabled(false);

			mProgBar.setVisibility(View.VISIBLE);
		}

		else
		{
			mList.setEnabled(true);

			mProgBar.setVisibility(View.GONE);
		}

		mView = inflater.inflate(R.layout.fragment_generic_list, container, false);

		mProgBar = (ProgressBar) mView.findViewById(R.id.progressBar_fragment_generic);

		mList = (ListView) mView.findViewById(R.id.listView_fragment_generic);

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				switch (mItemsType)
				{
					case REPOSITORY_OWNED:

						Utils.startActivityRepository(getActivity(), (Repository) mItems.get(position), false);

						break;

					case REPOSITORY_STARRED:

						Utils.startActivityRepository(getActivity(), (Repository) mItems.get(position), true);

						break;

					case ISSUES_ALL:
					case ISSUES_OPENED:
					case ISSUES_CLOSED:

						//do nothing - no need to handle click request

						break;

					default:

						throw new IllegalArgumentException("you forgot to add " + mItemsType + " to this switch statement");
				}
			}
		});

		mListFooterView = inflater.inflate(R.layout.list_footer, null, false);

		mbtnLoadMore = (FancyButton) mListFooterView.findViewById(R.id.button_list_loadMore);

		mbtnLoadMore.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				mList.setEnabled(false);

				mProgBar.setVisibility(View.VISIBLE);

				mbtnLoadMore.setVisibility(View.GONE);

				mPageToLoad++;

				getNextPage(false);
			}
		});

		mList.addFooterView(mListFooterView);

		return mView;
	}

	/**
	 *
	 */
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		getNextPage(true);
	}

	/**
	 * starts a network request to get the next page of the given item type
	 * (e.g. closed issues, starred repositories, etc.)
	 * @param firstTime whether this is a "fresh" request
	 *                  or if the user clicked "load more items" button
	 */
	private void getNextPage(final boolean firstTime)
	{
		MyActivity hostActivity = (MyActivity) getActivity();

		final SpiceManager spiceMan = hostActivity.getSpiceManager();

		//we are loading issues
		if(hostActivity instanceof ActivityRepository)
		{
			Repository repository = ((ActivityRepository)hostActivity).getRepository();

			String noDataText = "";

			switch (mItemsType)
			{
				case ISSUES_ALL:

					noDataText = getString(R.string.noData_issues, "");

					break;

				case ISSUES_OPENED:

					noDataText = getString(R.string.noData_issues, getString(R.string.open));

					break;

				case ISSUES_CLOSED:

					noDataText = getString(R.string.noData_issues, getString(R.string.closed));

					break;
			}

			//if there are no issues, no need to start a network request
			if(repository.isHas_issues() == false)
			{
				//all that matters here is the third argument
				finalizeSuccess(null, null, noDataText, true);
			}

			else
			{
				final String totalNumPagesCacheKey = Utils.CacheKeys.GET_ISSUES + repository.getId();

				mIsRequestRunning = true;

				final RoboRequest<IssuesList> reqGetIssues = new RoboRequest<>(repository.getIssues_url() + "&page=" + mPageToLoad,
																			   IssuesList.class,
																			   Utils.CacheKeys.GET_ISSUES + repository.getId() + mPageToLoad,
																			   HttpMethod.GET);

				final String noDataTextFinal = noDataText;

				spiceMan.execute(reqGetIssues,
								 reqGetIssues.getCacheKey(),
								 Utils.Constants.REQUESTS_CACHE_TIME_OUT,
								 new RequestListener<IssuesList>()
								 {
									 @Override
									 public void onRequestFailure(SpiceException e)
									 {
										 requestFailure(e, firstTime);
									 }

									 @Override
									 public void onRequestSuccess(IssuesList result)
									 {
										 mIsRequestRunning = false;

										 mProgBar.setVisibility(View.GONE);

										 mList.setEnabled(true);

										 IssuesList newIssues = null;

										 switch (mItemsType)
										 {
											 case ISSUES_ALL:

												 newIssues = result;

												 break;

											 case ISSUES_OPENED:

												 newIssues = result.getOpenedOrClosedIssues(true);

												 break;

											 case ISSUES_CLOSED:

												 newIssues = result.getOpenedOrClosedIssues(false);

												 break;
										 }

										 //this is a "fresh" search
										 if (firstTime == true)
										 {
											 HttpHeaders headers = reqGetIssues.getResponseHeaders();

											 if(headers != null)
											 {
												 initializeTotalNumOfPagesFromResponseHeader(spiceMan,
																							 totalNumPagesCacheKey,
																							 headers);
											 }

											 //this request has already been done before and
											 //we can get the data from cache
											 else
											 {
												 initializeTotalNumOfPagesFromCache(spiceMan, totalNumPagesCacheKey);
											 }

											 //the second arguments' content don't have meaning in this case.
											 //it only needs to NOT be null
											 //noinspection unchecked
											 finalizeSuccess((T) newIssues, new String[1], noDataTextFinal, true);
										 }

										 //the user clicked the "load more items" button
										 else
										 {
											 //at this point i know for a fact that we are looking
											 //at a list of issues. so mItems MUST be of type IssuesList.
											 //therefore there is no need to check the casting
											 //noinspection unchecked
											 mItems.addAll(result);

											 //we are getting issues,
											 //so we know the adapter is AdapterIssues
											 //noinspection unchecked
											 AdapterIssues adapter = (AdapterIssues) ((HeaderViewListAdapter) mList.getAdapter()).getWrappedAdapter();

											 adapter.getIssuesList()
													.addAll(newIssues);

											 adapter.notifyDataSetChanged();

											 showOrHideLoadMoreButton();
										 }
									 }
								 });
			}
		}

		//we are loading repositories
		else if(hostActivity instanceof ActivityUserProfile)
		{
			String url = "";
			String requestCacheKey = "";
			String totalNumPagesCacheKey = "";
			String noDataText = "";

			User user = ((ActivityUserProfile)hostActivity).getUser();

			if(mItemsType == eItemsType.REPOSITORY_OWNED)
			{
				url = user.getRepos_url() + "?page=" + mPageToLoad;

				requestCacheKey = Utils.CacheKeys.GET_OWNED_REPOSITORIES + user.getId() + mPageToLoad;

				totalNumPagesCacheKey = Utils.CacheKeys.GET_OWNED_REPOSITORIES + user.getId();

				noDataText = getString(R.string.noData_repositories, getString(R.string.doesntOwn));
			}

			else if (mItemsType == eItemsType.REPOSITORY_STARRED)
			{
				url = user.getStarred_url() + "?page=" + mPageToLoad;

				requestCacheKey = Utils.CacheKeys.GET_STARRED_REPOSITORIES + user.getId() + mPageToLoad;

				totalNumPagesCacheKey = Utils.CacheKeys.GET_STARRED_REPOSITORIES + user.getId();

				noDataText = getString(R.string.noData_repositories, getString(R.string.doesntHaveAnyStarred));
			}

			mIsRequestRunning = true;

			final RoboRequest<RepositoriesList> reqGetRepos = new RoboRequest<>(url,
																		  RepositoriesList.class,
																		  requestCacheKey,
																		  HttpMethod.GET);

			final String NoDataTextFinal = noDataText;

			final String totalNumPagesCacheKeyFinal = totalNumPagesCacheKey;

			spiceMan.execute(reqGetRepos,
							 reqGetRepos.getCacheKey(),
							 Utils.Constants.REQUESTS_CACHE_TIME_OUT,
							 new RequestListener<RepositoriesList>()
							 {
								 @Override
								 public void onRequestFailure(SpiceException e)
								 {
									 requestFailure(e, firstTime);
								 }

								 @Override
								 public void onRequestSuccess(RepositoriesList result)
								 {
									 mIsRequestRunning = false;

									 mProgBar.setVisibility(View.GONE);

									 mList.setEnabled(true);

									 String[] namesArray = result.getNamesAsArray();

									 //this is a "fresh" request
									 if(firstTime == true)
									 {
										 HttpHeaders headers = reqGetRepos.getResponseHeaders();

										 if(headers != null)
										 {
											 initializeTotalNumOfPagesFromResponseHeader(spiceMan,
																						 totalNumPagesCacheKeyFinal,
																						 headers);
										 }

										 //this request has already been done before and
										 //we can get the data from cache
										 else
										 {
											 initializeTotalNumOfPagesFromCache(spiceMan, totalNumPagesCacheKeyFinal);
										 }

										 //noinspection unchecked
										 finalizeSuccess((T) result, namesArray, NoDataTextFinal, false);
									 }

									 //the user clicked the "load more items" button
									 else
									 {
										 //at this point i know for a fact that we are looking
										 //at a list of repositories. so mItems MUST be of type RepositoriesList.
										 //therefore there is no need to check the casting
										 //noinspection unchecked
										 mItems.addAll(result);

										 //we are getting repositories,
										 //so we know the adapter is just an MyArrayAdapter<String>
										 //noinspection unchecked
										 MyArrayAdapter<String> adapter = (MyArrayAdapter<String>) ((HeaderViewListAdapter)mList.getAdapter()).getWrappedAdapter();

										 adapter.getItems()
												.addAll(Arrays.asList(namesArray));

										 adapter.notifyDataSetChanged();

										 showOrHideLoadMoreButton();
									 }
								 }
							 });
		}
	}

	/**
	 * @param spiceMan the {@link SpiceManager} of the host activity
	 * @param numPagesCacheKey the cache key to use when searching the cache
	 */
	private void initializeTotalNumOfPagesFromCache(SpiceManager spiceMan, String numPagesCacheKey)
	{
		spiceMan.getFromCache(Integer.class,
							  numPagesCacheKey,
							  Utils.Constants.REQUESTS_CACHE_TIME_OUT,
							  new RequestListener<Integer>()
							  {
								  @Override
								  public void onRequestFailure(SpiceException spiceException)
								  {
									  mNumPagesTotal = 1;
								  }

								  @Override
								  public void onRequestSuccess(Integer num)
								  {
									  //no data was found in cache for some reason
									  if(num == null)
									  {
										  mNumPagesTotal = 1;
									  }

									  else
									  {
										  mNumPagesTotal = num;

										  showOrHideLoadMoreButton();
									  }
								  }
							  });
	}

	/**
	 * @param spiceMan the {@link SpiceManager} of the host activity
	 * @param numPagesCacheKey the cache key to use when inserting the
	 *                         new value (number of pages) into the cache
	 * @param headers the response headers of the request
	 */
	private void initializeTotalNumOfPagesFromResponseHeader(SpiceManager spiceMan,
															 String numPagesCacheKey,
															 @NonNull HttpHeaders headers)
	{
		List<String> list = headers.get("Link");

		if(list != null)
		{
			String link = list.get(0);

			int lastIndex = link.lastIndexOf(";");

			mNumPagesTotal = Integer.parseInt(link.substring(lastIndex - 2, lastIndex - 1));

			spiceMan.putInCache(Integer.class,
								numPagesCacheKey,
								mNumPagesTotal);
		}

		//if there is no header names "Link"
		//then there is only 1 page
		else
		{
			mNumPagesTotal = 1;
		}

		showOrHideLoadMoreButton();
	}

	/**
	 *
	 */
	private void showOrHideLoadMoreButton()
	{
		if (mPageToLoad < mNumPagesTotal)
		{
			mbtnLoadMore.setVisibility(View.VISIBLE);
		}

		else
		{
			mbtnLoadMore.setVisibility(View.GONE);
		}
	}

	/**
	 * performs final stages of a successful request
	 * @param result the result fo the request
	 * @param namesArray an array of the names of issues/repositories to display in the list
	 * @param noDataText the text to display if no data was found
	 * @param useIssuesAdapter whether or not we need to use the custom {@link AdapterIssues}
	 */
	private void finalizeSuccess(T result, String[] namesArray, String noDataText, boolean useIssuesAdapter)
	{
		mProgBar.setVisibility(View.GONE);

		if(result != null)
		{
			mItems = result;
		}

		else
		{
			//if result is null, and this instance is showing IssuesList,
			//then make namesArray null so we show noDataText.
			//e.g. a repository does not have any open/closed issues
			if(useIssuesAdapter == true)
			{
				namesArray = null;
			}
		}

		TextView noData = (TextView) mView.findViewById(R.id.textView_fragment_generic_noData);

		//no data to display
		if(namesArray == null)
		{
			noData.setText(noDataText);

			noData.setVisibility(View.VISIBLE);
		}

		else
		{
			if(useIssuesAdapter == true)
			{
				AdapterIssues adapter = new AdapterIssues(getActivity(), (IssuesList) result);

				mList.setAdapter(adapter);
			}

			else
			{
				MyArrayAdapter<String> adapter = new MyArrayAdapter<>(getActivity(),
																	  android.R.layout.simple_list_item_1,
																	  new ArrayList<>(Arrays.asList(namesArray)));

				mList.setAdapter(adapter);
			}
		}
	}

	/**
	 * handles a case where a network request has failed
	 * @param e the exception that occurred
	 * @param firstTime whether or not this is a "Fresh" request or
	 *                  the user clicked "load more items button".<br><br/>
	 *                  if this is a "fresh" request, a generic text will appear over the (empty) list.
	 *                  if the user clicked "load more items", a toast will be shown
	 */
	private void requestFailure(SpiceException e, boolean firstTime)
	{
		mIsRequestRunning = false;

		Log.e(TAG, "SpiceException", e);

		mList.setEnabled(true);

		mProgBar.setVisibility(View.GONE);

		if(firstTime == true)
		{
			if(mbtnLoadMore != null)
			{
				mbtnLoadMore.setVisibility(View.GONE);
			}

			TextView failed = (TextView) mView.findViewById(R.id.textView_fragment_generic_failed);

			failed.setVisibility(View.VISIBLE);
		}

		else
		{
			showOrHideLoadMoreButton();

			Toast.makeText(getActivity(), R.string.failedToRetrieveData, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 *
	 */
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putSerializable(Utils.Keys.LIST_ITEMS, mItems);

		outState.putSerializable(Utils.Keys.ITEMS_TYPE, mItemsType);

		outState.putInt(Utils.Keys.PAGE_TO_LOAD, mPageToLoad);

		outState.putInt(Utils.Keys.NUM_PAGES_TOTAL, mNumPagesTotal);

		outState.putBoolean(Utils.Keys.IS_REQUEST_RUNNING, mIsRequestRunning);

		super.onSaveInstanceState(outState);
	}
}
