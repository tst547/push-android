package cn.link.box;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import cn.link.common.MyMath;
import cn.link.common.WifiUtil;
import cn.link.net.download.DownLoadMsg;
import cn.link.net.Base;
import cn.link.net.Scanner;
import cn.link.net.Session;

/**
 * 全局类 类似j2ee中的 Application的概念 作为类与类之间的交互中心，存放 共用数据
 *
 * @author hanyu
 *
 */
public class App {

	public static List<DownLoadMsg> downloadMsgs = new ArrayList<>();

	public static String SdCardPath = Environment.getExternalStorageDirectory()
			+ "/";
	private static String broadcastAddr;// 广播地址
	private static int ServerScannerPort = 22555; //扫描端口(广播使用)
	private static String hostIp;// 主机IP
	private static int hostPort;//  主机服务端口
	private static String gateWay;// 网关IP
	private static String DownLoadPath = "JCdownload";// 文件到手机SD卡上的指定目录

	private static List<Activity> activities = new ArrayList<>();// 存放Activity

	public static File path;

	static {
		path = new File(App.SdCardPath + App.DownLoadPath);
		if (!path.exists()) {
			path.mkdirs();
		}
	}


/*	public static Object getTemp(String key){
		if(temp.containsKey(key))
			return temp.get(key);
		return null;
	}*/

	/**
	 * 根据Base.File对象创建
	 * 手机对应文件
	 * @param fi
	 * @return
	 * @throws IOException
	 */
	public static File createFileByBaseFile(Base.File fi) throws IOException {
		File path = new File(App.SdCardPath + App.DownLoadPath);
		if (!path.exists()) {
			path.mkdirs();
		}
		File fl = new File(path.getPath() + "/" + fi.name);
		if (!fl.exists()) {
			fl.createNewFile();
		} else {
			int num = 1;
			while (fl.exists()) {
				fl = new File(path.getPath() + "/" + "(" + num + ")"
						+ fi.name);
				num++;
			}
			fl.createNewFile();
		}
		return fl;
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

	public static String HostPort() {
		return String.valueOf(App.hostPort);
	}

	public static Session getSession(){
		return Session.create();
	}

	public static String getFileMsg(Base.File file){
		return new StringBuffer(ConstStrings.FileName)
				.append(ConstStrings.Colon)
				.append(file.name)
				.append(ConstStrings.LineSeparator)
				.append(ConstStrings.FilePath)
				.append(ConstStrings.Colon)
				.append(file.path)
				.append(ConstStrings.LineSeparator)
				.append(ConstStrings.FileType)
				.append(ConstStrings.Colon)
				.append(file.isDir?ConstStrings.Folder:ConstStrings.File)
				.append(ConstStrings.LineSeparator)
				.append(ConstStrings.FileSize)
				.append(ConstStrings.Colon)
				.append(String.valueOf(Long.valueOf(file.size).intValue())
						.concat(
								MyMath.divide(file.size, 1048576, ConstStrings.DivideFormat)
										.concat(ConstStrings.FileUnits))).toString();

	}
}
