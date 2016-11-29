package or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * a simple wrapper class to be used with the RoboSpice library
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchUserResults implements Serializable
{
	//IMPORTANT NOTE:
	//all of these variables MUST be named exactly as they are on the
	//downloaded JSON file. this is a requirement for the JACKSON library!

	private UserList items;

	//IMPORTANT NOTE:
	//some of the getter/setter methods here may appear to not be used,
	//however this is NOT the case - they are used by the JACKSON library.
	//DO NOT DELETE ANY OF THESE METHODS

	/**
	 * a simple empty constructor used the the JACKSON library
	 */
	public SearchUserResults()
	{
		//constructor used by JACKSON library
	}

	/**
	 *
	 */
	public UserList getItems()
	{
		return items;
	}

	/**
	 *
	 */
	public void setItems(UserList items)
	{
		this.items = items;
	}
}
