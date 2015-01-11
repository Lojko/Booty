package game.booty.GameClasses;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class Achievements 
{	
	private DatabaseHelper m_DBHelper;
	private static SQLiteDatabase m_DB;
	private static SQLiteStatement m_IncrementStatement;
	private static SQLiteStatement m_UnlockStatement;
	
	private static final String DATABASE_TABLE = "Achievements";
	
	private static Achievements instance = null;
	
	private static Context m_Context;
	
	/*private static final String CHECK_UNLOCK =
			"SELECT Image FROM " + DATABASE_TABLE + 
			" WHERE Achievement = ? AND Count >= UnlockCount AND Unlocked = 0";*/
	
	private static final String CHECK_UNLOCK =
			"SELECT Image FROM " + DATABASE_TABLE + 
			" WHERE Achievement = ? AND Count < UnlockCount AND Unlocked = 0";
	
	private Achievements(Context ctx) {
		m_Context = ctx;
	}
	
	public static Achievements getInstance(Context context) {

		if(instance == null) {
			instance = new Achievements(context);
		}
		else {
			m_Context = context;
		}
		
		return instance;
	}
	
	//Achievements Constructor, Sets up Increment and Unlock statements
	public Achievements open() throws android.database.SQLException {
		m_DBHelper = DatabaseHelper.getInstance(m_Context);
		m_DB = m_DBHelper.getWritableDatabase();
		
		//m_IncrementStatement = m_DB.compileStatement("UPDATE " + DATABASE_TABLE + " SET Count = Count + 1 WHERE Achievement = ? AND Unlocked = 0");
		//m_UnlockStatement = m_DB.compileStatement("UPDATE " + DATABASE_TABLE + " SET Unlocked = 1 WHERE Achievement = ? AND Count >= UnlockCount");
		
		//Testing
		m_IncrementStatement = m_DB.compileStatement("UPDATE " + DATABASE_TABLE + " SET Count = Count + 0 WHERE Achievement = ? AND Unlocked = 0");
		m_UnlockStatement = m_DB.compileStatement("UPDATE " + DATABASE_TABLE + " SET Unlocked = 0 WHERE Achievement = ? AND Count = UnlockCount");
		return this;
	}
	
	public boolean CheckDatabaseExists() {
		File database = m_Context.getDatabasePath(m_DBHelper.getDbName() + ".db");

		if (!database.exists()) {
			return false;
		} 

		return true;
	}
	
	public void Close() {
		m_DBHelper.close();
	}
	
	public String incrementAchievement(String achievementName) {	
		m_IncrementStatement.bindString(1, achievementName);
		m_IncrementStatement.execute();
		
		return checkUnlockAchievement(achievementName);
	}
	
	private String checkUnlockAchievement(String achievementName) {
		Cursor check = m_DB.rawQuery(CHECK_UNLOCK, new String[] {achievementName});
		check.moveToFirst();
		
		//if(check !=null && check.getCount() > 0) {
		//TODO: Test Achievements remove always unlock
		//if(check !=null && check.getCount() > 0) {
			String imageName = check.getString(check.getColumnIndex("Image"));
			check.close();
			//m_UnlockStatement.bindString(1, achievementName);
			//m_UnlockStatement.execute();
			return imageName;
		//}
		//else {
			//check.close();
			//return null;
		//}
	}
}
