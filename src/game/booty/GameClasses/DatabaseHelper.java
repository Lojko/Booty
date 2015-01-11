package game.booty.GameClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "BootyDB";
	private static final int DATABASE_VERSION = 1;
	
	private static final String KEY_ACHIEVEMENT = "Achievement";
	private static final String KEY_DESCRIPTION = "Description";
	private static final String KEY_COUNT = "Count";
	private static final String KEY_UNLOCKCOUNT = "UnlockCount";
	private static final String KEY_UNLOCKED = "Unlocked";
	private static final String KEY_IMAGE = "Image";
	private static final String KEY_ROWID = "ID";
	private static final String UPDATE_TAG = "Updating Database";
	
	private static DatabaseHelper instance = null;
	
	//Create Database procedure
	private static final String DATABASE_CREATE =
			"CREATE TABLE Achievements ("
					+ KEY_ROWID + " INTEGER primary key autoincrement, "
					+ KEY_ACHIEVEMENT + " TEXT not null, "
					+ KEY_DESCRIPTION + " TEXT not null, "
					+ KEY_COUNT + " INTEGER DEFAULT 0, "
					+ KEY_UNLOCKCOUNT + " INTEGER DEFAULT 0, "
					+ KEY_UNLOCKED + " INTEGER DEFAULT 0, "
					+ KEY_IMAGE + " TEXT not null)";
	
	private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	public static DatabaseHelper getInstance(Context context)
	{
		if(instance == null) {
			instance = new DatabaseHelper(context);
		}
		
		return instance;
	}
	
	public String getDbName() {
		return DATABASE_NAME;
	}
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        createAchievement(db, "Ready to Booty!", "Completed the Tutorial", 1, "iconachievementtutorial");
        createAchievement(db, "Bronze Booty!", "Found 50 Booty!", 50, "iconachievementbronzebooty");
        createAchievement(db, "Silver Booty!", "Found 150 Booty!", 150, "iconachievementsilverbooty");
        createAchievement(db, "Gold Booty!", "Found 300 Booty!", 300, "iconachievementgoldbooty");
        createAchievement(db, "Bronze Booty Winner!", "Won 50 Games of Booty!", 50, "iconachievementbronzewinner");
        createAchievement(db, "Silver Booty Winner!", "Won 150 Games of Booty!", 150, "iconachievementsilverwinner");
        createAchievement(db, "Gold Booty Winner!", "Won 300 Games of Booty!", 300, "iconachievementgoldwinner");
        createAchievement(db, "Bootylicious", "Won a game without having any of your booty found!", 1, "iconachievementbootylicious");
        createAchievement(db, "Bootyfied", "Found an opponent's booty after chain finding two of their traps!", 1, "iconachievementbootyfied");
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion)
    {
        Log.w(UPDATE_TAG, "Upgrading database from version " + oldVersion + " to "
              + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS Achievements");
        onCreate(db);
    }
    
	private static long createAchievement(SQLiteDatabase db, String achievementName, String description, int unlockCount, String image)
	{
		ContentValues databaseValues = new ContentValues();
		databaseValues.put(KEY_ACHIEVEMENT, achievementName);
		databaseValues.put(KEY_DESCRIPTION, description);
		databaseValues.put(KEY_COUNT, 0);
		databaseValues.put(KEY_UNLOCKCOUNT, unlockCount);
		databaseValues.put(KEY_UNLOCKED, 0);
		databaseValues.put(KEY_IMAGE, image);
		
		return db.insert("Achievements", null, databaseValues);
	}
}