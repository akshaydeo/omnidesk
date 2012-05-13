/***
 * Excerpted from "Hello, Android!",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband for more book information.
***/
package omniDesk.database;

import static omniDesk.database.Constants.TABLE_NAME;
import static omniDesk.database.Constants.IP_ADDRESS;
import static omniDesk.database.Constants.NAME_OF_CONNECTION;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SavedConnectionData extends SQLiteOpenHelper {
   private static final String DATABASE_NAME = "omniDeskSavedConnectiontestTest2.db";
   private static final int DATABASE_VERSION = 1;

   /** Create a helper object for the Events database */
   public SavedConnectionData(Context ctx) { 
      super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
   }

   @Override
   public void onCreate(SQLiteDatabase db) { 
      db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + NAME_OF_CONNECTION
            + " TEXT PRIMARY KEY," + IP_ADDRESS + " TEXT NOT NULL);");
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, 
         int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
      onCreate(db);
   }
}