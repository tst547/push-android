package cn.link.modular;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import android.util.Log;
import cn.link.beans.FileInfo;
import cn.link.beans.Msg;
import cn.link.box.App;
import cn.link.common.FileUtil;
import cn.link.common.MyGson;
import cn.link.common.MyMath;
import cn.link.data.dao.DownLoadTaskDao;
/**
 * 
 * @author hanyu
 *
 */
public class DownLoader {
	private static DownLoader loader;
	
	private DownLoadTaskDao dtDao;

	private List<DownLoadTask> array_task;//?????????งา?
	private ArrayBlockingQueue<DownLoadTask> down_queue;//??????????????
	
	private boolean runDown;
	/**
	 * ??????
	 * @param res
	 * @return
	 */
	public static DownLoader getDownLoader(){
		if(null==loader)
			loader = new DownLoader();
		return loader;
	}
	
	public int DownLoadQueueSize(){
		return down_queue.size();
	}
	
	public ArrayBlockingQueue<DownLoadTask> DownLoadQueue(){
		return down_queue;
	}
	
	public List<DownLoadTask> TaskList(){
		return array_task;
	}
	
	/**
	 * ???????????
	 * @param fi
	 * @throws IOException 
	 */
	public DownLoadTask newTask(List<FileInfo> fls,String fileName,boolean isDir) throws IOException{
		File file = null;
		if(!isDir){
			file = FileUtil.createFile(fileName);
		}else{
			file = FileUtil.createDirs(fileName);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		DownLoadTask dt = new DownLoadTask(fls,file,Long.valueOf(sdf.format(date)),isDir);
		array_task.add(dt);
		dtDao.saveTask(dt);
		return dt;
	}
	
	/**
	 * ???????
	 * @param dt
	 * @return
	 */
	public boolean startTask(DownLoadTask dt){
		try {
			dt.Run();
			down_queue.put(dt);
		} catch (InterruptedException e) {
			Log.e("startTask", e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * ???????????
	 * @param dt
	 * @return
	 */
	public boolean removeTask(DownLoadTask dt){
		dt.pause();
		if(down_queue.size()>0)
			down_queue.remove(dt);
		array_task.remove(dt);
		dtDao.deleteTask(dt);
		return true;
	}
	
	private void tellHostFileMsg(FileInfo fi){
		App.sendMsg(MyGson.getString(new Msg("downfile", MyGson.getString(fi))));
	}
	
	private DownLoader(){
		this.down_queue = new ArrayBlockingQueue<DownLoadTask>(100);
		this.dtDao = new DownLoadTaskDao();
		this.array_task = dtDao.getTaskList();
		this.runDown = true;
		runDown run = new runDown();
		Thread th = new Thread(run);
		th.start();
	}
	
	class runDown implements Runnable{
		int bufferSizeKB = 512;
		int bufferSize = bufferSizeKB * 1024;
		SocketChannel sc;
		@Override
		public void run() {
			ByteBuffer buff = ByteBuffer.allocateDirect(bufferSize);
			int flag = 0;
			while(runDown){
				DownLoadTask dt = null;
				try {
					dt = down_queue.take();
					conn();
					List<FileInfo> fls = dt.getProgress().getFls();
					for(FileInfo fi:fls){
						Progress pro = dt.getProgress();
						File down_file = null;
						if(!dt.isDir()){
							down_file = dt.getFile();
							if(!down_file.exists()){
								down_file.createNewFile();
								if(fi.getCurrent()!=0){
									pro.setProgress(MyMath.subtract(pro.getProgress(), fi.getCurrent()));
									fi.setCurrent(0);
								}
							}
						}else{
							if(!dt.isFileExists())
								dt.getFile().mkdirs();
							down_file = new File(dt.getFile().getPath()+"/"+fi.getName());
							if(!down_file.exists()){
								down_file.createNewFile();
								if(fi.getCurrent()!=0){
									pro.setProgress(MyMath.subtract(pro.getProgress(), fi.getCurrent()));
									fi.setCurrent(0);
								}
							}
						}
						
						if(down_file.length()==fi.getLength()){
							continue;
						}else{
							tellHostFileMsg(fi);
						}
						
						FileOutputStream fos = new FileOutputStream(down_file);
						FileChannel fc = fos.getChannel();
						fc.position(fi.getCurrent());
						while(dt.isRun()&&fi.getCurrent()<fi.getLength()&&(flag=sc.read(buff))>0){
							buff.flip();
							while(buff.hasRemaining()){
								fc.write(buff);
							}
							fi.setCurrent(MyMath.add(fi.getCurrent(), flag));
							pro.setProgress(MyMath.add(pro.getProgress(), flag));
							buff.clear();
						}
						dtDao.updateTask(dt);
						dt.pause();
						fc.force(false);
						fc.close();
						fos.close();
						sc.close();
					}
				} catch (InterruptedException e) {
					Log.e("file run error", e.getMessage());
				} catch (IOException e) {
					Log.e("file run error", e.getMessage());
				}finally{
					if(null!=dt){
						dtDao.updateTask(dt);
						dt.pause();
					}
					
				}
			}
			try {
				close();
			} catch (IOException e) {
				Log.e("file close error", e.getMessage());
			}finally{
				for(DownLoadTask dt:array_task){
					dtDao.updateTask(dt);
				}
			}
		}
		
		public boolean conn(){
			try {
				App.sendMsg(MyGson.getString(new Msg("fileconn", "")));
				Thread.sleep(800);
				Log.e("runDown", "open SocketChannel");
				sc = SocketChannel.open();
				Log.e("runDown", "try conn");
				sc.connect(new InetSocketAddress(App.HostIp(),App.file_port));
				Log.e("runDown", "conn finish");
			} catch (InterruptedException e1) {
				Log.e("file conn error", e1.getMessage());
				return false;
			} catch (IOException e) {
				Log.e("file conn error", e.getMessage());
				return false;
			}
			return true;
		}
		
		public void close() throws IOException{
			if(null!=sc)
				sc.close();
		}
	}
}
