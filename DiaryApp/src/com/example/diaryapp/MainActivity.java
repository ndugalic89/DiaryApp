package com.example.diaryapp;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private EditText editDescription;
	private ImageButton btnTakePicture, btnCheckIn, btnSave, btnNewFeeling;
	private ImageView imgView;
	private Spinner spinnerMood;
	private TextView textCount;

	public static final String GET_DIARY_ITEM = "com.example.diaryapp.diary_item";
	private static final int TAKE_PICTURE = 1888;
	private List<Address> address;
	private Double latitude, longitude;
	private String sAddress, sCity, imagePath;
	private DBHelper db;
	private ModelDiary mDiary = null;
	private MoodModel mMood = null;
	private ArrayAdapter<MoodModel> spinnerAdapter;
	private List<MoodModel> moodList;
	private boolean flgUpdate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_item);
		db = new DBHelper(this);

		editDescription = (EditText) findViewById(R.id.description_editText);
		imgView = (ImageView) findViewById(R.id.imageView);
		textCount = (TextView) findViewById(R.id.text_count_textView);
		btnTakePicture = (ImageButton) findViewById(R.id.btn_take_pic);
		btnCheckIn = (ImageButton) findViewById(R.id.btn_checkIn);
		btnSave = (ImageButton) findViewById(R.id.btn_save);
		btnNewFeeling = (ImageButton) findViewById(R.id.btn_new_feeling);
		spinnerMood = (Spinner) findViewById(R.id.mood_spinner);

		btnCheckIn.setOnClickListener(this);
		btnTakePicture.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnNewFeeling.setOnClickListener(this);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		editDescription.addTextChangedListener(textWatcherCount);

		if (db.getAllMoods().size() <= 0)
			db.loadMoods();

		getAllMoods();

		Intent extras = getIntent();
		if (extras.hasExtra("com.example.diaryapp.selectedItem")) {
			ModelDiary m = (ModelDiary) extras
					.getParcelableExtra("com.example.diaryapp.selectedItem");
			fillform(m);
		}

	}

	private void getAllMoods() {
		// TODO Auto-generated method stub
		moodList = db.getAllMoods();
		if (moodList != null) {
			spinnerAdapter = new ArrayAdapter<MoodModel>(this,
					android.R.layout.simple_spinner_dropdown_item, moodList);
			spinnerMood.setAdapter(spinnerAdapter);
			spinnerAdapter.notifyDataSetChanged();
		}
	}

	private void fillform(ModelDiary diary) {
		mDiary = db.getSignleDiaryItem(diary);
		editDescription.setText(mDiary.getDescription());
		int index = 0;

		for (int i = 0; i < moodList.size(); i++) {
			if (moodList.get(i).getId() == mDiary.getMood().getId()) {
				index = i;
				break;
			}
		}
		spinnerMood.setSelection(index);
		try {
			imgView.setImageBitmap(decodeBitmap(mDiary.getImagePath(), 100, 100));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toast.makeText(
				this,
				"City: " + mDiary.getCity() + "\n" + "Address: "
						+ mDiary.getAddress(), Toast.LENGTH_LONG).show();
		this.flgUpdate = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.action_add).setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.homeAsUp) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_checkIn:
			checkIn();
			break;
		case R.id.btn_take_pic:
			takePhoto();
			break;
		case R.id.btn_save:
			if (this.flgUpdate) {
				update();
			} else {
				save();
			}
			break;
		case R.id.btn_new_feeling:
			addNewFeeling();
			break;
		}
	}

	private void update() {
		// TODO Auto-generated method stub
		mDiary.setDescription(editDescription.getText().toString());
		MoodModel m = (MoodModel) spinnerMood.getSelectedItem();
		this.mDiary.setMood(m);
		db.updateDiaryItem(this.mDiary);
		Toast.makeText(this,
				"Item with id: " + this.mDiary.getId() + "is updated!",
				Toast.LENGTH_SHORT).show();

	}

	private void addNewFeeling() {
		// TODO Auto-generated method stub
		AlertDialog.Builder newMoodDialog = new AlertDialog.Builder(this);
		final EditText editText = new EditText(this);
		newMoodDialog.setTitle("Create new mood");
		newMoodDialog.setMessage("How do You feel?");
		newMoodDialog.setView(editText);
		newMoodDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						MoodModel mood = new MoodModel(editText.getText()
								.toString());
						db.createMoodItemEntry(mood);
						dialog.cancel();
						dialog.dismiss();
						getAllMoods();
					}
				});

		newMoodDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
						dialog.dismiss();
					}
				});
		newMoodDialog.show();
	}


	private final TextWatcher textWatcherCount = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			textCount.setText(String.valueOf(s.length()));
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	};

	private void checkIn() {
		// TODO Auto-generated method stub
		GPSTracker gps = new GPSTracker(this);
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		// check if GPS enabled
		if (gps.canGetLocation()) {

			this.latitude = gps.getLatitude();
			this.longitude = gps.getLongitude();

			try {
				address = geocoder.getFromLocation(this.latitude,
						this.longitude, 1);
				this.sAddress = address.get(0).getAddressLine(0);
				this.sCity = address.get(0).getLocality();
				Toast.makeText(
						this.getApplicationContext(),
						"Your Location is - \naddress: " + this.sAddress
								+ "\ncity: " + this.sCity, Toast.LENGTH_LONG)
						.show();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				gps.stopUsingGPS();
			}

		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}
	}

	public Uri setImageUri() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/DCIM/Camera", sPhotoName());
		Uri imgUri = Uri.fromFile(file);
		this.imagePath = file.getAbsolutePath();
		return imgUri;
	}

	private void takePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
		startActivityForResult(intent, TAKE_PICTURE);
	}

	public String getImagePath() {
		return imagePath;
	}

	private String sPhotoName() {
		// TODO Auto-generated method stub
		return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
				.format(new Date()) + "pic.jpg";
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != Activity.RESULT_CANCELED) {
			if (requestCode == TAKE_PICTURE) {
				this.imagePath = getImagePath();
				imgView.setImageBitmap(decodeBitmap(this.imagePath, 100, 100));
				Toast.makeText(this, this.imagePath, Toast.LENGTH_LONG).show();
			} else {
				super.onActivityResult(requestCode, resultCode, data);
			}
		}
	}

	public static Bitmap decodeBitmap(String filePath, int reqWidth,
			int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateBitmapSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public static int calculateBitmapSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int size = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			while ((halfHeight / size) >= reqHeight
					&& (halfWidth / size) >= reqWidth) {
				size *= 2;
			}
		}

		return size;
	}

	public void save() {
		mDiary = new ModelDiary();
		mMood = new MoodModel();
		mMood = (MoodModel) spinnerMood.getSelectedItem();
		mDiary.setDescription(editDescription.getText().toString().trim());
		mDiary.setAddress(this.sAddress);
		mDiary.setCity(this.sCity);
		mDiary.setImagePath(this.imagePath);
		mDiary.setLatitude(this.latitude);
		mDiary.setLongitude(this.longitude);
		mDiary.setDate(new Date());
		mDiary.setMood(mMood);
		db.createDiaryItemEntry(mDiary);
		Toast.makeText(this, "Added to Diary", Toast.LENGTH_LONG).show();
	}
}
