package cn.link.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * ���ݿ�sqlite������
 * 
 * @author hanyu
 * 
 */
public class MyDataBase extends SQLiteOpenHelper {
	
	private static SQLiteDatabase base;

	// �������
	public static final String CREATE_TABLE_TASK = "create table task("
			+ "id integer primary key, " + "fipath text,"
			+ "progress integer, " + " max integer,"+" isdir integer)";

	public static final String CREATE_TABLE_FILES = "create table files("
			+ "id bigint," + "name text," + "path text," + "isdir integer,"
			+ "length integer," + "current integer)";

	public MyDataBase(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	/**
	 * �������ݿ�ķ�����������ݿ��Ѵ�����÷�������ִ��
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_TASK);
		db.execSQL(CREATE_TABLE_FILES);
		Log.d("create database", "success");
	}

	/**
	 * ���ݿ������ķ���
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * ��ȡ�ɶ�д�����ݿ�ʵ��
	 */
	@Override
	public SQLiteDatabase getWritableDatabase() {
		if(null==MyDataBase.base)
			MyDataBase.base = super.getWritableDatabase();
		return MyDataBase.base;
	}

}
