package cn.link.modular;

import java.io.File;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import android.util.Log;

import cn.link.beans.FileInfo;
import cn.link.common.MyMath;
/**
 * 下载任务
 * @author hanyu
 *
 */
public class DownLoadTask {
	
	private long timeId; 
	
	private Progress pro;//进度条
	
	private File file;//下载文件手机文件
	
	private boolean run_flag;//下载标志
	
	private boolean isDir;//是否是文件夹标志
	
	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	public long getTimeId() {
		return timeId;
	}

	public void setTimeId(long timeId) {
		this.timeId = timeId;
	}

	/**
	 * 获取进度条
	 * @return
	 */
	public Progress getProgress(){
		return pro;
	}
	
	/**
	 * 获取手机文件
	 * @return
	 */
	public File getFile(){
		return file;
	}
	
	/**
	 * 获取运行标志
	 * @return
	 */
	public boolean isRun(){
		return run_flag;
	}

	public void Run(){
		this.run_flag = true;
	}
	
	public void pause(){
		run_flag = false;
	}
	
	public boolean isFileExists(){
		if(null!=file)
			if(file.exists())
				return true;
		return false;
	}

	public DownLoadTask(List<FileInfo> fls,File file,long timeId,boolean isDir){
		long size = 0;
		for(FileInfo f:fls){
			size = MyMath.add(size, f.getLength());
		}
		Log.e("task length", "name:"+file.getName()+"  size:"+size);
		this.isDir = isDir;
		this.pro = new Progress(fls, 0, size);
		this.file = file;
		this.timeId = timeId;
	}
	
	public DownLoadTask(List<FileInfo> fls,File file,long length,long current,long timeId,boolean isDir){
		long size = 0;
		for(FileInfo f:fls){
			size = MyMath.add(size, f.getLength());
		}
		Log.e("task length", "name:"+file.getName()+"  size:"+size+" length:"+length+" current:"+current);
		this.isDir = isDir;
		this.pro = new Progress(fls, current, length);
		this.file = file;
		this.timeId = timeId;
	}

}
