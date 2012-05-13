package omniDesk.gui;


import static omniDesk.database.Constants.IP_ADDRESS;
import static omniDesk.database.Constants.NAME_OF_CONNECTION;
import static omniDesk.database.Constants.TABLE_NAME;
import omniDesk.database.SavedConnectionData;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SaveConnectionDialog extends Dialog{
	Context context;
	Button save;
	Button cancel;
	EditText nameOfConnection;
	String ipAddress;
	boolean flag=true;
	SavedConnectionData events;
	public SaveConnectionDialog(Context context) {
		super(context);
		this.context = context;
		setOwnerActivity((Activity) context);
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.save_connection_dialog);
		setTitle("Save connection information");
		save = (Button) findViewById(R.id.Button01);
		cancel= (Button) findViewById(R.id.Button02);
		nameOfConnection = (EditText) findViewById(R.id.widget29);
		events = new SavedConnectionData(context); 
		save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
			        
			    	 addEvent(nameOfConnection.getText().toString(),ipAddress); 
			    	 if(flag)
			    		 dismiss();
			    	 flag=true;
			      } 
				 catch (SQLiteConstraintException e) {
					 Toast.makeText(context,"Every connection should have different name", 10).show();
					 nameOfConnection.setText("");
				}
				 finally {
			         events.close(); 
			      }	
				
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
					dismiss();
			}
		});
		
		
	}
	   private void addEvent(String nameOfTheConnection,String ipAddress) {
		      // Insert a new record into the Events data source.
		      // You would do something similar for delete and update.
		      SQLiteDatabase db = events.getWritableDatabase();
		      ContentValues values = new ContentValues();
		      if(nameOfTheConnection.equalsIgnoreCase("") || ipAddress.equalsIgnoreCase(""))
		      {
		    	  Toast.makeText(context,"insert valid name and ipaddress", 10).show();
		    	  flag=false;
		    	  return;
		      }
		      values.put(NAME_OF_CONNECTION,nameOfTheConnection);
		      values.put(IP_ADDRESS,ipAddress);
		      db.insertOrThrow(TABLE_NAME, null, values);
		   }


}
