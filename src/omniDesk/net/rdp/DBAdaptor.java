/**
 * OmniDesk: Your Desktop Everywhere...
 * @omniDeksMain: DBadaptor helper class
 * @author: Mazze group
 */
package omniDesk.net.rdp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBAdaptor {

	public static final String KEY_NAME       = "name";
	public static final String KEY_HOST       = "host";
	public static final long   KEY_PORT       =  3389;
	public static final String KEY_USER       = "user";
	public static final String KEY_PASSWD     = "password";
	public static final String KEY_DOMAIN     = "domain";
	public static final long   KEY_COL_DEPTH  =  16;
	public static final String KEY_RESOLUTION = "resolution";
	
	public static final String DB_NAME        = "omniDesk";
	public static final String DB_TABLE       = "remotePCInfo";
	public static final int    DB_VERSION     = 3;
	
	public static final String DB_CREATE = "create table " + DB_TABLE + " ( "+ KEY_NAME + " varchar(40), "+ KEY_HOST + " varchar(20), " 
	+ KEY_PORT + " number(10), "+ KEY_USER + " varchar(40), " + KEY_PASSWD + " varchar(40), " + KEY_DOMAIN + " varchar(40), "
	+ KEY_COL_DEPTH + " number(10), "+ KEY_RESOLUTION + " varchar(40));";
	
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public DBAdaptor(Context ctx)
	{
		this.context=ctx;
		DBHelper = new DatabaseHelper(context,null,null, 0);
	}
	//opens the database
	public DBAdaptor open()
	{
		try{
			if(this.db == null)
				db = DBHelper.getWritableDatabase();
		}
		catch(Exception e)
		{
			System.out.println("Error in opening database: "+e);
		}
		return this;
	}
	 //---closes the database---    
    public void close() 
    {
    	try{
    		if(this.db != null)
    			DBHelper.close();
    		this.db = null;
    	}
    	catch(Exception e)
    	{
    		System.out.println("Error in closing database: "+e);
    	}
    }
    
    //---insert a title into the database---
    public long insertTitle(String name, String host,int port,String user,String passwd,String domain,int colDepth,String resolution) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME,name);
        initialValues.put(KEY_HOST,host);
    //    initialValues.put(KEY_PORT,port);
        initialValues.put(KEY_USER,user);
        initialValues.put(KEY_PASSWD,passwd);
        initialValues.put(KEY_DOMAIN,domain);
	//	initialValues.put(KEY_COL_DEPTH,colDepth);
		initialValues.put(KEY_RESOLUTION,resolution);
        return db.insert(DB_TABLE, null, initialValues);
    }

    //---deletes a particular title---
    public boolean deleteTitle(String name) 
    {
        return db.delete(DB_TABLE, KEY_NAME + "=" + name, null) > 0;
    }

    //---retrieves all the titles---
    public Cursor getAllTitles() 
    {
        return db.query(DB_TABLE, new String[] {
        		KEY_NAME, 
        		}, 
                null, 
                null, 
                null, 
                null, 
                null);
    }
	private class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context,DB_NAME,null,DB_VERSION);
		}
	
		@Override
		public void onCreate(SQLiteDatabase arg0) {
			try{
				db.execSQL(DB_CREATE);
			}
			catch(SQLException e)
			{
				System.out.println("Error in creating database: "+e);
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			this.onCreate(db);	
		}
		
	}
	
}
