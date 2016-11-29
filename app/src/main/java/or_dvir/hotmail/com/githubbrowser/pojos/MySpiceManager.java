package or_dvir.hotmail.com.githubbrowser.pojos;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;

public class MySpiceManager extends JacksonSpringAndroidSpiceService
{
	public MySpiceManager()
	{
		super();
	}

	@Override
	public int getThreadCount()
	{
		return 4;
	}
}
