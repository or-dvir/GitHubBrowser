package or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos;

import java.util.ArrayList;

import or_dvir.hotmail.com.githubbrowser.pojos.Repository;

/**
 * a simple wrapper class to be used with the RoboSpice library
 */
@SuppressWarnings("PointlessBooleanExpression")
public class RepositoriesList extends ArrayList<Repository>
{
	/**
	 * @return the names of all the repositories as a regular array
	 */
	public String[] getNamesAsArray()
	{
		ArrayList<String> temp = new ArrayList<>();

		for(int i = 0; i < this.size(); i++)
		{
			temp.add(this.get(i).getName());
		}

		if(temp.isEmpty() == true)
		{
			return null;
		}

		return temp.toArray(new String[temp.size()]);
	}
}
