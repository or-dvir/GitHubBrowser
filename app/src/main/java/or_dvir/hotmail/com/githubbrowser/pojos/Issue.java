package or_dvir.hotmail.com.githubbrowser.pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * a class representing a repository issue
 */
@SuppressWarnings("PointlessBooleanExpression")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue implements Serializable
{
	private static final String OPENED = "open";

	//IMPORTANT NOTE:
	//all of these variables MUST be named exactly as they are on the
	//downloaded JSON file. this is a requirement for the JACKSON library!

	private String title;
	private String created_at;
	private String state;

	private boolean mIsOpen;

	private long id;

	//IMPORTANT NOTE:
	//some of the getter/setter methods here may appear to not be used,
	//however this is NOT the case - they are used by the JACKSON library.
	//DO NOT DELETE ANY OF THESE METHODS

	/**
	 * a simple empty constructor used the the JACKSON library
	 */
	public Issue()
	{
		//empty constructor used by JACKSON library
	}

	/**
	 *
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 *
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 *
	 */
	public boolean isOpen()
	{
		return mIsOpen;
	}

	/**
	 *
	 */
	public String getCreated_at()
	{
		return created_at;
	}

	/**
	 *
	 */
	public void setCreated_at(String created_at)
	{
		int index = created_at.indexOf("T");

		if (index == -1)
		{
			this.created_at = created_at;
		}

		else
		{
			this.created_at = created_at.substring(0, index);
		}
	}

	/**
	 *
	 */
	public String getState()
	{
		return state;
	}

	/**
	 *
	 */
	public void setState(String state)
	{
		mIsOpen = state.equals(OPENED);

		this.state = state;
	}

	/**
	 *
	 */
	public long getId()
	{
		return id;
	}

	/**
	 *
	 */
	public void setId(long id)
	{
		this.id = id;
	}
}
