package cn.link.modular;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import android.util.Log;

import cn.link.beans.FileInfo;
import cn.link.beans.Msg;
import cn.link.box.App;
import cn.link.common.MyGson;
import cn.link.common.MyMath;

public class FileDownLoad {

/*	private SocketChannel sc;
	private Progress pro;
	private FileInfo fi;
	private File fl;
	
	String ip;
	int port;
	boolean isDown;
	
	runDown run;
	
	public FileDownLoad(String ip,int port,FileInfo fi,File fl) throws IOException{
		this.ip = ip;
		this.port = port;
		this.fl = fl;
		this.fi = fi;
	}
	
	public boolean downLoadFile(){
		this.pro = new Progress(fi,0, fi.getLength());
		if(null==run){
			isDown = true;
			run = new runDown(fl);
			Thread th = new Thread(run);
			th.setPriority(10);
			th.start();
			return true;
		}
		return false;
	}
	
	public void stop(){
		if(null!=run)
			try {
				run.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	class runDown implements Runnable{
		File fl;
		int bufferSizeKB = 512;
		int bufferSize = bufferSizeKB * 1024;
		FileOutputStream fos;
		FileChannel fc;
		@Override
		public void run() {
			try {
				Thread.sleep(800);
				Log.i("runDown", "open SocketChannel");
				sc = SocketChannel.open();
				Log.i("runDown", "try conn");
				sc.connect(new InetSocketAddress(ip,port));
				Log.i("runDown", "conn finish");
				int flag = 0;
				fos = new FileOutputStream(fl);
				fc = fos.getChannel();
				ByteBuffer buff = ByteBuffer.allocateDirect(bufferSize);
				while(pro.getProgress()<pro.getMax()&&(flag=sc.read(buff))>0&&isDown){
					buff.flip();
					while(buff.hasRemaining()){
						fc.write(buff);
					}
					buff.clear();
					pro.setProgress(MyMath.add(pro.getProgress(), flag));
				}
				fc.force(false);
				fc.close();
				fos.close();
				sc.close();
			} catch (FileNotFoundException e) {
				Log.e("download", e.getMessage());
			} catch (IOException e) {
				Log.e("download",e.getMessage());
			} catch (InterruptedException e) {
				Log.e("download",e.getMessage());
			}
			
		}
		
		public runDown(File fl){
			this.fl = fl;
		}
		
		public void close() throws IOException{
			if(null!=fc)
				fc.close();
			if(null!=fos)
				fos.close();
			if(null!=sc)
				sc.close();
		}
	}

	public Progress getProgress() {
		return pro;
	}
	*/
}
