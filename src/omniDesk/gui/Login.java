package omniDesk.gui;

import java.util.StringTokenizer;

import omniDesk.database.SavedConnectionData;
import omniDesk.net.rdp.Common; //import omniDesk.net.rdp.R;
import omniDesk.net.rdp.SpecialKeyDialog;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	SavedConnectionData events;
	EditText ipAddress;
	SaveConnectionDialog saveConnectionDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		makeFullScreen();
		setContentView(R.layout.login);
		events = new SavedConnectionData(this);
		Button connect = (Button) findViewById(R.id.widget44);
		Button saveAndConnect = (Button) findViewById(R.id.widget43);
		saveConnectionDialog = new SaveConnectionDialog(this);
		ipAddress = (EditText) findViewById(R.id.widget41);
		connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (validIPAddress(ipAddress.getText().toString())) {
					Common.ipAddress = ipAddress.getText().toString();

					ConnectivityManager mgr = (ConnectivityManager) getBaseContext()
							.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo netInfo = mgr.getActiveNetworkInfo();

					if (netInfo != null) {
						if (netInfo.getState() == NetworkInfo.State.CONNECTED
								&& netInfo.isAvailable()) {
							Intent intent = new Intent(Login.this,
									OmniDeskMain.class);

							startActivity(intent);
							finish();
						}

					} else
						Toast.makeText(Login.this, "No Connectivity available",
								1).show();

				} else
					Toast.makeText(Login.this,
							"Please enter a valid IP address", 1).show();
			}

		});

		saveAndConnect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (validIPAddress(ipAddress.getText().toString())) {
					showDialog(R.layout.save_connection_dialog);
				} else
					Toast.makeText(Login.this,
							"Please enter a valid IP address", 1).show();
			}
		});

	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog);
		saveConnectionDialog.ipAddress = ipAddress.getText().toString();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Log.v(" oncreatedialog", "in oncreatedialog");

		return saveConnectionDialog;

	}

	private void makeFullScreen() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	/** Return true if IPAdr is a valid ip adresse */
	public static boolean validIPAddress(String IPAdr) {
		StringTokenizer st = new StringTokenizer(IPAdr, ".");
		if (st.countTokens() != 4)
			return false;
		while (st.hasMoreTokens()) {
			try {
				int value = Integer.parseInt(st.nextToken());
				if (value < 0 || value > 255)
					return false;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

}
