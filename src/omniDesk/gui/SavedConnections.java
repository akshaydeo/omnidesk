package omniDesk.gui;

import static omniDesk.database.Constants.IP_ADDRESS;
import static omniDesk.database.Constants.NAME_OF_CONNECTION;
import static omniDesk.database.Constants.TABLE_NAME;
import java.util.ArrayList;
import omniDesk.database.SavedConnectionData;
import omniDesk.net.rdp.Common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SavedConnections extends Activity {

	ListView savedConnections;
	int lastSelectedItem = -1;
	private static String[] FROM = { NAME_OF_CONNECTION, IP_ADDRESS };
	private static String ORDER_BY = NAME_OF_CONNECTION + " DESC";
	SavedConnectionData events;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		makeFullScreen();
		setContentView(R.layout.saved_connections);
		events = new SavedConnectionData(this);
		savedConnections = (ListView) findViewById(R.id.savedConnectionListView);
		Cursor cursor = getEvents();
		showEvents(cursor);

		savedConnections
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Cursor cursor = getEvents();
						cursor.moveToPosition(arg2);
						SQLiteDatabase db = events.getWritableDatabase();
						final int selectedRecordNo = arg2;
						final String ipAddressToConnect = cursor.getString(1);
						AlertDialog errorDialog = new AlertDialog.Builder(
								SavedConnections.this).create();
						errorDialog.setTitle("Saved Connection: "
								+ cursor.getString(0));
						errorDialog.setMessage("IP address: "
								+ cursor.getString(1));
						errorDialog.setCancelable(true);
						errorDialog.setIcon(R.drawable.map_blue);
						errorDialog.setButton("Connect",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Common.ipAddress = ipAddressToConnect;
										ConnectivityManager mgr = (ConnectivityManager) getBaseContext()
												.getSystemService(
														Context.CONNECTIVITY_SERVICE);
										NetworkInfo netInfo = mgr
												.getActiveNetworkInfo();

										if (netInfo != null) {
											if (netInfo.getState() == NetworkInfo.State.CONNECTED
													&& netInfo.isAvailable()) {
												Intent intent = new Intent(
														SavedConnections.this,
														OmniDeskMain.class);
												startActivity(intent);
												finish();
											}

										} else {
											Toast
													.makeText(
															SavedConnections.this,
															"No Connectivity available",
															1).show();
										}
									}

								});
						errorDialog.setButton2("Delete",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Cursor cursor = getEvents();
										cursor.moveToPosition(selectedRecordNo);
										SQLiteDatabase db = events
												.getWritableDatabase();
										try {
											db
													.delete(
															TABLE_NAME,
															NAME_OF_CONNECTION
																	+ "= '"
																	+ cursor
																			.getString(0)
																	+ "'", null);
										} catch (Exception e) {
											// TODO: handle exception
										}
										cursor = getEvents();
										showEvents(cursor);
									}
								});
						errorDialog.show();
					}
				});
		events.close();

	}

	private Cursor getEvents() {
		// Perform a managed query. The Activity will handle closing
		// and re-querying the cursor when needed.
		SQLiteDatabase db = events.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null, null,
				ORDER_BY);
		startManagingCursor(cursor);
		return cursor;
	}

	private void makeFullScreen() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	private void showEvents(Cursor cursor) {
		// Stuff them all into a big string

		final LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ArrayList<String> strings = new ArrayList<String>();

		while (cursor.moveToNext()) {
			// Could use getColumnIndexOrThrow() to get indexes

			String name = cursor.getString(0);
			String ipAdd = cursor.getString(1);
			strings.add(name + "@" + ipAdd);
		}

		savedConnections.setAdapter(new ArrayAdapter<String>(this,
				R.layout.list_item, strings) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;

				if (null == convertView) {
					row = mInflater.inflate(R.layout.list_item, null);
				} else {
					row = convertView;
				}

				TextView tv = (TextView) row.findViewById(R.id.TextView01);
				tv.setText(getItem(position));

				return row;
			}
		});

	}
}
