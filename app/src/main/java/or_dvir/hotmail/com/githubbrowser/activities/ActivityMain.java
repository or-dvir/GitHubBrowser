package or_dvir.hotmail.com.githubbrowser.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;
import com.octo.android.robospice.request.listener.RequestListener;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

import mehdi.sakout.fancybuttons.FancyButton;
import or_dvir.hotmail.com.githubbrowser.R;
import or_dvir.hotmail.com.githubbrowser.pojos.User;
import or_dvir.hotmail.com.githubbrowser.pojos.Utils;
import or_dvir.hotmail.com.githubbrowser.pojos.RoboRequest;

/**
 * the login screen
 */
@SuppressWarnings("PointlessBooleanExpression")
public class ActivityMain extends MyActivity implements PendingRequestListener<User>
{
	private static final String TAG = "ActivityMain";

	private SpiceManager mSpiceMan;

	private EditText metUserName;

	private EditText metPassword;

	private ProgressDialog mProgDiag;

	/**
	 * used to bind an already running task to a RequestListener
	 * in case this activity is destroyed while a request is still running
	 * (e.g. orientation change)
	 */
	private boolean mIsRequestRunning;

	/**
	 *
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		ActionBar bar = getSupportActionBar();

		if(bar != null)
		{
			bar.hide();
		}

		mSpiceMan = getSpiceManager();

		mProgDiag = new ProgressDialog(this);

		mProgDiag.setCancelable(false);

		mProgDiag.setMessage(getString(R.string.loading));

		if(savedInstanceState != null)
		{
			//must restore this for the next time there is an orientation change
			mIsRequestRunning = savedInstanceState.getBoolean(Utils.Keys.IS_REQUEST_RUNNING);

			if(mIsRequestRunning == true)
			{
				mProgDiag.show();
			}
		}

		metUserName = (EditText) findViewById(R.id.editText_login_userName);

		metPassword = (EditText) findViewById(R.id.editText_login_password);

		FancyButton btnLogin = (FancyButton) findViewById(R.id.button_login);

		mSpiceMan.addListenerIfPending(User.class, Utils.CacheKeys.LOGIN, ActivityMain.this);

		btnLogin.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				View focusedView = ActivityMain.this.getCurrentFocus();

				//hide the keyboard
				if (focusedView != null)
				{
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}

				String userName = metUserName.getText().toString();

				String password = metPassword.getText().toString();

				boolean error = false;

				if(userName.isEmpty() == true)
				{
					error = true;

					metPassword.setError(getString(R.string.requiredField));

					metPassword.requestFocus();
				}

				if(password.isEmpty() == true)
				{
					error = true;

					metUserName.setError(getString(R.string.requiredField));

					metUserName.requestFocus();
				}

				if(error == false)
				{
					mIsRequestRunning = true;

					mProgDiag.show();

					String credentials = userName + ":" + password;

					String AuthHeaderValue = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

					User.setCurrentUserAuthenticationValue(AuthHeaderValue);

					String url = "https://api.github.com/user";

					RoboRequest<User> reqLogin = new RoboRequest<>(url,
																   User.class,
																   Utils.CacheKeys.LOGIN + userName,
																   HttpMethod.GET);

					mSpiceMan.execute(reqLogin, reqLogin.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new RequestListener<User>()
					{
						@Override
						public void onRequestFailure(SpiceException e)
						{
							handleLoginFailure(e);
						}

						@Override
						public void onRequestSuccess(User user)
						{
							handleLoginSuccess(user);
						}
					});
				}
			}
		});
	}

	/**
	 *
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putBoolean(Utils.Keys.IS_REQUEST_RUNNING, mIsRequestRunning);

		super.onSaveInstanceState(outState);
	}

	/**
	 *
	 */
	@Override
	public void onRequestNotFound()
	{
		//do nothing
	}

	/**
	 * see {@link #handleLoginFailure}
	 */
	@Override
	public void onRequestFailure(SpiceException e)
	{
		handleLoginFailure(e);
	}

	/**
	 * see {@link #handleLoginSuccess}
	 */
	@Override
	public void onRequestSuccess(User user)
	{
		handleLoginSuccess(user);
	}

	/**
	 * handles the case where the login process failed.
	 * if it failed to an incorrect username and/or password, a message is shown to the user.
	 * if it failed for another reason, a general error message is shown.
	 */
	private void handleLoginFailure(SpiceException e)
	{
		mIsRequestRunning = false;

		mProgDiag.dismiss();

		if (e.getCause() instanceof HttpClientErrorException)
		{
			if (((HttpClientErrorException) e.getCause()).getStatusCode()
														 .value() == 401)
			{
				metPassword.setError(getString(R.string.incorrectUserNameOrPassword));

				metUserName.setError(getString(R.string.incorrectUserNameOrPassword));

				metUserName.requestFocus();

				return;
			}
		}

		Log.e(TAG, "SpiceException", e);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.oops)
			   .setMessage(R.string.someErrorOccurred)
			   .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
			   {
				   @Override
				   public void onClick(DialogInterface dialogInterface, int i)
				   {
					   dialogInterface.dismiss();
				   }
			   }).show();
	}

	/**
	 * handles a successful login by opening {@link ActivityUserProfile}.
	 */
	private void handleLoginSuccess(User user)
	{
		mIsRequestRunning = false;

		mProgDiag.dismiss();

		User.setCurrentUser(user);

		Intent intent = new Intent(this, ActivityUserProfile.class);

		intent.putExtra(Utils.Extras.USER, user)
			  .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

		startActivity(intent);
	}
}
