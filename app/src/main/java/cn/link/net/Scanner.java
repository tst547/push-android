package cn.link.net;

import cn.link.box.App;
import cn.link.common.MyGson;
import cn.link.common.WifiUtil;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.*;

/**
 * Created by hanyu on 2017/11/15 0015.
 */
public class Scanner {

    private NetWorkInfo netWorkInfo;

    public Scanner(NetWorkInfo netWorkInfo) {
        this.netWorkInfo = netWorkInfo;
    }

    /**
     * 广播地址查询主机
     *
     * @return
     * @throws IOException
     */
    public boolean conn() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(2500);
            socket.setBroadcast(true);
            byte[] tempBuf = new byte[1];
            tempBuf[0] = 0x01;
            InetAddress addr = InetAddress.getByName(WifiUtil.long2ip(netWorkInfo.getBroadcastAddr()));
            DatagramPacket packet = new DatagramPacket(tempBuf, tempBuf.length, addr, netWorkInfo.getScanPort());
            socket.send(packet);
            byte[] buf = new byte[2048];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            socket.receive(recv);
            netWorkInfo.setHostIp(WifiUtil.ipToInt(recv.getAddress().getHostAddress()));
            Base.BaseMsg<Base.IpMsg> baseMsg = (Base.BaseMsg<Base.IpMsg>) MyGson.getObject(
                    new String(recv.getData(), 0, recv.getLength()).trim(),
                    new TypeToken<Base.BaseMsg<Base.IpMsg>>() {
                    }.getType());
            netWorkInfo.setHostPort(baseMsg.msg.port);;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 1-254段扫描
     *
     * @return
     * @throws Exception
     */
    public boolean scan() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(2500);
            socket.setBroadcast(true);
            socket.setSoTimeout(100);

            byte[] buff = new byte[2048];
            //根据子网掩码 和 主机ip地址 计算出 网络号
            int network = netWorkInfo.getNetmask() & netWorkInfo.getHostIp();
            byte[] buf = new byte[1];
            buf[0] = 0x01;
            //子网掩码取反值后 二进制格式的长度n 则2^n是最大主机数(幂) -2则是可用数量
            int total = (int) Math.pow(2,Integer.toBinaryString((~netWorkInfo.getNetmask())).length())-2;

            for (int i = 0; i < total; i++) {
                //从网络号开始 有total数量的主机数
                String host = WifiUtil.intToIp(network+i);
                if (netWorkInfo.getBroadcastAddr()==network+i||network+i==netWorkInfo.getIp())
                    continue;//如果是广播地址 or 自身ip则跳过
                InetAddress addr = InetAddress.getByName(host);
                DatagramPacket tempPacket = new DatagramPacket(buf, buf.length, addr, netWorkInfo.getScanPort());
                socket.send(tempPacket);
                DatagramPacket recv = new DatagramPacket(buff, buff.length);
                socket.receive(recv);
                Base.BaseMsg<Base.IpMsg> baseMsg = (Base.BaseMsg<Base.IpMsg>) MyGson.getObject(
                        new String(recv.getData(), 0, recv.getLength()).trim(),
                        new TypeToken<Base.BaseMsg<Base.IpMsg>>() {
                        }.getType());
                netWorkInfo.setHostIp(WifiUtil.ipToInt(recv.getAddress().getHostAddress()));
                netWorkInfo.setHostPort(baseMsg.msg.port);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
