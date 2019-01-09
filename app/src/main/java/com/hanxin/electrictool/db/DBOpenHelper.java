package com.hanxin.electrictool.db;

import java.util.ArrayList;
import java.util.List;

import com.hanxin.electrictool.entity.ElectricData;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASENAME = "electric.db"; // 数据库名称
	private static final int DATABASEVERSION = 2;// 数据库版本,大于0

	public DBOpenHelper(Context context) {
		super(context, DATABASENAME, null, DATABASEVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE if not exists electric (id integer primary key autoincrement,begintime text, result text,user text)");// 执行有更改的sql语句
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS electric");
		onCreate(db);

	}

	public void save(ElectricData electricData) {
		// 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(
				"insert into electric (begintime,result,user) values(?,?,?)",
				new Object[] { electricData.getBegin_time(),
						electricData.getResult(), electricData.getUser() });
	}

	public void deleteSportsData(ElectricData electricData) {
		// 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		SQLiteDatabase db = getWritableDatabase();
		// 删除SQL语句
		String sql = "delete from electric where id=1";

		// 执行SQL语句
		db.execSQL(sql);

		// db.close();
	}

	public void deleteFirstElectricData() {
		// 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		SQLiteDatabase db = getWritableDatabase();
		// 删除SQL语句
		String sql = "delete from electric where id=1";

		// 执行SQL语句
		db.execSQL(sql);

		// db.close();
	}

	private void update() {

		SQLiteDatabase db = getWritableDatabase();
		// 修改SQL语句
		String sql = "update electric set snumber = 654321 where id = 1";

		// 执行SQL
		db.execSQL(sql);
		db.close();

	}

	public void updateElectricData(int id, String newDate) {
		SQLiteDatabase db = getWritableDatabase();
		// 修改SQL语句
		String sql = "update electric set begintime ='" + newDate
				+ "' where id = " + id;

		// 执行SQL
		db.execSQL(sql);
		db.close();

	}

	public ElectricData queryElectricDataById(int queryId) {
		ElectricData electricData = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select * from electric where id=" + queryId,
				null);// 得到游标

		if (c != null && c.moveToNext()) {

			int id = c.getInt(c.getColumnIndex("id"));
			String result = c.getString(c.getColumnIndex("result"));
			String time = c.getString(c.getColumnIndex("begintime"));
			String user = c.getString(c.getColumnIndex("user"));
			electricData = new ElectricData(time, result, user);
			electricData.id = id;
			Log.i("DBOpenHelper", id + time + result);
			c.close();
		}

		return electricData;
	}

	public ElectricData queryElectricData(String time) {
		ElectricData electricData = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(
				"select * from electric where begintime=" + time, null);// 得到游标

		if (c != null && c.moveToNext()) {

			int id = c.getInt(c.getColumnIndex("id"));
			String result = c.getString(c.getColumnIndex("result"));
			String user = c.getString(c.getColumnIndex("user"));
			electricData = new ElectricData(time, result, user);
			electricData.id = id;
			Log.i("DBOpenHelper", time + result);
			c.close();
		}

		return electricData;
	}

	public List<ElectricData> getAllElectricData() {
		List<ElectricData> list = new ArrayList<ElectricData>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select * from electric where id>-1", null);// 得到游标

		if (c != null) {
			while (c.moveToNext()) {
				int id = c.getInt(c.getColumnIndex("id"));
				String result = c.getString(c.getColumnIndex("result"));
				String begintime = c.getString(c.getColumnIndex("begintime"));
				String user = c.getString(c.getColumnIndex("user"));
				ElectricData electricData = new ElectricData(begintime, result,
						user);
				electricData.id = id;
				list.add(electricData);
				Log.i("DBOpenHelper", id + begintime + result + user);
			}
			c.close();
		}

		return list;
	}

	public long getCount() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from electric", null);
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
