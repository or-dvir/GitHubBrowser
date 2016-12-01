package or_dvir.hotmail.com.githubbrowser.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;
import or_dvir.hotmail.com.githubbrowser.R;
import or_dvir.hotmail.com.githubbrowser.pojos.AdapterUserSearch;
import or_dvir.hotmail.com.githubbrowser.pojos.User;
import or_dvir.hotmail.com.githubbrowser.pojos.Utils;
import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.SearchUserResults;
import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.UserList;
import or_dvir.hotmail.com.githubbrowser.pojos.RoboRequest;

/**
 * an activity which performs a search.
 * this activity will be used either when the user explicitly asks
 * to search for other users, or if he clicked on a button which would display a list of users
 * (e.g. the "followers" or "following" buttons)
 */
@SuppressWarnings({"PointlessBooleanExpression", "FieldCanBeLocal"})
public class ActivitySearch extends MyActivity
{
	private static final String TAG = "ActivitySearch";

	private TextView mtvInfo;

	private ProgressBar mProgBar;

	private ListView mListView;

	private AdapterUserSearch mAdapter;

	/**
	 * a list of all (or part of) the users to be displayed
	 */
	private UserList mUserList;

	private SearchView mSearchView;

	private SpiceManager mSpiceMan;

	/**
	 * whether we are performing a "regular" search (e.g. the user is actively searching)
	 * or we are simply showing a list of other users (e.g. "followers" button was clicked)
	 */
	private boolean mIsRegularSearch;

	/**
	 * whether or not a request is currently running so we can display the progress bar
	 * if the activity is killed before the request was finished (e.g. orientation change)
	 */
	private boolean mIsRequestRunning;

	/**
	 * the query the user is searching for
	 */
	private String mQuery;

	/**
	 * a "load more" button to be placed at the bottom of the list
	 * if the search results hold more than 1 page
	 */
	private View mListFooterView;

	/**
	 * the next page to be loaded from the search results
	 */
	private int mPageToLoad;

	/**
	 * the total number of pages this search results holds
	 */
	private int mNumPagesTotal;

	private FancyButton mbtnLoadMore;

	/**
	 *
	 */
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_search);

		if(savedInstanceState != null)
		{
			mPageToLoad = savedInstanceState.getInt(Utils.Keys.PAGE_TO_LOAD);

			mNumPagesTotal = savedInstanceState.getInt(Utils.Keys.NUM_PAGES_TOTAL);

			mQuery = savedInstanceState.getString(Utils.Keys.QUERY);

			mIsRegularSearch = savedInstanceState.getBoolean(Utils.Keys.IS_REGULAR_SEARCH);

			mIsRequestRunning = savedInstanceState.getBoolean(Utils.Keys.IS_REQUEST_RUNNING);

			mUserList = (UserList) savedInstanceState.getSerializable(Utils.Extras.USER_LIST);

			getIntent().putExtra(Utils.Extras.USER_LIST, mUserList);
		}

		else
		{
			mPageToLoad = 1;

			mUserList = new UserList();

			mIsRegularSearch = false;

			mIsRequestRunning = false;
		}

		ActionBar bar = getSupportActionBar();

		if(bar != null)
		{
			bar.setTitle(R.string.search);
		}

		mtvInfo = (TextView) findViewById(R.id.textView_activitySearch_info);

		mProgBar = (ProgressBar) findViewById(R.id.progressBar_activitySearch);

		mListView = (ListView) findViewById(R.id.listView_activitySearch);

		if(mIsRequestRunning == true)
		{
			mListView.setEnabled(false);

			updateUi(true, true, false);
		}

		else
		{
			mListView.setEnabled(true);

			updateUi(true, false, false);
		}

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
			{
				Intent intent = new Intent(ActivitySearch.this, ActivityUserProfile.class);

				intent.putExtra(Utils.Extras.USER, mUserList.get(position));

				startActivity(intent);
			}
		});

		mListFooterView = getLayoutInflater().inflate(R.layout.list_footer, null, false);

		mListView.addFooterView(mListFooterView);

		mbtnLoadMore = (FancyButton) mListFooterView.findViewById(R.id.button_list_loadMore);

		mbtnLoadMore.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				mListView.setEnabled(false);

				mProgBar.setVisibility(View.VISIBLE);

				mbtnLoadMore.setVisibility(View.GONE);

				mPageToLoad++;

				String url = "https://api.github.com/search/users?q=" + mQuery.trim() + "\"&page=" + mPageToLoad;

				searchUsers(url, false);
			}
		});

		mbtnLoadMore.setVisibility(View.GONE);

		mSpiceMan = getSpiceManager();

		mAdapter = new AdapterUserSearch(ActivitySearch.this, mUserList);

		mListView.setAdapter(mAdapter);

		onNewIntent(getIntent());
	}

	/**
	 *
	 */
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);

		//no need to check the casting.
		//i know for a fact it's a "UserList".
		//also, "getSerializableExtra" is returning
		//an "ArrayList" object and not "UserList"
		@SuppressWarnings("unchecked")
		ArrayList<User> users = (ArrayList<User>) intent.getSerializableExtra(Utils.Extras.USER_LIST);

		if(users == null ||
		   users.isEmpty() == true)
		{
			mIsRegularSearch = true;
		}

		//if we are NOT performing a "regular" search (e.g. user clicked "followers" button")
		//then show the list which was attached to the intent which started this activity.
		//otherwise (i.e. this is a "regular" search), simply display this activity at its previous state
		if(mIsRegularSearch == false)
		{
			updateUi(true, false, false);

			invalidateOptionsMenu();

			mUserList.clear();

			mUserList.addAll(users);

			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 *
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		//in this activity, we need to override the default behaviour
		//of the "search" button.
		//also, we customize the SearchView here

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.main, menu);

		MenuItem searchItem = menu.findItem(R.id.actionBar_search);

		if(mSearchView != null)
		{
			searchItem.setActionView(mSearchView);
		}

		else
		{
			searchItem.setActionView(new SearchView(this));

			mSearchView = (SearchView) searchItem.getActionView();
		}

		searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

		if(mIsRegularSearch == true)
		{
			searchItem.expandActionView();
		}

		else
		{
			searchItem.collapseActionView();
		}

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		ImageView closeIcon = (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);

		//only deprecated in API 22
		//noinspection deprecation
		closeIcon.setImageResource(R.drawable.ic_close);

		EditText searchEditText = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

		//only deprecated in API 23
		//noinspection deprecation
		searchEditText.setTextColor(getResources().getColor(android.R.color.white));

		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(String query)
			{
				View view = ActivitySearch.this.getCurrentFocus();

				//hide the keyboard
				if (view != null)
				{
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}

				mQuery = query.trim();

				String url = "https://api.github.com/search/users?q=\"" + mQuery + "\"&page=" + mPageToLoad;

				searchUsers(url, true);

				return true;
			}

			@Override
			public boolean onQueryTextChange(String query)
			{
				//do nothing

				return true;
			}
		});

		mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		return true;
	}

	/**
	 * a convenience method to hide or show any of its' parameters (views).<br><br/>
	 * TRUE - shows the view, FALSE - hides the view
	 */
	private void updateUi(boolean showListView,
						  boolean showPorgressBar,
						  boolean showInfoText)
	{
		if(showListView == true)
		{
			mListView.setVisibility(View.VISIBLE);
		}

		else
		{
			mListView.setVisibility(View.GONE);
		}

		if(showPorgressBar == true)
		{
			mProgBar.setVisibility(View.VISIBLE);
		}

		else
		{
			mProgBar.setVisibility(View.GONE);
		}

		if(showInfoText == true)
		{
			mtvInfo.setVisibility(View.VISIBLE);
		}

		else
		{
			mtvInfo.setVisibility(View.GONE);
		}
	}

	/**
	 * starts the request to search for users
	 * @param url the url to search (contains the query and the page number)
	 * @param firstTime whether or not this is a new search
	 *                  (i.e. if the user clicked the "search" button, or the "load more items" button)
	 */
	private void searchUsers(String url, final boolean firstTime)
	{
		//the user clicked the "search" button
		if(firstTime == true)
		{
			updateUi(false, true, false);
		}

		//the user clicked the "load more items" button
		else
		{
			mListView.setEnabled(false);

			updateUi(true, true, false);
		}

		final RoboRequest<SearchUserResults> reqSearchUsers = new RoboRequest<>(url,
																				SearchUserResults.class,
																		  		Utils.CacheKeys.SEARCH_USERS + mQuery + mPageToLoad,
																				HttpMethod.GET);

		mIsRequestRunning = true;

		mSpiceMan.execute(reqSearchUsers,
						  reqSearchUsers.getCacheKey(),
						  Utils.Constants.REQUESTS_CACHE_TIME_OUT,
						  new RequestListener<SearchUserResults>()
						  {
							  @Override
							  public void onRequestFailure(SpiceException e)
							  {
								  Log.e(TAG, "SpiceException", e);

								  mIsRequestRunning = false;

								  mListView.setEnabled(true);

								  //this is a "Fresh" search
								  if(firstTime == true)
								  {
									  updateUi(false, false, true);

									  mbtnLoadMore.setVisibility(View.GONE);

									  mtvInfo.setText(R.string.someErrorOccurred);
								  }

								  //user clicked "load more items" button
								  else
								  {
									  updateUi(true, false, false);

									  showOrHideLoadMoreButton();

									  Toast.makeText(ActivitySearch.this, R.string.failedToRetrieveData, Toast.LENGTH_SHORT).show();
								  }
							  }

							  @Override
							  public void onRequestSuccess(SearchUserResults result)
							  {
								  mIsRequestRunning = false;

								  mListView.setEnabled(true);

								  //this is a "Fresh" search
								  if(firstTime == true)
								  {
									  //no results were found
									  if(result.getItems().isEmpty() == true)
									  {
										  mtvInfo.setText(R.string.noResult);

										  mbtnLoadMore.setVisibility(View.GONE);

										  updateUi(false, false, true);
									  }

									  //some results were found
									  else
									  {
										  //because this is a new search, we need to clear the previous list
										  mUserList.clear();

										  mUserList.addAll(result.getItems());

										  mAdapter.notifyDataSetChanged();

										  updateUi(true, false, false);

										  HttpHeaders headers = reqSearchUsers.getResponseHeaders();

										  //this is a "new" search
										  if(headers != null)
										  {
											  initializeTotalNumOfPagesFromResponseHeader(headers);
										  }

										  //this search has already been performed and the results
										  //were returned from cache
										  else
										  {
											  initializeTotalNumOfPagesFromCache();
										  }

										  showOrHideLoadMoreButton();
									  }
								  }

								  //user clicked "load more items" button
								  else
								  {
									  //in this case, we need to ADD the results
									  //to the already existing list instead of replacing them
									  mUserList.addAll(result.getItems());

									  mAdapter.notifyDataSetChanged();

									  updateUi(true, false, false);

									  showOrHideLoadMoreButton();
								  }
							  }
						  });
	}

	/**
	 * initializes the {@link #mNumPagesTotal} variable from cache
	 */
	private void initializeTotalNumOfPagesFromCache()
	{
		mSpiceMan.getFromCache(Integer.class,
							   Utils.CacheKeys.SEARCH_USERS,
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
									  }
								  }
							  });
	}

	/**
	 * initializes the {@link #mNumPagesTotal} variable from cache
	 * @param headers the servers' response headers
	 */
	private void initializeTotalNumOfPagesFromResponseHeader(@NonNull HttpHeaders headers)
	{
		List<String> list = headers.get("Link");

		if(list != null)
		{
			String link = list.get(0);

			int lastIndex = link.lastIndexOf(";");

			mNumPagesTotal = Integer.parseInt(link.substring(lastIndex - 2, lastIndex - 1));

			mSpiceMan.putInCache(Integer.class,
								 Utils.CacheKeys.SEARCH_USERS,
								mNumPagesTotal);
		}

		//if there is no header names "Link"
		//then there is only 1 page
		else
		{
			mNumPagesTotal = 1;
		}
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
	 *
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return item.getItemId() == R.id.actionBar_search ||
			   super.onOptionsItemSelected(item);
	}

	/**
	 *
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putInt(Utils.Keys.PAGE_TO_LOAD, mPageToLoad);

		outState.putInt(Utils.Keys.NUM_PAGES_TOTAL, mNumPagesTotal);

		outState.putString(Utils.Keys.QUERY, mQuery);

		outState.putBoolean(Utils.Keys.IS_REGULAR_SEARCH, mIsRegularSearch);

		outState.putSerializable(Utils.Extras.USER_LIST, mUserList);

		outState.putBoolean(Utils.Keys.IS_REQUEST_RUNNING, mIsRequestRunning);

		super.onSaveInstanceState(outState);
	}
}
