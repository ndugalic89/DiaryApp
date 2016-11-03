package com.example.diaryapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	//private static DBHelper sInstance;
	
	private static final String DATABASE_NAME = "mydiary.db";
	private static final int DATABASE_VERSION = 20;

	public static final String DIARY_TABLE = "diary";
	public static final String MOOD_TABLE = "moods";

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_MOOD = "moodname";
	public static final String DIARY_COLUMN_IMAGEPATH = "imagePath";
	public static final String DIARY_COLUMN_ADDRESS = "address";
	public static final String DIARY_COLUMN_CITY = "city";
	public static final String DIARY_COLUMN_DATE = "date";
	public static final String DIARY_COLUMN_MOOD_ID = "mood_id";

	private static final String WHERE_ID_EQUALS = COLUMN_ID + " =?";

	private static final String CREATE_TABLE_DIARY = "CREATE TABLE "
			+ DIARY_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_DESCRIPTION + " TEXT, " + DIARY_COLUMN_IMAGEPATH
			+ " TEXT, " + DIARY_COLUMN_ADDRESS + " TEXT, " + DIARY_COLUMN_CITY
			+ " TEXT, " + DIARY_COLUMN_DATE + " DATE, " + DIARY_COLUMN_MOOD_ID + " INT, "
			+ "FOREIGN KEY(" + DIARY_COLUMN_MOOD_ID + ") REFERENCES "
			+ MOOD_TABLE + "(id) " + ")";

	private static final String CREATE_TABLE_MOOD = "CREATE TABLE "
			+ MOOD_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_MOOD + " TEXT" + ")";
	
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
	
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_DIARY);
		db.execSQL(CREATE_TABLE_MOOD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + DIARY_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + MOOD_TABLE);

		// create new tables
		onCreate(db);
	}
	

	public long createDiaryItemEntry(ModelDiary mdData){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(DIARY_COLUMN_ADDRESS, mdData.getAddress());
		cv.put(DIARY_COLUMN_CITY, mdData.getCity());
		cv.put(COLUMN_DESCRIPTION, mdData.getDescription());
		cv.put(DIARY_COLUMN_IMAGEPATH, mdData.getImagePath());
		cv.put(DIARY_COLUMN_DATE, this.df.format(mdData.getDate()));
		cv.put(DIARY_COLUMN_MOOD_ID, mdData.getMood().getId());
		return db.insert(DIARY_TABLE, null, cv);
			
	}
	
	public ArrayList<ModelDiary> getDiaryItems(){
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<ModelDiary> diaryList = new ArrayList<ModelDiary>();
		String sQuery = "SELECT d.id, d.description, date, imagePath, city, address, mood_id, m.moodname FROM diary d, moods m WHERE d.mood_id = m.id";
		Log.e("Nikola", "getAll query:" + sQuery);
		Cursor c = db.rawQuery(sQuery, null);
		Log.e("Nikola", "number of rows in cursor: " + c.getCount());
		 
		
		while(c.moveToNext()){
			ModelDiary md = new ModelDiary();
			MoodModel mm = new MoodModel();
			md.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
			md.setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
			md.setAddress(c.getString(c.getColumnIndex(DIARY_COLUMN_ADDRESS)));
			md.setCity(c.getString(c.getColumnIndex(DIARY_COLUMN_CITY)));
			md.setImagePath(c.getString(c.getColumnIndex(DIARY_COLUMN_IMAGEPATH)));
			try {
				md.setDate(this.df.parse(c.getString(c.getColumnIndex(DIARY_COLUMN_DATE))));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mm.setId(c.getInt(c.getColumnIndex(DIARY_COLUMN_MOOD_ID)));
			mm.setMood(c.getString(c.getColumnIndex(COLUMN_MOOD)));
			md.setMood(mm);
			
			diaryList.add(md);	
		}
		return diaryList;
	}
	
	public ModelDiary getSignleDiaryItem(ModelDiary diary) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
//	    String query = "SELECT diary.*, moods.* FROM " 
//			  	  + DIARY_TABLE 
//			  	  + " LEFT JOIN " + MOOD_TABLE + " ON diary.mood_id = moods._id where diary._id = ?";
	    String query = "SELECT d.id, d.description, date, imagePath, city, address, mood_id, m.moodname FROM diary d INNER JOIN moods m ON d.mood_id = m.id WHERE d.id = ?";
	    Log.e("Nikola", query);
	    Cursor rs = db.rawQuery(query, new String[] { diary.getId() + ""});
//	    List<ModelDiary> diaryItem = new ArrayList<ModelDiary>();
	    ModelDiary mDiary = new ModelDiary();
	    MoodModel mMood = new MoodModel();
	    Log.e("Nikola" , "number of rows in cursor: " + rs.getCount());
	    Log.e("Nikola" , "diary id: " + diary.getId());
	    
	    rs.moveToFirst();
	    mDiary.setId(rs.getInt(rs.getColumnIndex(COLUMN_ID)));
	    mDiary.setDescription(rs.getString(rs.getColumnIndex(COLUMN_DESCRIPTION)));
	    mDiary.setAddress(rs.getString(rs.getColumnIndex(DIARY_COLUMN_ADDRESS)));
	    mDiary.setCity(rs.getString(rs.getColumnIndex(DIARY_COLUMN_CITY)));
	    mDiary.setImagePath(rs.getString(rs.getColumnIndex(DIARY_COLUMN_IMAGEPATH)));
    	try {
    		mDiary.setDate(this.df.parse(rs.getString(rs.getColumnIndex(DIARY_COLUMN_DATE))));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	mMood.setId(rs.getInt(rs.getColumnIndex(DIARY_COLUMN_MOOD_ID)));
    	mMood.setMood(rs.getString(rs.getColumnIndex(COLUMN_MOOD)));
    	mDiary.setMood(mMood);
	    
	    Log.e("Nikola", "returning: " + mDiary.getCity() + "\n" + mDiary.getAddress() + "\n" + mDiary.getMood().getMood() + "\n" + mDiary.getDescription());
	    if(!rs.isClosed()){
	    	rs.close();
	    }
		return mDiary;
	}
	
	public int updateDiaryItem(ModelDiary md){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_DESCRIPTION, md.getDescription());
		cv.put(DIARY_COLUMN_MOOD_ID, md.getMood().getId());
		
		int retVal = db.update(DIARY_TABLE, cv, WHERE_ID_EQUALS, new String[] { String.valueOf(md.getId()) } );
		Log.e("Nikola", "update retVal: " + retVal);
		
		return retVal;
	}
	
	public void deleteDiaryItem(ModelDiary diary){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(DIARY_TABLE, WHERE_ID_EQUALS, new String[] { String.valueOf(diary.getId()) });
	}
	
	public long createMoodItemEntry(MoodModel mmData){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MOOD, mmData.getMood());
		
		return db.insert(MOOD_TABLE, null, cv);
	}
	
	public List<MoodModel> getAllMoods(){
		List<MoodModel> moodList = new ArrayList<MoodModel>();
//		String query = "SELECT * FROM " + MOOD_TABLE;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(MOOD_TABLE,
				new String[] { COLUMN_ID, COLUMN_MOOD }, null, null, null, null,
				null);
		
		
		while(c.moveToNext()){
			MoodModel mMood = new MoodModel();
			mMood.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
			mMood.setMood(c.getString(c.getColumnIndex(COLUMN_MOOD)));
			moodList.add(mMood);
			
		}
		return moodList;
	}
	
	
	public int updateMoodItem(MoodModel mm){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MOOD, mm.getMood());
	
		return db.update(MOOD_TABLE, cv, COLUMN_ID + " = ?", new String[] { String.valueOf(mm.getId()) });
	}
	
	
	
	public void deleteMoodItem(long mood_id){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(MOOD_TABLE, COLUMN_ID + " =?", new String[] { String.valueOf(mood_id) });
	}
	
	public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
	
	public void loadMoods(){
		SQLiteDatabase db = this.getWritableDatabase();
		MoodModel mood = new MoodModel("Feeling lazy");
		MoodModel mood2 = new MoodModel("Feeling sleepy");
		MoodModel mood3 = new MoodModel("Feeling excited");
		
		List<MoodModel> moodList = new ArrayList<MoodModel>();
		moodList.add(mood);
		moodList.add(mood2);
		moodList.add(mood3);
		
		for(MoodModel m : moodList){
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_MOOD, m.getMood());
			db.insert(MOOD_TABLE, null, cv);
		}
		
	}
	
}




