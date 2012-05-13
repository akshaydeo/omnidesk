package omniDesk.errorReporting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorReport {

	public static void showErrorDialog(int ErrorCode,String Title,String ErrorMessage,Context currentContext)
	{
		AlertDialog errorDialog = new AlertDialog.Builder(currentContext).create();
		errorDialog.setTitle(Title);
		errorDialog.setMessage(ErrorMessage);
		errorDialog.setCancelable(true);
		errorDialog.setButton("OK",new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
		}
	});
		errorDialog.show();
	}
}
