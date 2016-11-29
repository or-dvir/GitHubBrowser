package or_dvir.hotmail.com.githubbrowser.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.UserList;

/**
 * a class representing a user
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable
{
	//IMPORTANT NOTE:
	//all of these variables MUST be named exactly as they are on the
	//downloaded JSON file. this is a requirement for the JACKSON library!

	/**
	 * the currently logged on user
	 */
	private static User currentUser;

	private static String mCurrentUserAuthenticationValue;

	private String login;
	private String avatar_url;
	private String followers_url;
	private String following_url;
	private String starred_url;
	private String repos_url;

	private int id;

	private UserList mUserFollowers;
	private UserList mUserFollowing;

	//IMPORTANT NOTE:
	//some of the getter/setter methods here may appear to not be used,
	//however this is NOT the case - they are used by the JACKSON library.
	//DO NOT DELETE ANY OF THESE METHODS

	/**
	 * a simple empty constructor used the the JACKSON library
	 */
	public User()
	{
		//empty constructor used by JACKSON library
	}

	/**
	 *
	 */
	public String getLogin()
	{
		return login;
	}

	/**
	 *
	 */
	public void setLogin(String login)
	{
		this.login = login;
	}

	/**
	 *
	 */
	public String getAvatar_url()
	{
		return avatar_url;
	}

	/**
	 *
	 */
	public void setAvatar_url(String avatar_url)
	{
		this.avatar_url = avatar_url;
	}

	public String getFollowers_url()
	{
		return followers_url;
	}

	/**
	 *
	 */
	public void setFollowers_url(String followers_url)
	{
		this.followers_url = followers_url;
	}

	/**
	 *
	 */
	public String getFollowing_url()
	{
		return following_url;
	}

	/**
	 *
	 */
	public void setFollowing_url(String following_url)
	{
		int index = following_url.indexOf("{");

		if (index == -1)
		{
			this.following_url = following_url;
		}

		else
		{
			this.following_url = following_url.substring(0, index);
		}
	}

	/**
	 *
	 */
	public String getStarred_url()
	{
		return starred_url;
	}

	public void setStarred_url(String starred_url)
	{
		int index = starred_url.indexOf("{");

		if (index == -1)
		{
			this.starred_url = starred_url;
		}

		else
		{
			this.starred_url = starred_url.substring(0, index);
		}
	}

	/**
	 *
	 */
	public String getRepos_url()
	{
		return repos_url;
	}

	/**
	 *
	 */
	public void setRepos_url(String repos_url)
	{
		this.repos_url = repos_url;
	}

	/**
	 *
	 */
	public int getId()
	{
		return id;
	}

	/**
	 *
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 *
	 */
	public static User getCurrentUser()
	{
		return currentUser;
	}

	/**
	 *
	 */
	public static void setCurrentUser(User user)
	{
		User.currentUser = user;
	}

	/**
	 *
	 */
	public static String getCurrentUserAuthenticationValue()
	{
		return mCurrentUserAuthenticationValue;
	}

	/**
	 *
	 */
	public static void setCurrentUserAuthenticationValue(String authenticationValue)
	{
		mCurrentUserAuthenticationValue = authenticationValue;
	}

	/**
	 *
	 */
	public UserList getUserFollowers()
	{
		return mUserFollowers;
	}

	/**
	 *
	 */
	public void setUserFollowers(UserList followers)
	{
		this.mUserFollowers = followers;
	}

	/**
	 *
	 */
	public UserList getUserFollowing()
	{
		return mUserFollowing;
	}

	/**
	 *
	 */
	public void setUserFollowing(UserList following)
	{
		this.mUserFollowing = following;
	}
}
