package or_dvir.hotmail.com.githubbrowser.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

import or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos.UserList;

/**
 * a class representing a single repository.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository implements Serializable
{
	//IMPORTANT NOTE:
	//all of these variables MUST be named exactly as they are on the
	//downloaded JSON file. this is a requirement for the JACKSON library!

	private String name;
	private String commits_url;
	private String branches_url;
	private String releases_url;
	private String contributors_url;
	private String languages_url;
	private String issues_url;

	private boolean has_issues;
	private boolean isStarred;

	private int id;
	private int stargazers_count;
	private int forks;

	private User owner;

	private UserList mContributors;

	//IMPORTANT NOTE:
	//some of the getter/setter methods here may appear to not be used,
	//however this is NOT the case - they are used by the JACKSON library.
	//DO NOT DELETE ANY OF THESE METHODS

	/**
	 * a simple empty constructor used the the JACKSON library
	 */
	public Repository()
	{
		//empty constructor used by JACKSON library
	}

	/**
	 *
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *
	 */
	public String getCommits_url()
	{
		return commits_url;
	}

	/**
	 *
	 */
	public void setCommits_url(String commits_url)
	{
		int index = commits_url.indexOf("{");

		if (index == -1)
		{
			this.commits_url = commits_url;
		}

		else
		{
			this.commits_url = commits_url.substring(0, index);
		}
	}

	/**
	 *
	 */
	public String getBranches_url()
	{
		return branches_url;
	}

	/**
	 *
	 */
	public void setBranches_url(String branches_url)
	{
		int index = branches_url.indexOf("{");

		if (index == -1)
		{
			this.branches_url = branches_url;
		}

		else
		{
			this.branches_url = branches_url.substring(0, index);
		}
	}

	/**
	 *
	 */
	public String getReleases_url()
	{
		return releases_url;
	}

	/**
	 *
	 */
	public void setReleases_url(String releases_url)
	{
		int index = releases_url.indexOf("{");

		if (index == -1)
		{
			this.releases_url = releases_url;
		}

		else
		{
			this.releases_url = releases_url.substring(0, index);
		}
	}

	/**
	 *
	 */
	public String getContributors_url()
	{
		return contributors_url;
	}

	/**
	 *
	 */
	public void setContributors_url(String contributors_url)
	{
		this.contributors_url = contributors_url;
	}

	/**
	 *
	 */
	public int getStargazers_count()
	{
		return stargazers_count;
	}

	/**
	 *
	 */
	public void setStargazers_count(int stargazers_count)
	{
		this.stargazers_count = stargazers_count;
	}

	/**
	 *
	 */
	public int getForks()
	{
		return forks;
	}

	/**
	 *
	 */
	public void setForks(int forks)
	{
		this.forks = forks;
	}

	/**
	 *
	 */
	public User getOwner()
	{
		return owner;
	}

	/**
	 *
	 */
	public void setOwner(User owner)
	{
		this.owner = owner;
	}

	/**
	 *
	 */
	public String getLanguages_url()
	{
		return languages_url;
	}

	/**
	 *
	 */
	public void setLanguages_url(String languages_url)
	{
		this.languages_url = languages_url;
	}

	/**
	 *
	 */
	public String getIssues_url()
	{
		return issues_url;
	}

	/**
	 *
	 */
	public void setIssues_url(String issues_url)
	{
		int index = issues_url.indexOf("{");

		if (index == -1)
		{
			this.issues_url = issues_url + "?state=all";
		}

		else
		{
			this.issues_url = issues_url.substring(0, index) + "?state=all";
		}
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
	public UserList getContributors()
	{
		return mContributors;
	}

	/**
	 *
	 */
	public void setContributors(UserList contributors)
	{
		this.mContributors = contributors;
	}

	/**
	 *
	 */
	public boolean isHas_issues()
	{
		return has_issues;
	}

	/**
	 *
	 */
	public void setHas_issues(boolean has_issues)
	{
		this.has_issues = has_issues;
	}

	/**
	 *
	 */
	public boolean isStarred()
	{
		return isStarred;
	}

	/**
	 *
	 */
	public void setStarred(boolean starred)
	{
		isStarred = starred;
	}
}
