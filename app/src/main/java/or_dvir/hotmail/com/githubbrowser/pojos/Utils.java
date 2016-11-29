package or_dvir.hotmail.com.githubbrowser.pojos;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.octo.android.robospice.persistence.DurationInMillis;

import or_dvir.hotmail.com.githubbrowser.activities.ActivityRepository;
import or_dvir.hotmail.com.githubbrowser.activities.ActivitySearch;
import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.UserList;

/**
 * a convenience class to hold all of this apps' constants,
 * and to perform app-wide actions
 */
@SuppressWarnings("PointlessBooleanExpression")
public class Utils
{
	/**
	 *
	 */
	public static class Constants
	{
		public static final long REQUESTS_CACHE_TIME_OUT = DurationInMillis.ONE_MINUTE * 5;
	}

	/**
	 *
	 */
	public static class Keys
	{
		public static final String IS_REQUEST_RUNNING = "IS_REQUEST_RUNNING";

		public static final String PAGE_TO_LOAD = "PAGE_TO_LOAD";

		public static final String NUM_PAGES_TOTAL = "NUM_PAGES_TOTAL";

		public static final String LIST_ITEMS = "LIST_ITEMS";

		public static final String ITEMS_TYPE = "ITEMS_TYPE";

		public static final String IS_STARRED = "IS_STARRED";

		public static final String QUERY = "QUERY";

		public static final String IS_REGULAR_SEARCH = "IS_REGULAR_SEARCH";
	}

	/**
	 *
	 */
	public static class Extras
	{
		public static final String REPOSITORY = "REPOSITORY";

		public static final String USER = "USER";

		public static final String USER_LIST = "USER_LIST";

		public static final String CLICKED_STARRED = "CLICKED_STARRED";
	}

	/**
	 *
	 */
	public static class CacheKeys
	{
		public static final String STAR_OR_UNSTAR = "STAR_OR_UNSTAR";

		public static final String LOGIN = "LOGIN";

		public static final String GET_ISSUES = "GET_ISSUES";

		public static final String GET_OWNED_REPOSITORIES = "GET_OWNED_REPOSITORIES";

		public static final String GET_STARRED_REPOSITORIES = "GET_STARRED_REPOSITORIES";

		public static final String SEARCH_USERS = "SEARCH_USERS";

		public static final String GET_COMMITS = "GET_COMMITS";

		public static final String GET_RELEASES = "GET_RELEASES";

		public static final String GET_BRANCHES = "GET_BRANCHES";

		public static final String GET_CONTRIBUTORS = "GET_CONTRIBUTORS";

		public static final String GET_LANGUAGES = "GET_LANGUAGES";

		public static final String IS_STARRED = "IS_STARRED";

		public static final String GET_FOLLOWERS = "GET_FOLLOWERS";

		public static final String GET_FOLLOWING = "GET_FOLLOWING";
	}

	/**
	 * a convenience method to start {@link ActivityRepository}
	 * and bring it to the top of the stack
	 * @param context the context to be used
	 * @param repository the repository to load
	 * @param clickedStarred whether the user clicked a repository in the "owned" or "starred" list
	 */
	public static void startActivityRepository(Context context, Repository repository, boolean clickedStarred)
	{
		startActivityBringToFront(context, ActivityRepository.class, null, repository, clickedStarred);
	}

	/**
	 * a convenience method to start {@link ActivitySearch}
	 * and bring it to the top of the stack
	 * @param context the context to be used
	 * @param users a list of users to be shown
	 */
	public static void startActivitySearch(Context context, UserList users)
	{
		startActivityBringToFront(context, ActivitySearch.class, users, null, false);
	}

	/**
	 *
	 * @param context the context ot be used
	 * @param clazz the class type of the activity to be started
	 * @param users a list of users (in case we are starting {@link ActivitySearch})
	 * @param repository a repository to load (in case we are starting {@link ActivityRepository})
	 * @param clickedStarred whether the user clicked a repository in the "owned" or "starred" list
	 *                       (in case we are starting {@link ActivityRepository})
	 */
	private static void startActivityBringToFront(Context context,
												  Class clazz,
												  @Nullable UserList users,
												  @Nullable Repository repository,
												  boolean clickedStarred)
	{
		Intent intent = new Intent(context, clazz);

		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

		//we are opening ActivitySearch
		if (repository == null)
		{
			intent.putExtra(Extras.USER_LIST, users);
		}

		//we are opening ActivityRepository
		else
		{
			intent.putExtra(Extras.REPOSITORY, repository)
				  .putExtra(Extras.CLICKED_STARRED, clickedStarred);
		}

		context.startActivity(intent);
	}
}
