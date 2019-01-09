package com.hanxin.electrictool.db;

import java.util.ArrayList;
import java.util.List;

import com.hanxin.electrictool.entity.ElectricData;
import com.hanxin.electrictool.entity.UserData;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDBOpenHelper extends SQLiteOpenHelper {


	private static final String DATABASENAME = "user.db"; // 数据库名称
	private static final int DATABASEVERSION = 2;// 数据库版本,大于0

	public UserDBOpenHelper(Context context) {
		super(context, DATABASENAME, null, DATABASEVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE if not exists user (id integer primary key autoincrement,username text, password text,usertype text)");// 执行有更改的sql语句
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS user");
		onCreate(db);
	}

	public void save(String userName, String password,String type) {
		// 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("insert into user (username,password,usertype) values(?,?,?)",
				new Object[] { userName, password,type});
		db.close();
	}
	
	public void deleteUserDataByType(String type) {
		// 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		SQLiteDatabase db = getWritableDatabase();
		// 删除SQL语句
		String sql = "delete from user where usertype='"+type+"'";
		// 执行SQL语句
		db.execSQL(sql);

		db.close();
	}

	public void deleteUserData(String userName) {
		// 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		SQLiteDatabase db = getWritableDatabase();
		// 删除SQL语句
		String sql = "delete from user where username='" + userName+"'";

		// 执行SQL语句
		db.execSQL(sql);

		db.close();
	}


	public UserData queryUserData(String userName) {
		UserData userData = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select * from user where username=?", new String[]{userName});// 得到游标

		if (c != null && c.moveToNext()) {

			int id = c.getInt(c.getColumnIndex("id"));
			String passWord = c.getString(c.getColumnIndex("password"));
			String type = c.getString(c.getColumnIndex("usertype"));
			userData = new UserData(userName, passWord,type);

			Log.i("DBOpenHelper", userName + passWord+"/"+type);
			c.close();
		}

		return userData;
	}
	

	public UserData queryUserDataByType(String type) {
		UserData userData = null;
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.rawQuery("select * from user where usertype=?", new String[]{type});// 得到游标

		if (c != null && c.moveToNext()) {

			int id = c.getInt(c.getColumnIndex("id"));
			String passWord = c.getString(c.getColumnIndex("password"));
			String userName = c.getString(c.getColumnIndex("username"));
			userData = new UserData(userName, passWord,type);
			//userData.id = id;
			Log.i("DBOpenHelper", userName + passWord+"/"+type);
			c.close();
		}

		return userData;
	}

	public String queryUserPassword(String userName) {
		UserData userData = null;
		SQLiteDatabase db = getReadableDatabase();
		String passWord ="";
		// Cursor cursor = db.rawQuery("select * from city",null);//得到游标
		Cursor c = db.rawQuery(
				"select * from user where username=?", new String[]{userName});// 得到游标
		if (c != null && c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex("id"));
			passWord = c.getString(c.getColumnIndex("password"));
			//userData = new UserData(userName, passWord);
			//userData.id = id;
			Log.i("DBOpenHelper", userName + passWord);
			c.close();
		}
		return passWord;
	}
	

	public void updateUserDataByType(UserData userData,String type) {
		//UserData userData = null;
		SQLiteDatabase db = getReadableDatabase();
		// Cursor cursor = db.rawQuery("select * from city",null);//得到游标
		//Cursor c = db.rawQuery("select * from user where usertype=?", new String[]{type});// 得到游标
		//String sql = "update user set username = 654321 where usertype = type";
		//db.execSQL(sql);
        ContentValues values = new ContentValues();
        values.put("username", userData.userName);
        values.put("password", userData.passWord);
        
        db.update("user", values, "usertype=?", new String[]{type});
	}
	
	public List<UserData> getAllUserDatas() {
		List<UserData> list = new ArrayList<UserData>();
		SQLiteDatabase db = getReadableDatabase();
		// Cursor cursor = db.rawQuery("select * from city",null);//得到游标
		Cursor c = db.rawQuery("select * from user where id>-1", null);// 得到游标

		if (c != null) {
			while (c.moveToNext()) {
				int id = c.getInt(c.getColumnIndex("id"));
				String username = c.getString(c.getColumnIndex("username"));
				String password = c.getString(c.getColumnIndex("password"));
				String type = c.getString(c.getColumnIndex("usertype"));
				UserData userData = new UserData(username, password,type);
				 
				// electricData.id = id;
				list.add(userData);
				Log.i("DBOpenHelper", id + username + password+"/"+type);
			}
			c.close();
		}
		db.close();
		return list;
	}
	
	public List<UserData> getCommonUserDatas() {
		List<UserData> list = new ArrayList<UserData>();
		SQLiteDatabase db = getReadableDatabase();
		// Cursor cursor = db.rawQuery("select * from city",null);//得到游标
		Cursor c = db.rawQuery("select * from user where id>-1 and usertype=?", new String[]{"common"});// 得到游标

		if (c != null) {
			while (c.moveToNext()) {
				int id = c.getInt(c.getColumnIndex("id"));
				String username = c.getString(c.getColumnIndex("username"));
				String password = c.getString(c.getColumnIndex("password"));
				String type = c.getString(c.getColumnIndex("usertype"));
				UserData userData = new UserData(username, password,type);
				 
				// electricData.id = id;
				list.add(userData);
				Log.i("DBOpenHelper", id + username + password+"/"+type);
			}
			c.close();
		}
		db.close();
		return list;
	}
	
	public List<String> getAllUserNames() {
		List<String> list = new ArrayList<String>();
		SQLiteDatabase db = getReadableDatabase();
		// Cursor cursor = db.rawQuery("select * from city",null);//得到游标
		Cursor c = db.rawQuery("select * from user where id>-1 and usertype=?", new String[]{"common"});// 得到游标

		if (c != null) {
			while (c.moveToNext()) {
				int id = c.getInt(c.getColumnIndex("id"));
				String username = c.getString(c.getColumnIndex("username"));
				String password = c.getString(c.getColumnIndex("password"));
				String type = c.getString(c.getColumnIndex("usertype"));
				UserData userData = new UserData(username, password,type);
				 
				// electricData.id = id;
				list.add(username);
				Log.i("DBOpenHelper", id + username + password+"/"+type);
			}
			c.close();
		}
		db.close();
		return list;
	}

	public long getCount() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from user", null);
		cursor.moveToFirst();
		return cursor.getLong(0);
	}

	public void close() {
		SQLiteDatabase db = getReadableDatabase();
		if (db != null) {
			// 关闭数据库
			db.close();
			db = null;
		}
	}

}
