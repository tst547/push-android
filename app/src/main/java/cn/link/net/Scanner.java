package cn.link.net;

import cn.link.box.App;
import cn.link.common.MyGson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Created by hanyu on 2017/11/15 0015.
 */
public class Scanner {

    private int port;
    private String broadcastAddr;
    private DatagramSocket socket;
    private DatagramPacket packet;

    public Scanner(String broadcastAddr, int port) {
        this.broadcastAddr = broadcastAddr;
        this.port = port;
    }

    public Scanner init() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        socket.setSoTimeout(2500);
        socket.setBroadcast(true);
        byte[] buf = new byte[1];
        buf[0] = 0x01;
        InetAddress addr = InetAddress.getByName(broadcastAddr);
        packet = new DatagramPacket(buf, buf.length, addr, port);
        return this;
    }

    /**
     * 广播地址查询主机
     *
     * @return
     * @throws IOException
     */
    public boolean conn() {
        try {
            socket.send(packet);
            byte[] buf = new byte[2048];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            socket.receive(recv);
            App.HostIp(recv.getAddress().getHostAddress());
            Base.BaseMsg<Base.IpMsg> baseMsg = (Base.BaseMsg<Base.IpMsg>) MyGson.getObject(
                    new String(recv.getData(), 0, recv.getLength()).trim(),
                    new TypeToken<Base.BaseMsg<Base.IpMsg>>() {
                    }.getType());
            App.HostIp(recv.getAddress().getHostAddress());
            App.HostPort(baseMsg.msg.port);
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
            socket.setSoTimeout(100);
            byte[] buff = new byte[2048];
            String temp = broadcastAddr.substring(0, broadcastAddr.lastIndexOf("."));
            byte[] buf = new byte[1];
            buf[0] = 0x01;
            for (int i = 1; i < 255; i++) {
                String host = temp.concat(".").concat(String.valueOf(i));
                InetAddress addr = InetAddress.getByName(host);
                if (host.equals(broadcastAddr)) {
                    continue;
                }
                DatagramPacket tempPacket = new DatagramPacket(buf, buf.length, addr, port);
                socket.send(tempPacket);
                DatagramPacket recv = new DatagramPacket(buff, buff.length);
                socket.receive(recv);
                Base.BaseMsg<Base.IpMsg> baseMsg = (Base.BaseMsg<Base.IpMsg>) MyGson.getObject(
                        new String(recv.getData(), 0, recv.getLength()).trim(),
                        new TypeToken<Base.BaseMsg<Base.IpMsg>>() {
                        }.getType());
                App.HostIp(recv.getAddress().getHostAddress());
                App.HostPort(baseMsg.msg.port);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
