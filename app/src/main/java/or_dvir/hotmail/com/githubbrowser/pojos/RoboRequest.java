package or_dvir.hotmail.com.githubbrowser.pojos;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * a generic class representing a network request
 * @param <T> the type of class this request returns
 */
public class RoboRequest<T> extends SpringAndroidSpiceRequest<T>
{
	/**
	 * the url of the request
	 */
	private String mUrl;

	/**
	 * the type of class this request returns
	 */
	private Class<T> mResponseType;

	/**
	 * the cache key to be used for storing the results
	 */
	private String mCacheKey;

	/**
	 * the method to use (e.g. GET/POST/DELETE etc...)
	 */
	private HttpMethod mHttpMethod;

	/**
	 * the headers of the response
	 * (used to extract the number of pages the result holds)
	 */
	private HttpHeaders mResponseHeaders;

	/**
	 * creates and starts a new network request
	 * @param url the url to load
	 * @param responseType the type of class of the response
	 * @param cacheKey the cache key to be used for storing the results
	 * @param httpMethod the method to use (e.g. GET/POST/DELETE etc...)
	 */
	public RoboRequest(String url, Class<T> responseType, String cacheKey, HttpMethod httpMethod)
	{
		super(responseType);

		this.mResponseType = responseType;

		this.mCacheKey = cacheKey;

		this.mUrl= url;

		this.mHttpMethod = httpMethod;
	}

	/**
	 *
	 */
	public String getCacheKey()
	{
		return mCacheKey;
	}

	/**
	 *
	 */
	@Override
	public T loadDataFromNetwork() throws Exception
	{
		HttpHeaders headers = new HttpHeaders();

		headers.add("Accept", "application/vnd.github.v3+json");

		headers.add("Authorization", User.getCurrentUserAuthenticationValue());

		if(mHttpMethod == HttpMethod.PUT)
		{
			headers.add("Content-Length", "0");
		}

		ResponseEntity<T> entity = getRestTemplate().exchange(mUrl, mHttpMethod, new HttpEntity<>(headers), mResponseType);

		mResponseHeaders = entity.getHeaders();

		return entity.getBody();
	}

	/**
	 *
	 */
	public HttpHeaders getResponseHeaders()
	{
		return mResponseHeaders;
	}
}
