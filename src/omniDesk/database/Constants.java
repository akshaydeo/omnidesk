package omniDesk.database;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns {
   public static final String TABLE_NAME = "events";

   // Columns in the Events database
   public static final String NAME_OF_CONNECTION = "nickname";
   public static final String IP_ADDRESS = "ipaddress";
}
