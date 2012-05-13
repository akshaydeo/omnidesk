/*

 */
package omniDesk.gui;

import java.io.FileInputStream;

import omniDesk.coverFlow.CoverAdapterView;
import omniDesk.coverFlow.CoverFlow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ImageView.ScaleType;

public class MainScreen extends Activity {
	private ViewFlipper mFlipper;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		makeFullScreen();
		setContentView(R.layout.main_screen);
		CoverFlow coverFlow;
		coverFlow = (CoverFlow)findViewById(R.id.coverflow);

		coverFlow.setAdapter(new ImageAdapter(this));

		mFlipper = ((ViewFlipper) this.findViewById(R.id.flipper));

		mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_up_in));
		mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_up_out));

		coverFlow.setOnItemSelectedListener(new CoverAdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(CoverAdapterView<?> parent, View view,
					int position, long id) {
				mFlipper.setDisplayedChild(position);
			}

			@Override
			public void onNothingSelected(CoverAdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		coverFlow.setOnItemLongClickListener(new CoverAdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(CoverFlow coverFlow, View view,
					int position, long id) {
				switch(position)
				{
				case 0:
					Intent getToTheConnectToRemotePCForm = new Intent(MainScreen.this,Login.class);
					startActivity(getToTheConnectToRemotePCForm);
					break;
				case 1:
					Intent getToTheSavedConnections = new Intent(MainScreen.this,SavedConnections.class);
					startActivity(getToTheSavedConnections);
					break;
				case 2:
					Intent getToTheSettings = new Intent(MainScreen.this,Settings.class);
					startActivity(getToTheSettings);
					break;
				case 3:
					Builder builder = new AlertDialog.Builder(MainScreen.this);
					builder.setTitle("Help");
					builder.setIcon(R.drawable.chotikey);
					builder.setView(LayoutInflater.from(MainScreen.this).inflate(R.layout.help,null));
					builder.setPositiveButton("Ok", null);
					builder.show(); 
					break;
				case 4:
					Builder builder1 = new AlertDialog.Builder(MainScreen.this);
					builder1.setTitle("About");
					builder1.setIcon(R.drawable.chota_cloud_comment);
					builder1.setView(LayoutInflater.from(MainScreen.this).inflate(R.layout.about,null));
					builder1.setPositiveButton("Ok", null);
					builder1.show(); 

					break;
				}
				return true;
			}
		});


		ImageAdapter coverImageAdapter =  new ImageAdapter(this);

		coverImageAdapter.createReflectedImages();

		coverFlow.setAdapter(coverImageAdapter);

		coverFlow.setSpacing(-15);
		coverFlow.setSelection(1, true);
		coverFlow.setAnimationDuration(1000);


	}
	private void makeFullScreen() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;

		private FileInputStream fis;

		private Integer[] mImageIds = {
				R.drawable.laptop,
				R.drawable.box,
				R.drawable.tools,
				R.drawable.books,
				R.drawable.cloud_comment
		};

		private ImageView[] mImages;

		public ImageAdapter(Context c) {
			mContext = c;
			mImages = new ImageView[mImageIds.length];
		}
		public boolean createReflectedImages() {
			//The gap we want between the reflection and the original image
			final int reflectionGap = 4;


			int index = 0;
			for (int imageId : mImageIds) {
				Bitmap originalImage = BitmapFactory.decodeResource(getResources(), 
						imageId);
				int width = originalImage.getWidth();
				int height = originalImage.getHeight();


				//This will not scale but will flip on the Y axis
				Matrix matrix = new Matrix();
				matrix.preScale(1, -1);

				//Create a Bitmap with the flip matrix applied to it.
				//We only want the bottom half of the image
				Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width, height/2, matrix, false);


				//Create a new bitmap with same width but taller to fit reflection
				Bitmap bitmapWithReflection = Bitmap.createBitmap(width 
						, (height + height/2), Config.ARGB_8888);

				//Create a new Canvas with the bitmap that's big enough for
				//the image plus gap plus reflection
				Canvas canvas = new Canvas(bitmapWithReflection);
				//Draw in the original image
				canvas.drawBitmap(originalImage, 0, 0, null);

				//Draw in the gap
				Paint deafaultPaint = new Paint();
				canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
				//Draw in the reflection
				canvas.drawBitmap(reflectionImage,0, height + reflectionGap, null);

				//Create a shader that is a linear gradient that covers the reflection
				Paint paint = new Paint(); 
				LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, 
						bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, 
						TileMode.CLAMP); 
				//Set the paint to use this shader (linear gradient)
				paint.setShader(shader); 
				//Set the Transfer mode to be porter duff and destination in
				paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN)); 
				//Draw a rectangle using the paint with our linear gradient
				canvas.drawRect(0, height, width, 
						bitmapWithReflection.getHeight() + reflectionGap, paint); 

				//----------------



				//===
				ImageView imageView = new ImageView(mContext);
				imageView.setImageBitmap(bitmapWithReflection);
				imageView.setLayoutParams(new CoverFlow.LayoutParams(60, 120));//(120, 180));
				imageView.setScaleType(ScaleType.MATRIX);

				mImages[index++] = imageView;

			}
			return true;
		}

		public int getCount() {
			return mImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			//Use this code if you want to load from resources
			ImageView i = new ImageView(mContext);
			i.setImageResource(mImageIds[position]);
			i.setLayoutParams(new CoverFlow.LayoutParams(90,90));//(130, 130));
			i.setScaleType(ImageView.ScaleType.CENTER_INSIDE); 

			//Make sure we set anti-aliasing otherwise we get jaggies
			BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
			drawable.setAntiAlias(true);
			return i;

			//return mImages[position];
		}
		/** Returns the size (0.0f to 1.0f) of the views 
		 * depending on the 'offset' to the center. */ 
		public float getScale(boolean focused, int offset) { 
			/* Formula: 1 / (2 ^ offset) */ 
			return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset))); 
		} 

	}
}
