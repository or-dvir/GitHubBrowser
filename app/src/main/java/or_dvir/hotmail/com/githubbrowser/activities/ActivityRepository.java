package or_dvir.hotmail.com.githubbrowser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

import goldzweigapps.tabs.Builder.EasyTabsBuilder;
import goldzweigapps.tabs.Colors.EasyTabsColors;
import goldzweigapps.tabs.Items.TabItem;
import goldzweigapps.tabs.View.EasyTabs;
import mehdi.sakout.fancybuttons.FancyButton;
import or_dvir.hotmail.com.githubbrowser.R;
import or_dvir.hotmail.com.githubbrowser.fragments.FragmentGeneric;
import or_dvir.hotmail.com.githubbrowser.pojos.Utils;
import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.IssuesList;
import or_dvir.hotmail.com.githubbrowser.pojos.Languages;
import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.ObjList;
import or_dvir.hotmail.com.githubbrowser.pojos.Repository;
import or_dvir.hotmail.com.githubbrowser.pojos.eItemsType;
import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.UserList;
import or_dvir.hotmail.com.githubbrowser.pojos.RoboRequest;

/**
 * an activity which displays information about a specific {@link Repository}
 */
@SuppressWarnings("PointlessBooleanExpression")
public class ActivityRepository extends MyActivity
{
	private static final String TAG = "ActivityRepository";

	/**
	 * whether or not this repository is starred
	 */
	private boolean mIsStarred;

	/**
	 * whether or not the user got to this activity by clicking
	 * on a "starred" repository from {@link ActivityUserProfile}
	 */
	private boolean mClickedStarred;

	private Repository mRepository;

	private SpiceManager mSpiceMan;

	private TextView mtvCommits;
	private TextView mtvBranches;
	private TextView mtvReleases;
	private TextView mtvLanguages;
	private TextView mtvOwnerName;
	private TextView mtvFork;
	private TextView mtvStar;

	private FancyButton mbtnContributors;

	private ImageView mivOwnerImage;

	private EasyTabs mTabs;

	private LinearLayout mllImageContainer;

	/**
	 *
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_repository);

		if(savedInstanceState != null)
		{
			mIsStarred = savedInstanceState.getBoolean(Utils.Keys.IS_STARRED);

			mClickedStarred = savedInstanceState.getBoolean(Utils.Extras.CLICKED_STARRED);

			mRepository = (Repository) savedInstanceState.getSerializable(Utils.Extras.REPOSITORY);

			getIntent().putExtra(Utils.Extras.REPOSITORY, mRepository);

			getIntent().putExtra(Utils.Keys.IS_STARRED, mIsStarred);

			getIntent().putExtra(Utils.Extras.CLICKED_STARRED, mClickedStarred);
		}

		mSpiceMan = getSpiceManager();

		findAllViews();

		onNewIntent(getIntent());
	}

	/**
	 *
	 */
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);

		mRepository = (Repository) intent.getSerializableExtra(Utils.Extras.REPOSITORY);

		mClickedStarred = intent.getBooleanExtra(Utils.Extras.CLICKED_STARRED, true);

		ActionBar bar = getSupportActionBar();

		if(bar != null)
		{
			bar.setTitle(getString(R.string.repositoryActionBarTitle, mRepository.getName()));
		}

		initializeTabs();

		initializeViews();

		initializeNetworkRequests();
	}

	/**
	 * start all network requests relating to the repository chosen
	 * (getting commits, branches, releases, contributors, and languages).
	 */
	private void initializeNetworkRequests()
	{
		//getting commits

		RoboRequest<ObjList> reqGetCommits = new RoboRequest<>(mRepository.getCommits_url(),
															   ObjList.class,
															   Utils.CacheKeys.GET_COMMITS + mRepository.getName(),
															   HttpMethod.GET);
		mSpiceMan.execute(reqGetCommits,
						  reqGetCommits.getCacheKey(),
						  Utils.Constants.REQUESTS_CACHE_TIME_OUT,
						  new RequestListener<ObjList>()
						  {
							  @Override
							  public void onRequestFailure(SpiceException e)
							  {
								  Log.e(TAG, "SpiceException", e);

								  mtvCommits.setText(getString(R.string.commits, getString(R.string.notAvailable)));
							  }

							  @Override
							  public void onRequestSuccess(ObjList result)
							  {
								  mtvCommits.setText(getString(R.string.commits, result.size() + ""));
							  }
						  });



		//getting branches

		RoboRequest<ObjList> reqGetBranches = new RoboRequest<>(mRepository.getBranches_url(),
																ObjList.class,
															 	Utils.CacheKeys.GET_BRANCHES + mRepository.getName(),
																HttpMethod.GET);

		mSpiceMan.execute(reqGetBranches,
						  reqGetBranches.getCacheKey(),
						  Utils.Constants.REQUESTS_CACHE_TIME_OUT,
						  new RequestListener<ObjList>()
						  {
							  @Override
							  public void onRequestFailure(SpiceException e)
							  {
								  Log.e(TAG, "SpiceException", e);

								  mtvBranches.setText(getString(R.string.branches, getString(R.string.notAvailable)));
							  }

							  @Override
							  public void onRequestSuccess(ObjList result)
							  {
								  mtvBranches.setText(getString(R.string.branches, result.size() + ""));
							  }
						  });



		//getting releases

		RoboRequest<ObjList> reqGetReleases = new RoboRequest<>(mRepository.getReleases_url(),
																ObjList.class,
															 	Utils.CacheKeys.GET_RELEASES + mRepository.getName(),
																HttpMethod.GET);

		mSpiceMan.execute(reqGetReleases,
						  reqGetReleases.getCacheKey(),
						  Utils.Constants.REQUESTS_CACHE_TIME_OUT,
						  new RequestListener<ObjList>()
						  {
							  @Override
							  public void onRequestFailure(SpiceException e)
							  {
								  Log.e(TAG, "SpiceException", e);

								  mtvReleases.setText(getString(R.string.releases, getString(R.string.notAvailable)));
							  }

							  @Override
							  public void onRequestSuccess(ObjList result)
							  {
								  mtvReleases.setText(getString(R.string.releases, result.size() + ""));
							  }
						  });



		//getting contributors

		RoboRequest<UserList> reqGetContributors = new RoboRequest<>(mRepository.getContributors_url(),
																	 UserList.class,
																	 Utils.CacheKeys.GET_CONTRIBUTORS + mRepository.getName(),
																	 HttpMethod.GET);

		mSpiceMan.execute(reqGetContributors,
						  reqGetContributors.getCacheKey(),
						  Utils.Constants.REQUESTS_CACHE_TIME_OUT,
						  new RequestListener<UserList>()
						  {
							  @Override
							  public void onRequestFailure(SpiceException e)
							  {
								  Log.e(TAG, "SpiceException", e);

								  mbtnContributors.setText(getString(R.string.contributors, getString(R.string.notAvailable)));
							  }

							  @Override
							  public void onRequestSuccess(UserList result)
							  {
								  mRepository.setContributors(result);

								  mbtnContributors.setText(getString(R.string.contributors, result.size() + ""));

								  if(result.size() > 0)
								  {
									  mbtnContributors.setEnabled(true);
								  }
							  }
						  });

		//getting languages

		RoboRequest<Languages> reqGetLanguages = new RoboRequest<>(mRepository.getLanguages_url(),
																   Languages.class,
																   Utils.CacheKeys.GET_LANGUAGES + mRepository.getName(),
																   HttpMethod.GET);

		mSpiceMan.execute(reqGetLanguages,
						  reqGetLanguages.getCacheKey(),
						  Utils.Constants.REQUESTS_CACHE_TIME_OUT,
						  new RequestListener<Languages>()
						  {
							  @Override
							  public void onRequestFailure(SpiceException e)
							  {
								  Log.e(TAG, "SpiceException", e);

								  mtvLanguages.setText(getString(R.string.languages, getString(R.string.notAvailable)));
							  }

							  @Override
							  public void onRequestSuccess(Languages result)
							  {
								  mtvLanguages.setText(getString(R.string.languages, result.getPercentages()));
							  }
						  });
	}

	/**
	 * initializes the tabs of this activity (opened/closed/all issues)
	 */
	private void initializeTabs()
	{
		FragmentGeneric fragIssuesOpen = FragmentGeneric.<IssuesList>newInstance(eItemsType.ISSUES_OPENED);

		FragmentGeneric fragIssuesClosed = FragmentGeneric.<IssuesList>newInstance(eItemsType.ISSUES_CLOSED);

		FragmentGeneric fragIssuesAll = FragmentGeneric.<IssuesList>newInstance(eItemsType.ISSUES_ALL);

		TabItem issuesOpened = new TabItem(fragIssuesOpen, getString(R.string.open));

		TabItem issuesClosed = new TabItem(fragIssuesClosed, getString(R.string.closed));

		TabItem issuesAll = new TabItem(fragIssuesAll, getString(R.string.all));

		//noinspection deprecation
		EasyTabsBuilder.init(mTabs)
					   .addTabs(issuesOpened,
								issuesClosed,
								issuesAll)
					   .setBackgroundColor(getResources().getColor(R.color.colorPrimary))
					   .setIndicatorColor(getResources().getColor(R.color.colorAccent))
					   .setTextColors(EasyTabsColors.White, getResources().getColor(R.color.colorAccent))
					   .setTabLayoutScrollable(false)
					   .Build();
	}

	/**
	 * attaches all the views from the layout file
	 */
	private void findAllViews()
	{
		mTabs = (EasyTabs) findViewById(R.id.tabs_repository);

		mllImageContainer = (LinearLayout) findViewById(R.id.linearLayout_repository_imageContainer);

		mivOwnerImage = (ImageView) findViewById(R.id.imageView_repository_ownerImage);

		mtvOwnerName = (TextView) findViewById(R.id.textView_repository_ownerName);

		mtvCommits = (TextView) findViewById(R.id.textView_repository_commits);

		mtvBranches = (TextView) findViewById(R.id.textView_repository_branches);

		mtvReleases = (TextView) findViewById(R.id.textView_repository_releases);

		mbtnContributors = (FancyButton) findViewById(R.id.button_repository_contributors);

		mtvStar = (TextView) findViewById(R.id.textView_repository_star);

		mtvFork = (TextView) findViewById(R.id.textView_repository_fork);

		mtvLanguages = (TextView) findViewById(R.id.textView_repository_languages);
	}

	/**
	 * initializes the contents of all the views in this activity
	 */
	private void initializeViews()
	{
		Picasso.with(this)
			   .load(mRepository.getOwner().getAvatar_url())
			   .error(R.drawable.background_login)
			   .into(mivOwnerImage);

		mtvOwnerName.setText(mRepository.getOwner().getLogin());

		mtvCommits.setText(getString(R.string.commits, "..."));

		mtvBranches.setText(getString(R.string.branches, "..."));

		mtvReleases.setText(getString(R.string.releases, "..."));

		mbtnContributors.setText(getString(R.string.contributors, "..."));

		mbtnContributors.setEnabled(false);

		mbtnContributors.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Utils.startActivitySearch(ActivityRepository.this,
										  mRepository.getContributors());
			}
		});

		mtvStar.setText(getString(R.string.star, mRepository.getStargazers_count()));

		mtvFork.setText(getString(R.string.fork, mRepository.getForks()));

		mtvLanguages.setText(getString(R.string.languages, "..."));

		mllImageContainer.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				//if we got to this activity by clicking a repository
				//under the "owned" tab, then the previous activity is the owner's profile
				if(mClickedStarred == false)
				{
					onBackPressed();
				}

				else
				{
					Intent intent = new Intent(ActivityRepository.this, ActivityUserProfile.class);

					intent.putExtra(Utils.Extras.USER, mRepository.getOwner());

					startActivity(intent);
				}
			}
		});
	}

	/**
	 * @return the {@link Repository} this activity is associated with
	 */
	public Repository getRepository()
	{
		return mRepository;
	}

	/**
	 *
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		//checking whether or not this repository is "Starred" by the logged in user
		//and displaying the "starred" icon accordingly

		final MenuItem favorite = menu.findItem(R.id.actionBar_favorite);

		String url = "https://api.github.com/user/starred/" + mRepository.getOwner().getLogin() + "/" + mRepository.getName();

		RoboRequest<Object> reqIsStarred = new RoboRequest<>(url,
															 Object.class,
															 Utils.CacheKeys.IS_STARRED + mRepository.getName(),
															 HttpMethod.GET);

		mSpiceMan.execute(reqIsStarred,
						  reqIsStarred.getCacheKey(),
						  Utils.Constants.REQUESTS_CACHE_TIME_OUT,
						  new RequestListener<Object>()
						  {
							  @Override
							  public void onRequestFailure(SpiceException e)
							  {
								  //a failure with a code of "404 not found"
								  //is not actually a failure.
								  //it means that the repository is NOT starred.
								  //if there is a failure that is not 404,
								  //we assume that the repository is NOT starred.

								  boolean is404 = ((HttpClientErrorException) e.getCause()).getStatusCode()
																						   .value() == 404;

								  if (is404 == false)
								  {
									  Log.e(TAG, "SpiceException", e);
								  }

								  mIsStarred = false;

								  favorite.setIcon(R.drawable.ic_actionbar_favorite_outline);

								  favorite.setTitle(R.string.starRepository);

								  favorite.setVisible(true);
							  }

							  @Override
							  public void onRequestSuccess(Object result)
							  {
								  mIsStarred = true;

								  favorite.setIcon(R.drawable.ic_actionbar_favorite_full);

								  favorite.setTitle(R.string.unstarRepository);

								  favorite.setVisible(true);
							  }
						  });

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 *
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		//just need to handle the extra actionbar button
		//this activity has.
		if(item.getItemId() == R.id.actionBar_favorite)
		{
			//if this repository is already starred, un-star it
			if(mIsStarred == true)
			{
				starOrUnstarRepository(false, item);
			}

			//if this repository is not starred, star it
			else
			{
				starOrUnstarRepository(true, item);
			}

			return true;
		}

		//if any other button is pressed, use default behaviour
		else
		{
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * stars or un-stars the {@link Repository} this activity represents
	 * @param star whether to star or un-star the {@link Repository}
	 * @param item the "favorite" or "starred" menu item, so we can change its' icon
	 */
	private void starOrUnstarRepository(final boolean star, final MenuItem item)
	{
		HttpMethod method = star ? HttpMethod.PUT : HttpMethod.DELETE;

		String url = "https://api.github.com/user/starred/" +
					 mRepository.getOwner().getLogin() +
					 "/" +
					 mRepository.getName();

		RoboRequest<Object> reqStarOrUnstar = new RoboRequest<>(url,
															 Object.class,
															 Utils.CacheKeys.STAR_OR_UNSTAR + mRepository.getName(),
															 method);

		mSpiceMan.execute(reqStarOrUnstar,
						  reqStarOrUnstar.getCacheKey(),
						  Utils.Constants.REQUESTS_CACHE_TIME_OUT,
						  new RequestListener<Object>()
						  {
							  @Override
							  public void onRequestFailure(SpiceException e)
							  {
								  Log.e(TAG, "SpiceException", e);

								  String message = getString(R.string.someErrorOccurred);

								  Toast.makeText(ActivityRepository.this, message, Toast.LENGTH_LONG)
									   .show();
							  }

							  @Override
							  public void onRequestSuccess(Object result)
							  {
								  mIsStarred = star;

								  String message;

								  if(star == true)
								  {
									  item.setIcon(R.drawable.ic_actionbar_favorite_full);

									  item.setTitle(R.string.unstarRepository);

									  message = getString(R.string.starredRepository);
								  }

								  else
								  {
									  item.setIcon(R.drawable.ic_actionbar_favorite_outline);

									  item.setTitle(R.string.starRepository);

									  message = getString(R.string.unstarredRepository);
								  }

								  Toast.makeText(ActivityRepository.this, message, Toast.LENGTH_SHORT)
									   .show();
							  }
						  });
	}

	/**
	 *
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putBoolean(Utils.Keys.IS_STARRED, mIsStarred);

		outState.putBoolean(Utils.Extras.CLICKED_STARRED, mClickedStarred);

		outState.putSerializable(Utils.Extras.REPOSITORY, mRepository);

		super.onSaveInstanceState(outState);
	}
}
