package or_dvir.hotmail.com.githubbrowser.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import org.springframework.http.HttpMethod;

import goldzweigapps.tabs.Builder.EasyTabsBuilder;
import goldzweigapps.tabs.Colors.EasyTabsColors;
import goldzweigapps.tabs.Items.TabItem;
import goldzweigapps.tabs.View.EasyTabs;
import mehdi.sakout.fancybuttons.FancyButton;
import or_dvir.hotmail.com.githubbrowser.R;
import or_dvir.hotmail.com.githubbrowser.fragments.*;
import or_dvir.hotmail.com.githubbrowser.pojos.*;
import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.RepositoriesList;
import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.UserList;
import or_dvir.hotmail.com.githubbrowser.pojos.RoboRequest;

/**
 * an activity which displays information about a specific {@link User}
 */
@SuppressWarnings({"PointlessBooleanExpression", "FieldCanBeLocal"})
public class ActivityUserProfile extends MyActivity
{
	private static final String TAG = "ActivityUserProfile";

	/**
	 * the user which this activity is associated with
	 */
	private User mUser;

	private SpiceManager mSpiceMan;

	private FancyButton mbtnFollowing;

	private FancyButton mbtnFollowers;

	private ImageView mUserImage;

	private TextView mUserName;

	/**
	 *
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user_profile);

		if(savedInstanceState != null)
		{
			mUser = (User) savedInstanceState.getSerializable(Utils.Extras.USER);

			getIntent().putExtra(Utils.Extras.USER, mUser);
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

		mUser = (User) intent.getSerializableExtra(Utils.Extras.USER);

		ActionBar bar = getSupportActionBar();

		if(bar != null)
		{
			bar.setTitle(getString(R.string.user, mUser.getLogin()));
		}

		initializeTabs();

		initializeViews();

		initializeNetworkRequests();
	}

	/**
	 * attaches all the views from the layout file
	 */
	private void findAllViews()
	{
		mbtnFollowing = (FancyButton) findViewById(R.id.button_userProfile_following);

		mbtnFollowers = (FancyButton) findViewById(R.id.button_userProfile_followers);

		mUserImage = (ImageView) findViewById(R.id.imageView_userProfile);

		mUserName = (TextView) findViewById(R.id.textView_userProfile_userName);
	}

	/**
	 * initializes all the views of this activity
	 */
	private void initializeViews()
	{
		mUserName.setText(mUser.getLogin());

		mbtnFollowing.setEnabled(false);

		mbtnFollowing.setText(getString(R.string.following, "..."));

		mbtnFollowing.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Utils.startActivitySearch(ActivityUserProfile.this,
										  mUser.getUserFollowing());
			}
		});

		mbtnFollowers.setEnabled(false);

		mbtnFollowers.setText(getString(R.string.followers, "..."));

		mbtnFollowers.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Utils.startActivitySearch(ActivityUserProfile.this,
										  mUser.getUserFollowers());
			}
		});

		Picasso.with(this)
			   .load(mUser.getAvatar_url())
			   .error(R.drawable.background_login)
			   .fit()
			   .centerInside()
			   .into(mUserImage);
	}

	/**
	 * start all network requests related to this activity
	 * (getting "followers" and "following")
	 */
	private void initializeNetworkRequests()
	{
		//getting followers

		RoboRequest<UserList> reqGetFollowers = new RoboRequest<>(mUser.getFollowers_url(),
																  UserList.class,
																  Utils.CacheKeys.GET_FOLLOWERS + mUser.getId(),
																  HttpMethod.GET);

		mSpiceMan.execute(reqGetFollowers,
						  reqGetFollowers.getCacheKey(),
						  Utils.Constants.REQUESTS_CACHE_TIME_OUT,
						  new RequestListener<UserList>()
						  {
							  @Override
							  public void onRequestFailure(SpiceException e)
							  {
								  Log.e(TAG, "SpiceException", e);

								  mbtnFollowers.setText(getString(R.string.followers, getString(R.string.notAvailable)));
							  }

							  @Override
							  public void onRequestSuccess(UserList result)
							  {
								  mUser.setUserFollowers(result);

								  mbtnFollowers.setText(getString(R.string.followers, result.size() + ""));

								  if(result.size() > 0)
								  {
									  mbtnFollowers.setEnabled(true);
								  }
							  }
						  });



		//getting following

		RoboRequest<UserList> reqGetFollowing = new RoboRequest<>(mUser.getFollowing_url(),
																  UserList.class,
																  Utils.CacheKeys.GET_FOLLOWING + mUser.getId(),
																  HttpMethod.GET);

		mSpiceMan.execute(reqGetFollowing,
						  reqGetFollowing.getCacheKey(),
						  Utils.Constants.REQUESTS_CACHE_TIME_OUT,
						  new RequestListener<UserList>()
						  {
							  @Override
							  public void onRequestFailure(SpiceException e)
							  {
								  Log.e(TAG, "SpiceException", e);

								  mbtnFollowing.setText(getString(R.string.following, getString(R.string.notAvailable)));
							  }

							  @Override
							  public void onRequestSuccess(UserList result)
							  {
								  mUser.setUserFollowing(result);

								  mbtnFollowing.setText(getString(R.string.following, result.size() + ""));

								  if(result.size() > 0)
								  {
									  mbtnFollowing.setEnabled(true);
								  }
							  }
						  });
	}

	/**
	 * initializes the tabs of this activity ("owned" and "starred" repositories)
	 */
	private void initializeTabs()
	{
		EasyTabs tabs = (EasyTabs) findViewById(R.id.tabs_userProfile);

		FragmentGeneric fragOwned = FragmentGeneric.<RepositoriesList>newInstance(eItemsType.REPOSITORY_OWNED);

		FragmentGeneric fragStarred = FragmentGeneric.<RepositoriesList>newInstance(eItemsType.REPOSITORY_STARRED);

		TabItem ownedRepositories = new TabItem(fragOwned, getString(R.string.owned));

		TabItem starredRepositories = new TabItem(fragStarred, getString(R.string.starred));

		//noinspection deprecation
		EasyTabsBuilder.init(tabs)
					   .addTabs(ownedRepositories, starredRepositories)
					   .setBackgroundColor(getResources().getColor(R.color.colorPrimary))
					   .setIndicatorColor(getResources().getColor(R.color.colorAccent))
					   .setTextColors(EasyTabsColors.White, getResources().getColor(R.color.colorAccent))
					   .setTabLayoutScrollable(false)
					   .Build();
	}

	/**
	 *
	 */
	@Override
	public void onBackPressed()
	{
		//in case this task the the "first" current users' profile,
		//display a dialog confirming that this action will exit the app.
		//otherwise, resort to default behaviour

		if(isTaskRoot() == true)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setMessage(R.string.areYouSureYouWantToExit)
				   .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
				   {
					   @Override
					   public void onClick(DialogInterface dialogInterface, int i)
					   {
						   dialogInterface.dismiss();

						   ActivityUserProfile.super.onBackPressed();
					   }
				   })
				   .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
				   {
					   @Override
					   public void onClick(DialogInterface dialogInterface, int i)
					   {
						   dialogInterface.dismiss();
					   }
				   })
				   .show();
		}

		else
		{
			super.onBackPressed();
		}
	}

	/**
	 *
	 */
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putSerializable(Utils.Extras.USER, mUser);

		super.onSaveInstanceState(outState);
	}

	/**
	 * @return the {@link User} this activity is associated with
	 */
	public User getUser()
	{
		return mUser;
	}
}