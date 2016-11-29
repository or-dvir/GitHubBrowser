package or_dvir.hotmail.com.githubbrowser.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.octo.android.robospice.SpiceManager;

import or_dvir.hotmail.com.githubbrowser.R;
import or_dvir.hotmail.com.githubbrowser.pojos.MySpiceManager;
import or_dvir.hotmail.com.githubbrowser.pojos.User;
import or_dvir.hotmail.com.githubbrowser.pojos.Utils;

/**
 * a shared Activity to hold a single instance of {@link SpiceManager}
 * which will be shared by all activities of this app
 */
public class MyActivity extends AppCompatActivity
{
	private SpiceManager mSpiceMan;

	/**
	 *
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		mSpiceMan = new SpiceManager(MySpiceManager.class);
	}

	/**
	 *
	 */
	@Override
	protected void onStart()
	{
		super.onStart();

		mSpiceMan.start(this);
	}

	/**
	 *
	 */
	@Override
	protected void onStop()
	{
		mSpiceMan.shouldStop();

		super.onStop();
	}

	public SpiceManager getSpiceManager()
	{
		return mSpiceMan;
	}

	/**
	 *
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.main, menu);

		return true;
	}

	/**
	 *
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.actionBar_search:

				Utils.startActivitySearch(this, null);

				return true;

			case R.id.actionBar_myProfile:

				Intent intent = new Intent(this, ActivityUserProfile.class);

				intent.putExtra(Utils.Extras.USER, User.getCurrentUser())
					  .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

				startActivity(intent);

				return true;

			case R.id.actionBar_logout:

				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				builder.setMessage(R.string.areYouSureYouWantToLogout)
					   .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
					   {
						   @Override
						   public void onClick(DialogInterface dialogInterface, int i)
						   {
							   dialogInterface.dismiss();

							   Intent intent1 = new Intent(MyActivity.this, ActivityMain.class);

							   intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

							   startActivity(intent1);
						   }
					   })
					   .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
					   {
						   @Override
						   public void onClick(DialogInterface dialogInterface, int i)
						   {
							   dialogInterface.dismiss();
						   }
					   })
					   .show();

				return true;

			default:

					return super.onOptionsItemSelected(item);
		}
	}
}
