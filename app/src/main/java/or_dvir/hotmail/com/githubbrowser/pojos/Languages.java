package or_dvir.hotmail.com.githubbrowser.pojos;

import org.codehaus.jackson.annotate.JsonAnySetter;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * a class representing a all programming languages used in a repository
 */
public class Languages implements Serializable
{
	/**
	 * a list of all the languages used in a repository.<br><br/>
	 * key - the name of the language<br><br/>
	 * value - how much of the repositorys' code is written in that language.
	 */
	private HashMap<String, Integer> mMap;

	//IMPORTANT NOTE:
	//some of the methods here may appear to not be used,
	//however this is NOT the case - they are used by the JACKSON library.
	//DO NOT DELETE ANY OF THESE METHODS

	/**
	 * a simple empty constructor used the the JACKSON library
	 */
	public Languages()
	{
		mMap = new HashMap<>();

		//constructor used by JACKSON library
	}

	/**
	 *
	 */
	@JsonAnySetter
	public void handleUnknown(String key, int value)
	{
		mMap.put(key, value);
	}

	/**
	 * converts {@link #mMap} to a human readable string with percentages
	 */
	public String getPercentages()
	{
		int total = 0;

		for(String key : mMap.keySet())
		{
			total += mMap.get(key);
		}

		StringBuilder builder = new StringBuilder();

		float value;

		DecimalFormat formatter = new DecimalFormat("#.##");

		for(String key : mMap.keySet())
		{
			value = mMap.get(key);

			builder.append(key)
				   .append(": ")
				   .append(formatter.format((value / total) * 100))
				   .append("%")
				   .append(", ");
		}

		//removing the last comma and space
		builder.delete(builder.length() - 2, builder.length());

		return builder.toString();
	}
}
