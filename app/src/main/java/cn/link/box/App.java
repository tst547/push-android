package cn.link.box;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
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
import com.google.gson.reflect.TypeToken;

/**
 * 全局类 类似j2ee中的 Application的概念 作为类与类之间的交互中心，存放 共用数据
 *
 * @author hanyu
 */
public class App {

    public static List<DownLoadMsg> downloadMsgs = new ArrayList<>();

    public static String SdCardPath = Environment.getExternalStorageDirectory()
            + "/";
    public static File path;

    public static Type fileListType = new TypeToken<List<Base.File>>() {}.getType();

    private static String broadcastAddr;// 广播地址
    private static int ServerScannerPort = 22555; //扫描端口(广播使用)
    private static String hostIp;// 主机IP
    private static int hostPort;//  主机服务端口
    private static String gateWay;// 网关IP
    private static String DownLoadPath = "JCdownload";// 文件到手机SD卡上的指定目录

    static {
        path = new File(App.SdCardPath + App.DownLoadPath);
        if (!path.exists()) {
            path.mkdirs();
        }
    }

    /**
     * 根据Base.File对象创建
     * 手机对应文件
     *
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
     * 获取网关
     *
     * @return
     */
    public static String getGateWay() {
        return App.gateWay;
    }

    /**
     * 带参设置网关
     *
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
                .substring(0, serverIp.lastIndexOf(".")).concat(".255"));
    }

    private static void BroadcastAddr(String broadcastAddr) {
        App.broadcastAddr = broadcastAddr;
    }

    public static boolean findHost() {
        if (null != App.hostIp && App.hostPort != 0)
            return true;
        try {
            return new Scanner(App.broadcastAddr, App.ServerScannerPort)
                    .init()
                    .conn();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean scanHost() {
        if (null != App.hostIp && App.hostPort != 0)
            return true;
        try {
            return new Scanner(App.broadcastAddr, App.ServerScannerPort)
                    .init()
                    .scan();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void HostPort(int port) {
        App.hostPort = port;
    }

    public static String HostPort() {
        return String.valueOf(App.hostPort);
    }

    public static Session getSession() {
        return Session.create();
    }

    public static String getFileMsg(Base.File file) {
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
                .append(file.isDir ? ConstStrings.Folder : ConstStrings.File)
                .append(ConstStrings.LineSeparator)
                .append(ConstStrings.FileSize)
                .append(ConstStrings.Colon)
                .append((file.size > 1048576 ? MyMath.divide(file.size, 1048576, ConstStrings.DivideFormat).concat(ConstStrings.FileUnits)
                        : (file.size > 1024 ? MyMath.divide(file.size, 1024, ConstStrings.DivideFormat).concat(ConstStrings.FileUnitsKb)
                        : String.valueOf(file.size).concat(ConstStrings.FileUnitsByte))))
                .toString();

    }
}
