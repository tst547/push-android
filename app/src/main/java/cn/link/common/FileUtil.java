package cn.link.common;

import java.io.File;
import java.io.IOException;

import cn.link.box.App;

import android.util.Log;

public class FileUtil {

	/**
	 * 创建文件夹
	 * 
	 * @param file
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
	 * @param file
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
}
