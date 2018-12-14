package cn.link.box;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.view.View;
import cn.link.common.MyMath;
import cn.link.common.WifiUtil;
import cn.link.net.NetWorkInfo;
import cn.link.net.download.DownLoadMsg;
import cn.link.net.Base;
import cn.link.net.Scanner;
import cn.link.net.Session;
import cn.link.net.download.ProgressTask;
import com.google.gson.reflect.TypeToken;

/**
 * 全局类 类似j2ee中的 Application的概念 作为类与类之间的交互中心，存放 共用数据
 *
 * @author hanyu
 */
public class App {

    public static List<DownLoadMsg> downloadMsgs = new ArrayList<>();//下载信息列表

    public static List<ProgressTask> taskList = new ArrayList<>();//进度条刷新任务列表

    public static List<View> viewList = new ArrayList<>();//

    public static String SdCardPath = Environment.getExternalStorageDirectory()
            + "/";
    public static File path;

    public static Type fileListType = new TypeToken<Base.BaseMsg<List<Base.File>>>() {}.getType();

    private static int ServerScannerPort = 22555; //扫描端口(广播使用)

    private static NetWorkInfo netWorkInfo;//网路配置信息

    private static String DownLoadPath = "JCdownload";// 文件到手机SD卡上的指定目录

    static {

        netWorkInfo = new NetWorkInfo();

        path = new File(App.SdCardPath + App.DownLoadPath);
        if (!path.exists()) {
            path.mkdirs();
        }

    }

    public static NetWorkInfo getNetWorkInfo(){
        return netWorkInfo;
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
     * 用Android带有WifiManager 来获取 当前网络信息
     * @param wm
     */
    public static void readWifiInfo(WifiManager wm) {
        // WifiInfo wifiInfo = wm.getConnectionInfo();
        DhcpInfo di = wm.getDhcpInfo();
        netWorkInfo.setNetmask(di.netmask==0?4294967040l:di.netmask);
        netWorkInfo.setIp(WifiUtil.ip2long(WifiUtil.androidLong2ip(di.ipAddress)));
        netWorkInfo.setBroadcastAddr(netWorkInfo.getNetmask(),netWorkInfo.getIp());
        netWorkInfo.setGateWay(WifiUtil.ip2long(WifiUtil.androidLong2ip(di.gateway)));
        netWorkInfo.setScanPort(App.ServerScannerPort);
    }

    public static boolean findHost() {
        if (0 != netWorkInfo.getHostIp() && netWorkInfo.getNetmask()!= 0)
            return true;
        try {
            return new Scanner(App.netWorkInfo).conn();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean scanHost() {
        if (0 != netWorkInfo.getIp() && netWorkInfo.getNetmask()!= 0)
            return true;
        try {
            return new Scanner(App.netWorkInfo).scan();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Session getSession() {
        return Session.create(netWorkInfo);
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
