package omniDesk.net.rdp;

import omniDesk.gui.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AnalogClock;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.view.View.OnClickListener;

public class SpecialKeyDialog extends Dialog {

	Context context;
	CheckBox ctlKey;
	CheckBox altKey;
	CheckBox shiftKey;
	Spinner characterSpinner;
	Spinner specialSpinner;
	Spinner functionSpinner;
	TextView Combination;
	Button sendButton;
	
	int scanCode;

	String[] CharacterKeys = { "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z" };

	int[] CharScanCode = { 0x1e, 0x30, 0x2e, 0x20, 0x12, 0x21, 0x22, 0x23,
			0x17, 0x24, 0x25, 0x26, 0x32, 0x31, 0x18, 0x19, 0x10, 0x13, 0x1f,
			0x14, 0x16, 0x2f, 0x11, 0x2d, 0x15, 0x2c };

	String[] SpecialKeys = { "Delete", "tab", "Backspace", "Esc", "Page Up",
			"Page Down", "Home", "End", "Print Screen", "Scroll Lock",
			"Arrow Up", "Arrow Down", "Arrow Left", "Arrow Right" };

	int[] SpecialScanCode = { 0xd3, 0x0f, 0x03, 0x01, 0xc9, 0xd1, 0x47, 0xcf,
			0x37, 0x48, 0xc8, 0xd0, 0xcb, 0xcd };

	String[] FunctionKeys = { "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8",
			"F9", "F10", "F11", "F12" };

	int[] FunctionScanCode = { 0x3b, 0x3c, 0x3d, 0x3e, 0x3f, 0x40, 0x41, 0x42,
			0x43, 0x44, 0x57, 0x58 };

	public SpecialKeyDialog(Context context) {
		super(context);
		this.context = context;
		setOwnerActivity((Activity) context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.special);
		setTitle("Special keys");
	
		ctlKey = (CheckBox) findViewById(R.id.Ctl);
		altKey = (CheckBox) findViewById(R.id.Alt);
		shiftKey = (CheckBox) findViewById(R.id.Shilft);

		characterSpinner = ((Spinner) findViewById(R.id.characters));
		specialSpinner = ((Spinner) findViewById(R.id.Special));
		functionSpinner = ((Spinner) findViewById(R.id.Function));

		Combination = (TextView) findViewById(R.id.combination);
		sendButton = ((Button) findViewById(R.id.send));

		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// Common.inputHandler.sendCtlAltDel();
				Common.inputHandler.sendKeyCombinations(altKey.isChecked(),
						ctlKey.isChecked(), shiftKey.isChecked(), scanCode);
				dismiss();
			}

		});

		ArrayAdapter<String> charAdapter = new ArrayAdapter<String>(
				getOwnerActivity(), android.R.layout.simple_spinner_item);
		characterSpinner.setAdapter(charAdapter);
		populateCharacterKeys(charAdapter);

		ArrayAdapter<String> specialAdapter = new ArrayAdapter<String>(
				getOwnerActivity(), android.R.layout.simple_spinner_item);
		specialSpinner.setAdapter(specialAdapter);
		populateSpecialKeys(specialAdapter);

		ArrayAdapter<String> functionAdapter = new ArrayAdapter<String>(
				getOwnerActivity(), android.R.layout.simple_spinner_item);
		functionSpinner.setAdapter(functionAdapter);
		populateFunctionkeys(functionAdapter);

		characterSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// Toast.makeText(context, "arg0 : "+ arg2 + "arg3 : " +
						// arg3, 1).show();
						String text = "";
						Combination.setText("");
						if (shiftKey.isChecked())
							text += "Shift + ";
						if (altKey.isChecked())
							text += "Alt + ";
						if (ctlKey.isChecked())
							text += "Ctl + ";
						text += CharacterKeys[arg2];
						scanCode = CharScanCode[arg2];
						Combination.setText(text);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

		specialSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				String text = "";
				Combination.setText("");
				if (shiftKey.isChecked())
					text += "Shift + ";
				if (altKey.isChecked())
					text += "Alt + ";
				if (ctlKey.isChecked())
					text += "Ctl + ";
				text += SpecialKeys[arg2];
				scanCode = SpecialScanCode[arg2];
				Combination.setText(text);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		functionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				String text = "";
				Combination.setText("");
				if (shiftKey.isChecked())
					text += "Shift + ";
				if (altKey.isChecked())
					text += "Alt + ";
				if (ctlKey.isChecked())
					text += "Ctl + ";
				text += FunctionKeys[arg2];
				scanCode = FunctionScanCode[arg2];
				Combination.setText(text);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		((Button) findViewById(R.id.ctlc))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Common.inputHandler.sendKeyCombinations(false, true,
								false, 0x2e);
						dismiss();
					}

				});

		((Button) findViewById(R.id.ctlx))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Common.inputHandler.sendKeyCombinations(false, true,
								false, 0x2d);
						dismiss();
					}

				});

		((Button) findViewById(R.id.ctlv))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Common.inputHandler.sendKeyCombinations(false, true,
								false, 0x2f);
						dismiss();
					}

				});

		((Button) findViewById(R.id.ctls))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Common.inputHandler.sendKeyCombinations(false, true,
								false, 0x1f);
						dismiss();
					}

				});

		((Button) findViewById(R.id.ctlz))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Common.inputHandler.sendKeyCombinations(false, true,
								false, 0x2c);
						dismiss();
					}

				});
		
		((Button) findViewById(R.id.alt_ctl_del))
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.inputHandler.sendKeyCombinations(true, true,
						false, 0xd3);
				dismiss();
			}

		});
		
		((Button) findViewById(R.id.altf4))
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.inputHandler.sendKeyCombinations(true, false,
						false, 0x3e);
				dismiss();
			}

		});

		((Button) findViewById(R.id.shift_del))
		.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.inputHandler.sendKeyCombinations(false, false,
						true, 0xd3);
				dismiss();
			}

		});


		super.onCreate(savedInstanceState);
	}

	private void populateSpecialKeys(ArrayAdapter<String> specialAdapter) {
		for (int i = 0; i < SpecialKeys.length; i++)
			specialAdapter.add(SpecialKeys[i]);
	}

	private void populateFunctionkeys(ArrayAdapter<String> functionAdapter) {

		for (int i = 0; i < 12; i++)
			functionAdapter.add(FunctionKeys[i]);
	}

	private void populateCharacterKeys(ArrayAdapter<String> adapter) {

		for (int i = 0; i < 26; i++)
			adapter.add(CharacterKeys[i]);

	}

}
