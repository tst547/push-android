package cn.link.box;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import android.app.Activity;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import cn.link.beans.FileInfo;
import cn.link.common.WifiUtil;
import cn.link.modular.DownLoader;
import cn.link.net.Base;
import cn.link.net.Scanner;

/**
 * 全局类 类似j2ee中的 Application的概念 作为类与类之间的交互中心，存放 共用数据
 *
 * @author hanyu
 *
 */
public class App {

	public static final int FIND = 1;
	public static final int SCAN = 2;
	private static String broadcastAddr;// 广播地址
	private static int ServerScannerPort = 22555; //扫描端口(广播使用)
	private static String hostIp;// 主机IP
	private static int hostPort;//  主机服务端口
	private static String gateWay;// 网关IP

	public static String downLoadPath = "JCdownload";// 文件到手机SD卡上的指定目录

	public static String SdCardPath = Environment.getExternalStorageDirectory()
			+ "/";


	private static List<Activity> activities = new ArrayList<Activity>();// 存放Activity
	private static Map<String,Object> temp = new HashMap<String, Object>();
	public static ArrayBlockingQueue<FileInfo> fi_queue = new ArrayBlockingQueue<FileInfo>(
			100);

	public static File path;


	static {
		path = new File(App.SdCardPath + App.downLoadPath);
		if (!path.exists()) {
			path.mkdirs();
		}
	}

	public static void setTemp(String key,Object obj){
		if(temp.containsKey(key))
			temp.remove(key);
		temp.put(key, obj);
	}

	public static Object getTemp(String key){
		if(temp.containsKey(key))
			return temp.get(key);
		return null;
	}

	/**
	 * 根据FileInfo对象获取下载器
	 *
	 * @param fi
	 * @return
	 * @throws IOException
	 */
	/*
	 * public static FileDownLoad getFileDownload(FileInfo fi) throws
	 * IOException{ File path = new File(App.SD_path+App.downLoad_path);
	 * if(!path.exists()){ path.mkdirs(); } File fl = new
	 * File(path.getPath()+"/"+fi.getName()); if(!fl.exists()){
	 * fl.createNewFile(); }else{ int num = 1; while(!fl.exists()){ fl = new
	 * File(path.getPath()+"/"+"("+num+")"+fi.getName()); num++; }
	 * fl.createNewFile(); } FileDownLoad fd = new FileDownLoad(App.HostIp(),
	 * App.file_port,fi,fl); return fd; }
	 */


	public static File getFileByFileInfo(FileInfo fi) throws IOException {
		File path = new File(App.SdCardPath + App.downLoadPath);
		if (!path.exists()) {
			path.mkdirs();
		}
		File fl = new File(path.getPath() + "/" + fi.getName());
		if (!fl.exists()) {
			fl.createNewFile();
		} else {
			int num = 1;
			while (fl.exists()) {
				fl = new File(path.getPath() + "/" + "(" + num + ")"
						+ fi.getName());
				num++;
			}
			fl.createNewFile();
		}
		return fl;
	}

	/**
	 * 单例模式获取
	 *
	 * @return
	 */
	public static DownLoader getDownLoader() {
		return DownLoader.getDownLoader();
	}

	/**
	 * 添加新活动
	 *
	 * @param act
	 */
	public static void addActivity(Activity act) {
		activities.add(act);
	}

	/**
	 * 当活动被销毁时 移除活动
	 *
	 * @param act
	 */
	public static void removeActivity(Activity act) {
		activities.remove(act);
	}

	/**
	 * 获取网关
	 *
	 * @return
	 */
	public static String getGateWay() {
		return App.gateWay;
	}

	/**
	 * 带参设置网关
	 * @param gateWay
	 */
	public static void GateWay(String gateWay) {
		App.gateWay = gateWay;
	}

	/**
	 * 获取主机IP
	 *
	 * @return
	 */
	public static String HostIp() {
		return hostIp;
	}

	/**
	 * 设置主机IP
	 *
	 * @param hostIp
	 */
	public static void HostIp(String hostIp) {
		App.hostIp = hostIp;
	}

	/**
	 * 用Android带有WifiManager 来获取 当前网络信息(主机IP,网关IP) 该方法假设网关即为主机
	 *
	 * @param wm
	 */
	public static void readWifiInfo(WifiManager wm) {
		// WifiInfo wifiInfo = wm.getConnectionInfo();
		DhcpInfo di = wm.getDhcpInfo();
		String serverIp = WifiUtil.long2ip(di.gateway);
		App.GateWay(serverIp);
		App.BroadcastAddr(serverIp
				.substring(0,serverIp.lastIndexOf(".")).concat(".255"));
	}

	private static void BroadcastAddr(String broadcastAddr) {
		App.broadcastAddr = broadcastAddr;
	}

	public static boolean findHost() {
		if (null!=App.hostIp&&App.hostPort!=0)
			return true;
		try {
			Base.BaseMsg<Base.IpMsg> rMsg = new Scanner(App.broadcastAddr, App.ServerScannerPort)
					.init()
					.conn();
			App.HostIp(rMsg.msg.addr);
			App.HostPort(rMsg.msg.port);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean scanHost() {
		if (null!=App.hostIp&&App.hostPort!=0)
			return true;
		try {
			Base.BaseMsg<Base.IpMsg> rMsg = new Scanner(App.broadcastAddr, App.ServerScannerPort)
					.init()
					.scan();
			App.HostIp(rMsg.msg.addr);
			App.HostPort(rMsg.msg.port);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static void HostPort(int port) {
		App.hostPort = port;
	}
}
