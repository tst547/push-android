package cn.link.data.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.link.beans.FileInfo;
import cn.link.modular.DownLoadTask;
import android.content.ContentValues;
import android.database.Cursor;

public class DownLoadTaskDao extends DaoSupport{

	/**
	 * 更新下载进度
	 * @param dt
	 * @return
	 */
	public boolean updateTask(DownLoadTask dt){
		ContentValues values = new ContentValues();
		values.put("progress", dt.getProgress().getProgress());
		values.put("max", dt.getProgress().getMax());
		super.update("task", values, "id=?", new String[]{String.valueOf(dt.getTimeId())});
		List<FileInfo> ls_fi = dt.getProgress().getFls();
		if(ls_fi.size()>1){
			for(FileInfo fi : ls_fi){
				ContentValues fiValues = new ContentValues();
				fiValues.put("current", fi.getCurrent());
				super.update("files", fiValues, "id=? and name=?", new String[]{String.valueOf(dt.getTimeId()),fi.getName()});
			}
		}else{
			FileInfo fi = ls_fi.get(0);
			ContentValues fiValues = new ContentValues();
			fiValues.put("current", fi.getCurrent());
			super.update("files", fiValues, "id=? and name=?", new String[]{String.valueOf(dt.getTimeId()),fi.getName()});
		}
		return true;
		
	}
	/**
	 * 保存下载进度信息
	 * @param dt
	 * @return
	 */
	public boolean saveTask(DownLoadTask dt){
		ContentValues values = new ContentValues();
		values.put("progress", dt.getProgress().getProgress());
		values.put("max", dt.getProgress().getMax());
		values.put("id", dt.getTimeId());
		values.put("fipath", dt.getFile().getPath());
		values.put("isdir", dt.isDir());
		super.insert("task", values);
		List<FileInfo> ls_fi = dt.getProgress().getFls();
		if(ls_fi.size()>1){
			for(FileInfo fi : ls_fi){
				ContentValues fiValues = new ContentValues();
				fiValues.put("id", dt.getTimeId());
				fiValues.put("name", fi.getName());
				fiValues.put("path", fi.getPath());
				fiValues.put("length", fi.getLength());
				fiValues.put("current", fi.getCurrent());
				fiValues.put("isdir", 0);
				super.insert("files", fiValues);
			}
		}else{
			FileInfo fi = ls_fi.get(0);
			ContentValues fiValues = new ContentValues();
			fiValues.put("id", dt.getTimeId());
			fiValues.put("name", fi.getName());
			fiValues.put("path", fi.getPath());
			fiValues.put("length", fi.getLength());
			fiValues.put("current", fi.getCurrent());
			fiValues.put("isdir", 0);
			super.insert("files", fiValues);
		}
		return true;
	}
	
	/**
	 * 删除指定下载任务信息
	 * @param dt
	 * @return
	 */
	public boolean deleteTask(DownLoadTask dt){
		super.delete("task", "id=?", new String[]{String.valueOf(dt.getTimeId())});
		super.delete("files", "id=?", new String[]{String.valueOf(dt.getTimeId())});
		return true;
	}

	/**
	 * 删除所有下载信息
	 * @return
	 */
	public boolean deleteAllTask(){
		super.delete("task", null, null);
		super.delete("files", null, null);
		return true;
	}
	
	/**
	 * 查询
	 * @return
	 */
	public List<DownLoadTask> getTaskList(){
		Cursor cursor = db.query("task", null, null, null, null, null, null);
		List<DownLoadTask> ls = new ArrayList<DownLoadTask>();
		if(cursor.moveToFirst()){
			do{
				Cursor ficursor = db.query("files", null, "id=?", new String[]{cursor.getString(cursor.getColumnIndex("id"))}, null, null, null);
				List<FileInfo> fls = new ArrayList<FileInfo>();
				if(ficursor.moveToFirst()){
					do{
						String name = ficursor.getString(ficursor.getColumnIndex("name"));
						String path = ficursor.getString(ficursor.getColumnIndex("path"));
						long length = ficursor.getLong(ficursor.getColumnIndex("length"));
						long current = ficursor.getLong(ficursor.getColumnIndex("current"));
						fls.add(new FileInfo(name, path, false, length, current));
					}while(ficursor.moveToNext());
				}
				String fipath = cursor.getString(cursor.getColumnIndex("fipath"));
				long timeId = cursor.getLong(cursor.getColumnIndex("id"));
				long max = cursor.getLong(cursor.getColumnIndex("max"));
				long progress = cursor.getLong(cursor.getColumnIndex("progress"));
				boolean isDir = cursor.getInt(cursor.getColumnIndex("isdir"))==1?true:false;
				ls.add(new DownLoadTask(fls, new File(fipath),max,progress,timeId,isDir));
			}while(cursor.moveToNext());
		}
		return ls;
	}
}
