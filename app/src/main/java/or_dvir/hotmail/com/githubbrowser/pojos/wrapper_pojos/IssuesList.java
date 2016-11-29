package or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos;

import java.util.ArrayList;

import or_dvir.hotmail.com.githubbrowser.pojos.Issue;

/**
 * a simple wrapper class to be used with the RoboSpice library
 */
@SuppressWarnings("PointlessBooleanExpression")
public class IssuesList extends ArrayList<Issue>
{
	/**
	 * @param opened set to true to return all open issues.
	 *               set to false to return all closed issues.
	 * @return a list of opened or closed issues, or null if no such issues
	 */
	public IssuesList getOpenedOrClosedIssues(boolean opened)
	{
		IssuesList temp = new IssuesList();

		for(int i = 0; i < this.size(); i++)
		{
			if(this.get(i).isOpen() == opened)
			{
				temp.add(this.get(i));
			}
		}

		if(temp.isEmpty() == true)
		{
			return null;
		}

		return temp;
	}
}
