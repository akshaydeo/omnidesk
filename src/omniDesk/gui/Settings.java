package omniDesk.gui;

import omniDesk.net.rdp.Options;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

public class Settings extends Activity {

	Spinner resolution;
	Spinner colorDepth;
	CheckBox compression;
	CheckBox theming;
	CheckBox wallpaper;
	protected boolean disable_wallpaper;
	protected boolean disable_theming;
	protected int width;
	protected int height;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		
		makeFullScreen();
		setContentView(R.layout.settings);
		resolution = (Spinner) findViewById(R.id.widget98);
		resolution.setSelection(3);
		ArrayAdapter resolutionAdaptor = ArrayAdapter.createFromResource(this,
				R.array.resolutionArray, android.R.layout.simple_spinner_item);
		resolution.setAdapter(resolutionAdaptor);
		width=Options.width;
		height=Options.height;
		resolution.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch(arg2-1)
				{
				case 0:
					width=480;
					height=320;
					break;
				case 1:
					width=640;
					height=480;
					break;
				case 2:
					width=800;
					height=600;
					break;
				case 3:
					width=1024;
					height=748;
					break;
				case 4:
					width=1280;
					height=600;
					break;
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
/*		colorDepth=(Spinner) findViewById(R.id.depth);
		colorDepth.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch(arg2)
				{
				case 0:
					Options.server_bpp=8;
					break;
				case 1:
					Options.server_bpp=16;
					break;
				case 2:
					Options.server_bpp=24;
					break;
				case 3:
					Options.server_bpp=32;
					break;
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		colorDepth = (Spinner) findViewById(R.id.depth);
		ArrayAdapter colorDepthAdaptor = ArrayAdapter.createFromResource(this,
				R.array.colorBits, android.R.layout.simple_spinner_item);
		colorDepth.setAdapter(colorDepthAdaptor);

		compression = (CheckBox) findViewById(R.id.compression);
		compression.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked)
					Options.bitmap_compression = true;
				else
					Options.bitmap_compression = false;

			}
		});
	 */
		wallpaper = (CheckBox) findViewById(R.id.wallpaper);
		disable_wallpaper=Options.disable_wallpaper;
		wallpaper.setChecked(disable_wallpaper);
		wallpaper.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked)
					disable_wallpaper = true;
				else
					disable_wallpaper = false;

			}
		});

		theming = (CheckBox) findViewById(R.id.theme);
		disable_theming=Options.disable_theming;
		theming.setChecked(disable_theming);
		theming.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked)
					disable_theming = true;
				else
					disable_theming = false;

			}
		});
		
		Button save=(Button) findViewById(R.id.save );
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Options.disable_theming=disable_theming;
				Options.disable_wallpaper=disable_wallpaper;
				Options.height=height;
				Options.width=width;
				finish();
				
			}
		});
		
		Button cancel=(Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	}

	private void makeFullScreen() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
}