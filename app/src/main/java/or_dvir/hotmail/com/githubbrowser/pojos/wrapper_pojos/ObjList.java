package or_dvir.hotmail.com.githubbrowser.pojos.wrapper_pojos;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * a simple and generic wrapper class to be used with the RoboSpice library.
 * the only use of this class is to count the number of results from a network request
 * which doesn't require further processing (i.e. branches)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjList extends ArrayList<Object>
{
	//just a wrapper class so i can deserialize with JACKSON
}
