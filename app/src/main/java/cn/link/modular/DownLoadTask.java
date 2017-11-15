package cn.link.modular;

import java.io.File;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import android.util.Log;

import cn.link.beans.FileInfo;
import cn.link.common.MyMath;
/**
 * ��������
 * @author hanyu
 *
 */
public class DownLoadTask {
	
	private long timeId; 
	
	private Progress pro;//������
	
	private File file;//�����ļ��ֻ��ļ�
	
	private boolean run_flag;//���ر�־
	
	private boolean isDir;//�Ƿ����ļ��б�־
	
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
	 * ��ȡ������
	 * @return
	 */
	public Progress getProgress(){
		return pro;
	}
	
	/**
	 * ��ȡ�ֻ��ļ�
	 * @return
	 */
	public File getFile(){
		return file;
	}
	
	/**
	 * ��ȡ���б�־
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
