package or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;

import or_dvir.hotmail.com.githubbrowser.pojos.User;

/**
 * a simple wrapper class to be used with the RoboSpice library
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserList extends ArrayList<User>
{
	//just a wrapper class so i can deserialize with JACKSON
}
