package cn.link.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.link.box.App;

import android.util.Log;

public class FileUtil {

	/**
	 * 创建文件夹
	 * 
	 * @param filePath
	 */
	public static File createDirs(String filePath) {
		File file = new File(App.path.getPath() + "/" + filePath);
		if (!file.exists()) {
			file.mkdirs();
		} else {
			int num = 1;
			while (!file.exists()) {
				file = new File(App.path.getPath() + "/" + "(" + num + ")"
						+ file.getName());
				num++;
			}
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 创建文件
	 * 
	 * @param filePath
	 */
	public static File createFile(String filePath) {
		File fl = new File(App.path.getPath() + "/" + filePath);
		try {
			if (!fl.exists()) {
				fl.createNewFile();
			} else {
				int num = 1;
				while (!fl.exists()) {
					fl = new File(App.path.getPath() + "/" + "(" + num + ")"
							+ filePath);
					num++;
				}
				fl.createNewFile();
			}
		} catch (IOException e) {
			Log.e("createFile", e.getMessage());
			return null;
		}
		return fl;
	}

	/**
	 * 获取指定文件大小(单位：字节)
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static long getFileSize(File file){
		if (file == null) {
			return 0;
		}
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				size = fis.available();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return size;
	}
}
